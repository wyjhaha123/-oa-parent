package com.wyj.auth.service.impl;

import com.wyj.auth.mapper.SysRoleMapper;
import com.wyj.auth.mapper.SysUserRoleMapper;
import com.wyj.auth.service.SysRoleService;
import com.wyj.auth.service.SysUserRoleService;
import com.wyj.model.system.SysRole;
import com.wyj.model.system.SysUserRole;
import com.wyj.vo.system.AssginRoleVo;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class SysRoleServiceImpl extends ServiceImpl<SysRoleMapper, SysRole> implements SysRoleService {

    @Autowired
    SysUserRoleService sysUserRoleService;

    @Autowired
    private SysUserRoleMapper sysUserRoleMapper;
    @Override
    public Map<String, Object> findRoleByAdminId(Long userId) {
        //查询所有的角色
        List<SysRole> allRolesList = baseMapper.selectList(null);


        //从用户角色关系表根据用户id查询出对应的角色id 封装
//        List<SysUserRole> existUserRoleList =
//                sysUserRoleMapper.selectList(new LambdaQueryWrapper<SysUserRole>().eq(SysUserRole::getUserId, userId).select(SysUserRole::getRoleId));
//        List<Long> existRoleIdList = existUserRoleList.stream().map(c->c.getRoleId()).collect(Collectors.toList());


        LambdaQueryWrapper<SysUserRole> wrapper=new LambdaQueryWrapper<>();
         wrapper.eq(SysUserRole::getUserId, userId);
        List<SysUserRole> existUserRoleList = sysUserRoleMapper.selectList(wrapper);

        List<Long> existRoleIdList=new ArrayList<>();
        for (SysUserRole sysUserRole:existUserRoleList){
            Long roleId = sysUserRole.getRoleId();
            existRoleIdList.add(roleId);
        }
        //从所有的角色数据中allRolesList根据list的角色id获取出对应角色信息
        //根据角色id 进行比较
        List<SysRole> assignRoleList= new ArrayList<>();
        for (SysRole sysRole:allRolesList){
            if (existRoleIdList.contains(sysRole.getId())){
                assignRoleList.add(sysRole);
            }
        }


        Map<String, Object> roleMap = new HashMap<>();
        roleMap.put("assginRoleList", assignRoleList);
        roleMap.put("allRolesList", allRolesList);
        Object o = roleMap.get(assignRoleList);
        Object o2 = roleMap.get(allRolesList);


        return roleMap;

    }

    //重新给用户分配角色
    @Override
    public void doAssign(AssginRoleVo assginRoleVo) {
        //删除用户角色信息

        LambdaQueryWrapper<SysUserRole> wrapper=new LambdaQueryWrapper<>();
        wrapper.eq(SysUserRole::getUserId,assginRoleVo.getUserId());
        sysUserRoleMapper.delete(wrapper);
        //assginRoleVo 根据请求体中的list集合循环 遍历出角色id 进行循环添加
        for (Long roleId:assginRoleVo.getRoleIdList()){
            SysUserRole sysUserRole=new SysUserRole();
            sysUserRole.setUserId(assginRoleVo.getUserId());
            sysUserRole.setRoleId(roleId);
            sysUserRoleMapper.insert(sysUserRole);
        }

    }
}
