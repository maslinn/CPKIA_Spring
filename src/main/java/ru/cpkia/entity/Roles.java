package ru.cpkia.entity;

import org.springframework.security.core.GrantedAuthority;

public enum Roles implements GrantedAuthority {
    CLIENT, ADMIN, MANAGER;

    @Override
    public String getAuthority() {
        return name();
    }
}
