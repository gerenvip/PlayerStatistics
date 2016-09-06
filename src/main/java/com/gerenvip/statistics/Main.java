package com.gerenvip.statistics;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by wangwei on 16/8/20.
 * 数据源 按version 分不同文件
 */
public class Main {

    /**
     * 输出所有的Load Time log
     */
    private static final boolean sLogLoadTimeInfo = false;

    /**
     * 输出所有的 time Sequence  log
     */
    private static final boolean sLogSequenceInfo = false;

    /**
     * 是否过滤掉本地ip
     */
    private static final boolean sFilterLocalIp = true;

    public static String slogFileName = "238";

    public static void main(String[] args) {
        System.out.println("main");
//        handleLog("237");
        for (int start = 238; start <= 239; start++) {
            slogFileName = start + "";
            Utils.deleteResultFile(start + "");
            handleLog(start + "");
        }
    }

    private static void handleLog(String logName) {

        List<LoadTimeItem> loadTimeItemList = new ArrayList<LoadTimeItem>();
        List<SequenceItem> sequenceItemList = new ArrayList<SequenceItem>();
        File log = new File(logName);
        System.out.println("log \"" + logName + "\" exists=" + log.exists());
        try {
            InputStreamReader is = new InputStreamReader(new FileInputStream(log));
            BufferedReader bufferedReader = new BufferedReader(is);
            String line = null;
            while ((line = bufferedReader.readLine()) != null) {
                buildItem(line, loadTimeItemList, sequenceItemList);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        log("loadTimeItemList size=" + loadTimeItemList.size());

        // log all loadtime item
        if (sLogLoadTimeInfo) {
            for (LoadTimeItem loadTimeItem : loadTimeItemList) {
                log(loadTimeItem.toString(), false);
            }
        }

        log("sequenceItemList size=" + sequenceItemList.size());
        if (sLogSequenceInfo) {
            for (SequenceItem sequenceItem : sequenceItemList) {
                log(sequenceItem.toString(), false);
            }
        }

        Utils.logAverageTimeForLoadTime(loadTimeItemList, false);
        Utils.logAverageTimeForTimeSequence(sequenceItemList, false);


        Utils.logPlayerAverageTimeForLoadTime(loadTimeItemList, false);
        Utils.logPlayerAverageTimeForTimeSequence(sequenceItemList, false);


        Utils.logInTimeIntervalCount(sequenceItemList,false);
        Utils.logPlayerInTimeIntervalCount(sequenceItemList, false);

        Utils.logIframeAverageTime(sequenceItemList, true, true);
        Utils.logNoIframeReason(sequenceItemList);

        Utils.logResourceAverageTime(sequenceItemList, false);
        Utils.logResourcePercent(sequenceItemList);

        Utils.logPlayerResourceAverageTime(sequenceItemList);

        Utils.logNetWorkAverageTime(sequenceItemList);

        Utils.logPlayerNetWorkAverageTime(sequenceItemList);

        Utils.logNoIframeReason(sequenceItemList);
        Utils.logIframeAverageTime(sequenceItemList, false, true);
        Utils.logPlayerInTimeIntervalCount(sequenceItemList, false);
        Utils.logIframeTimeIntervalCount(sequenceItemList, true);

    }

    private static void buildItem(String line, List<LoadTimeItem> loadTimeItemList, List<SequenceItem> sequenceItemList) {
        if (StringUtils.isEmpty(line)) {
            return;
        }
        if (StringUtils.contains(line, "\"event\":\"LOAD_TIME\"")) {
            LoadTimeItem loadTimeItem = buildLoadtimeItem(line);
            if (sFilterLocalIp) {
                if (StringUtils.equals(loadTimeItem.ip, "127.0.0.1")) {
                    return;
                }
            }

            if (loadTimeItem.info.loadTime == 0) {
                return;
            }
            loadTimeItemList.add(loadTimeItem);
        } else if (StringUtils.contains(line, "\"event\":\"PLAY_SEQUENCE\"")) {
            SequenceItem sequenceItem = buildSequenceItem(line);
            if (sFilterLocalIp) {
                if (StringUtils.equals(sequenceItem.ip, "127.0.0.1")) {
                    return;
                }
            }
            if (sequenceItem.info.startPlayTime == 0) {
                return;
            }
            sequenceItemList.add(sequenceItem);
        }
    }

    private static SequenceItem buildSequenceItem(String line) {
        String date = get2016DateString(line);
        boolean hasDate = hasDate(line);
        String json = hasDate ? line.substring(23) : line;
        JSONObject jsonObject = null;
        jsonObject = new JSONObject(json);

        int version = jsonObject.optInt("vc");
        String user = jsonObject.optString("user");
        String network = jsonObject.optString("network");
        String ip = jsonObject.optString("ip");
        String content = jsonObject.optString("content");
        JSONObject contentJson = convertToJson(content);
        String facebookResponse = null;
        String initTime = null;
        String prepareTime = null;
        String downloadLink = null;
        String startPlay = null;
        String isSwitch = null;
        String waitTime = null;
        String isCustomIframe = null;
        String iframeReason = null;
        String closeReason = null;

        ResourceType resourceType = null;
        PlayerType playerType = null;

        if (contentJson != null) {
            facebookResponse = contentJson.optString("facebookResponse");
            prepareTime = contentJson.optString("prepare");
            if (StringUtils.isEmpty(prepareTime)) {
                prepareTime = contentJson.optString("load");
            }
            initTime = contentJson.optString("init");
            downloadLink = contentJson.optString("downloadLink");
            startPlay = contentJson.optString("startPlay");
            isSwitch = contentJson.optString("isSwitch");
            waitTime = contentJson.optString("waitTime");
            isCustomIframe = contentJson.optString("isCustomIframe");
            iframeReason = contentJson.optString("iframeReason");
            closeReason = contentJson.optString("closeReason");

            String resource = contentJson.optString("resource");
            resourceType = ResourceType.parseResourceType(resource);
            String playerTypeString = contentJson.optString("playerType");
            playerType = PlayerType.parseType(playerTypeString);

        }
        return new SequenceItem.Builder()
                .setDate(date)
                .setIp(ip)
                .setVersion(version)
                .setUserId(user)
                .setNetWork(network)
                .setPlayerType(playerType)
                .setResourceType(resourceType)
                .setCloseReason(closeReason)
                .setDownloadTime(StringUtils.isEmpty(downloadLink) ? 0 : Integer.parseInt(downloadLink))
                .setFacebookTime(StringUtils.isEmpty(facebookResponse) ? 0 : Integer.parseInt(facebookResponse))
                .setInitTime(StringUtils.isEmpty(initTime) ? 0 : Integer.parseInt(initTime))
                .setPrepareTime(StringUtils.isEmpty(prepareTime) ? 0 : Integer.parseInt(prepareTime))
                .setStartPlayTime(StringUtils.isEmpty(startPlay) ? 0 : Integer.parseInt(startPlay))
                .setWaitTime(StringUtils.isEmpty(waitTime) ? 0 : Integer.parseInt(waitTime))
                .setIsSwitch(StringUtils.equals("true", isSwitch))
                .setIsIframe(StringUtils.equals("true", isCustomIframe))
                .setIframeReason(iframeReason)
                .setCloseReason(closeReason)
                .build();
    }

    private static LoadTimeItem buildLoadtimeItem(String line) {
        String date = get2016DateString(line);
        String jsonString = line.substring(23);
        JSONObject jsonObject = new JSONObject(jsonString);
        String playerTypeString = jsonObject.optString("content");
        PlayerType playerType = PlayerType.parseType(playerTypeString);
        String ip = jsonObject.optString("ip");
        String network = jsonObject.optString("network");
        if (playerType == null) {
            log("buildLoadtimeItem exception item =" + line);
        }
        return new LoadTimeItem.Builder()
                .setDate(date)
                .setUserId(jsonObject.optString("user"))
                .setPlayerType(playerType)
                .setLoadTime(jsonObject.optInt("cost"))
                .setVersion(Integer.parseInt(jsonObject.optString("vc")))
                .setIp(ip)
                .setNetWork(network)
                .build();
    }

    private static JSONObject convertToJson(String conent) {
        if (StringUtils.isEmpty(conent)) {
            return null;
        }
        String[] result = conent.split(";");
        JSONObject jsonObject = new JSONObject();
        for (String s : result) {
            if (s.contains(":")) {
                String[] keyValue = s.split(":");
                if (keyValue.length >= 2) {
                    jsonObject.put(keyValue[0], keyValue[1]);
                }
            } else if (s.contains("=")) {
                String[] keyValue = s.split("=");
                if (keyValue.length >= 2) {
                    jsonObject.put(keyValue[0], keyValue[1]);
                }
            }


        }
        return jsonObject;
    }

    private static boolean hasDate(String line) {
        return line.startsWith("user-log");
    }

    private static String get2016DateString(String line) {
        String date = null;
        try {
            int index = line.indexOf("2016");
            date = line.substring(index, index + 8);
        } catch (Exception e) {
        }
        if (date == null) {
            SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd");
            date = format.format(new Date(System.currentTimeMillis()));
        }


        return date;
    }


    private static void log(String s, boolean toFile) {
        if (toFile) {
            Utils.log(s);
        } else {
            System.out.println(s);
        }
    }

    private static void log(String s) {
        log(s, true);
    }

}
