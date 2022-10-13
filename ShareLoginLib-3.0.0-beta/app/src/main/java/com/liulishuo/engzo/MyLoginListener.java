package com.liulishuo.engzo;

import android.support.annotation.NonNull;
import android.widget.Toast;

import kale.sharelogin.LoginListener;
import kale.sharelogin.OAuthUserInfo;

/**
 * @author Kale
 * @date 2016/4/5
 *
 * 这里的每个回调打印toast是为了测试不保留活动情况的activity情况，因为toast是全局的
 */
public class MyLoginListener extends LoginListener {

    private MainActivity activity;

    MyLoginListener(MainActivity activity) {
        this.activity = activity;
    }

    @Override
    public void onReceiveToken(String accessToken, String userId, long expiresIn, String data) {
        super.onReceiveToken(accessToken, userId, expiresIn, data);
        String result = "登录成功";

        Toast.makeText(activity, result, Toast.LENGTH_SHORT).show();
        activity.handResult(result);
    }

    @Override
    public void onReceiveUserInfo(@NonNull OAuthUserInfo userInfo) {
        String info = " nickname = " + userInfo.nickName + "\n"
                + " sex = " + userInfo.sex + "\n"
                + " id = " + userInfo.userId;

//        Toast.makeText(activity, info, Toast.LENGTH_SHORT).show();
        activity.onGotUserInfo(info, userInfo.headImgUrl);
    }

    @Override
    public void onCancel() {
        super.onCancel();
        String result = "取消登录";

        Toast.makeText(activity, result, Toast.LENGTH_SHORT).show();
        activity.handResult(result);
    }

    @Override
    public void onError(String msg) {
        super.onError(msg);
        String result = "登录失败,失败信息：" + msg;

        Toast.makeText(activity, result, Toast.LENGTH_SHORT).show();
        activity.handResult(result);
    }
    
}