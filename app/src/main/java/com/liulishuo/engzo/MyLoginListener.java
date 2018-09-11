package com.liulishuo.engzo;

import android.support.annotation.NonNull;

import com.liulishuo.share.LoginListener;
import com.liulishuo.share.OAuthUserInfo;

/**
 * @author Kale
 * @date 2016/4/5
 */
public class MyLoginListener extends LoginListener {

    private MainActivity activity;

    MyLoginListener(MainActivity activity) {
        this.activity = activity;
    }

    @Override
    public void onSuccess(String accessToken, String userId, long expiresIn, String data) {
        super.onSuccess(accessToken, userId, expiresIn, data);
        String result = "登录成功";
        activity.handResult(result);
    }

    @Override
    public void onError(String msg) {
        super.onError(msg);
        String result = "登录失败,失败信息：" + msg;
        activity.handResult(result);
    }

    @Override
    public void onCancel() {
        super.onCancel();
        String result = "取消登录";
        activity.handResult(result);
    }

    @Override
    public void onReceiveUserInfo(@NonNull OAuthUserInfo userInfo) {
        final String info = " nickname = " + userInfo.nickName + "\n"
                + " sex = " + userInfo.sex + "\n"
                + " id = " + userInfo.userId;

        activity.onGotUserInfo(info, userInfo.headImgUrl);
    }
}
