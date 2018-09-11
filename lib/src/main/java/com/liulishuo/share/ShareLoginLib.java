package com.liulishuo.share;

import java.io.File;
import java.util.ArrayList;
import java.util.Map;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;

import com.liulishuo.share.content.ShareContent;
import com.liulishuo.share.content.ShareContentPic;
import com.liulishuo.share.qq.QQPlatform;
import com.liulishuo.share.utils.EventHandlerActivity;
import com.liulishuo.share.utils.IPlatform;
import com.liulishuo.share.utils.SlUtils;
import com.liulishuo.share.weibo.WeiBoPlatform;
import com.liulishuo.share.weixin.WeiXinPlatform;
import com.sina.weibo.sdk.exception.WeiboException;
import com.sina.weibo.sdk.net.RequestListener;
import com.sina.weibo.sdk.utils.LogUtil;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * @author Kale
 * @date 2018/9/11
 */
public class ShareLoginLib {

    public static final String TAG = "ShareLoginLib";

    private static Map<String, String> sMap;

    private static boolean DEBUG = false;

    public static String APP_NAME, TEMP_PIC_PATH;

    private static Class<? extends IPlatform>[] supportPlatforms;

    public static IPlatform curPlatform;

    public static EventHandlerActivity.OnCreateListener onCreateListener;

    public static void initParams(Application application, @Nullable String appName, @Nullable String tempPicPath) {
        APP_NAME = appName;

        if (TextUtils.isEmpty(tempPicPath)) {
            if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
                try {
                    TEMP_PIC_PATH = application.getExternalCacheDir() + File.separator;
                    File dir = new File(TEMP_PIC_PATH);
                    if (!dir.exists()) {
                        dir.mkdirs();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    TEMP_PIC_PATH = null;
                }
            }
        }
    }

    public static void initPlatforms(Map<String, String> keyValue, Class<? extends IPlatform>... platforms) {
        sMap = keyValue;
        supportPlatforms = platforms;
    }

    public static void doLogin(@NonNull final Activity activity, String type, @Nullable LoginListener listener) {
        doAction(activity, true, type, null, listener, null);
    }

    public static void doShare(@NonNull final Activity activity, String type, @NonNull ShareContent shareContent, @Nullable ShareListener listener) {
        if (shareContent instanceof ShareContentPic) {
            final ShareContentPic content = (ShareContentPic) shareContent;
            content.setThumbBmpBytes(SlUtils.getImageThumbByteArr(content.getThumbBmp()));
            content.setLargeBmpPath(SlUtils.saveLargeBitmap(content.getLargeBmp()));

            shareContent = content;
        }

        doAction(activity, false, type, shareContent, null, listener);
    }

    private static void doAction(Activity activity, boolean login, String type, @Nullable ShareContent content,
            LoginListener loginListener, ShareListener shareListener) {

        ArrayList<IPlatform> platforms = new ArrayList<>();

        for (Class<? extends IPlatform> platformClz : supportPlatforms) {
            try {
                platforms.add(platformClz.newInstance());
            } catch (InstantiationException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }

        for (IPlatform platform : platforms) {
            for (String s : platform.getSupportedTypes()) {
                if (s.equals(type)) {
                    curPlatform = platform;
                    break;
                }
            }
        }

        try {
            curPlatform.checkEnvironment(activity, type, content != null ? content.getType() : -1);
        } catch (Throwable throwable) {
            if (loginListener != null) {
                loginListener.onError(throwable.getMessage());
                return;
            }
        }

        onCreateListener = (eventAct) -> {
            if (login) {
                curPlatform.doLogin(eventAct, loginListener);
            } else {
                assert content != null;
                curPlatform.doShare(eventAct, type, content, shareListener);
            }
        };

        activity.startActivity(new Intent(activity, EventHandlerActivity.class));
        activity.overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }

    public static String getValue(String key) {
        return sMap.get(key);
    }

    public static void debug(boolean debug) {
        DEBUG = debug;

        if (DEBUG) {
            LogUtil.enableLog();
        } else {
            LogUtil.disableLog();
        }
    }

    public static void printLog(String message) {
        if (DEBUG) {
            Log.i(TAG, message);
        }
    }

    public static void printErr(String message) {
        if (DEBUG) {
            Log.e(TAG, message);
        }
    }


    public static boolean isQQInstalled(Context context) {
        return new QQPlatform().isAppInstalled(context);
    }

    public static boolean isWeiBoInstalled(Context context) {
        return new WeiBoPlatform().isAppInstalled(context);
    }

    public static boolean isWeiXinInstalled(Context context) {
        return new WeiXinPlatform().isAppInstalled(context);
    }

    public abstract static class UserInfoListener implements RequestListener {

        private LoginListener listener;

        public UserInfoListener(LoginListener listener) {
            this.listener = listener;
        }

        @Override
        public void onComplete(String s) {
            OAuthUserInfo userInfo = null;
            try {
                userInfo = onSuccess(new JSONObject(s));
            } catch (JSONException e) {
                e.printStackTrace();
                if (listener != null) {
                    listener.onError(e.getMessage());
                }
            }
            if (listener != null && userInfo != null) {
                listener.onReceiveUserInfo(userInfo);
            }
        }

        @Override
        public void onWeiboException(WeiboException e) {
            if (DEBUG) {
                e.printStackTrace();
            }
            if (listener != null) {
                listener.onError(e.getMessage());
            }
        }

        public abstract OAuthUserInfo onSuccess(JSONObject jsonObj) throws JSONException;
    }

}
