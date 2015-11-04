package com.liulishuo.share.qq;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

/**
 * @author Jack Tony
 * @date 2015/10/26
 */
public class QQLoginActivity extends Activity{
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        QQLoginManager.sendLoginMsg(this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        QQLoginManager.handlerOnActivityResult(requestCode, resultCode, data);
        finish();
    }
}
