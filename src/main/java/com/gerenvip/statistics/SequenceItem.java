package com.gerenvip.statistics;

/**
 * Created by wangwei on 16/8/20.
 */
public class SequenceItem extends Item {

    public SequenceInfo info;

    private SequenceItem(Builder builder) {
        this.userId = builder.userId;
        this.version = builder.version;
        this.date = builder.date;
        this.info = builder.info;
        this.ip = builder.ip;
        this.netWork = builder.netWork;
        this.debug = builder.debug;
        this.device = builder.device;
        this.pkg = builder.pkg;
        this.reportDate = builder.reportDate;
    }

    @Override
    public String toString() {
        return "{\ndate:" + date + "\n"
                + "userId:" + userId + "\n"
                + "version:" + version + "\n"
                + "ip:" + ip + "\n"
                + "netWork:" + netWork + "\n"
                + "debug:" + debug + "\n"
                + "device:" + device + "\n"
                + "pkg:" + pkg + "\n"
                + "time:" + reportDate + "\n"
                + "info:[" + info + "]}\n";
    }

    public static class Builder {

        private SequenceInfo info;

        private String date;
        private int version;
        private String userId;
        private String ip;
        private String netWork;
        private boolean debug;
        private String device;
        private String pkg;
        private String reportDate;

        public Builder() {
            info = new SequenceInfo();
        }

        public Builder setNetWork(String netWork) {
            this.netWork = netWork;
            return this;
        }

        public Builder setIp(String ip) {
            this.ip = ip;
            return this;
        }

        public Builder setDate(String date) {
            this.date = date;
            return this;
        }

        public Builder setVersion(int version) {
            this.version = version;
            return this;
        }

        public Builder setUserId(String userId) {
            this.userId = userId;
            return this;
        }

        public Builder setPlayerType(PlayerType type) {
            info.playerType = type;
            return this;
        }

        public Builder setResourceType(ResourceType type) {
            info.resourceType = type;
            return this;
        }

        public Builder setFacebookTime(long time) {
            info.facebookResponseTime = time;
            return this;
        }

        public Builder setDownloadTime(long time) {
            info.downloadResponseTime = time;
            return this;
        }

        public Builder setInitTime(long initTime) {
            info.initTime = initTime;
            return this;
        }

        public Builder setPrepareTime(long prepareTime) {
            info.prepareTime = prepareTime;
            return this;
        }

        public Builder setStartPlayTime(long startPlayTime) {
            info.startPlayTime = startPlayTime;
            return this;
        }

        public Builder setWaitTime(long waitTime) {
            info.waitTime = waitTime;
            return this;
        }

        public Builder setIsSwitch(boolean isSwitch) {
            info.isSwitch = isSwitch;
            return this;
        }

        public Builder setIsIframe(boolean isIframe) {
            info.isIframe = isIframe;
            return this;
        }

        public Builder setIframeReason(String reason) {
            info.iframeReason = reason;
            return this;
        }

        public Builder setCloseReason(String reason) {
            info.closeReason = reason;
            return this;
        }

        public Builder setVideoId(String videoId) {
            info.videoId = videoId;
            return this;
        }

        public Builder setDebug(boolean debug) {
            this.debug = debug;
            return this;
        }

        public Builder setDevice(String device) {
            this.device = device;
            return this;
        }

        public Builder setPkg(String pkg) {
            this.pkg = pkg;
            return this;
        }

        public Builder setReportDate(String reportDate) {
            this.reportDate = reportDate;
            return this;
        }

        public Builder setPreLoad(boolean preLoad) {
            this.info.preLoad = preLoad;
            return this;
        }

        public Builder setFromFeed(boolean fromFeed) {
            this.info.fromFeed = fromFeed;
            return this;
        }

        public Builder setResourceId(String resourceId) {
            this.info.resourceId = resourceId;
            return this;
        }

        public SequenceItem build() {
            return new SequenceItem(this);
        }
    }

    public static class SequenceInfo extends Info {
        private SequenceInfo() {
        }

        public ResourceType resourceType;
        public long facebookResponseTime;
        public long downloadResponseTime;
        public long initTime;//youtube sdk only
        public long prepareTime;
        public long startPlayTime;
        public long waitTime;
        public boolean isSwitch;
        public boolean isIframe;
        public String iframeReason;
        public String closeReason;
        public String videoId;
        public boolean preLoad;
        public boolean fromFeed;
        public String resourceId;

        @Override
        public String toString() {
            return "playerType:" + playerType + "\n"
                    + "resourceType:" + resourceType + "\n"
                    + "facebookResponseTime:" + facebookResponseTime + "\n"
                    + "downloadResponseTime:" + downloadResponseTime + "\n"
                    + "initTime:" + initTime + "\n"
                    + "prepareTime:" + prepareTime + "\n"
                    + "startPlayTime:" + startPlayTime + "\n"
                    + "waitTime:" + waitTime + "\n"
                    + "isSwitch:" + isSwitch + "\n"
                    + "isIframe:" + isIframe + "\n"
                    + "iframeReason:" + iframeReason + "\n"
                    + "closeReason:" + closeReason + "\n"
                    + "videoId:" + videoId + "\n"
                    + "preLoad:" + preLoad + "\n"
                    + "fromFeed:" + fromFeed + "\n"
                    + "resourceId:" + resourceId + "\n";
        }
    }
}
