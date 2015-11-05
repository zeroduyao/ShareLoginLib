package com.liulishuo.share.weibo;

import com.liulishuo.share.ShareBlock;
import com.liulishuo.share.base.AuthUserInfo;
import com.liulishuo.share.base.login.ILoginManager;
import com.liulishuo.share.base.login.LoginListener;
import com.liulishuo.share.base.login.UserInfoListener;
import com.liulishuo.share.util.HttpUtil;
import com.liulishuo.share.weibo.model.AbsOpenAPI;
import com.sina.weibo.sdk.api.share.IWeiboShareAPI;
import com.sina.weibo.sdk.api.share.WeiboShareSDK;
import com.sina.weibo.sdk.auth.AuthInfo;
import com.sina.weibo.sdk.auth.Oauth2AccessToken;
import com.sina.weibo.sdk.auth.WeiboAuthListener;
import com.sina.weibo.sdk.auth.sso.SsoHandler;
import com.sina.weibo.sdk.exception.WeiboException;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;

/**
 * Created by echo on 5/19/15.
 */
public class WeiboLoginManager implements ILoginManager {

    /**
     * 注意：SsoHandler 仅当 SDK 支持 SSO 时有效
     */
    private static SsoHandler mSsoHandler;

    private static WeiboAuthListener mAuthLoginListener;

    @Override
    public void login(@NonNull final Activity activity, @NonNull final LoginListener loginListener) {
        String appId = ShareBlock.getInstance().weiboAppId;
        if (TextUtils.isEmpty(appId)) {
            throw new NullPointerException("请通过shareBlock初始化weiboAppId");
        }
        AccessTokenKeeper.clear(activity);
        /**
         * * 1. SSO 授权时，需要在 onActivityResult 中调用 {@link SsoHandler#authorizeCallBack} 后，
         * 该回调才会被执行。
         * 2. 非SSO 授权时，当授权结束后，该回调就会被执行
         */
        mAuthLoginListener = new WeiboAuthListener() {
            @Override
            public void onComplete(Bundle values) {
                final Oauth2AccessToken accessToken = Oauth2AccessToken.parseAccessToken(values);
                if (accessToken != null && accessToken.isSessionValid()) {
                    AccessTokenKeeper.writeAccessToken(activity, accessToken);

                    loginListener.onSuccess(accessToken.getToken(), accessToken.getUid(),
                            accessToken.getExpiresTime() / 1000000,
                            oAuthData2Json(accessToken));
                }
            }

            @Override
            public void onWeiboException(WeiboException e) {
                loginListener.onError(e.getMessage());
            }

            @Override
            public void onCancel() {
                loginListener.onCancel();
            }
        };
        
        // 启动activity后，应该立刻调用{sendLoginMsg}方法
        activity.startActivity(new Intent(activity, SL_WeiBoLoginActivity.class));
    }

    protected static void sendLoginMsg(Activity activity) {
        AuthInfo authInfo = new AuthInfo(activity.getApplicationContext(), 
                ShareBlock.getInstance().weiboAppId,
                ShareBlock.getInstance().weiboRedirectUrl,
                ShareBlock.getInstance().weiboScope);
        
        mSsoHandler = new SsoHandler(activity, authInfo);
        mSsoHandler.authorize(mAuthLoginListener);
    }

    protected static void handlerOnActivityResult(int requestCode, int resultCode, Intent data) {
        // SSO 授权回调
        // 重要：发起 SSO 登陆的 Activity 必须重写 onActivityResult
        if (mSsoHandler != null) {
            mSsoHandler.authorizeCallBack(requestCode, resultCode, data);
        }
    }

    private String oAuthData2Json(@NonNull Oauth2AccessToken data) {
        JSONObject sinaJson = new JSONObject();
        try {
            sinaJson.put("uid", data.getUid());
            sinaJson.put("refresh_token", data.getRefreshToken());
            sinaJson.put("access_token", data.getToken());
            sinaJson.put("expires_in", String.valueOf(data.getExpiresTime() / 1000000));
            return sinaJson.toString();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    // ---------------------------------- 得到用户信息 -------------------------------------

    @Override
    public void getUserInformation(@NonNull String accessToken, @NonNull String userId, @Nullable UserInfoListener listener) {
        getUserInfo(accessToken, userId, listener);
    }
    
    
    /**
     * 得到微博用户的信息
     *
     * @see "http://open.weibo.com/wiki/2/users/show"
     */
    public static void getUserInfo(final @NonNull String accessToken, final @NonNull String uid,
            @Nullable final UserInfoListener listener) {
        
        new AsyncTask<Void, Void, AuthUserInfo>() {
            
            @Override
            protected AuthUserInfo doInBackground(Void... params) {
                String respStr = HttpUtil.doGet(AbsOpenAPI.API_SERVER + "/users/show.json" 
                        + "?access_token=" + accessToken + "&uid=" + uid);
                if (respStr == null) {
                    return null;
                }
                
                AuthUserInfo userInfo;
                try {
                    userInfo = new AuthUserInfo();
                    JSONObject jsonObject = new JSONObject(respStr);
                    userInfo.nickName = jsonObject.getString("screen_name");
                    userInfo.sex = jsonObject.getString("gender");
                    userInfo.headImgUrl = jsonObject.getString("avatar_large");
                    userInfo.userId = jsonObject.getString("id");
                } catch (JSONException e) {
                    userInfo = null;
                    e.printStackTrace();
                }
                return userInfo;
            }

            @Override
            protected void onPostExecute(AuthUserInfo userInfo) {
                super.onPostExecute(userInfo);
                if (listener != null) {
                    if (userInfo != null) {
                        listener.onSuccess(userInfo);
                    } else {
                        listener.onError("用户信息解析异常");
                    }
                }
            }
        }.execute();
    }

    public static boolean isWeiBoInstalled(@NonNull Context context) {
        IWeiboShareAPI shareAPI = WeiboShareSDK.createWeiboAPI(context, ShareBlock.getInstance().weiboAppId);
        return shareAPI.isWeiboAppInstalled();
    }

}
