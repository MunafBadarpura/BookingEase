package com.munaf.airBnbApp.utils;

import com.munaf.airBnbApp.entities.User;
import org.springframework.security.core.context.SecurityContextHolder;

public class UserUtils {

    public static User getCurrentUser() {
        return (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }

}
