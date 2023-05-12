package com.wyj.auth;

import com.wyj.model.wechat.Menu;
import com.wyj.vo.wechat.MenuVo;
import com.wyj.wechat.service.MenuService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.List;

@SpringBootTest
public class TestPage {



    @Autowired
            private MenuService menuService;




    @Test
    public void findMenuInfo33() {
        //获取所有子菜单parent_id != 0
        //BeanUtils.copyProperties(twoMenu, twoMenuVo);
        LambdaQueryWrapper<Menu> wrapper=new LambdaQueryWrapper<>();
        wrapper.ne(Menu::getParentId,0);
        List<Menu> menusChildList = menuService.list(wrapper);
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
        List<Menu> menusParentList = menuService.list(wrapper2);
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
    }
    @Test
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


