package com.liulishuo.engzo;

import java.util.Map;

import android.app.Application;
import android.util.Log;

import com.liulishuo.share.ShareLoginLib;
import com.liulishuo.share.qq.QQPlatform;
import com.liulishuo.share.weibo.WeiBoPlatform;
import com.liulishuo.share.weixin.WeiXinPlatform;

/**
 * @author Kale
 * @date 2016/8/11
 */
public class AppApplication extends Application {

    private static final String TAG = "Application";

    protected static String qq_app_id, qq_scope,
            weibo_app_key, weibo_scope, weibo_redirect_url,
            weixin_app_id, weixin_secret;

    @Override
    public void onCreate() {
        super.onCreate();

        initConstant();

        ShareLoginLib.initParams(this, "AppName", null);
        ShareLoginLib.debug(true);

        Map<String, String> map = new android.support.v4.util.ArrayMap<>();
        map.put(QQPlatform.KEY_APP_ID, qq_app_id);
        map.put(QQPlatform.KEY_SCOPE, qq_scope);

        map.put(WeiBoPlatform.KEY_APP_KEY, weibo_app_key);
        map.put(WeiBoPlatform.KEY_SCOPE, weibo_scope);
        map.put(WeiBoPlatform.KEY_REDIRECT_URL, weibo_redirect_url);

        map.put(WeiXinPlatform.KEY_APP_ID, weixin_app_id);
        map.put(WeiXinPlatform.KEY_SECRET_KEY, weixin_secret);
        
        ShareLoginLib.initPlatforms(map, QQPlatform.class, WeiBoPlatform.class, WeiXinPlatform.class);

        Log.d(TAG, "onCreate: weixin:" + ShareLoginLib.isWeiXinInstalled(this));
        Log.d(TAG, "onCreate: weibo:" + ShareLoginLib.isWeiBoInstalled(this));
        Log.d(TAG, "onCreate: qq:" + ShareLoginLib.isQQInstalled(this));
    }

    /**
     * 初始化一些常量
     */
    protected void initConstant() {
        qq_app_id = "xxxxxxxxxxxx";
        qq_scope = "xxxxxxxxxxxx";
        weibo_app_key = "xxxxxxxxxxxx";
        weibo_redirect_url = "xxxxxxxxxxxx";
        weixin_app_id = "xxxxxxxxxxxx";
        weixin_secret = "xxxxxxxxxxxx";
        weibo_scope = "xxxxxxxxxxxx";
    }
}
