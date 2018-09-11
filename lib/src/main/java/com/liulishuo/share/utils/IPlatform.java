package com.liulishuo.share.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.liulishuo.share.LoginListener;
import com.liulishuo.share.ShareListener;
import com.liulishuo.share.content.ShareContent;
import com.liulishuo.share.content.ShareContentType;

/**
 * @author Kale
 * @date 2018/9/10
 */
public interface IPlatform {

    /**
     * @return 该平台支持的方式
     */
    String[] getSupportedTypes();

    /**
     * @return 目标平台的app是否已经安装在手机上
     */
    boolean isAppInstalled(@NonNull Context context);

    /**
     * 检查当前环境，如果异常则直接终止
     *
     * type：可以是loginType和shareType
     */
    void checkEnvironment(Context context, String type, @ShareContentType int contentType);

    void doLogin(@NonNull Activity activity, @Nullable LoginListener listener);

    void doShare(@NonNull final Activity activity, String shareType, @NonNull ShareContent shareContent, @Nullable ShareListener listener);

    void onResponse(Activity activity, Intent data);

}