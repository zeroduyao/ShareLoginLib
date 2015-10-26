package com.liulishuo.share.weibo;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

/**
 * @author Jack Tony
 * @date 2015/10/26
 */
public class WeiBoLoginActivity extends Activity{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        WeiBoLoginManager.sendLoginMsg(this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        WeiBoLoginManager.handlerOnActivityResult(requestCode, resultCode, data);
        finish();
    }
}
