package com.wyj.security.filter;

import com.alibaba.fastjson2.JSON;
import com.wyj.common.jwt.JwtHelper;
import com.wyj.common.result.ResponseUtil;
import com.wyj.common.result.Result;
import com.wyj.common.result.ResultCodeEnum;
import com.wyj.security.custom.LoginUserInfoHelper;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class TokenAuthenticationFilter extends OncePerRequestFilter {

    private RedisTemplate redisTemplate;

    public TokenAuthenticationFilter(RedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }
    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain chain) throws ServletException, IOException {
        logger.info("uri:"+request.getRequestURI());
        //如果是登录接口，直接放行
        if("/admin/system/index/login".equals(request.getRequestURI())) {
            chain.doFilter(request, response);
            return;
        }
        System.out.println(request+"++++++++++++++++++");
        System.out.println(request.getRequestURI()+"++++++++++++++++++");
        System.out.println( getAuthentication(request)+"++++++++++++++++++");
        System.out.println( request.getHeader("token")+"++++++++++++++++++");
        UsernamePasswordAuthenticationToken authentication = getAuthentication(request);
        if(null != authentication) {
            SecurityContextHolder.getContext().setAuthentication(authentication);
            chain.doFilter(request, response);
        } else {
            ResponseUtil.out(response, Result.build(null, ResultCodeEnum.PERMISSION));
        }
    }

    private UsernamePasswordAuthenticationToken getAuthentication(HttpServletRequest request) {
        // token置于header里 判断是否有token
        String token = request.getHeader("token");

        if (!StringUtils.isEmpty(token)) {
            String username = JwtHelper.getUsername(token);

            if (!StringUtils.isEmpty(username)) {
                //通过ThreadLocal记录当前登录人信息
                LoginUserInfoHelper.setUserId(JwtHelper.getUserId(token));
                LoginUserInfoHelper.setUsername(username);
            }
            if (!StringUtils.isEmpty(username)) {
                //从redis获取用户可以操作的权限
                String authoritiesString = (String) redisTemplate.opsForValue().get(username);

                List<Map> mapList = JSON.parseArray(authoritiesString, Map.class);
                List<SimpleGrantedAuthority> authorities = new ArrayList<>();
                for (Map map : mapList) {


                    authorities.add(new SimpleGrantedAuthority((String)map.get("authority")));
                }
                return new UsernamePasswordAuthenticationToken(username, null,authorities);
            }
            else {
                return new UsernamePasswordAuthenticationToken(username, null, new ArrayList<>());
            }
        }

        return null;
    }
}
