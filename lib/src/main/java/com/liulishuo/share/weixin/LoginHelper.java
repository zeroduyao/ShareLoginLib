package com.liulishuo.share.weixin;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;

import com.liulishuo.share.LoginListener;
import com.liulishuo.share.OAuthUserInfo;
import com.liulishuo.share.ShareLoginLib;
import com.sina.weibo.sdk.exception.WeiboException;
import com.sina.weibo.sdk.net.AsyncWeiboRunner;
import com.sina.weibo.sdk.net.RequestListener;
import com.sina.weibo.sdk.net.WeiboParameters;
import com.tencent.mm.opensdk.modelbase.BaseResp;
import com.tencent.mm.opensdk.modelmsg.SendAuth;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * @author Kale
 * @date 2018/9/10
 */
class LoginHelper {

    /**
     * BaseResp的getType函数获得的返回值
     * 1：第三方授权；2：分享
     */
    private static final int TYPE_LOGIN = 1;

    /**
     * 解析用户登录的结果
     */
    static void parseLoginResp(final Activity activity, BaseResp baseResp, @NonNull LoginListener listener) {
        if (baseResp instanceof SendAuth.Resp && baseResp.getType() == TYPE_LOGIN ) {
            SendAuth.Resp resp = (SendAuth.Resp) baseResp;
            switch (resp.errCode) {
                case BaseResp.ErrCode.ERR_OK: // 登录成功
                    code2Token(activity, resp.code, listener); // 通过code换取token
                    break;
                case BaseResp.ErrCode.ERR_USER_CANCEL:
                    listener.onCancel();
                    break;
                case BaseResp.ErrCode.ERR_AUTH_DENIED:
                    listener.onError("用户拒绝授权");
                    break;
                default:
                    listener.onError("未知错误，错误码：" + resp.errCode);
            }
        }
    }

    /**
     * 返回结果：
     * {
     * "access_token":"ACCESS_TOKEN", // token
     * "expires_in":7200,
     * "refresh_token":"REFRESH_TOKEN",
     * "openid":"OPENID",
     * "scope":"SCOPE",
     * "unionid":"o6_bmajkjhjhhkj"
     * }
     */
    private static void code2Token(Context context, String code, @NonNull final LoginListener listener) {
        String appId = ShareLoginLib.getValue(WeiXinPlatform.KEY_APP_ID);
        String secret = ShareLoginLib.getValue(WeiXinPlatform.KEY_SECRET_KEY);

        WeiboParameters params = new WeiboParameters(null);
        params.put("appid", appId);
        params.put("secret", secret);
        params.put("grant_type", "authorization_code");
        params.put("code", code);

        new AsyncWeiboRunner(context).requestAsync("https://api.weixin.qq.com/sns/oauth2/access_token", params, "GET", new RequestListener() {
            @Override
            public void onComplete(String json) {
                try {
                    JSONObject jsonObject = new JSONObject(json);
                    String token = jsonObject.getString("access_token"); // 接口调用凭证
                    String openid = jsonObject.getString("openid"); // 授权用户唯一标识
                    long expires_in = jsonObject.getLong("expires_in"); // access_token接口调用凭证超时时间，单位（秒）

                    listener.onSuccess(token, openid, expires_in, jsonObject.toString());
                    
                    getUserInfo(context, token, openid, listener);
                } catch (JSONException e) {
                    e.printStackTrace();
                    listener.onError(e.getMessage());
                }
            }

            @Override
            public void onWeiboException(WeiboException e) {
                listener.onError(e.getMessage());
            }
        });
    }

    /**
     * {
     * "openid":"OPENID",
     * "nickname":"NICKNAME",
     * "sex":1,
     * "province":"PROVINCE",
     * "city":"CITY",
     * "country":"COUNTRY",
     * "headimgurl": "https://avatars3.githubusercontent.com/u/9552155?v=3&s=460",
     * "privilege":[
     * "PRIVILEGE1",
     * "PRIVILEGE2"
     * ],
     * "unionid": " o6_bmasdasdsad6_2sgVt7hMZOPfL"
     * }
     */
    public static void getUserInfo(Context context, String accessToken, String uid, final LoginListener listener) {
        WeiboParameters params = new WeiboParameters(null);
        params.put("access_token", accessToken);
        params.put("openid", uid);

        new AsyncWeiboRunner(context).requestAsync("https://api.weixin.qq.com/sns/userinfo", params, "GET", new ShareLoginLib.UserInfoListener(listener) {

            @Override
            public OAuthUserInfo json2UserInfo(JSONObject jsonObj) throws JSONException {
                OAuthUserInfo userInfo = new OAuthUserInfo();
                userInfo.nickName = jsonObj.getString("nickname");
                userInfo.sex = jsonObj.getString("sex");
                // 用户头像，最后一个数值代表正方形头像大小（有0、46、64、96、132数值可选，0代表640*640正方形头像），用户没有头像时该项为空
                userInfo.headImgUrl = jsonObj.getString("headimgurl");
                userInfo.userId = jsonObj.getString("unionid");
                return userInfo;
            }
        });
    }

}
