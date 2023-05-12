package com.wyj.process.service;

import com.wyj.model.process.Process;
import com.wyj.model.process.ProcessTemplate;
import com.wyj.vo.process.ApprovalVo;
import com.wyj.vo.process.ProcessFormVo;
import com.wyj.vo.process.ProcessQueryVo;
import com.wyj.vo.process.ProcessVo;


import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 * 审批类型 服务类
 * </p>
 *
 * @author atguigu
 * @since 2023-03-30
 */
public interface OaProcessService extends IService<Process> {

    IPage<ProcessVo> selectPage(Page<ProcessVo> pageParam, ProcessQueryVo processQueryVo);

    void deployByZip(ProcessTemplate processTemplate);


    void startUp(ProcessFormVo processFormVo);


    Page<ProcessVo> findPending(Page<Process> pageParam);

    Object show(Long id);

    void approve(ApprovalVo approvalVo);

    IPage<ProcessVo> findProcessed(Page<Process> pageParam);

    Object findStarted(Page<ProcessVo> pageParam);
}
