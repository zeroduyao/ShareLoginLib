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

        /**
         * 110201：未登陆
         * 110405：登录请求被限制
         * 110404：请求参数缺少appId
         * 110401：请求的应用不存在
         * 110407：应用已经下架
         * 110406：应用没有通过审核
         * 100044：错误的sign
         * 110500：获取用户授权信息失败
         * 110501：获取应用的授权信息失败
         * 110502：设置用户授权失败
         * 110503：获取token失败
         * 110504：系统内部错误
         * 
         * http://wiki.open.qq.com/wiki/mobile/API%E8%B0%83%E7%94%A8%E8%AF%B4%E6%98%8E
         */
        @Override
        public void onError(UiError resp) {
            listener.onError("code:" + resp.errorCode + ", message:" + resp.errorMessage + ", detail:" + resp.errorDetail);
        }

    }

}