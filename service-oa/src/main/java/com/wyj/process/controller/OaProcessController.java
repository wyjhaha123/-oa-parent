package com.wyj.process.controller;


import com.wyj.common.result.Result;
import com.wyj.model.process.Process;
import com.wyj.process.service.OaProcessService;
import com.wyj.vo.process.ApprovalVo;
import com.wyj.vo.process.ProcessQueryVo;
import com.wyj.vo.process.ProcessVo;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * <p>
 * 审批类型 前端控制器
 * </p>
 *
 * @author atguigu
 * @since 2023-03-30
 */
@Api(tags = "审批流管理")
@RestController
@RequestMapping(value = "/admin/process")
@CrossOrigin  //跨域
public class OaProcessController {

    @Autowired
    private OaProcessService oaProcessService;
    @ApiOperation(value = "获取分页列表")
    @GetMapping("{page}/{limit}")

    public Result index(@PathVariable Long page,
                        @PathVariable Long limit,
                        ProcessQueryVo processQueryVo){
        Page<ProcessVo> pageParam =new Page<>(page,limit);
        IPage<ProcessVo> pageModel = oaProcessService.selectPage(pageParam,processQueryVo);

        return Result.ok(pageModel);

    }

    @ApiOperation(value = "获取审批详情")
    @GetMapping("show/{id}")
    public Result show(@PathVariable Long id) {


        return Result.ok(oaProcessService.show(id));
    }

    @ApiOperation(value = "审批")
    @PostMapping("approve")
    public Result approve(@RequestBody ApprovalVo approvalVo) {
        oaProcessService.approve(approvalVo);
        return Result.ok();
    }

    @ApiOperation(value = "已处理")
    @GetMapping("/findProcessed/{page}/{limit}")
    public Result findProcessed(
            @ApiParam(name = "page", value = "当前页码", required = true)
            @PathVariable Long page,
            @ApiParam(name = "limit", value = "每页记录数", required = true)
            @PathVariable Long limit) {
        Page<Process> pageParam = new Page<>(page, limit);
        IPage<ProcessVo> processedVo = oaProcessService.findProcessed(pageParam);

        return Result.ok(processedVo);
    }

    @ApiOperation(value = "已发起")
    @GetMapping("/findStarted/{page}/{limit}")
    public Result findStarted(
            @ApiParam(name = "page", value = "当前页码", required = true)
            @PathVariable Long page,

            @ApiParam(name = "limit", value = "每页记录数", required = true)
            @PathVariable Long limit) {
        Page<ProcessVo> pageParam = new Page<>(page, limit);
        return Result.ok(oaProcessService.findStarted(pageParam));
    }


}

