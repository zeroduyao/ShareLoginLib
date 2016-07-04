package com.liulishuo.share;

import com.sina.weibo.sdk.api.share.IWeiboShareAPI;
import com.sina.weibo.sdk.api.share.WeiboShareSDK;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.WXAPIFactory;

import android.app.Application;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.text.TextUtils;

import java.io.File;
import java.util.List;
import java.util.Locale;

/**
 * Created by echo on 5/18/15.
 */
public class ShareBlock {

    private static ShareBlock mInstance;

    public static ShareBlock getInstance() {
        if (mInstance == null) {
            mInstance = new ShareBlock();
        }
        return mInstance;
    }

    private ShareBlock() {
    }

    public boolean debug = false;

    public String appName;

    public String pathTemp = null;

    public String weiXinAppId;

    public String weiXinSecret;

    public String weiBoAppId;

    public String weiBoRedirectUrl;

    public String weiBoScope;

    public String qqAppId;

    public String qqScope;

    public ShareBlock appName(@NonNull String appName) {
        this.appName = appName;
        return this;
    }

    /**
     * 初始化临时文件地址，这里仅仅是为了qq分享用的。
     * 这里必须用外部存储器，因为qq会读取这个目录下的图片文件
     */
    public ShareBlock picTempFile(Application application) {
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

    public ShareBlock debug(boolean debug) {
        this.debug = debug;
        return this;
    }

    public ShareBlock weiXin(@NonNull String weiXinAppId, @NonNull String weiXinSecret) {
        this.weiXinAppId = weiXinAppId;
        this.weiXinSecret = weiXinSecret;
        return this;
    }

    public ShareBlock weiBo(@NonNull String weiBoAppId, @NonNull String redirectUrl, @NonNull String scope) {
        this.weiBoAppId = weiBoAppId;
        weiBoRedirectUrl = redirectUrl;
        weiBoScope = scope;
        return this;
    }

    public ShareBlock qq(@NonNull String qqAppId, @NonNull String scope) {
        this.qqAppId = qqAppId;
        this.qqScope = scope;
        return this;
    }

    ///////////////////////////////////////////////////////////////////////////
    // 判断第三方客户端是否安装
    ///////////////////////////////////////////////////////////////////////////

    public static boolean isWeiXinInstalled(Context context) {
        IWXAPI api = WXAPIFactory.createWXAPI(context, ShareBlock.getInstance().weiXinAppId, true);
        return api.isWXAppInstalled();
    }

    public static boolean isWeiBoInstalled(@NonNull Context context) {
        IWeiboShareAPI shareAPI = WeiboShareSDK.createWeiboAPI(context, ShareBlock.getInstance().weiBoAppId);
        return shareAPI.isWeiboAppInstalled();
    }

    public static boolean isQQInstalled(@NonNull Context context) {
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

}
