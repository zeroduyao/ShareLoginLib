package com.liulishuo.share.weibo;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import com.liulishuo.share.utils.IPlatform;
import com.liulishuo.share.LoginListener;
import com.liulishuo.share.ShareListener;
import com.liulishuo.share.ShareLoginLib;
import com.liulishuo.share.content.ShareContent;
import com.liulishuo.share.utils.EventHandlerActivity;
import com.sina.weibo.sdk.api.share.BaseRequest;
import com.sina.weibo.sdk.api.share.IWeiboHandler;
import com.sina.weibo.sdk.api.share.IWeiboShareAPI;
import com.sina.weibo.sdk.api.share.SendMultiMessageToWeiboRequest;
import com.sina.weibo.sdk.api.share.WeiboShareSDK;
import com.sina.weibo.sdk.auth.AuthInfo;
import com.sina.weibo.sdk.auth.sso.SsoHandler;

/**
 * @author Kale
 * @date 2018/9/10
 */
public class WeiBoPlatform implements IPlatform {

    public static final String KEY_APP_KEY = "weibo_key_app_key";

    public static final String KEY_REDIRECT_URL = "key_redirect_url";

    public static final String KEY_SCOPE = "key_scope";

    // ---------------------------------------------------------------

    public static final String LOGIN = "weibo_login";

    public static final String TIME_LINE = "weibo_time_line";

    private IWeiboHandler.Response response;

    private SsoHandler ssoHandler;

    @Override
    public String[] getSupportedTypes() {
        return new String[]{LOGIN, TIME_LINE};
    }

    @Override
    public boolean isAppInstalled(@NonNull Context context) {
        return getApi(context).isWeiboAppInstalled();
    }

    @Override
    public void checkEnvironment(Context context, String type, int contentType) {
        if (TextUtils.isEmpty(ShareLoginLib.getValue(KEY_APP_KEY))) {
            throw new IllegalArgumentException("微博的appId未被初始化，当前为空");
        }
    }

    @Override
    public void doLogin(@NonNull Activity activity, @Nullable LoginListener listener) {
        // 注意：SsoHandler 仅当 SDK 支持 SSO 时有效
        ssoHandler = new SsoHandler(activity,
                new AuthInfo(
                        activity.getApplicationContext(),
                        ShareLoginLib.getValue(KEY_APP_KEY),
                        ShareLoginLib.getValue(KEY_REDIRECT_URL),
                        ShareLoginLib.getValue(KEY_SCOPE)
                )
        );

        ssoHandler.authorize(new LoginHelper.AbsAuthListener(listener) {
            @Override
            public void onComplete(Bundle bundle) {
                LoginHelper.parseLoginResp(activity,bundle, listener);
            }
        });
    }

    @Override
    public void doShare(@NonNull Activity activity, String shareType, @NonNull ShareContent shareContent, @Nullable ShareListener listener) {
        // 建立请求体
        SendMultiMessageToWeiboRequest request = new SendMultiMessageToWeiboRequest();
        request.transaction = String.valueOf(System.currentTimeMillis());// 用transaction唯一标识一个请求
        request.multiMessage = new ShareHelper().createShareObject(shareContent);

        sendRequest(activity, request, resp -> {
            ShareHelper.parseShareResp(resp, listener);
            activity.finish();
        });
    }

    private void sendRequest(Activity activity, BaseRequest request, IWeiboHandler.Response resp) {
        response = resp;
        IWeiboShareAPI api = getApi(activity);
        api.registerApp(); // 将应用注册到微博客户端
        api.sendRequest(activity, request);
    }

    @Override
    public void onResponse(Activity activity, Intent data) {
        if (response != null) {
            /*
              从当前应用唤起微博并进行分享后，返回到当前应用时，需要在此处调用该函数
              来接收微博客户端返回的数据；执行成功，返回 true，并调用
              {@link IWeiboHandler.Response#onResponse}；失败返回 false，不调用上述回调
             */
            getApi(activity).handleWeiboResponse(data, response); // 当前应用唤起微博分享后，返回当前应用
        } else if (ssoHandler != null) {
            // 處理登陸操作后的結果
            int requestCode = data.getIntExtra(EventHandlerActivity.KEY_REQUEST_CODE, -1);
            int resultCode = data.getIntExtra(EventHandlerActivity.KEY_RESULT_CODE, -1);

            ssoHandler.authorizeCallBack(requestCode, resultCode, data);
        }
    }

    private static IWeiboShareAPI getApi(Context context) {
        return WeiboShareSDK.createWeiboAPI(context.getApplicationContext(),
                ShareLoginLib.getValue(KEY_APP_KEY));
    }
}
