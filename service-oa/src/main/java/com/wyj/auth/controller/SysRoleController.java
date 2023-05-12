package com.wyj.auth.controller;


import com.wyj.auth.service.SysRoleService;
import com.wyj.common.result.Result;
import com.wyj.model.system.SysRole;
import com.wyj.vo.system.AssginRoleVo;
import com.wyj.vo.system.SysRoleQueryVo;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.support.SFunction;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Api(tags="角色管理接口")
@RestController
@RequestMapping("/admin/system/sysRole")
public class SysRoleController {

    @Autowired
    SysRoleService sysRoleService;

    //1, 点击分配 进入角色分配页面,获取用户Id,根据用户id 查询用户关系角色表,
    // 在用户关系角色表查询出对应的角色id,
    //根据角色id查询角色表对应的 角色数据
    //通过@PreAuthorize标签控制controller层接口权限
    @ApiOperation(value = "根据用户获取角色数据")
    @GetMapping("/toAssign/{userId}")
    public Result toAssign(@PathVariable Long userId){
        Map<String, Object> roleMap = sysRoleService.findRoleByAdminId(userId);

        return Result.ok(roleMap);
    }

    //2、保存分配角色：删除之前分配的角色和保存现在分配的角色

    @ApiOperation(value = "根据用户分配角色")
    @PostMapping("/doAssign")
    public Result doAssign(@RequestBody AssginRoleVo assginRoleVo) {
        sysRoleService.doAssign(assginRoleVo);
        return Result.ok();
    }

    //查询所以角色
//    @GetMapping("findAll")
//    public List<SysRole> findAll(){
//        List<SysRole> list = sysRoleService.list();
//        return list;
//    }

    @ApiOperation("查询所有角色")
    @GetMapping("findAll")
    public Result findAll(){
        List<SysRole> list = sysRoleService.list();
//        //模拟异常
//        try{
//            //手动抛出异常
//            int i=10/0;
//        }catch (Exception e){
//           throw  new GuiguException(2001,"执行自定义异常..");
//        }

        return Result.ok(list);
    }

    //条件分页查询
    @PreAuthorize("hasAuthority('bnt.sysRole.list')")//判断用户权限里是否有"bnt.sysRole.list"这个值 有才能访问这个方法
    @ApiOperation("条件分页查询")
    @GetMapping("{page}/{limit}")
    public Result pageQueryRole(@PathVariable Long page,
                                @PathVariable Long limit,
                                SysRoleQueryVo sysRoleQueryVo){
        //调用service的方法实现
        //1 创建Page对象，传递分页相关参数
        //page 当前页  limit 每页显示记录数
        Page<SysRole> pageParam = new Page<>(page,limit);

        //2 封装条件，判断条件是否为空，不为空进行封装
        LambdaQueryWrapper<SysRole> wrapper = new LambdaQueryWrapper<>();
        String roleName = sysRoleQueryVo.getRoleName();
        if(!StringUtils.isEmpty(roleName)) {
            //封装 like模糊查询
            SFunction<SysRole, String> getRoleName = SysRole::getRoleName;

            wrapper.like(getRoleName,roleName);





        }

        //3 调用方法实现
        IPage<SysRole> pageModel = sysRoleService.page(pageParam, wrapper);

        return Result.ok(pageModel);
    }

    //添加角色
    @PreAuthorize("hasAuthority('bnt.sysRole.add')")
    @ApiOperation("添加角色")
    @PostMapping("save")
    public Result save(@RequestBody SysRole sysRole){

        boolean is_succese = sysRoleService.save(sysRole);
        if (is_succese){
            return Result.ok(is_succese);
        }else
            return Result.fail();
    }

    //根据id查询
    @PreAuthorize("hasAuthority('bnt.sysRole.list')")
    @ApiOperation("根据Id查询")
    @GetMapping("get/{id}")
    public Result get(@PathVariable Long id){
        SysRole byId = sysRoleService.getById(id);
        return Result.ok(byId);
    }

    //修改角色-最终修改
    @PreAuthorize("hasAuthority('bnt.sysRole.update')")
    @ApiOperation("修改角色")
    @PutMapping ("update")
    public Result update(@RequestBody SysRole sysRole){

        boolean is_succese = sysRoleService.updateById(sysRole);
        if (is_succese){
            return Result.ok();
        }else
            return Result.fail();
    }
    //根据id删除
    @PreAuthorize("hasAuthority('bnt.sysRole.remove')")
    @ApiOperation("根据ID删除")
    @DeleteMapping("remove/{id}")
    public Result remove(@PathVariable Long id){
        boolean is_succese = sysRoleService.removeById(id);
        if (is_succese){
            return Result.ok();
        }else
            return Result.fail();
    }

    //批量删除
    //前端传数组 自动封装 为java List集合
    @PreAuthorize("hasAuthority('bnt.sysRole.remove')")
    @ApiOperation("批量删除")
    @DeleteMapping("batchRemove")
    public Result bachRemove(@RequestBody List<Long> id){
        boolean is_succese = sysRoleService.removeByIds(id);
        if (is_succese){
            return Result.ok();
        }else
            return Result.fail();

    }
}

