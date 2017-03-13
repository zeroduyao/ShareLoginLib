package com.liulishuo.share;

import android.app.Activity;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.liulishuo.share.activity.SL_QQHandlerActivity;
import com.liulishuo.share.activity.SL_WeiBoHandlerActivity;
import com.liulishuo.share.activity.SL_WeiXinHandlerActivity;
import com.liulishuo.share.content.ShareContent;
import com.liulishuo.share.type.ShareType;

import static com.liulishuo.share.type.ShareType.QQ_FRIEND;
import static com.liulishuo.share.type.ShareType.QQ_ZONE;
import static com.liulishuo.share.type.ShareType.WEIBO_TIME_LINE;
import static com.liulishuo.share.type.ShareType.WEIXIN_FAVORITE;
import static com.liulishuo.share.type.ShareType.WEIXIN_FRIEND;
import static com.liulishuo.share.type.ShareType.WEIXIN_FRIEND_ZONE;

/**
 * @author Kale
 * @date 2016/3/30
 */
public class ShareManager {

    public static final String KEY_CONTENT = "KEY_CONTENT";

    public static ShareStateListener listener;

    public static void share(@NonNull final Activity activity, @ShareType final String shareType,
            @NonNull final ShareContent shareContent, @Nullable final ShareStateListener listener) {
        ShareManager.listener = listener;
        switch (shareType) {
            case QQ_FRIEND:
            case QQ_ZONE:
                if (ShareBlock.isQQInstalled(activity)) {
                    activity.startActivity(
                            new Intent(activity, SL_QQHandlerActivity.class)
                                    .putExtra(SL_QQHandlerActivity.KEY_TO_FRIEND, shareType.equals(QQ_FRIEND))
                                    .putExtra(KEY_CONTENT, shareContent)
                                    .putExtra(ShareBlock.KEY_IS_LOGIN_TYPE, false)
                    );
                    activity.overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                } else if (listener != null) {
                    listener.onError("未安装QQ");
                }
                break;
            case WEIBO_TIME_LINE:
                if (ShareBlock.isWeiBoInstalled(activity)) {
                    activity.startActivity(
                            new Intent(activity, SL_WeiBoHandlerActivity.class)
                                    .putExtra(KEY_CONTENT, shareContent)
                                    .putExtra(ShareBlock.KEY_IS_LOGIN_TYPE, false)
                    );
                    activity.overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                } else if (listener != null) {
                    listener.onError("未安装微博");
                }
                break;
            case WEIXIN_FRIEND:
            case WEIXIN_FRIEND_ZONE:
            case WEIXIN_FAVORITE:
                if (ShareBlock.isWeiXinInstalled(activity)) {
                    new SL_WeiXinHandlerActivity().sendShareMsg(activity.getApplicationContext(), shareContent, shareType);
                } else if (listener != null) {
                    listener.onError("未安装微信");
                }
                break;
        }
    }

    public interface ShareStateListener {

        void onSuccess();

        void onCancel();

        void onError(String msg);
    }

    public static void recycle() {
        listener = null;
    }
}
