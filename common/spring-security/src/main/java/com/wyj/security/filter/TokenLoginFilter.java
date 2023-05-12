package com.wyj.security.filter;

import com.alibaba.fastjson2.JSON;
import com.wyj.common.jwt.JwtHelper;
import com.wyj.common.result.ResponseUtil;
import com.wyj.common.result.Result;
import com.wyj.common.result.ResultCodeEnum;
import com.wyj.security.custom.CustomUser;
import com.wyj.vo.system.LoginVo;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * <p>
 * 登录过滤器，继承UsernamePasswordAuthenticationFilter，对用户名密码进行登录校验
 * </p>
 */
public class TokenLoginFilter extends UsernamePasswordAuthenticationFilter {

    private RedisTemplate redisTemplate;

    //构造方法
    public TokenLoginFilter(AuthenticationManager authenticationManager, RedisTemplate redisTemplate){
        this.setAuthenticationManager(authenticationManager);
        this.setPostOnly(false);

        //指定登录接口及提交方式，可以指定任意路径
        this.setRequiresAuthenticationRequestMatcher(new AntPathRequestMatcher("/admin/system/index/login","POST"));
        this.redisTemplate = redisTemplate;
    }
    //登录认证
    //获取输入的用户名和密码完成认证
    public Authentication attemptAuthentication(HttpServletRequest request,
                                                HttpServletResponse response)
            throws AuthenticationException {
        try {
            //从流中获取用户信息 Login
            LoginVo loginVo = new ObjectMapper().readValue(request.getInputStream(), LoginVo.class);
            Authentication authentication = new UsernamePasswordAuthenticationToken(loginVo.getUsername(), loginVo.getPassword());
           //调用方法认证
            return this.getAuthenticationManager().authenticate(authentication);
        } catch (IOException e) {
           throw new RuntimeException(e);
        }

    }
    //认证成功执行方法
    protected void successfulAuthentication(HttpServletRequest request,
                                            HttpServletResponse response,
                                            FilterChain chain,
                                            Authentication authResult)
            throws IOException, ServletException {

        //获取当前用户

        CustomUser customUser =(CustomUser) authResult.getPrincipal();
        //生产Token
        String token = JwtHelper.createToken(customUser.getSysUser().getId(),
                customUser.getSysUser().getUsername());

        //登录成功我们将权限数据保单到reids
        redisTemplate.opsForValue().set(customUser.getUsername(),
                JSON.toJSONString(customUser.getAuthorities()));
        //返回
        Map<String,Object> map=new HashMap<>();
        map.put("token",token);
        ResponseUtil.out(response, Result.ok(map));
    }

        //认证失败方法
        protected void unsuccessfulAuthentication(HttpServletRequest request,
                                                  HttpServletResponse response,
                                                  AuthenticationException failed)
                throws IOException, ServletException {
        ResponseUtil.out(response,Result.build(null,ResultCodeEnum.LOGIN_ERROR));
        }
}
