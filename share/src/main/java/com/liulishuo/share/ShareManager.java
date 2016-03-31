package com.liulishuo.share;

import com.liulishuo.share.model.shareContent.ShareContent;
import com.liulishuo.share.qq.SL_QQShareActivity;
import com.liulishuo.share.type.ShareType;
import com.liulishuo.share.weibo.SL_WeiBoShareActivity;
import com.liulishuo.share.weixin.WeiXinShareManager;

import android.app.Activity;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.widget.Toast;

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
                if (!ShareBlock.isQQInstalled(activity)) {
                    Toast.makeText(activity, "请先安装QQ哦~", Toast.LENGTH_SHORT).show();
                    return;
                }
                activity.startActivity(new Intent(activity, SL_QQShareActivity.class)
                        .putExtra(SL_QQShareActivity.KEY_TO_FRIEND, shareType == ShareType.QQ_FRIEND)
                        .putExtra(KEY_CONTENT, shareContent));
                activity.overridePendingTransition(android.R.anim.fade_in,android.R.anim.fade_out);
                break;
            case WEIBO_TIME_LINE:
                if (!ShareBlock.isWeiBoInstalled(activity)) {
                    Toast.makeText(activity, "请先安装微博哦~", Toast.LENGTH_SHORT).show();
                    return;
                }
                activity.startActivity(new Intent(activity, SL_WeiBoShareActivity.class)
                        .putExtra(KEY_CONTENT, shareContent));
                activity.overridePendingTransition(android.R.anim.fade_in,android.R.anim.fade_out);
                break;
            case WEIXIN_FRIEND:
            case WEIXIN_FRIEND_ZONE:
                if (!ShareBlock.isWeiXinInstalled(activity)) {
                    Toast.makeText(activity, "请安装微信哦~", Toast.LENGTH_SHORT).show();
                    return;
                }
                new WeiXinShareManager().sendShareMsg(activity.getApplicationContext(), shareContent, shareType);
                break;
        }
    }

    public static void recycle() {
        listener = null;
    }

    public interface ShareStateListener {
    
        void onSuccess();
    
        void onCancel();
    
        void onError(String msg);
    }
}
