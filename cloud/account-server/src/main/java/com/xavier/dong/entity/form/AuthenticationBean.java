package com.xavier.dong.entity.form;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;

@Data
public class AuthenticationBean implements Serializable {

    private static final long serialVersionUID = -230567451887011746L;
    @NotBlank
    private String username;

    @NotBlank
    private String password;
}