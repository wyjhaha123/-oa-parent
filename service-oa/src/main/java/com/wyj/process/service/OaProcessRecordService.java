package com.wyj.process.service;

import com.wyj.model.process.ProcessRecord;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 * 审批记录 服务类
 * </p>
 *
 * @author atguigu
 * @since 2023-04-01
 */
public interface OaProcessRecordService extends IService<ProcessRecord> {

    void record(Long processId, Integer status, String description);

}
