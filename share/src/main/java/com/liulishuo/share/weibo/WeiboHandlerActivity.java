package com.liulishuo.share.weibo;

import com.liulishuo.share.base.share.ShareContent;
import com.sina.weibo.sdk.api.share.BaseResponse;
import com.sina.weibo.sdk.api.share.IWeiboHandler;
import com.sina.weibo.sdk.constant.WBConstants;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.CallSuper;
import android.text.TextUtils;

/**
 * @author Jack Tony
 * @date 2015/10/14
 */
public class WeiBoHandlerActivity extends Activity implements IWeiboHandler.Response {

    protected static final int RESULT_CODE = 0X31;

    private static final String KEY_SHARE_CONTENT = "KEY_SHARE_CONTENT";

    protected static final String KEY_RESULT_BUNDLE = "KEY_RESULT_BUNDLE";

    private boolean mIsFirstTime = true;

    private WeiBoRealShareManager mSM;

    public static Bundle sendShareContent(ShareContent content) {
        Bundle bundle = new Bundle();
        bundle.putParcelable(KEY_SHARE_CONTENT, content);
        return bundle;
    }

    @CallSuper
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mSM = new WeiBoRealShareManager(this);
        // 当 Activity 被重新初始化时（该 Activity 处于后台时，可能会由于内存不足被杀掉了），
        // 需要调用 {@link IWeiboShareAPI#handleWeiboResponse} 来接收微博客户端返回的数据。
        // 执行成功，返回 true，并调用 {@link IWeiboHandler.Response#onResponse}；
        // 失败返回 false，不调用上述回调
        if (savedInstanceState != null) {
            mSM.getSinaAPI().handleWeiboResponse(getIntent(), this);
        }
        Intent intent;
        if ((intent = getIntent()) != null) {
            if (TextUtils.equals(intent.getAction(), WeiBoShareManager.ACTION_WEIBO_SHARE)) {
                ShareContent shareContent = intent.getParcelableExtra(KEY_SHARE_CONTENT);
                mSM.share(this, shareContent);
            }
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
            Bundle bundle = new Bundle();
            // 这里的key参考自：BaseResponse
            bundle.putInt("_weibo_resp_errcode", WBConstants.ErrorCode.ERR_CANCEL);
            setResult(RESULT_CODE, new Intent().putExtra(KEY_RESULT_BUNDLE, bundle));
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
        mSM.getSinaAPI().handleWeiboResponse(intent, this);  // 当前应用唤起微博分享后，返回当前应用
    }

    @CallSuper
    @Override
    public void onResponse(BaseResponse baseResponse) {
        Bundle bundle = new Bundle();
        baseResponse.toBundle(bundle);
        setResult(RESULT_CODE, new Intent().putExtra(KEY_RESULT_BUNDLE, bundle));
        finish();
    }

}
