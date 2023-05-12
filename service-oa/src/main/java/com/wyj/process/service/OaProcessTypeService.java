package com.wyj.process.service;

import com.wyj.model.process.ProcessType;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * <p>
 * 审批类型 服务类
 * </p>
 *
 * @author atguigu
 * @since 2023-03-29
 */

public interface OaProcessTypeService extends IService<ProcessType> {

    List<ProcessType> findProcessType();
}
