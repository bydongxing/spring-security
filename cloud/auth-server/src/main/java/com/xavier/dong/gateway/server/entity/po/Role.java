package com.xavier.dong.gateway.server.entity.po;

import com.baomidou.mybatisplus.annotation.TableField;
import com.xavier.dong.gateway.server.entity.BaseEntity;
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

    @TableField("name_zh")
    private String nameZh;

}
