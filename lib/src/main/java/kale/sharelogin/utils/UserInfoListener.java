package kale.sharelogin.utils;

import com.sina.weibo.sdk.exception.WeiboException;
import com.sina.weibo.sdk.net.RequestListener;

import org.json.JSONException;
import org.json.JSONObject;

import kale.sharelogin.LoginListener;
import kale.sharelogin.OAuthUserInfo;

/**
 * @author Kale
 * @date 2018/9/14
 * 
 * 通过网络请求得到用户信息
 */
public abstract class UserInfoListener implements RequestListener {

    private LoginListener listener;

    protected UserInfoListener(LoginListener listener) {
        this.listener = listener;
    }

    @Override
    public void onComplete(String json) {
        OAuthUserInfo userInfo = null;
        try {
            userInfo = json2UserInfo(new JSONObject(json));
        } catch (JSONException e) {
            e.printStackTrace();
            listener.onError(e.getMessage());
        }
        if (userInfo != null) {
            listener.onReceiveUserInfo(userInfo);
        }
    }

    @Override
    public void onWeiboException(WeiboException e) {
        e.printStackTrace();
        listener.onError(e.getMessage());
    }

    public abstract OAuthUserInfo json2UserInfo(JSONObject jsonObj) throws JSONException;
}