package com.liulishuo.engzo;

import com.liulishuo.share.ShareBlock;

import android.app.Application;
import android.util.Log;

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

        Log.d(TAG, "onCreate: weixin:" + ShareBlock.isWeiXinInstalled(this));
        Log.d(TAG, "onCreate: weibo:" + ShareBlock.isWeiBoInstalled(this));
        Log.d(TAG, "onCreate: qq:" + ShareBlock.isQQInstalled(this));

        ShareBlock.getInstance()
                .debug(true)
                .appName("测试应用")
                .picTempFile(this)
                .qq(QQ_APPID, QQ_SCOPE)
                .weiXin(WEIXIN_APPID, WEIXIN_SECRET)
                .weiBo(WEIBO_APPID, WEIBO_REDIRECT_URL, WEIBO_SCOPE);
    }

    protected void initConstant() {
        QQ_APPID =              "xxxxxxxxxxxx";
        QQ_SCOPE =              "xxxxxxxxxxxx";
        WEIBO_APPID =           "xxxxxxxxxxxx";
        WEIBO_REDIRECT_URL =    "xxxxxxxxxxxx";
        WEIXIN_APPID =          "xxxxxxxxxxxx";
        WEIXIN_SECRET =         "xxxxxxxxxxxx";
        WEIBO_SCOPE =           "xxxxxxxxxxxx";
    }
}
