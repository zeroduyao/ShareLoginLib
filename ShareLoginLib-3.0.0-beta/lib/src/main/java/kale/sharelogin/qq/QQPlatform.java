package kale.sharelogin.qq;

import java.util.List;
import java.util.Locale;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import com.tencent.tauth.IUiListener;
import com.tencent.tauth.Tencent;

import kale.sharelogin.IPlatform;
import kale.sharelogin.LoginListener;
import kale.sharelogin.ShareListener;
import kale.sharelogin.ShareLoginLib;
import kale.sharelogin.content.ShareContent;
import kale.sharelogin.content.ShareContentType;

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
        }
    }

    @Override
    public void doLogin(@NonNull Activity activity, @NonNull LoginListener listener) {
        Tencent tencent = getTencent(activity);

        if (tencent.isSessionValid()) {
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

    /**
     * QQ分享并不需要提前登录
     */
    @Override
    public void doShare(Activity activity, String shareType, @NonNull ShareContent shareContent, @NonNull ShareListener listener) {
        uiListener = new LoginHelper.AbsUiListener(listener) {
            @Override
            public void onComplete(Object o) {
                listener.onSuccess();
            }
        };

        Tencent tencent = getTencent(activity);

        if (shareType.equals(FRIEND)) {
            tencent.shareToQQ(activity, ShareHelper.qqFriendBundle(shareContent), uiListener);
        } else {
            // 因为空间不支持分享单个文字和图片，在这里对于单个图片做了额外的处理，让其走发布说说的api
            if (shareContent.getType() == ShareContentType.TEXT || shareContent.getType() == ShareContentType.PIC) {
                tencent.publishToQzone(activity, ShareHelper.publishToQzoneBundle(shareContent), uiListener);
            } else {
                tencent.shareToQzone(activity, ShareHelper.zoneBundle(shareContent), uiListener);
            }
        }
    }

    @Override
    public void onResponse(@NonNull Activity activity, @Nullable Intent data) {
        if (uiListener != null) {
            Tencent.handleResultData(data, uiListener);
        }
    }

    @Override
    public void getUserInfo(Context context, String accessToken, String uid, LoginListener listener) {
        LoginHelper.getUserInfo(context, accessToken, uid, listener);
    }

    /**
     * 传入应用程序的全局context，可通过activity的getApplicationContext方法获取
     */
    public static Tencent getTencent(Context context) {
        return Tencent.createInstance(ShareLoginLib.getValue(KEY_APP_ID), context.getApplicationContext());
    }

}