package com.wyj.auth;

import org.springframework.stereotype.Component;

@Component
public class UserBean {

    public String getUsername(int id) {
        if(id == 1) {
            return "zhangsanxxxx";
        }
        if(id == 2) {
            return "lisi";
        }
        return "admin";
    }
}
