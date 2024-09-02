package com.hpugs.shiro.test.authc;

/**
 * 权限常量
 */
public interface PermissionCodeConstant {

    /****************************   菜单组   ******************************/
    interface MenuGroupCode {
        //菜单组-用户管理
        String USER_MANAGE = "user_manage";
    }

    /****************************   菜单   ******************************/
    interface MenuCode {
        //用户管理
        String USER_MANAGE_MENU = "user_manage_menu";

    }


    /****************************   功能权限   ******************************/

    interface FunctionCode {
        /****************用户管理******************/
        // 删除用户
        String DEL_USER = "del_user";
        // 编辑用户
        String EDIT_USER = "edit_user";

    }
}
