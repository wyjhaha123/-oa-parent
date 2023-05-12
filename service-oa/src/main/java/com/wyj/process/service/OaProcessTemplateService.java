package com.wyj.process.service;

import com.wyj.model.process.ProcessTemplate;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 * 审批模板 服务类
 * </p>
 *
 * @author atguigu
 * @since 2023-03-29
 */
public interface OaProcessTemplateService extends IService<ProcessTemplate> {

    IPage<ProcessTemplate> selectPage(Page<ProcessTemplate> pageParam);

    IPage<ProcessTemplate> selectTest(Page<ProcessTemplate> page);

    void publish(Long id);
}
