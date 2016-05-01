package com.liulishuo.share;

import com.liulishuo.share.qq.SL_QQLoginActivity;
import com.liulishuo.share.type.LoginType;
import com.liulishuo.share.weibo.SL_WeiBoLoginActivity;
import com.liulishuo.share.weixin.WeiXinLoginManager;

import android.app.Activity;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.widget.Toast;

import static com.liulishuo.share.type.LoginType.*;

/**
 * @author Kale
 * @date 2016/3/30
 */
public class LoginManager {

    public static
    @Nullable
    LoginListener listener;

    public static void login(@NonNull Activity activity, @LoginType String type, @Nullable LoginListener listener) {
        LoginManager.listener = listener;
        switch (type) {
            case WEIXIN:
                if (!ShareBlock.isWeiXinInstalled(activity)) {
                    Toast.makeText(activity, "请安装微信哦~", Toast.LENGTH_SHORT).show();
                    return;
                }
                new WeiXinLoginManager().login(activity.getApplicationContext());
                break;
            case WEIBO:
                activity.startActivity(new Intent(activity, SL_WeiBoLoginActivity.class));
                activity.overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                break;
            case QQ:
                if (!ShareBlock.isQQInstalled(activity)) {
                    Toast.makeText(activity, "请先安装QQ哦~", Toast.LENGTH_SHORT).show();
                    return;
                }
                activity.startActivity(new Intent(activity, SL_QQLoginActivity.class));
                activity.overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                break;
        }
    }

    public static void recycle() {
        listener = null;
    }

    public interface LoginListener {

        void onSuccess(String accessToken, String uId, long expiresIn, @Nullable String wholeData);

        void onError(String msg);

        void onCancel();
    }

}
