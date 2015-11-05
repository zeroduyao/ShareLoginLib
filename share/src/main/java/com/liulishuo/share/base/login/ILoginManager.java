package com.liulishuo.share.base.login;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

/**
 * Created by echo on 5/21/15.
 */
public interface ILoginManager {

    void login(@NonNull Activity activity, @NonNull LoginListener listener);

    void getUserInformation(@NonNull String accessToken, @NonNull String userId, @Nullable UserInfoListener listener);
}
