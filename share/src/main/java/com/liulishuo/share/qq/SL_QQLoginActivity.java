package com.liulishuo.share.qq;

import com.liulishuo.share.LoginManager;
import com.liulishuo.share.ShareBlock;
import com.tencent.connect.common.Constants;
import com.tencent.tauth.IUiListener;
import com.tencent.tauth.Tencent;
import com.tencent.tauth.UiError;

import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;

/**
 * @author Jack Tony
 * @date 2015/10/26
 */
public class SL_QQLoginActivity extends Activity {

    private IUiListener mUiListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        String appId = ShareBlock.getInstance().QQAppId;
        if (TextUtils.isEmpty(appId)) {
            throw new NullPointerException("请通过shareBlock初始化appId");
        }

        if (savedInstanceState == null) { // 防止不保留活动情况下activity被重置后直接进行操作的情况
            doLogin(this, appId, LoginManager.listener);
        }
    }

    /**
     * 解析用户登录的结果
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Tencent.onActivityResultData(requestCode, resultCode, data, mUiListener);
        finish();
    }

    private void doLogin(Activity activity, String appId, final LoginManager.LoginListener listener) {
        Tencent tencent = Tencent.createInstance(appId, activity.getApplicationContext());
        mUiListener = new IUiListener() {
            @Override
            public void onComplete(Object object) {
                if (listener != null) {
                    JSONObject jsonObject = ((JSONObject) object);
                    try {
                        String token = jsonObject.getString(Constants.PARAM_ACCESS_TOKEN);
                        String openId = jsonObject.getString(Constants.PARAM_OPEN_ID);
                        String expires = jsonObject.getString(Constants.PARAM_EXPIRES_IN);
                        listener.onSuccess(token, openId, Long.valueOf(expires), object.toString());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onError(UiError uiError) {
                if (listener != null) {
                    listener.onError(uiError.errorCode + " - " + uiError.errorMessage + " - " + uiError.errorDetail);
                }
            }

            @Override
            public void onCancel() {
                if (listener != null) {
                    listener.onCancel();
                }
            }
        };

        if (!tencent.isSessionValid()) {
            tencent.login(activity, ShareBlock.getInstance().QQScope, mUiListener);
        } else {
            tencent.logout(activity);
        }
    }
}
