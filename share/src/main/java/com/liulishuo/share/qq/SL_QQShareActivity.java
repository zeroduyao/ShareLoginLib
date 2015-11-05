package com.liulishuo.share.qq;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

/**
 * @author Jack Tony
 * @date 2015/10/26
 */
public class SL_QQShareActivity extends Activity{
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        QQShareManager.sendShareMsg(this, getIntent().getExtras());
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        QQShareManager.handlerOnActivityResult(data);
        finish();
    }
}
