package com.xavier.dong.entity.po;

import com.xavier.dong.entity.BaseEntity;
import com.xavier.dong.enums.UserStatus;
import lombok.*;

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
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class User extends BaseEntity {

    private String name;

    private String phone;

    private String telephone;

    private String address;

    private UserStatus enabled;

    private String username;

    private String password;

    private String userface;

    private String remark;


}
