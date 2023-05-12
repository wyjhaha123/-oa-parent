package com.wyj.auth;

import com.wyj.auth.service.SysUserService;
import com.wyj.model.system.SysUser;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.repository.Deployment;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SpringBootTest
public class TestAcitiviti {
    @Autowired
    private RepositoryService repositoryService;
    @Autowired
    private RuntimeService runtimeService;

    @Autowired
    private SysUserService sysUserService;

    @Autowired
    private TaskService taskService;

    @Test
    public void startup(){
        Deployment deploy = repositoryService.createDeployment().addClasspathResource("process/jiaban4.bpmn20.xml")
                .name("请假流程1")
                .deploy();

    }

    @Test
    public void taskAssignee(){
        SysUser sysUser = sysUserService.getById(1L);
        String bmid = sysUser.getBmid();
        Long zwid = 3L;

        LambdaQueryWrapper<SysUser> wrapper=new LambdaQueryWrapper<>();
        wrapper.eq(SysUser::getBmid,bmid);
        wrapper.eq(SysUser::getZwid,zwid);
        SysUser one = sysUserService.getOne(wrapper);
        System.out.println(one+"+++++++++++++++++++++++++++++++");

        Long zwid2 = 2L;
        LambdaQueryWrapper<SysUser> wrapper2=new LambdaQueryWrapper<>();
        wrapper2.eq(SysUser::getBmid,bmid);
        wrapper2.eq(SysUser::getZwid,zwid2);
        SysUser one2 = sysUserService.getOne(wrapper);

        //创建流程实例,我们需要知道流程定义的key
        Map<String,Object> variable=new HashMap<>();
        variable.put("user1",one.getName());
        variable.put("user2",one2.getName());

        ProcessInstance processInstance = runtimeService.startProcessInstanceByKey("jiaban3",variable);

        //输出实例的相关信息
        System.out.println("流程定义id：" + processInstance.getProcessDefinitionId());
        System.out.println("流程实例id：" + processInstance.getId());
        System.out.println("当前活动Id：" + processInstance.getActivityId());

    }

    @Test
    public void task(){


        //任务负责人
        String assignee = "王经理";
        List<Task> list = taskService.createTaskQuery()
               // .taskId("45798a98-dd18-11ed-902c-005056c00008")
      //  task.getProcessInstanceId()
                .processInstanceId("43371eaf-dd19-11ed-b8d5-005056c00008")

                .list();
        for (Task task : list) {
            System.out.println("流程实例id：" + task.getProcessInstanceId());
            System.out.println("任务id：" + task.getId());
            System.out.println("任务负责人：" + task.getAssignee());
            System.out.println("任务名称：" + task.getName());
        }

    }
    @Test
    public void test2(){
        //创建流程实例,我们需要知道流程定义的key
        Map<String,Object> variable=new HashMap<>();
        variable.put("user1","二狗子");
        variable.put("user2","大狗子");
        runtimeService.startProcessInstanceByKey("jiaban3",variable);
    }

    @Test
    public void  test1(){
        List<Task> list = taskService.createTaskQuery()
                .processInstanceId("583f193f-d181-11ed-bccb-005056c00008").list();
        for (Task task:list){
            task.getAssignee();
        }

    }
}
