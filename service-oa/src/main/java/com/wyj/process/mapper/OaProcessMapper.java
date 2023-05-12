package com.wyj.process.mapper;

import com.wyj.model.process.Process;
import com.wyj.vo.process.ProcessQueryVo;
import com.wyj.vo.process.ProcessVo;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;


/**
 * <p>
 * 审批类型 Mapper 接口
 * </p>
 *
 * @author atguigu
 * @since 2023-03-30
 */
@Mapper
public interface OaProcessMapper extends BaseMapper<Process> {

    IPage<ProcessVo> selectPage(Page<ProcessVo> page, @Param("vo") ProcessQueryVo processQueryVo);

}
