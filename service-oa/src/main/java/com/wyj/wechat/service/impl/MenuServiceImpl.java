package com.wyj.wechat.service.impl;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.wyj.model.wechat.Menu;
import com.wyj.vo.wechat.MenuVo;
import com.wyj.wechat.mapper.MenuMapper;
import com.wyj.wechat.service.MenuService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import me.chanjar.weixin.common.error.WxErrorException;
import me.chanjar.weixin.mp.api.WxMpService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 * 菜单 服务实现类
 * </p>
 *
 * @author atguigu
 * @since 2023-04-12
 */
@Service
public class MenuServiceImpl extends ServiceImpl<MenuMapper, Menu> implements MenuService {
    @Autowired
    private WxMpService wxMpService;

    @Override
    public List<MenuVo> findMenuInfo() {

        //获取所有子菜单parent_id != 0
        //BeanUtils.copyProperties(twoMenu, twoMenuVo);
        LambdaQueryWrapper<Menu> wrapper=new LambdaQueryWrapper<>();
        wrapper.ne(Menu::getParentId,0);
        List<Menu> menusChildList = baseMapper.selectList(wrapper);
        List<MenuVo> menusVoChildList=new ArrayList<>();
        for (Menu menu:menusChildList){
            MenuVo menuVo=new MenuVo();
            BeanUtils.copyProperties(menu,menuVo);
            menusVoChildList.add(menuVo);
        }
        BeanUtils.copyProperties(menusChildList,menusVoChildList);

        //2获取所有的最表层父菜单 parent_id=0
        LambdaQueryWrapper<Menu> wrapper2=new LambdaQueryWrapper<>();
        wrapper2.eq(Menu::getParentId,0);
        List<Menu> menusParentList = baseMapper.selectList(wrapper2);
        List<MenuVo> menusVoParentList=new ArrayList<>();

        for (Menu menu:menusParentList){
            MenuVo menuVo=new MenuVo();
            BeanUtils.copyProperties(menu,menuVo);
            menusVoParentList.add(menuVo);
        }




        //循环父菜单 每次循环结束数据添加到集合中
        List<MenuVo> menuVoListParent=new ArrayList<>();
        for (MenuVo menuVoParent:menusVoParentList){
            //循环子菜单 每次循环结束数据添加到集合中
            List<MenuVo> menuVoListChildTow=new ArrayList<>();
            for (MenuVo menuVoChild :menusVoChildList){


                //父菜单 id 对比子菜单的Parent_id
                if (menuVoParent.getId()==menuVoChild.getParentId()){
                    //调用递归函数
                    List<MenuVo> builtTree = this.getBuiltTree(menuVoChild, menusVoChildList);
                    // menuVoParent.setChildren(builtTree);
                    for (MenuVo menuVo:builtTree){
                        menuVoListChildTow.add(menuVo);
                    }
                    menuVoParent.setChildren(menuVoListChildTow);
                }

            }
            menuVoListParent.add(menuVoParent);
        }
        System.out.println(menuVoListParent);
        return menuVoListParent;
    }

    @Override
    public void syncMenu() {

        List<MenuVo> menuVoList = this.findMenuInfo();
        //菜单
        JSONArray buttonList = new JSONArray();
        for(MenuVo oneMenuVo : menuVoList) {
            JSONObject one = new JSONObject();
            one.put("name", oneMenuVo.getName());
            if(CollectionUtils.isEmpty(oneMenuVo.getChildren())) {
                one.put("type", oneMenuVo.getType());
                one.put("url", "http://wyj2.vipgz4.91tunnel.com/#"+oneMenuVo.getUrl());
            } else {
                JSONArray subButton = new JSONArray();
                for(MenuVo twoMenuVo : oneMenuVo.getChildren()) {
                    JSONObject view = new JSONObject();
                    view.put("type", twoMenuVo.getType());
                    if(twoMenuVo.getType().equals("view")) {
                        view.put("name", twoMenuVo.getName());
                        //H5页面地址
                        view.put("url", "http://wyj2.vipgz4.91tunnel.com#"+twoMenuVo.getUrl());
                    } else {
                        view.put("name", twoMenuVo.getName());
                        view.put("key", twoMenuVo.getMeunKey());
                    }
                    subButton.add(view);
                }
                one.put("sub_button", subButton);
            }
            buttonList.add(one);
        }
        //菜单
        JSONObject button = new JSONObject();
        button.put("button", buttonList);
        try {
            wxMpService.getMenuService().menuCreate(button.toJSONString());
        } catch (WxErrorException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void removeMenu() throws WxErrorException {
        wxMpService.getMenuService().menuDelete();
    }

    private List<MenuVo> getBuiltTree(MenuVo menuVoChild, List<MenuVo> menusVoChildList) {
        List<MenuVo> menuVoListChild=new ArrayList<>();
        for (MenuVo menuVoChildTeer:menusVoChildList){
            if (menuVoChild.getId()==menuVoChildTeer.getParentId()){
                List<MenuVo> builtTree = this.getBuiltTree(menuVoChildTeer, menusVoChildList);
                menuVoChild.setChildren(builtTree);
            }
        }
        menuVoListChild.add(menuVoChild);
        return menuVoListChild;
    }


}
