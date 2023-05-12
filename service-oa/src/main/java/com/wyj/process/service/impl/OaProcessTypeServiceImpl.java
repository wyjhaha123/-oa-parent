package com.wyj.process.service.impl;

import com.wyj.model.process.ProcessTemplate;
import com.wyj.model.process.ProcessType;
import com.wyj.process.mapper.OaProcessTypeMapper;
import com.wyj.process.service.OaProcessTemplateService;
import com.wyj.process.service.OaProcessTypeService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 * 审批类型 服务实现类
 * </p>
 *
 * @author atguigu
 * @since 2023-03-29
 */
@Service
public class OaProcessTypeServiceImpl extends ServiceImpl<OaProcessTypeMapper, ProcessType> implements OaProcessTypeService {

    @Autowired
    private OaProcessTypeService oaProcessTypeService;
    @Autowired
    private OaProcessTemplateService oaProcessTemplateService;
    @Override
    public List<ProcessType> findProcessType() {
        //1 查询所有审批分类，返回list集合
        List<ProcessType> processTypeList = oaProcessTypeService.list();
        //2 遍历返回所有审批分类list集合
        for (ProcessType processType:processTypeList){
            //3 得到每个审批分类，根据审批分类id查询对应审批模板
            //审批分类id
            //根据审批分类id查询对应审批模板
            LambdaQueryWrapper<ProcessTemplate> wrapper=new LambdaQueryWrapper<>();
            wrapper.eq(ProcessTemplate::getProcessTypeId,processType.getId());
            List<ProcessTemplate> processTemplateList = oaProcessTemplateService.list(wrapper);
            //4 根据审批分类id查询对应审批模板数据（List）封装到每个审批分类对象里面
            processType.setProcessTemplateList(processTemplateList);
        }

        return processTypeList;
    }
}
