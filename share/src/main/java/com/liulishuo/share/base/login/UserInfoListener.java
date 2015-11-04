package com.liulishuo.share.base.login;

import com.liulishuo.share.base.AuthUserInfo;

/**
 * @author Jack Tony
 * @date 2015/7/22
 */
public interface UserInfoListener {
    
    void onSuccess(AuthUserInfo userInfo);

    void onError(String msg);

}
