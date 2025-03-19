package com.springboot.tukserver.security;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.util.Collection;

public class CustomUserDetails extends User {

    private final String name;
    private final String nickname;

    public CustomUserDetails(String username, String password, String name, String nickname, Collection<? extends GrantedAuthority> authorities) {
        super(username, password, authorities);
        this.name = name;
        this.nickname = nickname;
    }

    public String getName() {
        return name;
    }

    public String getNickname() {
        return nickname;
    }

}
