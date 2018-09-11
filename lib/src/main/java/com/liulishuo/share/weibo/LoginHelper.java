package com.liulishuo.share.weibo;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.liulishuo.share.LoginListener;
import com.liulishuo.share.OAuthUserInfo;
import com.liulishuo.share.ShareLoginLib;
import com.sina.weibo.sdk.auth.Oauth2AccessToken;
import com.sina.weibo.sdk.auth.WeiboAuthListener;
import com.sina.weibo.sdk.exception.WeiboException;
import com.sina.weibo.sdk.net.AsyncWeiboRunner;
import com.sina.weibo.sdk.net.WeiboParameters;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * @author Kale
 * @date 2018/9/10
 */
class LoginHelper {

    static void parseLoginResp(Activity activity, Bundle values, LoginListener listener) {
        final Oauth2AccessToken accessToken = Oauth2AccessToken.parseAccessToken(values);
        if (listener != null && accessToken != null) {
            if (accessToken.isSessionValid()) {
                String token = accessToken.getToken();
                String uid = accessToken.getUid();
                
                listener.onSuccess(token, uid, accessToken.getExpiresTime() / 1000000, data2Json(accessToken));
                getUserInfo(activity, token, uid, listener);
            } else {
                String errorCode = values.getString("code");
                listener.onError("签名不正确，error code: " + errorCode);
            }
        }
    }

    @Nullable
    private static String data2Json(@NonNull Oauth2AccessToken data) {
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

    /**
     * 得到微博用户的信息
     *
     * @see "http://open.weibo.com/wiki/2/users/show"
     */
    public static void getUserInfo(Context context, String accessToken, String uid, LoginListener listener) {
        AsyncWeiboRunner runner = new AsyncWeiboRunner(context);
        WeiboParameters params = new WeiboParameters(null);
        params.put("access_token", accessToken);
        params.put("uid", uid);
        runner.requestAsync("https://api.weibo.com/2/users/show.json", params, "GET", new ShareLoginLib.UserInfoListener(listener) {
            @Override
            public OAuthUserInfo onSuccess(JSONObject jsonObj) throws JSONException {
                OAuthUserInfo userInfo = new OAuthUserInfo();
                userInfo.nickName = jsonObj.getString("screen_name");
                userInfo.sex = jsonObj.getString("gender");
                userInfo.headImgUrl = jsonObj.getString("avatar_large");
                userInfo.userId = jsonObj.getString("id");
                return userInfo;
            }
        });

    }

    static abstract class AbsAuthListener implements WeiboAuthListener {

        LoginListener listener;

        public AbsAuthListener(LoginListener listener) {
            this.listener = listener;
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
    }

}
