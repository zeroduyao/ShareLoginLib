package com.liulishuo.share.qq;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

/**
 * @author Jack Tony
 * @date 2015/10/26
 */
public class SL_QQHandlerActivity extends Activity {

    private static final String KEY_IS_LOGIN_REQ = "key_is_login_req";

    public static Intent withIntent(Activity activity, boolean isLoginReq) {
        return new Intent(activity, SL_QQHandlerActivity.class)
                .putExtra(KEY_IS_LOGIN_REQ, isLoginReq);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState == null) { // 防止不保留活动情况下activity被重置后直接进行操作的情况
            if (getIntent().getBooleanExtra(KEY_IS_LOGIN_REQ, true)) {
                QQLoginManager.sendLoginMsg(this);
            } else {
                QQShareManager.sendShareMsg(this, getIntent().getExtras());
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d(SL_QQHandlerActivity.class.getSimpleName(), "onActivityResult: reqCode = " + requestCode + " resCode = " + resultCode);
        /**
         * 11101 instead {@link com.tencent.tauth.Tencent#REQUEST_LOGIN}
         */
        if (requestCode == 11101) {
            QQLoginManager.handlerOnActivityResult(requestCode, resultCode, data);
        } else {
            QQShareManager.handlerOnActivityResult(data);
        }
        finish();
    }
}
