package kale.sharelogin;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import kale.sharelogin.content.ShareContent;
import kale.sharelogin.content.ShareContentType;

/**
 * @author Kale
 * @date 2018/9/10
 */
public interface IPlatform {

    /**
     * @return 该平台支持的操作类型
     */
    String[] getSupportedTypes();

    /**
     * @return 目标平台的app是否已经安装在手机上
     */
    boolean isAppInstalled(@NonNull Context context);

    /**
     * 检查当前环境，如果异常则直接终止
     *
     * @param type             当前平台支持的操作类型
     * @param shareContentType 分享时传入的分享类型，如果是登录则会传{@link ShareContent#NO_CONTENT}
     */
    void checkEnvironment(Context context, @NonNull String type, @ShareContentType int shareContentType);

    /**
     * 执行登录的操作
     */
    void doLogin(@NonNull Activity activity, @NonNull LoginListener listener);

    /**
     * 执行分享的操作
     */
    void doShare(Activity activity, String shareType, @NonNull ShareContent shareContent, @NonNull ShareListener listener);

    /**
     * 处理响应的结果
     */
    void onResponse(@NonNull Activity activity, @Nullable Intent data);

    /**
     * 得到用户的信息，会在{@link LoginListener#onReceiveUserInfo(OAuthUserInfo)}中进行回调
     */
    void getUserInfo(Context context, String accessToken, String uid, final LoginListener listener);

}