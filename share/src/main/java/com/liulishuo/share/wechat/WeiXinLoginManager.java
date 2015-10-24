package com.liulishuo.share.wechat;


import com.liulishuo.share.R;
import com.liulishuo.share.ShareBlock;
import com.liulishuo.share.base.login.GetUserListener;
import com.liulishuo.share.base.login.ILoginManager;
import com.liulishuo.share.base.login.LoginListener;
import com.liulishuo.share.base.Constants;
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
import android.widget.Toast;

import java.util.HashMap;

/**
 * Created by echo on 5/19/15.
 */
public class WeiXinLoginManager implements ILoginManager {

    private static final String SCOPE = "snsapi_userinfo";

    /**
     * 判断请求认证时传递的状态码和响应得到的状态码是否相等
     */
    private static final String STATE = "lls_engzo_wechat_login";


    private static IWXAPI mIWXAPI;

    private static LoginListener mLoginListener;


    public WeiXinLoginManager(Context context) {
        String weChatAppId = ShareBlock.getInstance().weiXinAppId;
        if (TextUtils.isEmpty(weChatAppId)) {
            throw new NullPointerException("请通过shareBlock初始化WeChatAppId");
        }
        mIWXAPI = WXAPIFactory.createWXAPI(context, weChatAppId, true);
        if (!mIWXAPI.isWXAppInstalled()) {
            Toast.makeText(context, context.getString(R.string.share_install_wechat_tips), Toast.LENGTH_SHORT).show();
        } else {
            mIWXAPI.registerApp(weChatAppId);
        }
    }

    /**
     * @return 是否已经安装微信
     */
    public static boolean isWeiXinInstalled(Context context) {
        IWXAPI api = WXAPIFactory.createWXAPI(context, ShareBlock.getInstance().weiXinAppId, true);
        return api.isWXAppInstalled();
    }

    public static IWXAPI getIWXAPI() {
        return mIWXAPI;
    }

    @Override
    public void login(@NonNull LoginListener platformActionListener) {
        if (mIWXAPI != null) {
            final SendAuth.Req req = new SendAuth.Req();
            req.scope = SCOPE;
            req.state = STATE;
            mIWXAPI.sendReq(req);
            mLoginListener = platformActionListener;
        }
    }

    private static String mAccessToken;

    private static String mOpenid;

    /**
     * 解析用户登录的结果
     */
    protected static void parseLoginResp(final Activity activity, SendAuth.Resp resp) {
        switch (resp.errCode) {
            case BaseResp.ErrCode.ERR_OK:
                // 登录成功
                StringBuilder builder = new StringBuilder();
                builder.append("https://api.weixin.qq.com")
                        .append("/sns/oauth2/access_token")
                        .append("?appid=")
                        .append(ShareBlock.getInstance().weiXinAppId)
                        .append("&secret=")
                        .append(ShareBlock.getInstance().weiXinSecret)
                        .append("&code=")
                        .append(resp.code)
                        .append("&grant_type=authorization_code");
                // 通过code获得access token
                HttpUtil.doGetAsyn(builder.toString(), new HttpUtil.CallBack() {
                    @Override
                    public void onRequestComplete(String result) {
                        try {
                            final JSONObject jsonObject = new JSONObject(result);
                            mAccessToken = jsonObject.getString("access_token");
                            mOpenid = jsonObject.getString("openid");
                            final long expires_in = jsonObject.getLong("expires_in");
                            activity.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    mLoginListener.onSuccess(mOpenid, mAccessToken, expires_in, jsonObject.toString());
                                }
                            });
                        } catch (JSONException e) {
                            e.printStackTrace();
                            activity.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    mLoginListener.onError("get login data parse error");
                                }
                            });
                        }
                    }

                    @Override
                    public void onError() {
                        mLoginListener.onError("get login data error : {netWork error}");
                    }
                });
                break;
            case BaseResp.ErrCode.ERR_USER_CANCEL:
                // 用户取消
                mLoginListener.onCancel();
                break;
            case BaseResp.ErrCode.ERR_AUTH_DENIED:
                // 用户拒绝授权
                mLoginListener.onError("用户拒绝授权");
                break;
            default:
                mLoginListener.onError("未知错误");
        }
    }

    /**
     * 得到用户信息的操作(在登录完毕后直接调用)
     */
    @Override
    public void getUserInfo(final @NonNull GetUserListener listener) {
        getUserInfo(mAccessToken, mOpenid, listener);
    }

    /**
     * 通过传入的参数来获得用户的信息
     */
    @Override
    public void getUserInfo(String accessToken, String uid, final @NonNull GetUserListener listener) {
        StringBuilder builder = new StringBuilder();
        builder.append("https://api.weixin.qq.com")
                .append("/sns/userinfo")
                .append("?access_token=").append(accessToken)
                .append("&openid").append(uid);
        HttpUtil.doGetAsyn(builder.toString(), new HttpUtil.CallBack() {
            @Override
            public void onRequestComplete(String result) {
                try {
                    JSONObject jsonObject = new JSONObject(result);
                    HashMap<String, String> userInfoHashMap = new HashMap<>();
                    userInfoHashMap.put(Constants.PARAMS_NICK_NAME, jsonObject.getString("nickname"));
                    userInfoHashMap.put(Constants.PARAMS_SEX, jsonObject.getString("sex"));
                    userInfoHashMap.put(Constants.PARAMS_IMAGEURL, jsonObject.getString("headimgurl"));
                    userInfoHashMap.put(Constants.PARAMS_USERID, jsonObject.getString("unionid"));

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

    private static LoginRespListener mRespListener;

    public interface LoginRespListener {

        void onloginResp(SendAuth.Resp resp);

    }

    public static void setRespListener(LoginRespListener respListener) {
        mRespListener = respListener;
    }

    protected static void onLoginResp(SendAuth.Resp resp) {
        if (mRespListener != null) {
            mRespListener.onloginResp(resp);
        }
    }

}
