package kale.sharelogin.utils;

import java.util.LinkedHashMap;

import android.content.Context;

import com.sina.weibo.sdk.exception.WeiboException;
import com.sina.weibo.sdk.net.AsyncWeiboRunner;
import com.sina.weibo.sdk.net.RequestListener;
import com.sina.weibo.sdk.net.WeiboParameters;

import org.json.JSONException;
import org.json.JSONObject;

import kale.sharelogin.LoginListener;
import kale.sharelogin.OAuthUserInfo;

/**
 * @author Kale
 * @date 2018/9/14
 */
public class UserInfoHelper {

    /**
     * 通过网络请求得到用户信息，这里用的是微博自带的网络请求库，也可以用腾讯的请求库
     */
    public static void getUserInfo(Context context, String url, LinkedHashMap<String, Object> params, LoginListener listener, UserAdapter adapter) {
        WeiboParameters wbParameters = new WeiboParameters(null);
        wbParameters.setParams(params);

        new AsyncWeiboRunner(context).requestAsync(url, wbParameters, "GET", new RequestListener() {
            @Override
            public void onComplete(String json) {
                OAuthUserInfo userInfo = null;
                try {
                    userInfo = adapter.json2UserInfo(new JSONObject(json));
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
                listener.onError(e.getMessage());
            }
        });
    }

    public interface UserAdapter {

        OAuthUserInfo json2UserInfo(JSONObject jsonObj) throws JSONException;

    }

}