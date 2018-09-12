package com.liulishuo.share.qq;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;

import com.liulishuo.share.LoginListener;
import com.liulishuo.share.OAuthUserInfo;
import com.liulishuo.share.ShareLoginLib;
import com.liulishuo.share.utils.ISsoListener;
import com.sina.weibo.sdk.net.AsyncWeiboRunner;
import com.sina.weibo.sdk.net.WeiboParameters;
import com.tencent.connect.common.Constants;
import com.tencent.tauth.IUiListener;
import com.tencent.tauth.UiError;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * @author Kale
 * @date 2018/9/11
 */
class LoginHelper {

    static void parseLoginResp(Activity activity, Object object, @NonNull LoginListener listener) {
        JSONObject jsonObject = ((JSONObject) object);
        try {
            String token = jsonObject.getString(Constants.PARAM_ACCESS_TOKEN);
            String openId = jsonObject.getString(Constants.PARAM_OPEN_ID);
            String expires = jsonObject.getString(Constants.PARAM_EXPIRES_IN);
            
            listener.onReceiveToken(token, openId, Long.valueOf(expires), object.toString());

            getUserInfo(activity.getApplicationContext(), token, openId, listener);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 得到用户的信息，是一个静态的基础方法
     *
     * @see "http://wiki.open.qq.com/wiki/website/get_simple_userinfo"
     */
    private static void getUserInfo(Context context, final String accessToken, final String userId, LoginListener listener) {
        AsyncWeiboRunner runner = new AsyncWeiboRunner(context);
        WeiboParameters params = new WeiboParameters(null);
        params.put("access_token", accessToken);
        params.put("openid", userId);
        params.put("oauth_consumer_key", ShareLoginLib.getValue(QQPlatform.KEY_APP_ID));
        params.put("format", "json");

        runner.requestAsync("https://graph.qq.com/user/get_simple_userinfo", params, "GET",
                new ShareLoginLib.UserInfoListener(listener) {

                    @Override
                    public OAuthUserInfo json2UserInfo(JSONObject jsonObj) throws JSONException {
                        OAuthUserInfo userInfo = new OAuthUserInfo();
                        userInfo.nickName = jsonObj.getString("nickname");
                        userInfo.sex = jsonObj.getString("gender");
                        userInfo.headImgUrl = jsonObj.getString("figureurl_qq_1");
                        userInfo.userId = userId;
                        return userInfo;
                    }
                });
    }

    abstract static class AbsUiListener implements IUiListener {

        private ISsoListener listener;

        AbsUiListener(ISsoListener listener) {
            this.listener = listener;
        }

        @Override
        public void onCancel() {
            listener.onCancel();
        }

        @Override
        public void onError(UiError resp) {
            listener.onError(resp.errorCode + " - " + resp.errorMessage + " - " + resp.errorDetail);
        }

    }

}
