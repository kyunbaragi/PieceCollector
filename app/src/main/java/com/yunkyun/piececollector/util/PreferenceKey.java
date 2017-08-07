package com.yunkyun.piececollector.util;

/**
 * Created by YunKyun on 2017-08-07.
 */

public enum PreferenceKey {
    DB_VERSION("DB_VERSION", String.class),
    USER_ID("USER_ID", Long.class),
    USER_EMAIL("USER_EMAIL", String.class),
    USER_NICKNAME("USER_NICKNAME", String.class),
    USER_PROFILE_IMAGE_PATH("USER_PROFILE_IMAGE_PATH", String.class);

    String key;
    Class valueType;

    PreferenceKey(String key, Class valueType) {
        this.key = key;
        this.valueType = valueType;
    }

    public String getKey() {
        return key;
    }

    public Class getValueType() {
        return valueType;
    }
}
