package com.liulishuo.share.weibo;

import com.liulishuo.share.LoginManager;
import com.liulishuo.share.ShareBlock;
import com.sina.weibo.sdk.auth.AuthInfo;
import com.sina.weibo.sdk.auth.Oauth2AccessToken;
import com.sina.weibo.sdk.auth.WeiboAuthListener;
import com.sina.weibo.sdk.auth.sso.SsoHandler;
import com.sina.weibo.sdk.exception.WeiboException;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;

/**
 * @author Jack Tony
 * @date 2015/10/26
 */
public class SL_WeiBoLoginActivity extends Activity {

    /**
     * 注意：SsoHandler 仅当 SDK 支持 SSO 时有效
     */
    private SsoHandler ssoHandler;

    private boolean isFirstIn = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ssoHandler = initHandler(this);
        if (savedInstanceState == null) {
            // 防止不保留活动情况下activity被重置后直接进行操作的情况
            doLogin(LoginManager.listener);
        } else {
            isFirstIn = false;
        }
    }

    /**
     * 解析用户登录的结果
     * SSO 授权回调   重要：发起 SSO 登陆的 Activity 必须重写 onActivityResult
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (ssoHandler != null) {
            ssoHandler.authorizeCallBack(requestCode, resultCode, data);
        }
        finish();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (isFirstIn) {
            isFirstIn = false;
        } else {
            // 这里处理通过网页登录无回调的问题
            finish();
        }
    }

    private void doLogin(final LoginManager.LoginListener listener) {
        WeiboAuthListener authListener = new WeiboAuthListener() {
            /*
             * 1. SSO 授权时，需要在 onActivityResult 中调用 {@link SsoHandler#authorizeCallBack} 后，
             * 该回调才会被执行。
             * 2. 非SSO 授权时，当授权结束后，该回调就会被执行
             */
            @Override
            public void onComplete(Bundle values) {
                final Oauth2AccessToken accessToken = Oauth2AccessToken.parseAccessToken(values);
                if (accessToken != null && accessToken.isSessionValid()) {
                    if (listener != null) {
                        listener.onSuccess(accessToken.getToken(), accessToken.getUid(), 
                                accessToken.getExpiresTime() / 1000000, oAuthData2Json(accessToken));
                    }
                }
            }

            @Override
            public void onWeiboException(WeiboException e) {
                if (listener != null) {
                    listener.onError(e.getMessage());
                }
            }

            @Override
            public void onCancel() {
                if (listener != null) {
                    listener.onCancel();
                }
            }
        };

        ssoHandler.authorize(authListener); // 启动微博的activity进行微博登录
    }

    private SsoHandler initHandler(Activity activity) {
        String appId = ShareBlock.Config.weiBoAppId;
        if (TextUtils.isEmpty(appId)) {
            throw new NullPointerException("请通过shareBlock初始化weiboAppId");
        }

        return new SsoHandler(activity, new AuthInfo(activity, appId,
                ShareBlock.Config.weiBoRedirectUrl,
                ShareBlock.Config.weiBoScope));
    }

    private
    @Nullable
    String oAuthData2Json(@NonNull Oauth2AccessToken data) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("uid", data.getUid());
            jsonObject.put("refresh_token", data.getRefreshToken());
            jsonObject.put("access_token", data.getToken());
            jsonObject.put("expires_in", String.valueOf(data.getExpiresTime() / 1000000));
            return jsonObject.toString();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }
}
