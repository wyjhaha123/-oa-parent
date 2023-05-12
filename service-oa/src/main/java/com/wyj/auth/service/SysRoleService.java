package com.wyj.auth.service;

import com.wyj.model.system.SysRole;
import com.wyj.vo.system.AssginRoleVo;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.Map;

public interface SysRoleService extends IService<SysRole> {
    Map<String, Object> findRoleByAdminId(Long userId);

    void doAssign(AssginRoleVo assginRoleVo);

}
