package com.gerenvip.statistics;

import org.apache.commons.lang3.StringUtils;

/**
 * Created by wangwei on 16/8/20.
 */
public enum ResourceType {
    FACEBOOK, YOUTUBE, VIMEO;

    public static ResourceType parseResourceType(String type) {
        if (StringUtils.equals(type, FACEBOOK.name())) {
            return FACEBOOK;
        } else if (StringUtils.equals(type, YOUTUBE.name())) {
            return YOUTUBE;
        } else if (StringUtils.equals(type, VIMEO.name())) {
            return VIMEO;
        }

        return null;
    }
}
