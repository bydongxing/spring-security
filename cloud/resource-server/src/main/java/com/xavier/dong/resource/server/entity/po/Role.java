package com.xavier.dong.resource.server.entity.po;

import com.baomidou.mybatisplus.annotation.TableField;
import com.xavier.dong.resource.server.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * <p>
 * 
 * </p>
 *
 * @author xavierdong
 * @since 2020-06-05
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class Role extends BaseEntity {

    private String name;

    @TableField("nameZh")
    private String nameZh;

}
