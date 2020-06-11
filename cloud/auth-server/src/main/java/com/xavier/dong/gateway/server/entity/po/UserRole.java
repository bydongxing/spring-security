package com.xavier.dong.gateway.server.entity.po;

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
public class UserRole extends BaseEntity {

    private static final long serialVersionUID = 1L;

    private Long uId;

    private Long rId;


}
