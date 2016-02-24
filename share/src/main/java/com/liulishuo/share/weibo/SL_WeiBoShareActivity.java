package com.liulishuo.share.weibo;

import com.liulishuo.share.ShareBlock;
import com.sina.weibo.sdk.api.share.BaseResponse;
import com.sina.weibo.sdk.api.share.IWeiboHandler;
import com.sina.weibo.sdk.api.share.IWeiboShareAPI;
import com.sina.weibo.sdk.api.share.WeiboShareSDK;
import com.sina.weibo.sdk.constant.WBConstants;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.CallSuper;

/**
 * @author Jack Tony
 * @date 2015/10/14
 */
public class SL_WeiBoShareActivity extends Activity implements IWeiboHandler.Response {

    private boolean mIsFirstTime = true;

    @CallSuper
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState == null) {
            // 防止不保留活动情况下activity被重置后直接进行操作的情况
            WeiboShareManager.sendShareMsg(this);
        }
        
        // 当 Activity 被重新初始化时（该 Activity 处于后台时，可能会由于内存不足被杀掉了），
        // 需要调用 {@link IWeiboShareAPI#handleWeiboResponse} 来接收微博客户端返回的数据。
        // 执行成功，返回 true，并调用 {@link IWeiboHandler.Response#onResponse}；
        // 失败返回 false，不调用上述回调
        if (savedInstanceState != null) {
            IWeiboShareAPI API = WeiboShareSDK.createWeiboAPI(getApplicationContext(),
                    ShareBlock.getInstance().weiboAppId);
            
            API.handleWeiboResponse(getIntent(), this);
        }
    }

    /**
     * 因为微博客户端在用户取消分享后，用户点击保存到草稿箱后就不能接收到回调。
     * 因此，在这里必须进行强制关闭，不能依赖回调来关闭。
     */
    @CallSuper
    @Override
    protected void onResume() {
        super.onResume();
        if (mIsFirstTime) {
            mIsFirstTime = false;
        } else {
            // 这里处理保存到草稿箱的逻辑
            WeiboShareManager.parseShareResp(WBConstants.ErrorCode.ERR_CANCEL, "weibo cancel");
            finish();
        }
    }

    @CallSuper
    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        // 从当前应用唤起微博并进行分享后，返回到当前应用时，需要在此处调用该函数
        // 来接收微博客户端返回的数据；执行成功，返回 true，并调用
        // {@link IWeiboHandler.Response#onResponse}；失败返回 false，不调用上述回调
        IWeiboShareAPI API = WeiboShareSDK.createWeiboAPI(getApplicationContext(), ShareBlock.getInstance().weiboAppId);
        API.handleWeiboResponse(intent, this); // 当前应用唤起微博分享后，返回当前应用
    }

    @CallSuper
    @Override
    public void onResponse(BaseResponse baseResponse) {
        WeiboShareManager.parseShareResp(baseResponse.errCode, baseResponse.errMsg);
        finish();
    }

}
