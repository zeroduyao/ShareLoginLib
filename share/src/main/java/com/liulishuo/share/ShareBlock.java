package com.liulishuo.share;

import com.tencent.mm.sdk.modelmsg.SendMessageToWX;

import android.app.Application;
import android.support.annotation.IntDef;
import android.support.annotation.NonNull;
import android.text.TextUtils;

import java.io.File;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Created by echo on 5/18/15.
 */
public class ShareBlock {

    /**
     * 发送给微信好友
     */
    public static final int WEIXIN_FRIEND = SendMessageToWX.Req.WXSceneSession;

    /**
     * 发送到朋友圈
     */
    public static final int WEIXIN_FRIEND_ZONE = SendMessageToWX.Req.WXSceneTimeline;

    /**
     * 发送到QQ空间
     */
    public static final int QQ_ZONE = 2;

    /**
     * 发送给QQ好友
     */
    public static final int QQ_FRIEND = 3;

    /**
     * 微博分享的类型（就一种，微博时间线）
     */
    public static final int WEIBO_TIME_LINE = 4;

    @Retention(RetentionPolicy.SOURCE)
    @IntDef({WEIXIN_FRIEND, WEIXIN_FRIEND_ZONE, QQ_ZONE, QQ_FRIEND, WEIBO_TIME_LINE})
    public @interface ShareType {
    }

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

    public ShareBlock initAppName(@NonNull String appName) {
        this.appName = appName;
        return this;
    }

    public String pathTemp = null;

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

    public String weiXinAppId;

    public String weiXinSecret;

    /**
     * init wechat config
     */
    public ShareBlock initWeiXin(@NonNull String weiXinAppId, @NonNull String weiXinSecret) {
        this.weiXinAppId = weiXinAppId;
        this.weiXinSecret = weiXinSecret;
        return this;
    }

    /**
     * init weibo config
     */
    public String weiboAppId;

    public String weiboRedirectUrl;

    public String weiboScope;

    public ShareBlock initWeibo(@NonNull String weiboAppId, @NonNull String redirectUrl, @NonNull String scope) {
        this.weiboAppId = weiboAppId;
        weiboRedirectUrl = redirectUrl;
        weiboScope = scope;
        return this;
    }

    /**
     * init QQ config
     */
    public String QQAppId;

    public String QQScope;

    public ShareBlock initQQ(@NonNull String qqAppId, @NonNull String scope) {
        QQAppId = qqAppId;
        QQScope = scope;
        return this;
    }

}
