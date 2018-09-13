package com.liulishuo.share.qq;

import java.util.List;
import java.util.Locale;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import com.liulishuo.share.LoginListener;
import com.liulishuo.share.ShareListener;
import com.liulishuo.share.ShareLoginLib;
import com.liulishuo.share.content.ShareContent;
import com.liulishuo.share.content.ShareContentType;
import com.liulishuo.share.utils.IPlatform;
import com.tencent.tauth.IUiListener;
import com.tencent.tauth.Tencent;

/**
 * @author Kale
 * @date 2018/9/11
 */
public class QQPlatform implements IPlatform {

    public static final String KEY_APP_ID = "qq_key_app_id";

    public static final String KEY_SCOPE = "qq_key_scope";

    // ---------------------------------------------------------------

    public static final String LOGIN = "qq_login";

    public static final String ZONE = "qq_zone", FRIEND = "QQ_FRIEND";

    private IUiListener uiListener;

    @Override
    public String[] getSupportedTypes() {
        return new String[]{LOGIN, ZONE, FRIEND};
    }

    @Override
    public boolean isAppInstalled(@NonNull Context context) {
        PackageManager pm = context.getApplicationContext().getPackageManager();
        if (pm == null) {
            return false;
        }
        List<PackageInfo> packages = pm.getInstalledPackages(0);
        for (PackageInfo info : packages) {
            String name = info.packageName.toLowerCase(Locale.ENGLISH);
            if ("com.tencent.mobileqq".equals(name)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void checkEnvironment(Context context, @NonNull String type, @ShareContentType int shareContentType) {
        // 1. 检测是否已经初始化
        if (TextUtils.isEmpty(ShareLoginLib.getValue(KEY_APP_ID))) {
            throw new IllegalArgumentException("appId未被初始化");
        }

        // 2. 检测分享的内容是否合法
        if (!type.equals(LOGIN)) {
            // 如果是分享到qq好友
            if (type.equals(FRIEND) && shareContentType == ShareContentType.TEXT) {
                // 文档中说：本接口支持3种模式，每种模式的参数设置不同"
                // （1） 分享图文消息；（2） 分享纯图片；（3） 分享音乐，即不包含纯文本
                throw new IllegalArgumentException("目前不支持分享纯文本信息给QQ好友");
            }

            // 分享到qq空间
            if (type.equals(ZONE)) {
                // 分享到QQ空间支持两种模式，(1)图文分享；（2)发表说说、视频或上传图片
                // 即qq空间不支持纯文字、纯图片

                // 注意：QZone接口暂不支持发送多张图片的能力，若传入多张图片，则会自动选入第一张图片作为预览图。多图的能力将会在以后支持。
                if (shareContentType == ShareContentType.TEXT || shareContentType == ShareContentType.PIC) {
                    throw new IllegalArgumentException("QQ空间目前只支持分享图文信息");
                }
            }
        }
    }

    @Override
    public void doLogin(@NonNull Activity activity, @NonNull LoginListener listener) {
        Tencent tencent = Tencent.createInstance(ShareLoginLib.getValue(KEY_APP_ID), activity.getApplicationContext());

        if (tencent.isSessionValid()) {
            tencent.logout(activity);
            return;
        }

        uiListener = new LoginHelper.AbsUiListener(listener) {
            @Override
            public void onComplete(Object obj) {
                LoginHelper.parseLoginResp(activity, obj, listener);
            }
        };

        tencent.login(activity, ShareLoginLib.getValue(KEY_SCOPE), uiListener);
    }

    @Override
    public void doShare(Activity activity, String shareType, @NonNull ShareContent shareContent, @NonNull ShareListener listener) {
        Tencent tencent = Tencent.createInstance(ShareLoginLib.getValue(KEY_APP_ID), activity.getApplicationContext());

        uiListener = new LoginHelper.AbsUiListener(listener) {
            @Override
            public void onComplete(Object o) {
                listener.onSuccess();
            }
        };

        if (shareType.equals(FRIEND)) {
            Bundle bundle = new ShareHelper().createQQFriendBundle(shareContent);
            tencent.shareToQQ(activity, bundle, uiListener);
        } else {
            Bundle bundle = new ShareHelper().createQZoneBundle(shareContent);
            tencent.shareToQzone(activity, bundle, uiListener);
        }
    }

    @Override
    public void onResponse(@NonNull Activity activity, @Nullable Intent data) {
        if (uiListener != null) {
            Tencent.handleResultData(data, uiListener);
        }
    }

}