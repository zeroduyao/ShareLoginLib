package com.liulishuo.share.qq;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

/**
 * @author Jack Tony
 * @date 2015/10/26
 */
public class SL_QQHandlerActivity extends Activity {

    private static final String KEY_IS_LOGIN_REQ = "key_is_login_req";

    private boolean isLoginReq;

    public static Intent withIntent(Activity activity, boolean isLoginReq) {
        return new Intent(activity, SL_QQHandlerActivity.class)
                .putExtra(KEY_IS_LOGIN_REQ, isLoginReq);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        isLoginReq = getIntent().getBooleanExtra(KEY_IS_LOGIN_REQ, false);
        if (isLoginReq) {
            QQLoginManager.sendLoginMsg(this);
        } else {
            QQShareManager.sendShareMsg(this, getIntent().getExtras());
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (isLoginReq) {
            QQLoginManager.handlerOnActivityResult(requestCode, resultCode, data);
        } else {
            QQShareManager.handlerOnActivityResult(data);
        }
        finish();
    }
}
