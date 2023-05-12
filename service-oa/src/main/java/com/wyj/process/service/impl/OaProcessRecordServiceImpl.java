package com.wyj.process.service.impl;

import com.wyj.auth.service.SysUserService;
import com.wyj.model.process.ProcessRecord;
import com.wyj.model.system.SysUser;
import com.wyj.process.mapper.OaProcessRecordMapper;
import com.wyj.process.service.OaProcessRecordService;
import com.wyj.security.custom.LoginUserInfoHelper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 审批记录 服务实现类
 * </p>
 *
 * @author atguigu
 * @since 2023-04-01
 */
@Service
public class OaProcessRecordServiceImpl extends ServiceImpl<OaProcessRecordMapper, ProcessRecord> implements OaProcessRecordService {

    @Autowired
    private OaProcessRecordMapper processRecordMapper;

    @Autowired
    private SysUserService sysUserService;
    @Override
    public void record(Long processId, Integer status, String description) {
        SysUser sysUser = sysUserService.getById(LoginUserInfoHelper.getUserId());
//        SysUser sysUser = sysUserService.getById(1L);
        ProcessRecord processRecord = new ProcessRecord();
        processRecord.setProcessId(processId);
        processRecord.setStatus(status);
        processRecord.setDescription(description);
        processRecord.setOperateUserId(sysUser.getId());
        processRecord.setOperateUser(sysUser.getName());
        processRecordMapper.insert(processRecord);
    }
}
