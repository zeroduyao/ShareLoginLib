package com.liulishuo.share.base.login;

import android.content.Context;

/**
 * Created by echo on 5/21/15.
 */
public interface ILoginManager {

    void login(Context context, LoginListener listener);

    //void getUserInfo(Context context ,final @NonNull GetUserListener listener);
}
