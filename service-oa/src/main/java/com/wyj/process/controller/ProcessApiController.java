package com.wyj.process.controller;


import com.wyj.common.result.Result;
import com.wyj.model.process.Process;
import com.wyj.model.process.ProcessTemplate;
import com.wyj.model.process.ProcessType;
import com.wyj.process.service.OaProcessService;
import com.wyj.process.service.OaProcessTemplateService;
import com.wyj.process.service.OaProcessTypeService;
import com.wyj.vo.process.ProcessFormVo;
import com.wyj.vo.process.ProcessVo;
//import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Api(tags = "审批流管理")
@RestController
@RequestMapping(value="/admin/process")
@CrossOrigin  //跨域
public class ProcessApiController {

    @Autowired
    private OaProcessTypeService processTypeService;

    @Autowired
    private OaProcessTemplateService oaProcessTemplateService;

    @Autowired
    private OaProcessService oaProcessService;

    @ApiOperation(value = "获取全部审批分类及模板")
    @GetMapping("findProcessType")
    public Result findProcessType() {
     List<ProcessType> processTypeList= processTypeService.findProcessType();
        return Result.ok(processTypeList);
    }

    @ApiOperation(value = "获取审批模板")
    @GetMapping("getProcessTemplate/{processTemplateId}")
    public Result get(@PathVariable Long processTemplateId) {
        ProcessTemplate processTemplate = oaProcessTemplateService.getById(processTemplateId);
        return Result.ok(processTemplate);
    }

    @ApiOperation(value = "启动流程")
    @PostMapping("/startUp")
    public Result start(@RequestBody(required = false) ProcessFormVo processFormVo) {
        if (processFormVo == null) {
            return Result.ok();
        }
        System.out.println(processFormVo.getProcessTemplateId()+"+++++++++++++++++++xxxxxxxxxxx");
        System.out.println(processFormVo.getProcessTemplateId()+"+++++++++++++++++++xxxxxxxxxxx");
        System.out.println(processFormVo.getFormValues()+"+++++++++++++++++++xxxxxxxxxxx");
        oaProcessService.startUp(processFormVo);
        return Result.ok();
    }

    @ApiOperation(value = "待处理")
    @GetMapping("/findPending/{page}/{limit}")
    public Result findPending(
          //  @ApiParam(name = "page", value = "当前页码", required = true)
            @PathVariable Long page,

           // @ApiParam(name = "limit", value = "每页记录数", required = true)
            @PathVariable Long limit) {
        Page<Process> pageParam = new Page<>(page, limit);
        Page<ProcessVo> pageProcessVo = oaProcessService.findPending(pageParam);
        return Result.ok(pageProcessVo);
    }




}
