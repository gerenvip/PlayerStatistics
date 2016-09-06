package com.gerenvip.statistics;

import org.apache.commons.lang3.StringUtils;

/**
 * Created by wangwei on 16/8/20.
 */
public enum PlayerType {
    WEB_URL, NATIVE, YOUTUBE;

    public static PlayerType parseType(String type) {
        if (StringUtils.equals(type, WEB_URL.name())) {
            return WEB_URL;
        } else if (StringUtils.equals(type, NATIVE.name())) {
            return NATIVE;
        } else if (StringUtils.equals(type, YOUTUBE.name())) {
            return YOUTUBE;
        } else if (StringUtils.startsWith(type, WEB_URL.name())) {
            return WEB_URL;
        }
        return null;
    }
}
