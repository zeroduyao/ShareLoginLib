package kale.sharelogin.qq;

import java.util.LinkedHashMap;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;

import com.tencent.connect.common.Constants;
import com.tencent.tauth.IUiListener;
import com.tencent.tauth.UiError;

import org.json.JSONObject;

import kale.sharelogin.LoginListener;
import kale.sharelogin.OAuthUserInfo;
import kale.sharelogin.ShareLoginLib;
import kale.sharelogin.utils.IBaseListener;
import kale.sharelogin.utils.UserInfoHelper;

/**
 * @author Kale
 * @date 2018/9/11
 */
class LoginHelper {

    /**
     * http://wiki.open.qq.com/wiki/QQ%E7%99%BB%E5%BD%95%E5%92%8C%E6%B3%A8%E9%94%80
     *
     * {
     * "ret":0,
     * "pay_token":"xxxxxxxxxxxxxxxx",
     * "pf":"openmobile_android",
     * "expires_in":"7776000",
     * "openid":"xxxxxxxxxxxxxxxxxxx",
     * "pfkey":"xxxxxxxxxxxxxxxxxxx",
     * "msg":"sucess",
     * "access_token":"xxxxxxxxxxxxxxxxxxxxx"
     * }
     */
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
     * "http://wiki.open.qq.com/wiki/website/get_simple_userinfo"
     *
     * 返回结果：
     * Content-type: text/html; charset=utf-8
     * {
     * "ret":0,
     * "msg":"",
     * "nickname":"Peter",
     * "figureurl":"http://qzapp.qlogo.cn/qzapp/111111/942FEA70050EEAFBD4DCE2C1FC775E56/30",
     * "figureurl_1":"http://qzapp.qlogo.cn/qzapp/111111/942FEA70050EEAFBD4DCE2C1FC775E56/50",
     * "figureurl_2":"http://qzapp.qlogo.cn/qzapp/111111/942FEA70050EEAFBD4DCE2C1FC775E56/100",
     * "figureurl_qq_1":"http://q.qlogo.cn/qqapp/100312990/DE1931D5330620DBD07FB4A5422917B6/40",
     * "figureurl_qq_2":"http://q.qlogo.cn/qqapp/100312990/DE1931D5330620DBD07FB4A5422917B6/100",
     * "gender":"男",
     * "is_yellow_vip":"1",
     * "vip":"1",
     * "yellow_vip_level":"7",
     * "level":"7",
     * "is_yellow_year_vip":"1"
     * }
     */
    static void getUserInfo(Context context, final String accessToken, final String userId, LoginListener listener) {
        LinkedHashMap<String, Object> params = new LinkedHashMap<>();
        params.put("access_token", accessToken);
        params.put("openid", userId);
        params.put("oauth_consumer_key", ShareLoginLib.getValue(QQPlatform.KEY_APP_ID));
        params.put("format", "json");

        UserInfoHelper.getUserInfo(context, "https://graph.qq.com/user/get_simple_userinfo", params, listener, jsonObj -> {
            OAuthUserInfo userInfo = new OAuthUserInfo();
            userInfo.nickName = jsonObj.getString("nickname");
            userInfo.sex = jsonObj.getString("gender");
            userInfo.headImgUrl = jsonObj.getString("figureurl_qq_1");
            userInfo.userId = userId;
            return userInfo;
        });
    }

    abstract static class AbsUiListener implements IUiListener {

        private IBaseListener listener;

        AbsUiListener(IBaseListener listener) {
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
         */
        @Override
        public void onError(UiError resp) {
            listener.onError("code:" + resp.errorCode + ", message:" + resp.errorMessage + ", detail:" + resp.errorDetail);
        }

    }

}