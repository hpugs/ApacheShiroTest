package com.hpugs.shiro.test.authc;

import com.hpugs.shiro.test.DTO.UserDTO;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;

public class UserUtil {

    public static UserDTO getCurrentUser() {
        Subject subject = SecurityUtils.getSubject();
        if (subject == null) {
            return null;
        }
        Object principal = subject.getPrincipal();
        if (principal == null) {
            return null;
        }
        return (UserDTO) principal;
    }

}
