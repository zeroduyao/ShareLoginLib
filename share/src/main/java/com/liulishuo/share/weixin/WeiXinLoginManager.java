package com.liulishuo.share.weixin;

import com.liulishuo.share.LoginManager;
import com.liulishuo.share.ShareBlock;
import com.liulishuo.share.util.HttpUtil;
import com.tencent.mm.sdk.modelbase.BaseResp;
import com.tencent.mm.sdk.modelmsg.SendAuth;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.WXAPIFactory;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.text.TextUtils;

/**
 * Created by echo on 5/19/15.
 */
public class WeiXinLoginManager {

    /**
     * 判断请求认证时传递的状态码和响应得到的状态码是否相等
     */
    private static final String STATE = "kale_is_weixin_login";

    private static final String SCOPE = "snsapi_userinfo";

    //private static LoginRespListener mRespListener;

    public void login(@NonNull Context context) {
        String appId = ShareBlock.getInstance().weiXinAppId;
        if (TextUtils.isEmpty(appId)) {
            throw new NullPointerException("请通过shareBlock初始化WeiXinAppId");
        }

        IWXAPI api = WXAPIFactory.createWXAPI(context.getApplicationContext(), appId, true);
        api.registerApp(appId);

        SendAuth.Req req = new SendAuth.Req();
        req.scope = SCOPE;
        req.state = STATE;
        api.sendReq(req); // 这里的请求的回调会在activity中收到，然后通过parseLoginResp方法解析
    }

    /**
     * 解析用户登录的结果
     */
    protected static void parseLoginResp(final Activity activity, SendAuth.Resp resp, LoginManager.LoginListener loginListener) {
        // 有可能是listener传入的是null，也可能是调用静态方法前没初始化当前的类
/*
        if (mRespListener != null) {
            mRespListener.onLoginResp(resp);
        }
*/
        if (loginListener != null) {
            switch (resp.errCode) {
                case BaseResp.ErrCode.ERR_OK: // 登录成功
                    handlerLoginResp(activity, resp, loginListener); // 登录成功后开始通过code换取token
                    break;
                case BaseResp.ErrCode.ERR_USER_CANCEL:
                    loginListener.onCancel();
                    break;
                case BaseResp.ErrCode.ERR_AUTH_DENIED:
                    loginListener.onError("用户拒绝授权");
                    break;
                default:
                    loginListener.onError("未知错误");
            }
        }
    }

    /*public static void setRespListener(LoginRespListener respListener) {
        mRespListener = respListener;
    }*/

    private static void handlerLoginResp(final Activity activity, SendAuth.Resp resp, final LoginManager.LoginListener loginListener) {
        HttpUtil.doGetAsync(
                "https://api.weixin.qq.com/sns/oauth2/access_token"
                        + "?appid=" + ShareBlock.getInstance().weiXinAppId
                        + "&secret=" + ShareBlock.getInstance().weiXinSecret
                        + "&code=" + resp.code // 通过code获得access token
                        + "&grant_type=authorization_code",
                new HttpUtil.CallBack() {
                    @Override
                    public void onRequestComplete(String result) {
                        try {
                            final JSONObject jsonObject = new JSONObject(result);
                            final String token = jsonObject.getString("access_token");
                            final String openid = jsonObject.getString("openid");
                            final long expires_in = jsonObject.getLong("expires_in");

                            activity.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    loginListener.onSuccess(token, openid, expires_in, jsonObject.toString());
                                }
                            });
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onError() {
                        activity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                loginListener.onError("无法得到登录结果 : {netWork error}");
                            }
                        });
                    }
                });
    }

   /* public interface LoginRespListener {

        void onLoginResp(SendAuth.Resp resp);
    }*/

}
