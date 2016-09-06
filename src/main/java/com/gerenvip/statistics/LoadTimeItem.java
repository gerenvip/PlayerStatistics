package com.gerenvip.statistics;

/**
 * Created by wangwei on 16/8/20.
 */
public class LoadTimeItem extends Item {

    public LoadTimeInfo info;

    private LoadTimeItem(Builder builder) {
        this.userId = builder.userId;
        this.info = builder.info;
        this.date = builder.date;
        this.version = builder.version;
        this.ip = builder.ip;
        this.netWork = builder.netWork;
    }

    @Override
    public String toString() {
        return "date:" + date + "\n"
                + "userId:" + userId + "\n"
                + "version:" + version + "\n"
                + "ip:" + ip + "\n"
                + "netWork:" + netWork + "\n"
                + "info:\n[" + info + "]\n";
    }

    public static class Builder {

        private String userId;
        private LoadTimeInfo info;
        private String date;
        private int version;
        private String ip;
        private String netWork;

        public Builder() {
            info = new LoadTimeInfo();
        }

        public Builder setNetWork(String netWork) {
            this.netWork = netWork;
            return this;
        }
        public Builder setIp(String ip) {
            this.ip = ip;
            return this;
        }

        public Builder setUserId(String userId) {
            this.userId = userId;
            return this;
        }

        public Builder setPlayerType(PlayerType type) {
            this.info.playerType = type;
            return this;
        }

        public Builder setLoadTime(long time) {
            this.info.loadTime = time;
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

        public LoadTimeItem build() {
            return new LoadTimeItem(this);
        }

    }

    public static class LoadTimeInfo extends Info {
        public long loadTime;

        @Override
        public String toString() {
            return "loadTime:"+loadTime +"\n"
                    +"playerType:"+playerType +"\n";
        }
    }
}
