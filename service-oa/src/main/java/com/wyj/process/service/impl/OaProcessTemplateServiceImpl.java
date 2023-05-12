package com.wyj.process.service.impl;

import com.wyj.model.process.ProcessTemplate;
import com.wyj.model.process.ProcessType;
import com.wyj.process.mapper.OaProcessTemplateMapper;
import com.wyj.process.service.OaProcessService;
import com.wyj.process.service.OaProcessTemplateService;
import com.wyj.process.service.OaProcessTypeService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;

/**
 * <p>
 * 审批模板 服务实现类
 * </p>
 *
 * @author atguigu
 * @since 2023-03-29
 */
@Service
public class OaProcessTemplateServiceImpl extends ServiceImpl<OaProcessTemplateMapper, ProcessTemplate> implements OaProcessTemplateService {

    @Autowired
    private OaProcessTypeService processTypeService;

    @Autowired
    private OaProcessService processService;

    //审批模板 根据模板类型字段关联模板类型表获取类型名称
    @Override
    public IPage<ProcessTemplate> selectPage(Page<ProcessTemplate> pageParam) {
        Page<ProcessTemplate> processTemplatePage = baseMapper.selectPage(pageParam,null);
        List<ProcessTemplate> records = processTemplatePage.getRecords();
        for (ProcessTemplate processTemplate :records){
            //获取模板类型id
            Long processTypeId = processTemplate.getProcessTypeId();
            //根据模板类型id查询模板名称
            LambdaQueryWrapper<ProcessType> wrapper=new LambdaQueryWrapper<>();
             wrapper.eq(ProcessType::getId, processTypeId);
            ProcessType processType = processTypeService.getOne(wrapper);
            if (processType == null){
                continue;
            }
            processTemplate.setProcessTypeName(processType.getName());
        }
        return processTemplatePage;
    }

    @Override
    public IPage<ProcessTemplate> selectTest(Page<ProcessTemplate> page) {
        Page<ProcessTemplate> processTemplatePage = baseMapper.selectPage(page,null);
        List<ProcessTemplate> records = processTemplatePage.getRecords();
        List<ProcessTemplate> records3 = processTemplatePage.getRecords();

        for (ProcessTemplate processTemplate :records){
            //获取模板类型id
            Long processTypeId = processTemplate.getProcessTypeId();
            //根据模板类型id查询模板名称
            LambdaQueryWrapper<ProcessType> wrapper=new LambdaQueryWrapper<>();
            wrapper.eq(ProcessType::getId, processTypeId);
            ProcessType processType = processTypeService.getOne(wrapper);
            if (processType == null){
                continue;
            }
            processTemplate.setProcessTypeName(processType.getName());

        }

        List<ProcessTemplate> records2 = processTemplatePage.getRecords();


        return processTemplatePage;
    }

    @Override
    public void publish(Long id) {
        ProcessTemplate processTemplate = baseMapper.selectById(id);
        processTemplate.setStatus(1);
        baseMapper.updateById(processTemplate);

        //TODO 部署流程定义，后续完善
        //优先发布在线流程设计
        if(!StringUtils.isEmpty(processTemplate.getProcessDefinitionPath())) {
            processService.deployByZip(processTemplate);
        }
    }
}
