package com.liulishuo.share;

import com.liulishuo.share.model.shareContent.ShareContent;
import com.liulishuo.share.qq.SL_QQShareActivity;
import com.liulishuo.share.type.ShareType;
import com.liulishuo.share.weibo.SL_WeiBoShareActivity;
import com.liulishuo.share.weixin.WeiXinShareManager;
import com.sina.weibo.sdk.api.share.IWeiboShareAPI;
import com.sina.weibo.sdk.api.share.WeiboShareSDK;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.WXAPIFactory;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.widget.Toast;

import java.util.List;
import java.util.Locale;

/**
 * @author Kale
 * @date 2016/3/30
 */
public class ShareManager {

    public static final String KEY_CONTENT = "key_content";

    public static ShareStateListener listener;

    public static void share(@NonNull Activity activity, @NonNull ShareContent shareContent, ShareType shareType,
            @Nullable final ShareStateListener listener) {
        ShareManager.listener = listener;
        switch (shareType) {
            case QQ_FRIEND:
            case QQ_ZONE:
                if (!LoginManager.isQQInstalled(activity)) {
                    Toast.makeText(activity, "请先安装QQ哦~", Toast.LENGTH_SHORT).show();
                    return;
                }
                activity.startActivity(new Intent(activity, SL_QQShareActivity.class)
                        .putExtra(SL_QQShareActivity.KEY_TO_FRIEND, shareType == ShareType.QQ_FRIEND)
                        .putExtra(KEY_CONTENT, shareContent));
                break;
            case WEIBO_TIME_LINE:
                if (!ShareManager.isWeiBoInstalled(activity)) {
                    Toast.makeText(activity, "请先安装微博哦~", Toast.LENGTH_SHORT).show();
                    return;
                }
                activity.startActivity(new Intent(activity, SL_WeiBoShareActivity.class)
                        .putExtra(KEY_CONTENT, shareContent));
                break;
            case WEIXIN_FRIEND:
            case WEIXIN_FRIEND_ZONE:
                if (!LoginManager.isWeiXinInstalled(activity)) {
                    Toast.makeText(activity, "请安装微信哦~", Toast.LENGTH_SHORT).show();
                    return;
                }
                new WeiXinShareManager().sendShareMsg(activity.getApplicationContext(), shareContent, shareType);
                break;
        }
    }

    /**
     * @return 是否已经安装微信
     */
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
        if (pm== null) {
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

    public interface ShareStateListener {
    
        void onSuccess();
    
        void onCancel();
    
        void onError(String msg);
    }
}
