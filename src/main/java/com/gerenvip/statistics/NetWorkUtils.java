package com.gerenvip.statistics;

import org.apache.commons.lang3.StringUtils;

/**
 * Created by wangwei on 16/8/24.
 */
public class NetWorkUtils {
    public static final String RESULT_TYPE_NONE = "none";
    public static final String RESULT_TYPE_GPRS = "GPRS";
    public static final String RESULT_TYPE_EDGE = "Edge";
    public static final String RESULT_TYPE_WCDMA = "WCDMA";//UMTS
    public static final String RESULT_TYPE_HSDPA = "HSDPA";
    public static final String RESULT_TYPE_HSUPA = "HSUPA";
    public static final String RESULT_TYPE_HSPA = "HSPA";
    public static final String RESULT_TYPE_IDEN = "iDen";
    public static final String RESULT_TYPE_CDMA1X = "CDMA1x";//CDMA
    public static final String RESULT_TYPE_1XRTT = "1xRTT";
    public static final String RESULT_TYPE_CDMAEVDOREV0 = "CDMAEVDORev0";//EVDO_0
    public static final String RESULT_TYPE_CDMAEVDOREVA = "CDMAEVDORevA";//EVDO_A
    public static final String RESULT_TYPE_CDMAEVDOREVB = "CDMAEVDORevB";//EVDO_B
    public static final String RESULT_TYPE_HRPD = "HRPD";//EHRPD
    public static final String RESULT_TYPE_LTE = "LTE";
    public static final String RESULT_TYPE_HSPAP = "HSPAP";//HSPA+
    public static final String RESULT_TYPE_WIFI = "WIFI";

    public static boolean isWifi(String netWorkName) {
        if (StringUtils.equals(netWorkName, RESULT_TYPE_WIFI)) {
            return true;
        }
        return false;
    }

    public static boolean is4G(String netWorkName) {
        if (StringUtils.equals(netWorkName, RESULT_TYPE_LTE) || StringUtils.equals(netWorkName, RESULT_TYPE_HSPAP)) {
            return true;
        }
        return false;
    }
}
