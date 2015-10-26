package com.liulishuo.share.weibo;

import com.liulishuo.share.ShareBlock;
import com.liulishuo.share.base.Constants;
import com.liulishuo.share.base.login.GetUserListener;
import com.liulishuo.share.base.login.ILoginManager;
import com.liulishuo.share.base.login.LoginListener;
import com.liulishuo.share.util.HttpUtil;
import com.liulishuo.share.weibo.model.AbsOpenAPI;
import com.liulishuo.share.weibo.model.User;
import com.liulishuo.share.weibo.model.UsersAPI;
import com.sina.weibo.sdk.api.share.IWeiboShareAPI;
import com.sina.weibo.sdk.api.share.WeiboShareSDK;
import com.sina.weibo.sdk.auth.AuthInfo;
import com.sina.weibo.sdk.auth.Oauth2AccessToken;
import com.sina.weibo.sdk.auth.WeiboAuthListener;
import com.sina.weibo.sdk.auth.sso.SsoHandler;
import com.sina.weibo.sdk.exception.WeiboException;
import com.sina.weibo.sdk.net.RequestListener;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.TextUtils;

import java.util.HashMap;

/**
 * Created by echo on 5/19/15.
 */
public class WeiBoLoginManager implements ILoginManager {

    private static Context mContext;

    private String mSinaAppKey;

    private static LoginListener mLoginListener;

    private static Oauth2AccessToken mAccessToken;

    /**
     * 注意：SsoHandler 仅当 SDK 支持 SSO 时有效
     */
    private static SsoHandler mSsoHandler;
    
    private static AuthInfo mAuthInfo;

    public WeiBoLoginManager(Context context) {
        mContext = context;
        mSinaAppKey = ShareBlock.getInstance().weiboAppId;
    }

    @Override
    public void login(@NonNull LoginListener loginListener) {
        mLoginListener = loginListener;
        AccessTokenKeeper.clear(mContext);
        mAuthInfo = new AuthInfo(mContext, mSinaAppKey,
                ShareBlock.getInstance().weiboRedirectUrl,
                ShareBlock.getInstance().weiboScope);
        // 启动activity后，应该立刻调用{sendLoginMsg}方法
        mContext.startActivity(new Intent(mContext, WeiBoLoginActivity.class));
    }

    public static void sendLoginMsg(Activity activity) {
        mSsoHandler = new SsoHandler(activity, mAuthInfo);
        mSsoHandler.authorize(new AuthLoginListener());
    }
    
    /**
     * * 1. SSO 授权时，需要在 onActivityResult 中调用 {@link SsoHandler#authorizeCallBack} 后，
     * 该回调才会被执行。
     * 2. 非SSO 授权时，当授权结束后，该回调就会被执行
     */
    private static class AuthLoginListener implements WeiboAuthListener {

        @Override
        public void onComplete(Bundle values) {
            final Oauth2AccessToken accessToken = Oauth2AccessToken.parseAccessToken(values);
            if (accessToken != null && accessToken.isSessionValid()) {
                mAccessToken = accessToken;
                AccessTokenKeeper.writeAccessToken(mContext, mAccessToken);
                mLoginListener.onSuccess(accessToken.getUid(), accessToken.getToken(), accessToken.getExpiresTime() / 1000000,
                        oAuthData2Json(mAccessToken));
            }
        }

        @Override
        public void onWeiboException(WeiboException e) {
            mLoginListener.onError(e.getMessage());
        }

        @Override
        public void onCancel() {
            mLoginListener.onCancel();
        }
    }

    /**
     * 得到用户信息的操作(在登录完毕后直接调用)
     */
    @Override
    public void getUserInfo(final @NonNull GetUserListener listener) {
        UsersAPI userAPI = new UsersAPI(mContext, mSinaAppKey, mAccessToken);
        userAPI.show(Long.parseLong(mAccessToken.getUid()), new RequestListener() {
            @Override
            public void onComplete(String response) {
                if (!TextUtils.isEmpty(response)) {
                    // 调用 User#parse 将JSON串解析成User对象
                    User user = User.parse(response);
                    if (user != null) {
                        HashMap<String, String> userInfoHashMap = new HashMap<>();
                        userInfoHashMap.put(Constants.PARAMS_NICK_NAME, user.name);
                        userInfoHashMap.put(Constants.PARAMS_SEX, user.gender);
                        userInfoHashMap.put(Constants.PARAMS_IMAGEURL, user.avatar_large);
                        userInfoHashMap.put(Constants.PARAMS_USERID, user.id);
                        listener.onComplete(userInfoHashMap);
                    }
                }
            }

            @Override
            public void onWeiboException(WeiboException e) {
                listener.onError(e.getMessage());
            }
        });
    }

    /**
     * @see "http://open.weibo.com/wiki/2/users/show"
     */
    @Override
    public void getUserInfo(String accessToken, String uid, final @NonNull GetUserListener listener) {
        StringBuilder builder = new StringBuilder();
        builder.append(AbsOpenAPI.API_SERVER)
                .append("/users/show.json")
                .append("?access_token=").append(accessToken)
                .append("&uid").append(uid);
        HttpUtil.doGetAsyn(builder.toString(), new HttpUtil.CallBack() {
            @Override
            public void onRequestComplete(String result) {
                try {
                    JSONObject jsonObject = new JSONObject(result);
                    HashMap<String, String> userInfoHashMap = new HashMap<>();
                    userInfoHashMap.put(Constants.PARAMS_NICK_NAME, jsonObject.getString("screen_name"));
                    userInfoHashMap.put(Constants.PARAMS_SEX, jsonObject.getString("gender"));
                    userInfoHashMap.put(Constants.PARAMS_IMAGEURL, jsonObject.getString("avatar_large"));
                    userInfoHashMap.put(Constants.PARAMS_USERID, jsonObject.getString("id"));

                    listener.onComplete(userInfoHashMap);
                } catch (Exception e) {
                    e.printStackTrace();
                    listener.onError("user data parse error");
                }
            }

            @Override
            public void onError() {
                listener.onError("get user data error : {network error}");
            }
        });
    }

    public static void handlerOnActivityResult(int requestCode, int resultCode, Intent data) {
        // SSO 授权回调
        // 重要：发起 SSO 登陆的 Activity 必须重写 onActivityResult
        if (mSsoHandler != null) {
            mSsoHandler.authorizeCallBack(requestCode, resultCode, data);
        }
    }

    private static String oAuthData2Json(@NonNull Oauth2AccessToken data) {
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

    public static boolean isWeiBoInstalled(@NonNull Context context) {
        IWeiboShareAPI shareAPI = WeiboShareSDK.createWeiboAPI(context, ShareBlock.getInstance().weiboAppId);
        return shareAPI.isWeiboAppInstalled();
    }

}
