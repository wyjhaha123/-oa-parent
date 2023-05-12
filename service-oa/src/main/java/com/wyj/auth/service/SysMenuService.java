package com.wyj.auth.service;

import com.wyj.model.system.SysMenu;
import com.wyj.vo.system.AssginMenuVo;
import com.wyj.vo.system.RouterVo;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * <p>
 * 菜单表 服务类
 * </p>
 *
 * @author atguigu
 * @since 2023-03-14
 */
public interface SysMenuService extends IService<SysMenu> {

    List<SysMenu> findNodes();

    void removeMenuById(Long id);

    List<SysMenu> findSysMenuByRoleId(Long roleId);

    void doAssign(AssginMenuVo assignMenuVo);

    List<RouterVo> findUserMenuListById(Long userId);

    List<String> findUserPermsByUserId(Long userId);
}
