package kale.sharelogin;

import android.support.annotation.CallSuper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import kale.sharelogin.utils.IBaseListener;
import kale.sharelogin.utils.SlUtils;

/**
 * @author Kale
 * @date 2018/9/10
 */
public class LoginListener implements IBaseListener {

    /**
     * @param accessToken 第三方给的一次性token，几分钟内会失效
     * @param uId         用户的id
     * @param expiresIn   过期时间
     * @param wholeData   第三方本身返回的全部数据
     */
    @CallSuper
    public void onReceiveToken(String accessToken, String uId, long expiresIn, @Nullable String wholeData) {
        SlUtils.printLog("login success \naccessToken = " + accessToken + "\nuserId = " + uId + "\nexpires_in = " + expiresIn);
    }

    /**
     * 得到第三方平台的用户信息
     *
     * 本库希望不要获取太多的用户信息，故{OAuthUserInfo}仅提供基础的信息，如果不满足请请提交{issue}
     */
    public void onReceiveUserInfo(@NonNull OAuthUserInfo userInfo) {
        SlUtils.printLog("nickname = " + userInfo.nickName + "\nsex = " + userInfo.sex + "\nid = " + userInfo.userId);
        onComplete();
    }
}