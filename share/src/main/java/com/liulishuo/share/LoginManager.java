package com.liulishuo.share;

import com.liulishuo.share.model.AuthUserInfo;
import com.liulishuo.share.qq.SL_QQLoginActivity;
import com.liulishuo.share.type.LoginType;
import com.liulishuo.share.util.HttpUtil;
import com.liulishuo.share.weibo.SL_WeiBoLoginActivity;
import com.liulishuo.share.weixin.WeiXinLoginManager;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.widget.Toast;

/**
 * @author Kale
 * @date 2016/3/30
 */
public class LoginManager {

    public static LoginListener listener;

    public static void login(@NonNull Activity activity, @Nullable LoginListener listener, LoginType type) {
        LoginManager.listener = listener;
        switch (type) {
            case WEIXIN:
                if (!ShareBlock.isWeiXinInstalled(activity)) {
                    Toast.makeText(activity, "请安装微信哦~", Toast.LENGTH_SHORT).show();
                    return;
                }
                new WeiXinLoginManager().login(activity.getApplicationContext());
                break;
            case WEIBO:
                activity.startActivity(new Intent(activity, SL_WeiBoLoginActivity.class));
                activity.overridePendingTransition(android.R.anim.fade_in,android.R.anim.fade_out);
                break;
            case QQ:
                if (!ShareBlock.isQQInstalled(activity)) {
                    Toast.makeText(activity, "请先安装QQ哦~", Toast.LENGTH_SHORT).show();
                    return;
                }
                activity.startActivity(new Intent(activity, SL_QQLoginActivity.class));
                activity.overridePendingTransition(android.R.anim.fade_in,android.R.anim.fade_out);
                break;
        }
    }

    public static void getUserInfo(final @NonNull String accessToken, final @NonNull String uid,
            @Nullable final UserInfoListener listener, LoginType type) {
        switch (type) {
            case WEIXIN:
                getWeiXinUserInfo(accessToken, uid, listener);
                break;
            case WEIBO:
                getWeiBoUserInfo(accessToken, uid, listener);
                break;
            case QQ:
                getQQUserInfo(accessToken, uid, listener);
                break;
        }
    }

    public static void recycle() {
        listener = null;
    }

    ///////////////////////////////////////////////////////////////////////////
    // 微信
    ///////////////////////////////////////////////////////////////////////////

    public static void getWeiXinUserInfo(@NonNull final String accessToken, @NonNull final String userId,
            @Nullable final UserInfoListener listener) {

        new AsyncTask<Void, Void, AuthUserInfo>() {

            @Override
            protected AuthUserInfo doInBackground(Void... params) {
                String respStr = HttpUtil.doGet("https://api.weixin.qq.com/sns/userinfo"
                        + "?access_token=" + accessToken + "&openid=" + userId);
                if (respStr == null) {
                    return null;
                }

                AuthUserInfo userInfo = null;
                try {
                    JSONObject jsonObject = new JSONObject(respStr);

                    userInfo = new AuthUserInfo();
                    userInfo.nickName = jsonObject.getString("nickname");
                    userInfo.sex = jsonObject.getString("sex");
                    userInfo.headImgUrl = jsonObject.getString("headimgurl");
                    userInfo.userId = jsonObject.getString("unionid");
                } catch (JSONException e) {
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

    ///////////////////////////////////////////////////////////////////////////
    // 微博
    ///////////////////////////////////////////////////////////////////////////

    /**
     * 得到微博用户的信息
     *
     * @see "http://open.weibo.com/wiki/2/users/show"
     */
    public static void getWeiBoUserInfo(final @NonNull String accessToken, final @NonNull String uid,
            @Nullable final UserInfoListener listener) {

        new AsyncTask<Void, Void, AuthUserInfo>() {

            @Override
            protected AuthUserInfo doInBackground(Void... params) {
                String respStr = HttpUtil.doGet("https://api.weibo.com/2/users/show.json"
                        + "?access_token=" + accessToken + "&uid=" + uid);
                if (respStr == null) {
                    return null;
                }

                AuthUserInfo userInfo = null;
                try {
                    userInfo = new AuthUserInfo();
                    JSONObject jsonObject = new JSONObject(respStr);
                    userInfo.nickName = jsonObject.getString("screen_name");
                    userInfo.sex = jsonObject.getString("gender");
                    userInfo.headImgUrl = jsonObject.getString("avatar_large");
                    userInfo.userId = jsonObject.getString("id");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                return userInfo;
            }

            @Override
            protected void onPostExecute(@Nullable AuthUserInfo userInfo) {
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

    ///////////////////////////////////////////////////////////////////////////
    // QQ
    ///////////////////////////////////////////////////////////////////////////

    /**
     * 得到用户的信息，是一个静态的基础方法
     *
     * @see "http://wiki.open.qq.com/wiki/website/get_simple_userinfo"
     */
    public static void getQQUserInfo(@NonNull final String accessToken, @NonNull final String userId,
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

    public interface LoginListener {

        void onSuccess(String accessToken, String uId, long expiresIn, @Nullable String wholeData);

        void onError(String msg);

        void onCancel();
    }

}
