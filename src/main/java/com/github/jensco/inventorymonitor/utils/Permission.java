package com.github.jensco.inventorymonitor.utils;

public enum Permission {
    ADMIN("inventorymonitor.admin"),
    RELOAD("inventorymonitor.reload"),
    ALERT("inventorymonitor.alert"),
    TEMPDISABLE("inventorymonitor.tempdisable"),
    Check("inventorymonitor.check");

    private final String key;

    Permission(String key) {
        this.key = key;
    }

    public String getPermission() {
        return key;
    }
}
