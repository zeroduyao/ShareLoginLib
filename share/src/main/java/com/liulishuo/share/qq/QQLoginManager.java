package com.liulishuo.share.qq;

import com.liulishuo.share.ShareBlock;
import com.liulishuo.share.base.AuthUserInfo;
import com.liulishuo.share.base.login.ILoginManager;
import com.liulishuo.share.base.login.LoginListener;
import com.liulishuo.share.base.login.UserInfoListener;
import com.liulishuo.share.util.HttpUtil;
import com.tencent.connect.common.Constants;
import com.tencent.tauth.IUiListener;
import com.tencent.tauth.Tencent;
import com.tencent.tauth.UiError;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;

/**
 * Created by echo on 5/19/15.
 */
public class QQLoginManager implements ILoginManager {

    private static IUiListener mUiListener;

    @Override
    public void login(@NonNull Activity activity, final @NonNull LoginListener loginListener) {
        mUiListener = new IUiListener() {

            @Override
            public void onComplete(Object object) {
                JSONObject jsonObject = ((JSONObject) object);
                try {
                    String token = jsonObject.getString(Constants.PARAM_ACCESS_TOKEN);
                    String openId = jsonObject.getString(Constants.PARAM_OPEN_ID);
                    String expires = jsonObject.getString(Constants.PARAM_EXPIRES_IN);
                    loginListener.onSuccess(token, openId, Long.valueOf(expires), object.toString());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(UiError uiError) {
                loginListener.onError(uiError.errorCode + " - " + uiError.errorMessage + " - " + uiError.errorDetail);
            }

            @Override
            public void onCancel() {
                loginListener.onCancel();
            }
        };
        // 启动activity后，应该立刻调用{sendLoginMsg}方法
        activity.startActivity(SL_QQHandlerActivity.withIntent(activity, true));
    }

    protected static void sendLoginMsg(Activity activity) {
        String appId = ShareBlock.getInstance().QQAppId;
        if (TextUtils.isEmpty(appId)) {
            throw new NullPointerException("请通过shareBlock初始化appId");
        }

        Tencent tencent = Tencent.createInstance(appId, activity);
        if (!tencent.isSessionValid()) {
            tencent.login(activity, ShareBlock.getInstance().QQScope, mUiListener);
        } else {
            tencent.logout(activity);
        }
    }

    /**
     * 解析用户登录的结果
     */
    protected static void handlerOnActivityResult(int requestCode, int resultCode, Intent data) {
        Tencent.onActivityResultData(requestCode, resultCode, data, mUiListener);
    }

    // ---------------------------------- 得到用户信息 -------------------------------------

    @Override
    public void getUserInformation(@NonNull String accessToken, @NonNull String userId, @Nullable UserInfoListener listener) {
        getUserInfo(accessToken, userId, listener);
    }

    /**
     * 得到用户的信息，是一个静态的基础方法
     *
     * @see "http://wiki.open.qq.com/wiki/website/get_simple_userinfo"
     */
    public static void getUserInfo(@NonNull final String accessToken, @NonNull final String userId,
            @Nullable final UserInfoListener listener) {

        new AsyncTask<Void, Void, AuthUserInfo>() {

            @Override
            protected AuthUserInfo doInBackground(Void... params) {
                String respStr = HttpUtil.doGet(
                        "https://graph.qq.com/user/get_simple_userinfo"
                                + "?access_token=" + accessToken
                                + "&oauth_consumer_key=" + ShareBlock.getInstance().QQAppId
                                + "&openid=" + userId
                                + "&format=json");
                if (respStr == null) {
                    return null;
                }

                AuthUserInfo userInfo;
                try {
                    userInfo = new AuthUserInfo();
                    JSONObject jsonObject = new JSONObject(respStr);
                    userInfo.nickName = jsonObject.getString("nickname");
                    userInfo.sex = jsonObject.getString("gender");
                    userInfo.headImgUrl = jsonObject.getString("figureurl_qq_1");
                    userInfo.userId = userId;
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

}


