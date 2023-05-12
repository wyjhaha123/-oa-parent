package com.wyj.process.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.wyj.auth.service.SysUserService;
import com.wyj.model.process.Process;
import com.wyj.model.process.ProcessRecord;
import com.wyj.model.process.ProcessTemplate;
import com.wyj.model.system.SysUser;
import com.wyj.process.mapper.OaProcessMapper;
import com.wyj.process.service.OaProcessRecordService;
import com.wyj.process.service.OaProcessService;
import com.wyj.process.service.OaProcessTemplateService;
import com.wyj.security.custom.LoginUserInfoHelper;
import com.wyj.vo.process.ApprovalVo;
import com.wyj.vo.process.ProcessFormVo;
import com.wyj.vo.process.ProcessQueryVo;
import com.wyj.vo.process.ProcessVo;
import com.wyj.wechat.service.MessageService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import org.activiti.bpmn.model.BpmnModel;
import org.activiti.bpmn.model.EndEvent;
import org.activiti.bpmn.model.FlowNode;
import org.activiti.bpmn.model.SequenceFlow;
import org.activiti.engine.*;
import org.activiti.engine.history.HistoricTaskInstance;
import org.activiti.engine.history.HistoricTaskInstanceQuery;
import org.activiti.engine.repository.Deployment;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.activiti.engine.task.TaskQuery;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipInputStream;

/**
 * <p>
 * 审批类型 服务实现类
 * </p>
 *
 * @author atguigu
 * @since 2023-03-30
 */
@Service
public class OaProcessServiceImpl extends ServiceImpl<OaProcessMapper, Process> implements OaProcessService {

    @Autowired
    private OaProcessMapper oaProcessMapper;
    @Autowired
    private RepositoryService repositoryService;


    @Autowired
    private SysUserService sysUserService;

    @Autowired
    private OaProcessTemplateService processTemplateService;

    @Autowired
    private RuntimeService runtimeService;

    @Autowired
    private TaskService taskService;

    @Autowired
    private  OaProcessMapper processMapper;

    @Autowired
    private OaProcessRecordService processRecordService;

    @Autowired
    private ProcessEngine processEngine;

    @Autowired
    private HistoryService historyService;

    @Autowired
    private MessageService messageService;


    @Override
    public IPage<ProcessVo> selectPage(Page<ProcessVo> pageParam, ProcessQueryVo processQueryVo) {
        IPage<ProcessVo> page = oaProcessMapper.selectPage(pageParam, processQueryVo);
        return page;
    }

    @Override
    public void deployByZip(ProcessTemplate processTemplate) {
        //定义zip输入流
        InputStream resourceAsStream = this.getClass().getClassLoader().getResourceAsStream(processTemplate.getProcessDefinitionPath());
        ZipInputStream zipInputStream = new ZipInputStream(resourceAsStream);
        // 流程部署
        Deployment deployment = repositoryService.createDeployment()
                .addZipInputStream(zipInputStream)
                .deploy();
        System.out.println(deployment.getName()+"+++++++++++++++++++++++++++"+processTemplate.getName()+"流程");
    }

    @Override
    public void startUp(ProcessFormVo processFormVo) {
        //根据当前用户id获取用户信息
        Long userId = LoginUserInfoHelper.getUserId();
       // System.out.println("LoginUserInfoHelper.getUserId()++++++++++++++++"+LoginUserInfoHelper.getUserId());
        SysUser sysUser = sysUserService.getById(userId);
      //  System.out.println("sysUser++++++++++++++++"+sysUser);
//        SysUser sysUser = sysUserService.getById(1L);
        //根据审批模板获取id获取模板信息
        ProcessTemplate processTemplate = processTemplateService.getById(processFormVo.getProcessTemplateId());
        //保存提交审批信息到业务表oa_process
        Process process = new Process();
        BeanUtils.copyProperties(processFormVo, process);
        String workNo = System.currentTimeMillis() + "";
        process.setProcessCode(workNo);
        process.setUserId(LoginUserInfoHelper.getUserId());
        process.setFormValues(processFormVo.getFormValues());
        System.out.println("sysUser+++++++++++++++"+sysUser.getName()+processTemplate.getName());
        process.setTitle(sysUser.getName() + "发起" + processTemplate.getName() + "申请");
        process.setStatus(1);
        oaProcessMapper.insert(process);

        //启动流程实例
        //绑定业务id
        String businessKey = String.valueOf(process.getId());
        //流程参数
        Map<String, Object> variables = new HashMap<>();
        //将表单数据放入流程实例中
        JSONObject jsonObject = JSON.parseObject(process.getFormValues());
        JSONObject formData = jsonObject.getJSONObject("formData");

       // System.out.println("formData++++++++++++++++++++++++++++++"+formData);

        Map<String, Object> map = new HashMap<>();
        //4.3流程参数 form 表单json数据,转成map集合
        //循环转换
        for (Map.Entry<String, Object> entry : formData.entrySet()) {
            map.put(entry.getKey(), entry.getValue());
        }
        variables.put("data", map);
        System.out.println(processTemplate.getProcessDefinitionKey()+"++++++++++++");
        //ProcessInstance processInstance2 = processEngine.getRuntimeService().startProcessInstanceByKey("processes/qingjiatest");
        //System.out.println(processInstance2);
        ProcessInstance processInstance =
                runtimeService.startProcessInstanceByKey(processTemplate.getProcessDefinitionKey(), businessKey, variables);


        //5查询下一个审批人
        //业务表关联当前流程实例id
        String processInstanceId = processInstance.getId();


        //计算下一个审批人，可能有多个（并行审批）
        List<Task> taskList = this.getCurrentTaskList(processInstanceId);
        if (!CollectionUtils.isEmpty(taskList)) {
            List<String> assigneeList = new ArrayList<>();
            for(Task task : taskList) {
                SysUser user = sysUserService.getUserByUserName(task.getAssignee());
                assigneeList.add(user.getName());

                //TODO 6 推送消息
                //推送消息给下一个审批人，后续完善
                messageService.pushPendingMessage(process.getId(), sysUser.getId(), task.getId());
            }
            process.setProcessInstanceId(processInstanceId);
            process.setDescription("等待" + StringUtils.join(assigneeList.toArray(), ",") + "审批");
        }
        //7流程和业务关联
        processMapper.updateById(process);
        //记录操作信息审批记录
        processRecordService.record( process.getId(),1,"发起申请");

    }



    @Override
    public Page<ProcessVo> findPending(Page<Process> pageParam) {
        // 根据当前人的ID查询
        TaskQuery query = taskService.createTaskQuery()
                .taskAssignee(LoginUserInfoHelper.getUsername())
                .orderByTaskCreateTime()
                .desc();
        List<Task> list = query.listPage((int) ((pageParam.getCurrent() - 1) * pageParam.getSize()), (int) pageParam.getSize());
        long totalCount = query.count();

        List<ProcessVo> processList = new ArrayList<>();
        // 根据流程实例id关系业务Id businessKey 查询实体并关联
        for (Task item : list) {
            String processInstanceId = item.getProcessInstanceId();
            ProcessInstance processInstance = runtimeService.createProcessInstanceQuery().processInstanceId(processInstanceId).singleResult();
            if (processInstance == null) {
                continue;
            }
            // 业务key
            String businessKey = processInstance.getBusinessKey();
            if (businessKey == null) {
                continue;
            }
            Process process = baseMapper.selectById(Long.parseLong(businessKey));
            ProcessVo processVo = new ProcessVo();
            BeanUtils.copyProperties(process, processVo);
            processVo.setTaskId(item.getId());
            processList.add(processVo);
        }
        Page<ProcessVo> page = new Page<ProcessVo>(pageParam.getCurrent(), pageParam.getSize(), totalCount);
        page.setRecords(processList);
        return page;
    }

    //审批详情显示
    @Override
    public Object show(Long id) {
        Process process = this.getById(id);
        List<ProcessRecord> processRecordList = processRecordService.list(new LambdaQueryWrapper<ProcessRecord>().eq(ProcessRecord::getProcessId, id));
        ProcessTemplate processTemplate = processTemplateService.getById(process.getProcessTemplateId());
        Map<String, Object> map = new HashMap<>();
        map.put("process", process);
        map.put("processRecordList", processRecordList);
        map.put("processTemplate", processTemplate);
        //计算当前用户是否可以审批，能够查看详情的用户不是都能审批，审批后也不能重复审批
        boolean isApprove = false;
        List<Task> taskList = this.getCurrentTaskList(process.getProcessInstanceId());
        if (!CollectionUtils.isEmpty(taskList)) {
            for(Task task : taskList) {
                if(task.getAssignee().equals(LoginUserInfoHelper.getUsername())) {
                    isApprove = true;
                }
            }
        }
        map.put("isApprove", isApprove);
        return map;
    }

    //审批
    @Override
    public void approve(ApprovalVo approvalVo) {

        Map<String, Object> variables1 = taskService.getVariables(approvalVo.getTaskId());
        for (Map.Entry<String, Object> entry : variables1.entrySet()) {
            System.out.println("Key = " + entry.getKey() + ", Value = " + entry.getValue());
        }
        String taskId = approvalVo.getTaskId();
        if (approvalVo.getStatus() == 1) {
            //已通过
            Map<String, Object> variables = new HashMap<String, Object>();
            taskService.complete(taskId, variables);
        } else {
            //驳回
            this.endTask(taskId);
        }
        String description = approvalVo.getStatus().intValue() == 1 ? "已通过" : "驳回";
        processRecordService.record(approvalVo.getProcessId(), approvalVo.getStatus(), description);

        //计算下一个审批人
        Process process = this.getById(approvalVo.getProcessId());
        List<Task> taskList = this.getCurrentTaskList(process.getProcessInstanceId());
        if (!CollectionUtils.isEmpty(taskList)) {
            List<String> assigneeList = new ArrayList<>();
            for(Task task : taskList) {
                SysUser sysUser = sysUserService.getUserByUserName(task.getAssignee());
                assigneeList.add(sysUser.getName());

                //推送消息给下一个审批人
                messageService.pushPendingMessage(process.getId(), sysUser.getId(), task.getId());

            }
            process.setDescription("等待" + StringUtils.join(assigneeList.toArray(), ",") + "审批");
            process.setStatus(1);
        } else {
            if(approvalVo.getStatus().intValue() == 1) {
                process.setDescription("审批完成（同意）");
                process.setStatus(2);
            } else {
                process.setDescription("审批完成（拒绝）");
                process.setStatus(-1);
            }
        }
        //TODO
        messageService.pushProcessedMessage(process.getId(), process.getUserId() , process.getStatus());
        //推送消息给申请人
        this.updateById(process);
    }

    //已处理任务
    @Override
    public IPage<ProcessVo> findProcessed(Page<Process> pageParam) {
        String username = LoginUserInfoHelper.getUsername();
        HistoricTaskInstanceQuery query = historyService.createHistoricTaskInstanceQuery()
                .taskAssignee(username)
                .finished()
                .orderByTaskCreateTime()
                .desc();
        List<HistoricTaskInstance> list = query.listPage((int)((pageParam.getCurrent() - 1)
                *  pageParam.getSize()),(int) pageParam.getSize());
        List<ProcessVo> processList=new ArrayList<>();
        for (HistoricTaskInstance instance:list){
            String processInstanceId = instance.getProcessInstanceId();

            Process process = baseMapper.selectOne(new LambdaQueryWrapper<Process>().eq(Process::getProcessInstanceId, processInstanceId));
            if (process !=null){
                ProcessVo processVo = new ProcessVo();
                BeanUtils.copyProperties(process, processVo);
                processVo.setTaskId("0");
                processList.add(processVo);
            }

        }
        long totalCount = query.count();
        IPage<ProcessVo> page = new Page<ProcessVo>(pageParam.getCurrent(), pageParam.getSize(), totalCount);
        page.setRecords(processList);
        return page;
    }

    @Override
    public Object findStarted(Page<ProcessVo> pageParam) {
        ProcessQueryVo processQueryVo = new ProcessQueryVo();
        processQueryVo.setUserId(LoginUserInfoHelper.getUserId());
        IPage<ProcessVo> page = processMapper.selectPage(pageParam, processQueryVo);
        for (ProcessVo item : page.getRecords()) {
            item.setTaskId("0");
        }
        return page;
    }

    private void endTask(String taskId) {
        //  获取当前任务
        Task task = taskService.createTaskQuery().taskId(taskId).singleResult();
        //获取流程定义模板
        BpmnModel bpmnModel = repositoryService.getBpmnModel(task.getProcessDefinitionId());
        //获取模板最终流向节点
        List endEventList = bpmnModel.getMainProcess().findFlowElementsOfType(EndEvent.class);
        // 并行任务可能为null
        if(CollectionUtils.isEmpty(endEventList)) {
            return;
        }
        FlowNode endFlowNode = (FlowNode) endEventList.get(0);

        //当前流向节点
        FlowNode currentFlowNode = (FlowNode) bpmnModel.getMainProcess().getFlowElement(task.getTaskDefinitionKey());

        //  临时保存当前活动的原始方向
        List originalSequenceFlowList = new ArrayList<>();
        originalSequenceFlowList.addAll(currentFlowNode.getOutgoingFlows());
        //  清理活动方向
        currentFlowNode.getOutgoingFlows().clear();

        //  建立新活动方向
        SequenceFlow newSequenceFlow = new SequenceFlow();
        newSequenceFlow.setId("newSequenceFlowId");
        newSequenceFlow.setSourceFlowElement(currentFlowNode);
        newSequenceFlow.setTargetFlowElement(endFlowNode);
        //一个节点可以指向多个方向 用list
        //  当前节点指向新的方向
        List newSequenceFlowList = new ArrayList<>();
        newSequenceFlowList.add(newSequenceFlow);

        currentFlowNode.setOutgoingFlows(newSequenceFlowList);

        //  完成当前任务
        taskService.complete(task.getId());
    }

    private List<Task> getCurrentTaskList(String processInstanceId) {
        List<Task> tasks = taskService.createTaskQuery().processInstanceId(processInstanceId).list();
        return tasks;
    }


}
