package com.liulishuo.engzo;

import java.util.Arrays;

import android.app.Application;

import com.liulishuo.demo.BuildConfig;
import com.liulishuo.demo.R;
import kale.sharelogin.ShareLoginLib;
import kale.sharelogin.qq.QQPlatform;
import kale.sharelogin.weibo.WeiBoPlatform;
import kale.sharelogin.weixin.WeiXinPlatform;

/**
 * @author Kale
 * @date 2016/8/11
 */
public class AppApplication extends Application {

    private static final String TAG = "Application";

    protected static String qq_app_id, qq_scope, // 应用需要获得哪些API的权限，由“，”分隔。例如：SCOPE = “get_user_info,add_t”；所有权限用“all”
            weibo_app_key, weibo_scope, weibo_redirect_url,
            weixin_app_id, weixin_secret;

    @Override
    public void onCreate() {
        super.onCreate();

        // 初始化常量
        initConstant();

        // 初始该库的基础常量
        ShareLoginLib.init(this, getString(R.string.app_name), null, BuildConfig.DEBUG);

        // 初始化第三方平台的信息
        ShareLoginLib.initPlatforms(
                MapBuilder.of(
                        QQPlatform.KEY_APP_ID, qq_app_id,
                        QQPlatform.KEY_SCOPE, qq_scope,

                        WeiBoPlatform.KEY_APP_KEY, weibo_app_key,
                        WeiBoPlatform.KEY_SCOPE, weibo_scope,
                        WeiBoPlatform.KEY_REDIRECT_URL, weibo_redirect_url,

                        WeiXinPlatform.KEY_APP_ID, weixin_app_id,
                        WeiXinPlatform.KEY_SECRET, weixin_secret
                ),
                Arrays.asList(
                        QQPlatform.class,
                        WeiBoPlatform.class,
                        WeiXinPlatform.class)
        );

        boolean installed = ShareLoginLib.isAppInstalled(this, QQPlatform.class);
    }

    /**
     * 初始化一些常量，这里需要替换成自己的值
     */
    protected void initConstant() {
        qq_app_id = "xxxxxxxxxxxx";
        qq_scope = "get_user_info,"
                + "get_simple_userinfo,"
                + "add_share,"
                + "add_topic,"
                + "add_pic_t";

        weibo_app_key = "xxxxxxxxxxxx";
        weibo_redirect_url = "xxxxxxxxxxxx";
        weibo_scope = "friendships_groups_read,"
                + "friendships_groups_write,"
                + "statuses_to_me_read,"
                + "follow_app_official_microblog";

        weixin_app_id = "xxxxxxxxxxxx";
        weixin_secret = "xxxxxxxxxxxx";
    }
}
