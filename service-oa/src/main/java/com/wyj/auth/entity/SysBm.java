package com.wyj.auth.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import java.io.Serializable;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * <p>
 * 
 * </p>
 *
 * @author atguigu
 * @since 2023-04-17
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class SysBm implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 部门id
     */
      @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 部门名称
     */
    private String bmmc;


}
