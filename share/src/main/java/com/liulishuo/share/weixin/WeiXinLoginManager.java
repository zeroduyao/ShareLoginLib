package com.liulishuo.share.weixin;


import com.liulishuo.share.R;
import com.liulishuo.share.ShareBlock;
import com.liulishuo.share.base.AuthUserInfo;
import com.liulishuo.share.base.login.ILoginManager;
import com.liulishuo.share.base.login.LoginListener;
import com.liulishuo.share.base.login.UserInfoListener;
import com.liulishuo.share.util.HttpUtil;
import com.tencent.mm.sdk.modelbase.BaseResp;
import com.tencent.mm.sdk.modelmsg.SendAuth;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.WXAPIFactory;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.widget.Toast;

/**
 * Created by echo on 5/19/15.
 */
public class WeiXinLoginManager implements ILoginManager {

    /**
     * 判断请求认证时传递的状态码和响应得到的状态码是否相等
     */
    private static final String STATE = "kale_is_weixin_login";

    private static final String SCOPE = "snsapi_userinfo";

    private static IWXAPI mApi;

    private static LoginListener mLoginListener;

    private static LoginRespListener mRespListener;

    /**
     * 解析用户登录的结果
     */
    protected static void parseLoginResp(final Activity activity, SendAuth.Resp resp) {
        switch (resp.errCode) {
            case BaseResp.ErrCode.ERR_OK: // 登录成功
                handlerLoginResp(activity, resp); // 登录成功后开始通过code换取token
                break;
            case BaseResp.ErrCode.ERR_USER_CANCEL:
                mLoginListener.onCancel();
                break;
            case BaseResp.ErrCode.ERR_AUTH_DENIED:
                mLoginListener.onError("用户拒绝授权");
                break;
            default:
                mLoginListener.onError("未知错误");
        }
    }

    @Override
    public void login(Context context, @NonNull LoginListener loginListener) {
        String weChatAppId = ShareBlock.getInstance().weiXinAppId;
        if (TextUtils.isEmpty(weChatAppId)) {
            throw new NullPointerException("请通过shareBlock初始化WeChatAppId");
        }
        mApi = WXAPIFactory.createWXAPI(context.getApplicationContext(), weChatAppId, true);
        if (!mApi.isWXAppInstalled()) {
            Toast.makeText(context, context.getString(R.string.share_install_wechat_tips), Toast.LENGTH_SHORT).show();
            return;
        } else {
            mApi.registerApp(weChatAppId);
        }

        if (mApi != null) {
            final SendAuth.Req req = new SendAuth.Req();
            req.scope = SCOPE;
            req.state = STATE;
            mApi.sendReq(req);
            mLoginListener = loginListener;
        }
    }

    public static void setRespListener(LoginRespListener respListener) {
        mRespListener = respListener;
    }

    protected static void onLoginResp(SendAuth.Resp resp) {
        if (mRespListener != null) {
            mRespListener.onLoginResp(resp);
        }
    }

    private static void handlerLoginResp(final Activity activity, SendAuth.Resp resp) {
        HttpUtil.doGetAsyn(
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
                                    mLoginListener.onSuccess(token, openid, expires_in, jsonObject.toString());
                                }
                            });
                        } catch (JSONException e) {
                            e.printStackTrace();
                            activity.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    mLoginListener.onError("login data parse error");
                                }
                            });
                        }
                    }

                    @Override
                    public void onError() {
                        activity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                mLoginListener.onError("get login data error : {netWork error}");
                            }
                        });
                    }
                });
    }

    public interface LoginRespListener {
        void onLoginResp(SendAuth.Resp resp);
    }

    public static IWXAPI getApi() {
        return mApi;
    }

    // ---------------------------------- 得到用户信息 -------------------------------------

    /**
     * 通过传入的参数来获得用户的信息
     */
    public static void getUserInfo(@NonNull final String accessToken, @NonNull final String uid, final UserInfoListener listener) {
        new AsyncTask<Void, Void, AuthUserInfo>() {

            @Override
            protected AuthUserInfo doInBackground(Void... params) {
                String respStr = HttpUtil.doGet("https://api.weixin.qq.com/sns/userinfo"
                        + "?access_token=" + accessToken + "&openid=" + uid);
                if (respStr == null) {
                    return null;
                }
                
                AuthUserInfo userInfo;
                try {
                    userInfo = new AuthUserInfo();
                    JSONObject jsonObject = new JSONObject(respStr);
                    userInfo.nickName = jsonObject.getString("nickname");
                    userInfo.sex = jsonObject.getString("sex");
                    userInfo.headImgUrl = jsonObject.getString("headimgurl");
                    userInfo.userId = jsonObject.getString("unionid");
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

    /**
     * @return 是否已经安装微信
     */
    public static boolean isWeiXinInstalled(Context context) {
        IWXAPI api = WXAPIFactory.createWXAPI(context, ShareBlock.getInstance().weiXinAppId, true);
        return api.isWXAppInstalled();
    }

}
