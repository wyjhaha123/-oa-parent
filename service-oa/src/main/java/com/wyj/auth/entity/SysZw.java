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
public class SysZw implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 职位id
     */
      @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 职位名称
     */
    private String zwmc;


}
