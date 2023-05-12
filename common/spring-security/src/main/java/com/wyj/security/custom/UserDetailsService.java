package com.wyj.security.custom;


import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

@Component
//自定义验证用户登录的方式
public interface UserDetailsService extends org.springframework.security.core.userdetails.UserDetailsService {
    @Override
    UserDetails loadUserByUsername(String username) throws UsernameNotFoundException;
    /**
     * 根据用户名获取用户对象（获取不到直接抛异常）
     */
//    @Override
//    UserDetails loadUserByUsername(String username) throws UsernameNotFoundException;
}
