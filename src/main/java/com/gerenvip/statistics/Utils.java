package com.gerenvip.statistics;

import org.apache.commons.lang3.StringUtils;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by wangwei on 16/8/20.
 */
public class Utils {

    private static final boolean sFilterExceptionTime = true;
    private static final boolean sLogFilterResult = true;
    private static final int sFilterTime = 20000;

    private static final String LOG_RESULT_FILE_DIR = "result";
    private static final String LOG_RESULT_FILE_NAME = Main.slogFileName;

    private static final boolean sDmpToSD = true;

    private static String getLogFileName() {
        return Main.slogFileName;
    }

    /**
     * 删除已经存在的文件
     *
     * @param fileName
     */
    public static void deleteResultFile(String fileName) {
        if (StringUtils.isEmpty(fileName)) {
            return;
        }
        File dir = new File(LOG_RESULT_FILE_DIR);
        if (!dir.exists() || dir.exists() && !dir.isDirectory()) {
            dir.delete();
            dir.mkdirs();
        }
        File logFile = new File(dir, fileName);
        if (logFile.exists()) {
            logFile.delete();
        }
    }

    public static void log(String s) {
        log(s, true);
    }

    /**
     * 输出运行结果到文件
     *
     * @param s
     */
    public static void log(String s, boolean toFile) {
        System.out.println(s);
        if (!toFile) {
            return;
        }

        if (sDmpToSD) {
            File dir = new File(LOG_RESULT_FILE_DIR);
            if (!dir.exists() || dir.exists() && !dir.isDirectory()) {
                dir.delete();
                dir.mkdirs();
            }

//            File logFile = new File(dir, LOG_RESULT_FILE_NAME);
            File logFile = new File(dir, getLogFileName());
//            if (logFile.exists()) {
//                logFile.delete();
//            }
            try {
                PrintWriter pw = new PrintWriter(new BufferedWriter(new FileWriter(logFile, true)));
//                pw.println("************************* START ***************************");
                pw.println();
                pw.print(s);
//                pw.println("\n************************* END ***************************");
                pw.close();
            } catch (Exception e) {
            }

        }

    }

    /**
     * 过滤掉异常数据-LoadTime 数据
     *
     * @param list
     * @param filterTime 定义的异常时间上限
     * @return
     */
    private static List<LoadTimeItem> filterForLoadTimeByTime(List<LoadTimeItem> list, int filterTime) {
        if (!sFilterExceptionTime) {
            return list;
        }
        if (list == null || list.size() == 0) {
            return list;
        }

        List<LoadTimeItem> result = new ArrayList<LoadTimeItem>();
        List<LoadTimeItem> discardedResult = new ArrayList<LoadTimeItem>();
        for (LoadTimeItem item : list) {
            if (item.info.loadTime < filterTime) {
                result.add(item);
            } else {
                discardedResult.add(item);
            }
        }

        if (sLogFilterResult) {
            log("\n:filterForLoadTimeByTime result size=" + result.size());
            log(":filterForLoadTimeByTime discardedResult size=" + discardedResult.size() + "\n");
            log(":filterForLoadTimeByTime discardedResult percent=%" + String.format("%.1f", (discardedResult.size() / (float) result.size()) * 100));
        }

        return result;
    }

    /**
     * 过滤掉异常数据--SQ数据
     *
     * @param list
     * @param filterTime
     * @return
     */
    private static List<SequenceItem> filterForTimeSequenceByTime(List<SequenceItem> list, int filterTime) {
        if (!sFilterExceptionTime) {
            return list;
        }
        if (list == null || list.size() == 0) {
            return list;
        }

        List<SequenceItem> result = new ArrayList<SequenceItem>();
        List<SequenceItem> discardedResult = new ArrayList<SequenceItem>();
        List<SequenceItem> switchList = new ArrayList<SequenceItem>();
        for (SequenceItem item : list) {
            if (item.info.startPlayTime < filterTime) {
                if (item.info.isSwitch) {
                    switchList.add(item);
                } else {
                    result.add(item);
                }
            } else {
                discardedResult.add(item);
            }
        }

        if (sLogFilterResult) {
            log("\n:filterForTimeSequenceByTime result size=" + result.size() + ";filterTime=" + filterTime);
            log(":filterForTimeSequenceByTime discardedResult size=" + discardedResult.size() + "\n");
            log(":filterForTimeSequenceByTime switchList size=" + switchList.size() + "\n");
            log(": filterForTimeSequenceByTime discardedResult percent=%" + String.format("%.1f", (discardedResult.size() / (float) result.size()) * 100));
        }

        return result;
    }

    /**
     * 按照 player type 分类 SQ数据
     *
     * @param sourceList
     * @param youtubetList
     * @param nativeList
     * @param webList
     */
    private static void typeItemForSequenceByPlayerType(List<SequenceItem> sourceList, List<SequenceItem> youtubetList, List<SequenceItem> nativeList, List<SequenceItem> webList) {
        if (sourceList == null || sourceList.size() == 0) {
            return;
        }
        if (youtubetList == null || nativeList == null || webList == null) {
            throw new IllegalArgumentException("youtubetList, nativeList, webList can not be null");
        }
        for (SequenceItem sequenceItem : sourceList) {
            switch (sequenceItem.info.playerType) {
                case YOUTUBE:
                    youtubetList.add(sequenceItem);
                    break;
                case NATIVE:
                    nativeList.add(sequenceItem);
                    break;
                case WEB_URL:
                    webList.add(sequenceItem);
                    break;
                default:
                    log("typeItemForSequence error -->unsupport playerType ");
                    break;
            }
        }
    }

    /**
     * 按照 player type 分类 LoadTime数据
     *
     * @param sourceList
     * @param youtubetList
     * @param nativeList
     * @param webList
     */
    private static void typeItemForLoadTimeByPlayerType(List<LoadTimeItem> sourceList, List<LoadTimeItem> youtubetList, List<LoadTimeItem> nativeList, List<LoadTimeItem> webList) {
        if (sourceList == null || sourceList.size() == 0) {
            return;
        }
        if (youtubetList == null || nativeList == null || webList == null) {
            throw new IllegalArgumentException("youtubetList, nativeList, webList can not be null");
        }
        for (LoadTimeItem loadTimeItem : sourceList) {
            if (loadTimeItem.info.playerType == PlayerType.YOUTUBE) {
                youtubetList.add(loadTimeItem);

            } else if (loadTimeItem.info.playerType == PlayerType.NATIVE) {
                nativeList.add(loadTimeItem);

            } else if (loadTimeItem.info.playerType == PlayerType.WEB_URL) {
                webList.add(loadTimeItem);

            } else {
                log("typeItemForLoadTime error -->unsupport playerType");

            }
        }
    }

    /**
     * 使用Loadtime 数据统计平均加载时长(不分播放器类型)
     *
     * @param loadTimeItemList
     * @param byDay            是否按天输出
     */
    public static void logAverageTimeForLoadTime(List<LoadTimeItem> loadTimeItemList, boolean byDay) {
        if (loadTimeItemList == null || loadTimeItemList.size() == 0) {
            return;
        }
        loadTimeItemList = filterForLoadTimeByTime(loadTimeItemList, sFilterTime);
        if (loadTimeItemList == null || loadTimeItemList.size() == 0) {
            return;
        }
        if (byDay) {
            int preDate = -1;
            int daySize = 0;
            int total = 0;
            for (LoadTimeItem loadTimeItem : loadTimeItemList) {
                String date = loadTimeItem.date;
                int dateInt = Integer.parseInt(date);
                if (preDate == -1) {
                    preDate = dateInt;
                }
                if (dateInt > preDate) {

                    if (daySize > 0) {
                        int r = total / daySize;
                        log("** " + preDate + ":" + r);
//                        log(r+"");
                    } else {
                        log("logAverageTimeForLoadTime error --> daySize=0");
                    }
                    preDate = dateInt;
                    total = (int) loadTimeItem.info.loadTime;
                    daySize = 1;
                } else if (dateInt == preDate) {
                    total += loadTimeItem.info.loadTime;
                    daySize++;
                } else {
                    throw new IllegalArgumentException("数据必须按照日期递增顺序排列");
                }
            }
            if (daySize > 0) {
                int r = total / daySize;
                log("** " + preDate + ":" + r);
//                log(r+"");
            } else {
                log("logAverageTimeForLoadTime ---> 2 daySize=0");
            }
        } else {
            int size = loadTimeItemList.size();
            int total = 0;
            for (LoadTimeItem loadTimeItem : loadTimeItemList) {
                total += loadTimeItem.info.loadTime;
            }
            int result = total / size;
            log("** AverageTimeForLoadTime:" + result);
        }

    }

    /**
     * 使用 TimeSequence 数据统计平均加载时长(不分播放器类型)
     *
     * @param sequenceItemList
     * @param byDay            是否按天输出
     */
    public static void logAverageTimeForTimeSequence(List<SequenceItem> sequenceItemList, boolean byDay) {
        if (sequenceItemList == null || sequenceItemList.size() == 0) {
            return;
        }
        sequenceItemList = filterForTimeSequenceByTime(sequenceItemList, sFilterTime);
        if (sequenceItemList == null || sequenceItemList.size() == 0) {
            return;
        }
        if (byDay) {
            int preDate = -1;
            int daySize = 0;
            int total = 0;
            for (SequenceItem sequenceItem : sequenceItemList) {
                String date = sequenceItem.date;
                int dateInt = Integer.parseInt(date);
                if (preDate == -1) {
                    preDate = dateInt;
                }
                if (dateInt > preDate) {

                    if (daySize > 0) {
                        int r = total / daySize;
                        log("** " + preDate + ":" + r);
                    } else {
                        log("AverageTimeForTimeSequence error --> daySize=0");
                    }
                    preDate = dateInt;
                    total = (int) sequenceItem.info.startPlayTime;
                    daySize = 1;
                } else if (dateInt == preDate) {
                    total += sequenceItem.info.startPlayTime;
                    daySize++;
                } else {
                    log("error ---> error 0");
                }
            }
            if (daySize > 0) {
                int r = total / daySize;
                log("** " + preDate + ":" + r);
            } else {
                log("AverageTimeForTimeSequence ---> 2 daySize=0");
            }
        } else {
            int size = sequenceItemList.size();
            int total = 0;
            for (SequenceItem sequenceItem : sequenceItemList) {
                total += sequenceItem.info.startPlayTime;
            }
            int result = total / size;
            log("** AverageTimeForTimeSequence:" + result);
        }
    }

    /**
     * 使用Loadtime 数据统计 youtube播放器, native 播放器,web 播放器的平均加载时长
     *
     * @param loadTimeItemList
     * @param byDay            是否按天输出
     */
    public static void logPlayerAverageTimeForLoadTime(List<LoadTimeItem> loadTimeItemList, boolean byDay) {

        List<LoadTimeItem> youtubeSDk = new ArrayList<LoadTimeItem>();
        List<LoadTimeItem> nativePlayer = new ArrayList<LoadTimeItem>();
        List<LoadTimeItem> webPlayer = new ArrayList<LoadTimeItem>();

        typeItemForLoadTimeByPlayerType(loadTimeItemList, youtubeSDk, nativePlayer, webPlayer);
        log("\n");
        if (byDay) {
            log("\nLoadTime Youtube sdk player by day:---> start");
            logAverageTimeForLoadTime(youtubeSDk, true);
            log("LoadTime Youtube sdk player :---> end \n");

            log("\nLoadTime Native player by day: ---> start");
            logAverageTimeForLoadTime(nativePlayer, true);
            log("LoadTime Native player by day: ---> end\n");

            log("\nLoadTime Web player by day: ---> start");
            logAverageTimeForLoadTime(webPlayer, true);
            log("LoadTime Web player by day: ---> end\n");

        } else {
            youtubeSDk = filterForLoadTimeByTime(youtubeSDk, sFilterTime);
            int size = youtubeSDk.size();
            int total = 0;
            if (size > 0) {
                for (LoadTimeItem item : youtubeSDk) {
                    total += item.info.loadTime;
                }

                log("** LoadTime youtubeSDk player AverageTime:" + (total / size));
            }

            nativePlayer = filterForLoadTimeByTime(nativePlayer, sFilterTime);
            size = nativePlayer.size();
            total = 0;
            if (size > 0) {
                for (LoadTimeItem item : nativePlayer) {
                    total += item.info.loadTime;
                }
                log("** LoadTime native player AverageTime:" + (total / size));
            }

            webPlayer = filterForLoadTimeByTime(webPlayer, sFilterTime);
            size = webPlayer.size();
            total = 0;
            if (size > 0) {
                for (LoadTimeItem item : webPlayer) {
                    total += item.info.loadTime;
                }
                log("** LoadTime web player AverageTime:" + (total / size));
            }
        }
    }

    /**
     * 使用 SQ 数据统计youtube播放器, native 播放器,web 播放器的平均加载时长
     *
     * @param byDay 是否按天输出
     */
    public static void logPlayerAverageTimeForTimeSequence(List<SequenceItem> list, boolean byDay) {
        List<SequenceItem> youtubeSDk = new ArrayList<SequenceItem>();
        List<SequenceItem> nativePlayer = new ArrayList<SequenceItem>();
        List<SequenceItem> webPlayer = new ArrayList<SequenceItem>();

        typeItemForSequenceByPlayerType(list, youtubeSDk, nativePlayer, webPlayer);

        log("\n");
        if (byDay) {
            log("\nTimeSequence Youtube sdk player by dat:---> start");
            logAverageTimeForTimeSequence(youtubeSDk, true);
            log("TimeSequence Youtube sdk player :---> end \n");

            log("\nTimeSequence Native player by day: ---> start");
            logAverageTimeForTimeSequence(nativePlayer, true);
            log("TimeSequence Native player by day: ---> end\n");

            log("\nTimeSequence Web player by day: ---> start");
            logAverageTimeForTimeSequence(webPlayer, true);
            log("TimeSequence Web player by day: ---> end\n");

        } else {
            youtubeSDk = filterForTimeSequenceByTime(youtubeSDk, sFilterTime);
            int size = youtubeSDk.size();
            int total = 0;
            if (size > 0) {
                for (SequenceItem item : youtubeSDk) {
                    total += item.info.startPlayTime;
                }

                log("** TimeSequence youtubeSDk player AverageTime:" + (total / size));
            }

            nativePlayer = filterForTimeSequenceByTime(nativePlayer, sFilterTime);
            size = nativePlayer.size();
            total = 0;
            if (size > 0) {
                for (SequenceItem item : nativePlayer) {
                    total += item.info.startPlayTime;
                }
                log("** TimeSequence native player AverageTime:" + (total / size));
            }

            webPlayer = filterForTimeSequenceByTime(webPlayer, sFilterTime);
            size = webPlayer.size();
            total = 0;
            if (size > 0) {
                for (SequenceItem item : webPlayer) {
                    total += item.info.startPlayTime;
                }
                log("** TimeSequence web player AverageTime:" + (total / size));
            }
        }
    }


    /**
     * 根据日期 输出 0-1, 1-3, 3-5, 5-10, 10 ~ 各时间段个数和百分比
     *
     * @param list
     * @param byDay
     */
    public static void logInTimeIntervalCount(List<SequenceItem> list, boolean byDay) {
        if (list == null || list.size() == 0) {
            return;
        }
        list = filterForTimeSequenceByTime(list, sFilterTime);
        if (byDay) {
            List<Integer> dayList = new ArrayList<Integer>();
            int preDate = -1;
            int dayCount = 0;//计算有几天的数据
            for (SequenceItem sequenceItem : list) {
                String date = sequenceItem.date;
                int dateInt = Integer.parseInt(date);
                if (preDate == -1) {
                    preDate = dateInt;
                    dayCount++;
                    dayList.add(dateInt);
                }
                if (dateInt > preDate) {
                    preDate = dateInt;
                    dayCount++;
                    dayList.add(dateInt);
                } else if (dateInt < preDate) {
                    throw new IllegalArgumentException("数据必须日期递增顺序排列");
                }
            }
            log("day count=" + dayCount + "; dayList size=" + dayList.size());
            for (int date : dayList) {
                List<SequenceItem> itemsForDay = new ArrayList<SequenceItem>();
                for (SequenceItem item : list) {
                    if (Integer.parseInt(item.date) == date) {
                        itemsForDay.add(item);
                    }
                }
                log("\n" + date + "--> start");
                logInTimeIntervalCount(itemsForDay);
                log(date + "--> end\n");
            }
        } else {
            logInTimeIntervalCount(list);
        }
    }

    public static void logIframeTimeIntervalCount(List<SequenceItem> list, boolean filter) {
        if (list == null || list.size() == 0) {
            return;
        }

        if (filter) {
            list = filterForTimeSequenceByTime(list, sFilterTime);
        }
        List<SequenceItem> iframeList = new ArrayList<SequenceItem>();
        List<SequenceItem> noIframeList = new ArrayList<SequenceItem>();
        for (SequenceItem item : list) {
            if (item.info != null && item.info.resourceType == ResourceType.YOUTUBE
                    && item.info.playerType == PlayerType.WEB_URL) {
                if (item.info.isIframe) {
                    iframeList.add(item);
                } else {
                    noIframeList.add(item);
                }
            }
        }

        log("\nlogIframeTimeIntervalCount iframe---> start");
        log("iframe count=" + iframeList.size());
        logInTimeIntervalCount(iframeList);
        log("logIframeTimeIntervalCount iframe-->End\n");

        log("logIframeTimeIntervalCount noIframe -->start");
        log("no iframe count=" + noIframeList.size());
        logInTimeIntervalCount(noIframeList);
        log("logIframeTimeIntervalCount noIframe -->End\n");
    }

    /**
     * 0-1, 1-3, 3-5, 5-10, 10 ~ 各时间段个数和百分比
     *
     * @param list
     */
    private static void logInTimeIntervalCount(List<SequenceItem> list) {
        if (list == null || list.size() == 0) {
            return;
        }
        List<SequenceItem> mList0_1 = new ArrayList<SequenceItem>();
        List<SequenceItem> mList1_3 = new ArrayList<SequenceItem>();
        List<SequenceItem> mList3_5 = new ArrayList<SequenceItem>();
        List<SequenceItem> mList5_10 = new ArrayList<SequenceItem>();
        List<SequenceItem> mList10_ = new ArrayList<SequenceItem>();

        for (SequenceItem item : list) {
            if (item.info.startPlayTime <= 1000) {
                mList0_1.add(item);
            } else if (item.info.startPlayTime <= 3000) {
                mList1_3.add(item);
            } else if (item.info.startPlayTime <= 5000) {
                mList3_5.add(item);
            } else if (item.info.startPlayTime <= 10000) {
                mList5_10.add(item);
            } else if (item.info.startPlayTime > 10000) {
                mList10_.add(item);
            }
        }
        log("** 0-1 count=" + mList0_1.size() + " ---> %" + String.format("%.1f", (mList0_1.size() / (float) list.size()) * 100));
        log("** 1-3 count=" + mList1_3.size() + " ---> %" + String.format("%.1f", (mList1_3.size() / (float) list.size()) * 100));
        log("** 3-5 count=" + mList3_5.size() + " ---> %" + String.format("%.1f", (mList3_5.size() / (float) list.size()) * 100));
        log("** 5-10 count=" + mList5_10.size() + " ---> %" + String.format("%.1f", (mList5_10.size() / (float) list.size()) * 100));
        log("** 10~ count=" + mList10_.size() + " ---> %" + String.format("%.1f", (mList10_.size() / (float) list.size()) * 100));
    }


    /**
     * 不同播放器类型 在 0-1, 1-3, 3-5, 5-10, 10 ~ 各时间段个数和百分比
     *
     * @param list
     * @param byDay
     */
    public static void logPlayerInTimeIntervalCount(List<SequenceItem> list, boolean byDay) {
        if (list == null || list.size() == 0) {
            return;
        }
        list = filterForTimeSequenceByTime(list, sFilterTime);
        List<SequenceItem> youtubeSDk = new ArrayList<SequenceItem>();
        List<SequenceItem> nativePlayer = new ArrayList<SequenceItem>();
        List<SequenceItem> webPlayer = new ArrayList<SequenceItem>();

        typeItemForSequenceByPlayerType(list, youtubeSDk, nativePlayer, webPlayer);

        log("\nyoutubeSDK player-->start");
        logInTimeIntervalCount(youtubeSDk, byDay);
        log("youtubeSDK player-->end\n");

        log("\nnative player-->start");
        logInTimeIntervalCount(nativePlayer, byDay);
        log("native player-->end\n");

        log("\nweb player-->start");
        logInTimeIntervalCount(webPlayer, byDay);
        log("web player-->end\n");
    }

    /**
     * 输出iframe 播放器/非iframe(线上web) 播放器的平均播放时长,以及 iframe 播放视频个数
     *
     * @param list
     */
    public static void logIframeAverageTime(List<SequenceItem> list, boolean iframe, boolean isFilter) {
        if (isFilter) {
            list = filterForTimeSequenceByTime(list, sFilterTime);
        }
        if (iframe) {
            int total = 0;
            int count = 0;
            int webCount = 0;
            for (SequenceItem item : list) {
                if (item.info.isIframe && item.info.playerType == PlayerType.WEB_URL && item.info.resourceType == ResourceType.YOUTUBE) {
                    total += item.info.startPlayTime;
                    count++;
                    log("item=" + item);
                }
                if (item.info.playerType == PlayerType.WEB_URL && item.info.resourceType == ResourceType.YOUTUBE) {
                    webCount++;
                }
            }
            log("** iframe count:" + count + "; webCount=" + webCount);
            log("** Average iframe time:" + (total / count));
        } else {

            int total = 0;
            int count = 0;
            int webCount = 0;
            for (SequenceItem item : list) {
                if (!item.info.isIframe && item.info.playerType == PlayerType.WEB_URL && item.info.resourceType == ResourceType.YOUTUBE) {
                    total += item.info.startPlayTime;
                    count++;
                    log("item=" + item);
                }
                if (item.info.playerType == PlayerType.WEB_URL && item.info.resourceType == ResourceType.YOUTUBE) {
                    webCount++;
                }
            }
            log("** no iframe count:" + count + "; webCount=" + webCount);
            log("** Average no iframe time:" + (total / count));
        }
    }

    /**
     * 输出 未使用iframe 播放器的原因
     *
     * @param list
     */
    public static void logNoIframeReason(List<SequenceItem> list) {
        if (list == null || list.size() == 0) {
            return;
        }
        int count = 0;
        int webCount = 0;
        for (SequenceItem item : list) {
            if (item.info.resourceType == ResourceType.YOUTUBE && item.info.playerType == PlayerType.WEB_URL) {
                webCount++;
            }
            if (!item.info.isIframe && item.info.resourceType == ResourceType.YOUTUBE && item.info.playerType == PlayerType.WEB_URL) {
                count++;
                if (!StringUtils.isEmpty(item.info.iframeReason)) {
                    log("reason:" + item.info.iframeReason +"; videoId="+item.info.videoId);
                    log("item="+item);
                } else {
                    log("reason:null");
                }
            }
        }
        log("no iframe count=" + count + ";webCount=" + webCount);
    }

    /**
     * 输出用户关闭播放器的原因
     *
     * @param list
     */
    public static void logCloseReason(List<SequenceItem> list) {
        if (list == null || list.size() == 0) {
            return;
        }
        for (SequenceItem item : list) {
            if (!StringUtils.isEmpty(item.info.closeReason)) {
                log(item.info.closeReason);
            }
        }
    }

    /**
     * 按照视频来源类型 分类SQ数据
     *
     * @param list
     * @param youtubeList
     * @param facebookList
     * @param vimeoList
     */
    private static void typeItemByResourceForSQ(List<SequenceItem> list,
                                               List<SequenceItem> youtubeList,
                                               List<SequenceItem> facebookList,
                                               List<SequenceItem> vimeoList) {
        if (list == null || list.size() == 0) {
            return;
        }
        for (SequenceItem item : list) {
            ResourceType resourceType = item.info.resourceType;
            if (resourceType == ResourceType.FACEBOOK) {
                facebookList.add(item);
            } else if (resourceType == ResourceType.YOUTUBE) {
                youtubeList.add(item);
            } else if (resourceType == ResourceType.VIMEO) {
                vimeoList.add(item);
            }
        }
    }

    /**
     * 计算不同视频源的平均播放时间
     *
     * @param list
     * @param byDay
     */
    public static void logResourceAverageTime(List<SequenceItem> list, boolean byDay) {
        if (list == null || list.size() == 0) {
            return;
        }
        list = filterForTimeSequenceByTime(list, sFilterTime);

        List<SequenceItem> youtubeList = new ArrayList<SequenceItem>();
        List<SequenceItem> facebookList = new ArrayList<SequenceItem>();
        List<SequenceItem> vimeoList = new ArrayList<SequenceItem>();
        typeItemByResourceForSQ(list, youtubeList, facebookList, vimeoList);

        log("\n:logResourceAverageTime youtube --> start");
        logAverageTimeForTimeSequence(youtubeList, byDay);
        log(":logResourceAverageTime youtube --> end");

        log("\n:logResourceAverageTime facebook --> start");
        logAverageTimeForTimeSequence(facebookList, byDay);
        log(":logResourceAverageTime facebook --> end");

        log("\n:logResourceAverageTime vimeo --> start");
        logAverageTimeForTimeSequence(vimeoList, byDay);
        log(":logResourceAverageTime vimeo --> end");
    }

    /**
     * 不同视频源占总视频量的百分比
     *
     * @param list
     */
    public static void logResourcePercent(List<SequenceItem> list) {
        if (list == null || list.size() == 0) {
            return;
        }
        List<SequenceItem> youtubeList = new ArrayList<SequenceItem>();
        List<SequenceItem> facebookList = new ArrayList<SequenceItem>();
        List<SequenceItem> vimeoList = new ArrayList<SequenceItem>();
        typeItemByResourceForSQ(list, youtubeList, facebookList, vimeoList);

        log("\n**youtube list size=" + youtubeList.size());
        log("**youtube percent= %" + String.format("%.1f", (youtubeList.size() / (float) list.size()) * 100));
        log("**facebook list size=" + facebookList.size());
        log("**facebook percent= %" + String.format("%.1f", (facebookList.size() / (float) list.size()) * 100));
        log("**vimeo list size=" + vimeoList.size());
        log("**vimeo percent= %" + String.format("%.1f", (vimeoList.size() / (float) list.size()) * 100) + "\n");

    }

    /**
     * 不同源的视频 不同播放器的平均加载时长
     *
     * @param list
     */
    public static void logPlayerResourceAverageTime(List<SequenceItem> list) {
        List<SequenceItem> youtubeList = new ArrayList<SequenceItem>();
        List<SequenceItem> facebookList = new ArrayList<SequenceItem>();
        List<SequenceItem> vimeoList = new ArrayList<SequenceItem>();

        list = filterForTimeSequenceByTime(list, sFilterTime);

        typeItemByResourceForSQ(list, youtubeList, facebookList, vimeoList);

        log(":youtube video player average time --> start");
        logPlayerAverageTimeForTimeSequence(youtubeList, false);
        log(":youtube video player average time --> end");

        log(":facebook video player average time --> start");
        logPlayerAverageTimeForTimeSequence(facebookList, false);
        log(":facebook video player average time --> end");

        log(":vimeo video player average time --> start");
        logPlayerAverageTimeForTimeSequence(vimeoList, false);
        log(":vimeo video player average time --> end");
    }

    private static void typeNetWork(List<SequenceItem> list, List<SequenceItem> wifiList, List<SequenceItem> _4Glist, List<SequenceItem> otherList) {
        if (list == null || list.size() == 0) {
            return;
        }
        if (wifiList == null || _4Glist == null || otherList == null) {
            throw new IllegalArgumentException("result list can not be null");
        }
        for (SequenceItem item : list) {
            if (NetWorkUtils.isWifi(item.netWork)) {
                wifiList.add(item);
            } else if (NetWorkUtils.is4G(item.netWork)) {
                _4Glist.add(item);
            } else {
                otherList.add(item);
            }
        }
    }

    /**
     * 输出不同网络类型的平均加载时长
     *
     * @param list
     */
    public static void logNetWorkAverageTime(List<SequenceItem> list) {
        if (list == null || list.size() == 0) {
            return;
        }
        filterForTimeSequenceByTime(list, sFilterTime);

        List<SequenceItem> wifiList = new ArrayList<SequenceItem>();
        List<SequenceItem> _4GList = new ArrayList<SequenceItem>();
        List<SequenceItem> otherList = new ArrayList<SequenceItem>();

        typeNetWork(list, wifiList, _4GList, otherList);

        log(":logNetWorkAverageTime wifi -->start");
        logAverageTimeForTimeSequence(wifiList, false);
        log(":logNetWorkAverageTime wifi -->end");

        log(":logNetWorkAverageTime 4G -->start");
        logAverageTimeForTimeSequence(_4GList, false);
        log(":logNetWorkAverageTime 4G -->end");

        log(":logNetWorkAverageTime other -->start");
        logAverageTimeForTimeSequence(otherList, false);
        log(":logNetWorkAverageTime other -->end");
    }

    /**
     * 输出不同网络类型下不同播放器的平均加载时长
     *
     * @param list
     */
    public static void logPlayerNetWorkAverageTime(List<SequenceItem> list) {
        filterForTimeSequenceByTime(list, sFilterTime);

        List<SequenceItem> wifiList = new ArrayList<SequenceItem>();
        List<SequenceItem> _4GList = new ArrayList<SequenceItem>();
        List<SequenceItem> otherList = new ArrayList<SequenceItem>();

        typeNetWork(list, wifiList, _4GList, otherList);

        log(":logPlayerNetWorkAverageTime NetWork Wifi -->Start");
        logPlayerAverageTimeForTimeSequence(wifiList, false);
        log(":logPlayerNetWorkAverageTime NetWork Wifi -->end");

        log(":logPlayerNetWorkAverageTime NetWork 4G -->Start");
        logPlayerAverageTimeForTimeSequence(_4GList, false);
        log(":logPlayerNetWorkAverageTime NetWork 4G -->end");

        log(":logPlayerNetWorkAverageTime NetWork other -->Start");
        logPlayerAverageTimeForTimeSequence(otherList, false);
        log(":logPlayerNetWorkAverageTime NetWork other -->end");
    }


}
