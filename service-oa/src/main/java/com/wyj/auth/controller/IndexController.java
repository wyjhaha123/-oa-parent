package com.wyj.auth.controller;

import com.wyj.auth.service.SysMenuService;
import com.wyj.auth.service.SysUserService;
import com.wyj.common.config.exception.GuiguException;
import com.wyj.common.jwt.JwtHelper;
import com.wyj.common.result.Result;
import com.wyj.common.utils.MD5;
import com.wyj.model.system.SysUser;
import com.wyj.vo.system.LoginVo;
import com.wyj.vo.system.RouterVo;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Api(tags = "后台登录管理")
@RestController
@RequestMapping("/admin/system/index")
public class IndexController {

    @Autowired
    private SysMenuService sysMenuService;
    @Autowired
    private SysUserService sysUserService;

    /**
     * 登录
     * @return
     */
    @PostMapping("login")
    public Result login(@RequestBody LoginVo loginVo){
//        Map<String,Object> map=new HashMap<>();
//        map.put("token","admin");
//        return Result.ok(map);
        //1 获取用户输入用户名和密码
        String username = loginVo.getUsername();
        String password = loginVo.getPassword();
        //2根据用户名查询数据库
        LambdaQueryWrapper<SysUser> wrapper=new LambdaQueryWrapper<>();
        wrapper.eq(SysUser::getUsername, loginVo.getUsername());
        SysUser sysUser = sysUserService.getOne(wrapper);
        //3用户信息是否存在
        if (sysUser ==null){
            throw new GuiguException(201,"用户不存在");
        }
        //4判断密码
        String password_db = sysUser.getPassword();
        String password_input = MD5.encrypt(loginVo.getPassword());

        if (password_db.equals(password_input)){
            throw new GuiguException(201,"密码错误");
        }
        //5判断用户是否被禁用
        if (sysUser.getStatus().intValue()==0){
            throw new GuiguException(201,"用户被禁用");
        }
        //6使用jwt根据用户id和用户名称生产token字符串
        String token =
                JwtHelper.createToken(sysUser.getId(), sysUser.getUsername());
        //7返回
        Map<String,Object> map=new HashMap<>();
        map.put("token",token);
        return  Result.ok(map);
    }
    /**
     * 获取用户信息
     */
    @GetMapping("info")
    public Result info(HttpServletRequest request) {

        //1从请求头获取用户信息 token字符串
        String token = request.getHeader("token");
        //2从token获取id 或者用户名
        Long userId = JwtHelper.getUserId(token);
        //3根据用户id 查询出用户信息
        SysUser sysUser = sysUserService.getById(userId);
        //4 根据用户ID获取用户可以操作的菜单
        //查询数据库动态构建路由,进行显示
        List<RouterVo> routerList=sysMenuService.findUserMenuListById(userId);
        //5 根据用户ID 获取用户可以操作的按钮列表
        List<String> permsList=sysMenuService.findUserPermsByUserId(userId);
        //6 返回相应的数据
        Map<String, Object> map = new HashMap<>();
        map.put("roles","[admin]");
        map.put("name",sysUser.getName());
        map.put("avatar","https://oss.aliyuncs.com/aliyun_id_photo_bucket/default_handsome.jpg");
        // 返回用户可以操作的菜单
        map.put("routers",routerList);
        // 返回用户可以操作的按钮
        map.put("buttons",permsList);
        return Result.ok(map);
    }

    /**
     * 退出
     * @return
     */
    @PostMapping("logout")
    public Result logout(){
        return Result.ok();
    }
}
