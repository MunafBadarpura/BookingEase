package com.munaf.bookingEase.utils;

import com.munaf.bookingEase.entities.User;
import org.springframework.security.core.context.SecurityContextHolder;

public class UserUtils {

    public static User getCurrentUser() {
        return (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }

}
