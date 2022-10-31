package com.hpugs.shiro.test.DTO;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class UserDTO implements Serializable {

    private String account;

    private String password;

    /**
     * 权限集合
     */
    private List<String> permissions;

}
