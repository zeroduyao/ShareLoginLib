package com.liulishuo.share.base.login;

/**
 * @author Jack Tony
 * @date 2015/7/22
 */
public interface LoginListener {

    void onSuccess(String accessToken, String uId , long expiresIn, String wholeData);

    void onError(String msg);

    void onCancel();
}
