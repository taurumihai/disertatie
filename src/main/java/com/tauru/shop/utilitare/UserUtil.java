package com.tauru.shop.utilitare;

import com.tauru.shop.entities.User;

public class UserUtil {


    private static final String ADMIN_USERNAME = "Admin123!@#";
    private static final String ADMIN_PASSWORD = "TauruMihai95";

    public static boolean isAdminUser(User user) {

        return user.getUsername().equals(ADMIN_USERNAME) && user.getPassword().equals(ADMIN_PASSWORD);
    }
}
