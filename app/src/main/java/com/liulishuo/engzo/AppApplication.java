package com.liulishuo.engzo;

import android.app.Application;
import android.util.Log;

import com.liulishuo.share.ShareLoginSDK;
import com.liulishuo.share.SlConfig;

/**
 * @author Kale
 * @date 2016/8/11
 */
public class AppApplication extends Application {

    private static final String TAG = "Application";

    protected static String QQ_APPID, QQ_SCOPE,
            WEIBO_APPID, WEIBO_SCOPE, WEIBO_REDIRECT_URL,
            WEIXIN_APPID, WEIXIN_SECRET;

    @Override
    public void onCreate() {
        super.onCreate();

        initConstant();

        Log.d(TAG, "onCreate: weixin:" + ShareLoginSDK.isWeiXinInstalled(this));
        Log.d(TAG, "onCreate: weibo:" + ShareLoginSDK.isWeiBoInstalled(this));
        Log.d(TAG, "onCreate: qq:" + ShareLoginSDK.isQQInstalled(this));

        SlConfig cfg = new SlConfig.Builder()
                .debug(true)
                .appName("test app")
                .picTempFile(null)
                .qq(QQ_APPID, QQ_SCOPE)
                .weiBo(WEIBO_APPID, WEIBO_REDIRECT_URL, WEIBO_SCOPE)
                .weiXin(WEIXIN_APPID, WEIXIN_SECRET)
                .build();

        ShareLoginSDK.init(this, cfg);
    }

    /**
     * 初始化一些常量
     */
    protected void initConstant() {
        QQ_APPID = "xxxxxxxxxxxx";
        QQ_SCOPE = "xxxxxxxxxxxx";
        WEIBO_APPID = "xxxxxxxxxxxx";
        WEIBO_REDIRECT_URL = "xxxxxxxxxxxx";
        WEIXIN_APPID = "xxxxxxxxxxxx";
        WEIXIN_SECRET = "xxxxxxxxxxxx";
        WEIBO_SCOPE = "xxxxxxxxxxxx";
    }
}
