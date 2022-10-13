package kale.sharelogin;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import kale.sharelogin.utils.SlUtils;

/**
 * Created by echo on 5/19/15.
 * 用来处理微信登录、微信分享的activity。这里真不知道微信非要个activity干嘛，愚蠢的设计!
 * 参考文档: https://open.weixin.qq.com/cgi-bin/showdocument?action=dir_list&t=resource/res_list&verify=1&id=open1419317853&lang=zh_CN
 *
 * https://open.weixin.qq.com/cgi-bin/showdocument?action=dir_list&t=resource/res_list&verify=1&id=open1419317851&token=&lang=zh_CN
 */
public class EventHandlerActivity extends Activity {

    public static final String KEY_REQUEST_CODE = "share_login_lib_key_request_code";

    public static final String KEY_RESULT_CODE = "share_login_lib_key_result_code";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 为了防止这个activity关不掉，这里给用户一个点击关闭的功能
        findViewById(android.R.id.content).setOnClickListener(v -> finish());

        if (savedInstanceState != null) {
            SlUtils.printLog("EventHandlerActivity:onCreate(2) intent:" + getIntent().getExtras());
            
            handleResp(getIntent());
        } else {
            ShareLoginLib.onActivityCreate(this);
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        SlUtils.printLog("EventHandlerActivity:onNewIntent() intent:" + intent.getExtras());

        handleResp(intent);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);

        if (intent != null) {
            intent.putExtra(KEY_REQUEST_CODE, requestCode);
            intent.putExtra(KEY_RESULT_CODE, resultCode);
            SlUtils.printLog("EventHandlerActivity:onActivityResult() intent:" + intent.getExtras());
        } else {
            SlUtils.printErr("EventHandlerActivity:onActivityResult() intent is null");
        }

        handleResp(intent);

        finish();
    }

    private void handleResp(Intent data) {
        if (ShareLoginLib.getCurPlatform() != null) {
            ShareLoginLib.getCurPlatform().onResponse(this, data);
        } else {
            SlUtils.printErr("ShareLoginLib.curPlatform is null");
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        SlUtils.printLog("EventHandlerActivity:onResume()");
    }

    @Override
    protected void onPause() {
        super.onPause();
        SlUtils.printLog("EventHandlerActivity:onPause()");
    }

    @Override
    protected void onStop() {
        super.onStop();
        SlUtils.printLog("EventHandlerActivity:onStop()");
    }

    @Override
    protected void onDestroy() {
        SlUtils.printLog("EventHandlerActivity:onDestroy()");
        ShareLoginLib.destroy();
        super.onDestroy();
    }

    public interface OnCreateListener {

        void onCreate(EventHandlerActivity activity);
    }

}