package com.wyj.vo.process;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

@Data
@ApiModel(description = "流程表单")
public class ProcessFormVo implements Serializable {
	//private static final long serialVersionUID = -1242493306307174690L;

	@ApiModelProperty(value = "审批模板id")
	private Long processTemplateId;

	@ApiModelProperty(value = "审批类型id")
	private Long processTypeId;

	@ApiModelProperty(value = "表单值")
	private String formValues;


}