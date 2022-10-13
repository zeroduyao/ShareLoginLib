package kale.sharelogin;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.support.annotation.CheckResult;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.VisibleForTesting;
import android.text.TextUtils;
import android.widget.Toast;

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

    private static EventHandlerActivity.OnCreateListener onCreateListener;

    private static WeakReference<IPlatform> wrPlatform;

    public static void init(Application application, @Nullable String curAppName, @Nullable String tempPicDir, boolean debug) {
        APP_NAME = curAppName;
        DEBUG = debug;

        if (TextUtils.isEmpty(tempPicDir)) {
            TEMP_PIC_DIR = SlUtils.generateTempPicDir(application);
        }

        if (DEBUG) {
            LogUtil.enableLog();
            com.tencent.mm.opensdk.utils.Log.setLogImpl(null); // 如果为null则会用默认的log输出
        } else {
            LogUtil.disableLog();
            com.tencent.mm.opensdk.utils.Log.setLogImpl(new com.tencent.mm.opensdk.utils.ILog() {

                @Override
                public void v(String s, String s1) {
                    // do nothing
                }

                @Override
                public void d(String s, String s1) {
                    // do nothing
                }

                @Override
                public void i(String s, String s1) {
                    // do nothing
                }

                @Override
                public void w(String s, String s1) {
                    // do nothing
                }

                @Override
                public void e(String s, String s1) {
                    // do nothing
                }
            });
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

        @Nullable IPlatform curPlatform = null;

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
                throw new UnsupportedOperationException("未找到支持该操作的平台，当前的操作类型为：" + type);
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
        final IPlatform finalCurPlatform = curPlatform;

        ShareLoginLib.onCreateListener = eventActivity -> {
            if (isLoginAction) {
                finalCurPlatform.doLogin(eventActivity, finalLoginListener);
            } else {
                assert content != null;
                finalCurPlatform.doShare(eventActivity, type, content, finalShareListener);
            }
        };
        activity.startActivity(new Intent(activity, EventHandlerActivity.class));
        activity.overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);

        wrPlatform = new WeakReference<>(curPlatform);
    }

    static void onActivityCreate(EventHandlerActivity activity) {
        onCreateListener.onCreate(activity);
    }

    static @Nullable
    IPlatform getCurPlatform() {
        return wrPlatform.get();
    }

    static void destroy() {
        // 如果不clear，那么在不保留活动的时候则会收到回调
        // 但此时的回调已经没有意义，收到回调的activity已经被销毁，所以一般这里需要清理引用
        wrPlatform.clear();
        onCreateListener = null;
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

    /**
     * 用来检测是否出现了内存泄漏
     */
    @VisibleForTesting
    public static void checkLeak(Activity activity) {
        new Handler().postDelayed(() -> {
            if (onCreateListener != null) {
                throw new RuntimeException("内存泄漏了");
            } else {
                SlUtils.printLog("没有内存泄漏，EventHandlerActivity已经destroy");
                Toast.makeText(activity, "--- DONE ---", Toast.LENGTH_SHORT).show();
            }
        }, 1000);
    }

}