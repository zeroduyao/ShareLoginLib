package com.liulishuo.share.weibo;

import com.liulishuo.share.ShareBlock;
import com.liulishuo.share.base.share.IShareManager;
import com.liulishuo.share.base.shareContent.ShareContent;
import com.liulishuo.share.base.share.ShareStateListener;
import com.sina.weibo.sdk.api.share.BaseResponse;
import com.sina.weibo.sdk.api.share.IWeiboShareAPI;
import com.sina.weibo.sdk.api.share.SendMessageToWeiboResponse;
import com.sina.weibo.sdk.api.share.WeiboShareSDK;
import com.sina.weibo.sdk.constant.WBConstants;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;

/**
 * @author Jack Tony
 * @date 2015/10/14
 */
public class WeiboShareManager implements IShareManager {

    protected static final String ACTION_WEIBO_SHARE = "ACTION_WEIBO_SHARE";

    private Activity mActivity;

    private static final int REQUEST_CODE = 0xff31;

    private ShareStateListener mShareStateListener;

    public WeiboShareManager(Activity activity) {
        mActivity = activity;
    }

    @Override
    public void share(ShareContent shareContent, @ShareBlock.ShareType int shareType, @NonNull ShareStateListener listener) {
        mShareStateListener = listener;
        mActivity.startActivityForResult(
                new Intent(mActivity, WeiboHandlerActivity.class)
                        .setAction(ACTION_WEIBO_SHARE)
                        .putExtras(WeiboHandlerActivity.sendShareContent(shareContent))
                , REQUEST_CODE);
    }

    public void handlerOnActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CODE && resultCode == WeiboHandlerActivity.RESULT_CODE) {
            BaseResponse response = new SendMessageToWeiboResponse();
            response.fromBundle(data.getBundleExtra(WeiboHandlerActivity.KEY_RESULT_BUNDLE));

            if (mShareStateListener != null) {
                switch (response.errCode) {
                    case WBConstants.ErrorCode.ERR_OK:
                        mShareStateListener.onSuccess();
                        break;

                    case WBConstants.ErrorCode.ERR_CANCEL:
                        mShareStateListener.onCancel();
                        break;

                    case WBConstants.ErrorCode.ERR_FAIL:
                        mShareStateListener.onError(response.errMsg);
                        break;
                }
            }
        }
    }

    public static boolean isWeiBoInstalled(@NonNull Context context) {
        IWeiboShareAPI shareAPI = WeiboShareSDK.createWeiboAPI(context, ShareBlock.getInstance().weiboAppId);
        return shareAPI.isWeiboAppInstalled();
    }
}
