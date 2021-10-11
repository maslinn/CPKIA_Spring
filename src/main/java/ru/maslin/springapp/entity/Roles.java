package ru.maslin.springapp.entity;

import org.springframework.security.core.GrantedAuthority;

public enum Roles implements GrantedAuthority {
    CLIENT, ADMIN;

    @Override
    public String getAuthority() {
        return name();
    }
}
