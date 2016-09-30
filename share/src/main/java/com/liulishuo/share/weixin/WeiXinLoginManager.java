package com.liulishuo.share.weixin;

import com.liulishuo.share.LoginManager;
import com.liulishuo.share.ShareBlock;
import com.sina.weibo.sdk.exception.WeiboException;
import com.sina.weibo.sdk.net.AsyncWeiboRunner;
import com.sina.weibo.sdk.net.RequestListener;
import com.sina.weibo.sdk.net.WeiboParameters;
import com.tencent.mm.sdk.modelbase.BaseResp;
import com.tencent.mm.sdk.modelmsg.SendAuth;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.WXAPIFactory;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import static com.liulishuo.share.ShareBlock.Config.weiXinAppId;

/**
 * Created by echo on 5/19/15.
 */
public class WeiXinLoginManager {

    private static final String SCOPE = "snsapi_userinfo";

    //private static LoginRespListener mRespListener;

    public void login(@NonNull Context context) {
        String appId = ShareBlock.Config.weiXinAppId;
        if (TextUtils.isEmpty(appId)) {
            throw new NullPointerException("请通过shareBlock初始化WeiXinAppId");
        }

        IWXAPI api = WXAPIFactory.createWXAPI(context.getApplicationContext(), appId, true);
        api.registerApp(appId);

        SendAuth.Req req = new SendAuth.Req();
        req.scope = SCOPE;
        api.sendReq(req); // 这里的请求的回调会在activity中收到，然后通过parseLoginResp方法解析
    }

    /**
     * 解析用户登录的结果
     */
    protected static void parseLoginResp(final Activity activity, SendAuth.Resp resp,
            @Nullable LoginManager.LoginListener listener) {
        // 有可能是listener传入的是null，也可能是调用静态方法前没初始化当前的类
/*
        if (mRespListener != null) {
            mRespListener.onLoginResp(resp);
        }
*/
        if (listener != null) {
            switch (resp.errCode) {
                case BaseResp.ErrCode.ERR_OK: // 登录成功
                    handlerLoginResp(activity, resp, listener); // 登录成功后开始通过code换取token
                    break;
                case BaseResp.ErrCode.ERR_USER_CANCEL:
                    listener.onCancel();
                    break;
                case BaseResp.ErrCode.ERR_AUTH_DENIED:
                    listener.onError("用户拒绝授权");
                    break;
                default:
                    listener.onError("未知错误");
            }
        }
    }

    /*public static void setRespListener(LoginRespListener respListener) {
        mRespListener = respListener;
    }*/

    private static void handlerLoginResp(Context context, SendAuth.Resp resp,
            final @Nullable LoginManager.LoginListener listener) {

        AsyncWeiboRunner runner = new AsyncWeiboRunner(context);
        WeiboParameters params = new WeiboParameters(null);
        params.put("appid", weiXinAppId);
        params.put("secret", ShareBlock.Config.weiXinSecret);
        params.put("code", resp.code);
        params.put("grant_type", "authorization_code");

        runner.requestAsync("https://api.weixin.qq.com/sns/oauth2/access_token", params, "GET", new RequestListener() {
            @Override
            public void onComplete(String s) {
                try {
                    JSONObject jsonObject = new JSONObject(s);
                    String token = jsonObject.getString("access_token");
                    String openid = jsonObject.getString("openid");
                    long expires_in = jsonObject.getLong("expires_in");

                    if (listener != null) {
                        listener.onSuccess(token, openid, expires_in, jsonObject.toString());
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onWeiboException(WeiboException e) {
                if (listener != null) {
                    listener.onError(e.getMessage());
                }
            }
        });
    }

   /* public interface LoginRespListener {

        void onLoginResp(SendAuth.Resp resp);
    }*/

}
