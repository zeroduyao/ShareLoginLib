package kale.sharelogin;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.CheckResult;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import com.sina.weibo.sdk.utils.LogUtil;

import kale.sharelogin.content.ShareContent;
import kale.sharelogin.utils.SlUtils;

/**
 * @author Kale
 * @date 2018/9/11
 */
public class ShareLoginLib {

    public static boolean DEBUG = false;

    public static String APP_NAME, TEMP_PIC_DIR;

    private static Map<String, String> sValueMap;

    private static List<Class<? extends IPlatform>> supportPlatforms;

    private static IPlatform curPlatform;

    private static EventHandlerActivity.OnCreateListener onCreateListener;

    public static void init(Application application, @Nullable String curAppName, @Nullable String tempPicDir, boolean debug) {
        APP_NAME = curAppName;
        DEBUG = debug;

        if (TextUtils.isEmpty(tempPicDir)) {
            TEMP_PIC_DIR = SlUtils.generateTempPicDir(application);
        }

        if (DEBUG) {
            LogUtil.enableLog();
        } else {
            LogUtil.disableLog();
        }
    }

    public static void initPlatforms(Map<String, String> keyValue, List<Class<? extends IPlatform>> platforms) {
        sValueMap = keyValue;
        supportPlatforms = platforms;
    }

    public static void doLogin(@NonNull final Activity activity, String type, @Nullable LoginListener listener) {
        if (type == null) {
            if (listener != null) {
                listener.onError("type is null");
            }
            return;
        }
        doAction(activity, true, type, null, listener, null);
    }

    public static void doShare(@NonNull final Activity activity, String type, @NonNull ShareContent shareContent, @Nullable ShareListener listener) {
        if (type == null || shareContent == null) {
            if (listener != null) {
                listener.onError("type or shareContent is null");
            }
            return;
        }
        doAction(activity, false, type, shareContent, null, listener);
    }

    private static void doAction(Activity activity, boolean isLoginAction, @NonNull String type, @Nullable ShareContent content,
            LoginListener loginListener, ShareListener shareListener) {

        // 1. 得到目前支持的平台列表
        ArrayList<IPlatform> platforms = new ArrayList<>();

        for (Class<? extends IPlatform> platformClz : supportPlatforms) {
            platforms.add(SlUtils.createPlatform(platformClz));
        }

        // 2. 根据type匹配出一个目标平台
        for (IPlatform platform : platforms) {
            for (String s : platform.getSupportedTypes()) {
                if (s.equals(type)) {
                    curPlatform = platform;
                    break;
                }
            }
        }

        // 3. 初始化监听器
        if (loginListener == null) {
            loginListener = new LoginListener();
        }

        if (shareListener == null) {
            shareListener = new ShareListener();
        }

        // 4. 检测当前运行环境，看是否正常
        try {
            if (curPlatform == null) {
                throw new UnsupportedOperationException("未找到支持该操作的平台");
            } else {
                curPlatform.checkEnvironment(activity, type, content != null ? content.getType() : ShareContent.NO_CONTENT);
            }
        } catch (Throwable throwable) {
            if (isLoginAction) {
                loginListener.onError(throwable.getMessage());
            } else {
                shareListener.onError(throwable.getMessage());
            }
            return;
        }

        // 5. 启动辅助的activity，最终执行具体的操作

        final LoginListener finalLoginListener = loginListener;
        final ShareListener finalShareListener = shareListener;

        ShareLoginLib.onCreateListener = eventActivity -> {
            if (DEBUG) {
                // 仅debug模式才只持有activity的引用，用来检测activity是否已经关闭
                SlUtils.sEventHandlerActivity = eventActivity;
            }

            if (isLoginAction) {
                curPlatform.doLogin(eventActivity, finalLoginListener);
            } else {
                assert content != null;
                curPlatform.doShare(eventActivity, type, content, finalShareListener);
            }
        };
        activity.startActivity(new Intent(activity, EventHandlerActivity.class));
        activity.overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }

    static void onActivityCreate(EventHandlerActivity activity) {
        onCreateListener.onCreate(activity);
    }

    static IPlatform getCurPlatform() {
        return curPlatform;
    }

    static void destroy() {
        curPlatform = null;
        onCreateListener = null;
        SlUtils.sEventHandlerActivity = null;
    }

    /**
     * 判断目标平台的app是否已经安装了
     */
    @CheckResult
    public static boolean isAppInstalled(Context context, Class<? extends IPlatform> platformClz) {
        return SlUtils.createPlatform(platformClz).isAppInstalled(context.getApplicationContext());
    }

    /**
     * 得到用户的信息，会在{@link LoginListener#onReceiveUserInfo(OAuthUserInfo)}中进行回调
     */
    public static void getUserInfo(Context context, Class<? extends IPlatform> platformClz, String accessToken, String uid, LoginListener listener) {
        SlUtils.createPlatform(platformClz).getUserInfo(context, accessToken, uid, listener);
    }

    public static String getValue(String key) {
        return sValueMap.get(key);
    }

}