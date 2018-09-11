package com.liulishuo.share;

import android.support.annotation.CallSuper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.liulishuo.share.utils.ISsoListener;

/**
 * @author Kale
 * @date 2018/9/10
 */
public class LoginListener implements ISsoListener {

    /**
     * @param accessToken 第三方给的一次性token，几分钟内会失效
     * @param uId         用户的id
     * @param expiresIn   过期时间
     * @param wholeData   第三方本身返回的全部json数据
     */
    @CallSuper
    public void onSuccess(String accessToken, String uId, long expiresIn, @Nullable String wholeData) {
        ShareLoginLib.printLog("login success \naccessToken = " + accessToken + "\nuserId = " + uId + "\nexpires_in = " + expiresIn);
        onComplete();
    }

    /**
     * 得到用户第三方平台的信息
     */
    public void onReceiveUserInfo(@NonNull OAuthUserInfo userInfo) {
        final String info = "nickname = " + userInfo.nickName
                + "\nsex = " + userInfo.sex
                + "\nid = " + userInfo.userId;
        ShareLoginLib.printLog(info);
    }

    @CallSuper
    public void onError(String errorMsg) {
        ShareLoginLib.printErr("login error:"+errorMsg);
        onComplete();
    }

    @CallSuper
    public void onCancel() {
        ShareLoginLib.printLog("login cancel");
        onComplete();
    }

    @CallSuper
    public void onComplete() {
    }
}
