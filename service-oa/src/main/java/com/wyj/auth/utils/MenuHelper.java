package com.wyj.auth.utils;

import com.wyj.model.system.SysMenu;

import java.util.ArrayList;
import java.util.List;

public class MenuHelper {

    /**
     * 使用递归方法建菜单
     * @param sysMenuList
     * @return
     */
    public static List<SysMenu> buildTree(List<SysMenu> sysMenuList) {
        //保存树形结构
        List<SysMenu> trees=new ArrayList<>();
        for (SysMenu sysMenu:sysMenuList){
            //找到递归开始的路口parent_id==0

//            id   parent_id
//             2       0
//             3       2
//             6       3
            if (sysMenu.getParentId().longValue()==0){
                trees.add(findChildren(sysMenu,sysMenuList));
            }
        }
        return trees;
    }

    /**
     * 递归查找子节点
     * @param treeNodes
     * @return
     */
    //            id   parent_id
    //             2       0
    //             3       2
    //             6       3
    private static SysMenu findChildren(SysMenu sysMenu, List<SysMenu> treeNodes) {
        sysMenu.setChildren(new ArrayList<SysMenu>());
        for (SysMenu t1:treeNodes){
            if (t1.getParentId().longValue()==sysMenu.getId().longValue()){
                if (sysMenu.getChildren() == null) {
                    sysMenu.setChildren(new ArrayList<>());
                }
                sysMenu.getChildren().add(findChildren(t1,treeNodes));
            }
        }
        return sysMenu;
    }
}
