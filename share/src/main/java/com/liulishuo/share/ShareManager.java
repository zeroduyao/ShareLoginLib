package com.liulishuo.share;

import com.liulishuo.share.content.ShareContent;
import com.liulishuo.share.qq.SL_QQShareActivity;
import com.liulishuo.share.type.ShareType;
import com.liulishuo.share.weibo.SL_WeiBoShareActivity;
import com.liulishuo.share.weixin.WeiXinShareManager;

import android.app.Activity;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import rx.Observable;
import rx.Subscriber;

import static com.liulishuo.share.type.ShareType.QQ_FRIEND;
import static com.liulishuo.share.type.ShareType.QQ_ZONE;
import static com.liulishuo.share.type.ShareType.WEIBO_TIME_LINE;
import static com.liulishuo.share.type.ShareType.WEIXIN_FRIEND;
import static com.liulishuo.share.type.ShareType.WEIXIN_FRIEND_ZONE;

/**
 * @author Kale
 * @date 2016/3/30
 */
public class ShareManager {

    public static final String KEY_CONTENT = "key_content";

    public static ShareStateListener listener;

    public static void share(@NonNull Activity activity, @ShareType String shareType,
            @NonNull ShareContent shareContent, @Nullable final ShareStateListener listener) {

        ShareManager.listener = listener;
        switch (shareType) {
            case WEIXIN_FRIEND:
            case WEIXIN_FRIEND_ZONE:
                if (ShareBlock.isWeiXinInstalled(activity)) {
                    new WeiXinShareManager().sendShareMsg(activity.getApplicationContext(), shareContent, shareType);
                } else {
                    if (listener != null) {
                        listener.onError("未安装微信");
                    }
                }
                break;
            case WEIBO_TIME_LINE:
                if (ShareBlock.isWeiBoInstalled(activity)) {
                    activity.startActivity(new Intent(activity, SL_WeiBoShareActivity.class)
                            .putExtra(KEY_CONTENT, shareContent));
                    activity.overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                } else {
                    if (listener != null) {
                        listener.onError("未安装微博");
                    }
                }
                break;
            case QQ_FRIEND:
            case QQ_ZONE:
                if (ShareBlock.isQQInstalled(activity)) {
                    activity.startActivity(new Intent(activity, SL_QQShareActivity.class)
                            .putExtra(SL_QQShareActivity.KEY_TO_FRIEND, shareType.equals(QQ_FRIEND))
                            .putExtra(KEY_CONTENT, shareContent));
                    activity.overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                } else {
                    if (listener != null) {
                        listener.onError("未安装QQ");
                    }
                }
                break;
        }
    }

    /**
     * @return 如果返回的是true，表示分享成功了；如果返回false，表示用户取消分享
     */
    public static Observable<Boolean> share(@NonNull final Activity activity, @ShareType final String shareType,
            @NonNull final ShareContent shareContent) {
        return Observable.create(new Observable.OnSubscribe<Boolean>() {
            @Override
            public void call(final Subscriber<? super Boolean> subscriber) {
                share(activity, shareType, shareContent, new ShareStateListener() {
                    @Override
                    public void onSuccess() {
                        subscriber.onNext(true);
                        listener = null;
                    }

                    @Override
                    public void onCancel() {
                        subscriber.onNext(false);
                        listener = null;
                    }

                    @Override
                    public void onError(String msg) {
                        subscriber.onError(new Throwable(msg));
                        listener = null;
                    }
                });
            }
        });
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
