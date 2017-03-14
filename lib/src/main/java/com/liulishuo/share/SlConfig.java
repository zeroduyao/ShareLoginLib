package com.liulishuo.share;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

/**
 * @author Kale
 * @date 2017/3/14
 */
public class SlConfig {

    public static String appName;

    public static String pathTemp = null;

    public static String weiXinAppId;

    public static String weiXinSecret;

    public static String weiBoAppId;

    public static String weiBoRedirectUrl;

    public static String weiBoScope;

    public static String qqAppId;

    public static String qqScope;

    static boolean isDebug = false;

    public static class Builder {

        public SlConfig.Builder appName(@NonNull String appName) {
            SlConfig.appName = appName;
            return this;
        }

        /**
         * 这里必须用外部存储器，因为第三方app会读取这个目录下的图片文件!!!
         */
        public SlConfig.Builder picTempFile(@Nullable String tempPath) {
            SlConfig.pathTemp = tempPath;
            return this;
        }

        public SlConfig.Builder debug(boolean debug) {
            SlConfig.isDebug = debug;
            return this;
        }

        public SlConfig.Builder qq(@NonNull String qqAppId, @NonNull String scope) {
            SlConfig.qqAppId = qqAppId;
            SlConfig.qqScope = scope;
            return this;
        }

        public SlConfig.Builder weiBo(@NonNull String weiBoAppId, @NonNull String redirectUrl, @NonNull String scope) {
            SlConfig.weiBoAppId = weiBoAppId;
            SlConfig.weiBoRedirectUrl = redirectUrl;
            SlConfig.weiBoScope = scope;
            return this;
        }

        public SlConfig.Builder weiXin(@NonNull String weiXinAppId, @NonNull String weiXinSecret) {
            SlConfig.weiXinAppId = weiXinAppId;
            SlConfig.weiXinSecret = weiXinSecret;
            return this;
        }

        public SlConfig build() {
            return new SlConfig();
        }
    }
}
