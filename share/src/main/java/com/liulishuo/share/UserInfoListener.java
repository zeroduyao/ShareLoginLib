package com.liulishuo.share;

import com.liulishuo.share.model.AuthUserInfo;

/**
 * @author Jack Tony
 * @date 2015/7/22
 */
public interface UserInfoListener {
    
    void onSuccess(AuthUserInfo userInfo);

    void onError(String msg);

}
