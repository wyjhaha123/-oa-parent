package com.wyj.auth.service.impl;


import com.wyj.auth.mapper.SysUserMapper;
import com.wyj.auth.service.SysUserService;
import com.wyj.model.system.SysUser;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 用户表 服务实现类
 * </p>
 *
 * @author atguigu
 * @since 2023-03-12
 */
@Service
public class SysUserServiceImpl extends ServiceImpl<SysUserMapper, SysUser> implements SysUserService {

    @Override
    public void updateStatus(Long id, Integer status) {
        SysUser sysUser = baseMapper.selectById(id);
        sysUser.setStatus(status.intValue());
         baseMapper.updateById(sysUser);
    }

    //根据用户名进行查询
    @Override
    public SysUser getUserByUserName(String username) {
        LambdaQueryWrapper<SysUser> wrapper=new LambdaQueryWrapper<>();
        wrapper.eq(SysUser::getUsername,username);
        SysUser sysUser = baseMapper.selectOne(wrapper);
        return sysUser;
    }
}
