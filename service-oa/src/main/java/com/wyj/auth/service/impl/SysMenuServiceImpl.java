package com.wyj.auth.service.impl;

import com.wyj.auth.service.SysRoleMenuService;
import com.wyj.auth.utils.MenuHelper;
import com.wyj.common.config.exception.GuiguException;
import com.wyj.model.system.SysMenu;
import com.wyj.auth.mapper.SysMenuMapper;
import com.wyj.auth.service.SysMenuService;
import com.wyj.model.system.SysRoleMenu;
import com.wyj.vo.system.AssginMenuVo;
import com.wyj.vo.system.MetaVo;
import com.wyj.vo.system.RouterVo;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * <p>
 * 菜单表 服务实现类
 * </p>
 *
 * @author atguigu
 * @since 2023-03-14
 */
@Service
public class SysMenuServiceImpl extends ServiceImpl<SysMenuMapper, SysMenu> implements SysMenuService {

    @Autowired
    SysRoleMenuService sysRoleMenuService;

    @Override
    public List<SysMenu> findNodes() {
        //查询所有菜单数据
        List<SysMenu> sysMenuList = baseMapper.selectList(null);
        //构建树形结构
        List<SysMenu> resultList= MenuHelper.buildTree(sysMenuList);
        return resultList;
    }

    @Override
    public void removeMenuById(Long id) {
        LambdaQueryWrapper<SysMenu> wrapper=new LambdaQueryWrapper<>();
        wrapper.eq(SysMenu::getParentId,id);
        Integer integer = baseMapper.selectCount(wrapper);
        if (integer>0){
            throw new GuiguException(201,"菜单不能删除");
        }
        baseMapper.deleteById(id);

    }


    @Override
    public List<SysMenu> findSysMenuByRoleId(Long roleId) {
        //1 获取所有的菜单列表 条件status=1代表启用的菜单 0停用
        LambdaQueryWrapper<SysMenu> wrapperSysMenu=new LambdaQueryWrapper<>();
        wrapperSysMenu.eq(SysMenu::getStatus,1);
        List<SysMenu> allSysMenuList = baseMapper.selectList(wrapperSysMenu);
        //2 根据角色id获取出对应的角色菜单关系表数据的 菜单id
        LambdaQueryWrapper<SysRoleMenu> wrapperRoleById=new LambdaQueryWrapper<>();
        wrapperRoleById.eq(SysRoleMenu::getRoleId,roleId);
        List<SysRoleMenu> roleIdList = sysRoleMenuService.list(wrapperRoleById);
        //3根据角色菜单关系表 的菜单id 获取出对应菜单对象的ID

        List<Long> iDList=new ArrayList<>();
       // List<SysMenu> list=new ArrayList<>();
        for (SysRoleMenu sysRoleMenu:roleIdList) {
            LambdaQueryWrapper<SysMenu> wrapperSysRoleMenuById = new LambdaQueryWrapper<>();
            wrapperSysRoleMenuById.eq(SysMenu::getId,sysRoleMenu.getMenuId());
            List<SysMenu> list = baseMapper.selectList(wrapperSysRoleMenuById);
            for (SysMenu sysMenu:list){
                iDList.add(sysMenu.getId()) ;
            }
        }

        //3.1遍历菜单对象获取菜单Id集合
//        List<Long> iDList=new ArrayList<>();
//        for (SysMenu sysMenu:list){
//            iDList.add(sysMenu.getId());
//        }
        //3.2 根据角色菜单关系表 的菜单id 跟 所有菜单列表的id进行比较
        // 获取出相同的数据封装(修改菜单对象SysMenu的isSelect字段值 trun)
        for (SysMenu sysMenu:allSysMenuList){
            if (iDList.contains(sysMenu.getId())){
                sysMenu.setSelect(true);
            }else
                sysMenu.setSelect(false);
        }
        //4返回固定格式的树形结构
        return  MenuHelper.buildTree(allSysMenuList);
    }

    @Override
    public void doAssign(AssginMenuVo assignMenuVo) {
        //根据角色id,删除菜单角色表数据,分配数据
        LambdaQueryWrapper<SysRoleMenu> wrapper=new LambdaQueryWrapper<>();
        wrapper.eq(SysRoleMenu::getRoleId,assignMenuVo.getRoleId());
        sysRoleMenuService.remove(wrapper);
        //从参数理取出已分配的菜单id 进行遍历插入到菜单角色表
        for (Long menuId:assignMenuVo.getMenuIdList()){
            SysRoleMenu sysRoleMenu=new SysRoleMenu();
            sysRoleMenu.setMenuId(menuId);
            sysRoleMenu.setRoleId(assignMenuVo.getRoleId());
            sysRoleMenuService.save(sysRoleMenu);
        }
    }

    //4 根据用户ID获取用户可以操作的菜单
    //查询数据库动态构建路由,进行显示
    @Override
    public List<RouterVo> findUserMenuListById(Long userId) {
        //1判断用户是否是管理员 ID 1 代表管理员
        //如果是管理员 查询所有菜单
        List<SysMenu> sysMeunList=null;
        if (userId.longValue()==1){
            LambdaQueryWrapper<SysMenu> wrapper=new LambdaQueryWrapper();
            wrapper.eq(SysMenu::getStatus,1);
            wrapper.orderByAsc(SysMenu::getSortValue);
             sysMeunList = baseMapper.selectList(wrapper);
        }else {
            //1.1不是管理员根据用户ID查询可以操作的菜单列表
            //多表关联查询
            sysMeunList=baseMapper.findMenuListByUserId(userId);
        }

        //2 把查询出的数据构成框架路由的结构
        //使用菜单的工具类构建树形结构
        List<SysMenu> sYSMenuTreeList = MenuHelper.buildTree(sysMeunList);
        //构建成框架要求的路由结构
        List<RouterVo> routerList=this.buildMenus(sYSMenuTreeList);

        return routerList;
    }

    /**
     * 路由结构
     * @param menus
     * @return
     */
    private List<RouterVo> buildMenus(List<SysMenu> menus) {
        //创建List集合,存储最终数据
        List<RouterVo> routers = new ArrayList<>();
        //menus 遍历
        for (SysMenu menu : menus) {
            RouterVo router = new RouterVo();
            router.setHidden(false);
            router.setAlwaysShow(false);
            router.setPath(getRouterPath(menu));
            router.setComponent(menu.getComponent());
            router.setMeta(new MetaVo(menu.getName(), menu.getIcon()));
            //下一层数据
            List<SysMenu> children = menu.getChildren();
            //如果当前是菜单，需将按钮对应的路由加载出来，如：“角色授权”按钮对应的路由在“系统管理”下面
            //  数字1 代表菜单下有隐藏路由 并且表示 子Children里面的Children为空
            if(menu.getType().intValue() == 1) {
                //加载出来下面隐藏路由
//                List<SysMenu> hiddenMenuList =
//                        children.stream()
//                                .filter(item -> !StringUtils
//                                        .isEmpty(item.getComponent()))
//                                .collect(Collectors.toList());
                //Component有值代表有路由
                List<SysMenu> hiddenMenuList =new ArrayList<>();
                for (SysMenu sysMenu : children){
                    if (!StringUtils.isEmpty(sysMenu.getComponent())){
                        hiddenMenuList.add(sysMenu);
                    }
                }

                for (SysMenu hiddenMenu : hiddenMenuList) {
                    RouterVo hiddenRouter = new RouterVo();
                    //隐藏路由
                    hiddenRouter.setHidden(true);
                    hiddenRouter.setAlwaysShow(false);
                    hiddenRouter.setPath(getRouterPath(hiddenMenu));
                    hiddenRouter.setComponent(hiddenMenu.getComponent());
                    hiddenRouter.setMeta(new MetaVo(hiddenMenu.getName(), hiddenMenu.getIcon()));
                    routers.add(hiddenRouter);
                }
            } else {
                if (!CollectionUtils.isEmpty(children)) {
                    if(children.size() > 0) {
                        router.setAlwaysShow(true);
                    }
                    //递归
                    router.setChildren(buildMenus(children));
                }
            }
            routers.add(router);
        }
        //****************************************************************************
//        for (SysMenu menu : menus){
//            //保存当前循环的menu对象
//            RouterVo router = new RouterVo();
//            //getComponent不为空代表有路由
//            if (!StringUtils.isEmpty(menu.getComponent())){
//                router.setHidden(false);
//                router.setAlwaysShow(false);
//                router.setPath(getRouterPath(menu));
//                router.setComponent(menu.getComponent());
//                router.setMeta(new MetaVo(menu.getName(), menu.getIcon()));
//                router.setChildren(buildMenus(menu.getChildren()));
//                routers.add(router);
//            }
//            if (!CollectionUtils.isEmpty(menu.getChildren())){
//
//            }
//
//        }

        return routers;
    }

    //5 根据用户ID 获取用户可以操作的按钮列表
    @Override
    public List<String> findUserPermsByUserId(Long userId) {

        //超级管理员admin账号id为：1
        List<SysMenu> sysMenuList = null;
        if (userId.longValue() == 1) {
            //判断是否是管理员 如果是管理员查询所有按钮
            sysMenuList = this.list(new LambdaQueryWrapper<SysMenu>().eq(SysMenu::getStatus, 1));
        } else {
            //2如果不是管理员根据userId查询可以操作的按钮列表
            //多表联查 用户表 用户角色关系表 角色菜单关系表
            sysMenuList = baseMapper.findMenuListByUserId(userId);
        }
        //3从查询出的数据,获取可以操作按钮的值 List集合,返回
        List<String> permsList = sysMenuList.stream().filter(item -> item.getType() == 2).map(item -> item.getPerms()).collect(Collectors.toList());
        return permsList;
    }


    /**
     * 获取路由地址
     *
     * @param menu 菜单信息
     * @return 路由地址
     */
    public String getRouterPath(SysMenu menu) {
        String routerPath = "/" + menu.getPath();
        if(menu.getParentId().intValue() != 0) {
            routerPath = menu.getPath();
        }
        return routerPath;
    }
}
