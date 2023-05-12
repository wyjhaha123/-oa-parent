package com.wyj.auth.service.impl;

import com.wyj.auth.service.SysMenuService;
import com.wyj.auth.service.SysUserService;
import com.wyj.model.system.SysUser;
import com.wyj.security.custom.CustomUser;
import com.wyj.security.custom.UserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;


@Service
//当前登录用户信息
public class UserDetailsServiceImpl implements UserDetailsService{

    @Autowired
    private SysMenuService sysMenuService;
    @Autowired
    private SysUserService sysUserService;
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        SysUser sysUser= sysUserService.getUserByUserName(username);
        if(null == sysUser) {
            throw new UsernameNotFoundException("用户名不存在！");
        }


        if(sysUser.getStatus().intValue() == 0) {
            throw new RuntimeException("账号已停用");
        }
        //根据用户id 查询用户可操作的权限按钮
        List<String> userPermsList = sysMenuService.findUserPermsByUserId(sysUser.getId());
        List<SimpleGrantedAuthority> authorities=new ArrayList<>();
        for (String perm:userPermsList){
            //消除空格
            String trim = perm.trim();
            authorities.add( new SimpleGrantedAuthority(trim));
        }
        return new CustomUser(sysUser, authorities);
    }
}
