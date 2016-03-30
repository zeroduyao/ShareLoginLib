package com.liulishuo.share;

import android.app.Application;
import android.support.annotation.NonNull;
import android.text.TextUtils;

import java.io.File;

/**
 * Created by echo on 5/18/15.
 */
public class ShareBlock {

    private static ShareBlock mInstance;

    private ShareBlock() {
    }

    public static ShareBlock getInstance() {
        if (mInstance == null) {
            mInstance = new ShareBlock();
        }
        return mInstance;
    }

    public String appName;

    public String pathTemp = null;

    public String weiXinAppId;

    public String weiXinSecret;

    public String weiBoAppId;

    public String weiBoRedirectUrl;

    public String weiBoScope;

    public String QQAppId;

    public String QQScope;

    public ShareBlock initAppName(@NonNull String appName) {
        this.appName = appName;
        return this;
    }

    /**
     * 初始化临时文件地址
     */
    public ShareBlock initSharePicFile(Application application) {
        if (TextUtils.isEmpty(pathTemp)) {
            if (android.os.Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED)) {
                pathTemp = application.getExternalCacheDir() + File.separator;
                File dir = new File(pathTemp);
                if (!dir.exists()) {
                    dir.mkdirs();
                }
            }
        }
        return this;
    }

    public ShareBlock initWeiXin(@NonNull String weiXinAppId, @NonNull String weiXinSecret) {
        this.weiXinAppId = weiXinAppId;
        this.weiXinSecret = weiXinSecret;
        return this;
    }

    public ShareBlock initWeiBo(@NonNull String weiBoAppId, @NonNull String redirectUrl, @NonNull String scope) {
        this.weiBoAppId = weiBoAppId;
        weiBoRedirectUrl = redirectUrl;
        weiBoScope = scope;
        return this;
    }

    public ShareBlock initQQ(@NonNull String qqAppId, @NonNull String scope) {
        QQAppId = qqAppId;
        QQScope = scope;
        return this;
    }

}
