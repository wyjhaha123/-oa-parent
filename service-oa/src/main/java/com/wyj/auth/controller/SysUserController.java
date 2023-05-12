package com.wyj.auth.controller;


import com.wyj.auth.service.SysUserService;
import com.wyj.common.result.Result;
import com.wyj.common.utils.MD5;
import com.wyj.model.system.SysUser;
import com.wyj.vo.system.SysUserQueryVo;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

/**
 * <p>
 * 用户表 前端控制器
 * </p>
 *
 * @author atguigu
 * @since 2023-03-12
 */

@Api(tags = "用户管理接口")
@RestController
@RequestMapping("/admin/system/sysUser")
public class SysUserController {

    @Autowired
    private  SysUserService service;



    @ApiOperation(value = "更新状态")
    @GetMapping("updateStatus/{id}/{status}")
    public Result updateStatus(@PathVariable Long id, @PathVariable Integer status) {
        service.updateStatus(id, status);
        return Result.ok();
    }

    //用户条件分页查询
    @ApiOperation("条件分页查询")
    @GetMapping("{page}/{limit}")
    public Result index(@PathVariable Long page,
                        @PathVariable Long limit,
                        SysUserQueryVo sysUserQueryVo){

        //创建分页page
        Page<SysUser> pageParam=new Page<>(page,limit);

        //获取条件
        String keyword = sysUserQueryVo.getKeyword();
        String createTimeBegin = sysUserQueryVo.getCreateTimeBegin();
        String createTimeEnd = sysUserQueryVo.getCreateTimeEnd();

        //封装条件
        LambdaQueryWrapper<SysUser> wrapper=new LambdaQueryWrapper<>();

        //判断条件是否为空
        //like 模糊查询
        if (!StringUtils.isEmpty(keyword)){
            wrapper.like(SysUser::getUsername,keyword);
        }
        //ge 大于等于
        if (!StringUtils.isEmpty(createTimeBegin)){
            wrapper.ge(SysUser::getCreateTime,keyword);
        }
        //le 小于等于
        if (!StringUtils.isEmpty(createTimeEnd)){
            wrapper.le(SysUser::getCreateTime,keyword);
        }
        //调用service分页查询
        Page<SysUser> pageModel = service.page(pageParam, wrapper);
        return Result.ok(pageModel);
    }

    @ApiOperation(value = "获取用户")
    @GetMapping("get/{id}")
    public Result get(@PathVariable Long id) {
        SysUser user = service.getById(id);
        return Result.ok(user);
    }
    @PreAuthorize("hasAuthority('bnt.sysUser.add')")
    @ApiOperation(value = "添加用户")
    @PostMapping("save")
    public Result save(@RequestBody SysUser user) {
        //密码加密
        String encrypt = MD5.encrypt(user.getPassword());
        user.setPassword(encrypt);
        service.save(user);
        return Result.ok();
    }

    @PreAuthorize("hasAuthority('bnt.sysUser.update')")
    @ApiOperation(value = "更新用户")
    @PutMapping("update")
    public Result updateById(@RequestBody SysUser user) {
        service.updateById(user);
        return Result.ok();
    }

    @PreAuthorize("hasAuthority('bnt.sysUser.remove')")
    @ApiOperation(value = "删除用户")
    @DeleteMapping("remove/{id}")
    public Result remove(@PathVariable Long id) {
        service.removeById(id);
        return Result.ok();
    }

}

