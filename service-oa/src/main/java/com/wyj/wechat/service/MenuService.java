package com.wyj.wechat.service;

import com.wyj.model.wechat.Menu;
import com.wyj.vo.wechat.MenuVo;
import com.baomidou.mybatisplus.extension.service.IService;
import me.chanjar.weixin.common.error.WxErrorException;

import java.util.List;

/**
 * <p>
 * 菜单 服务类
 * </p>
 *
 * @author atguigu
 * @since 2023-04-12
 */
public interface MenuService extends IService<Menu> {

    List<MenuVo> findMenuInfo();

    void syncMenu();

    void removeMenu() throws WxErrorException;
}
