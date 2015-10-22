package com.liulishuo.engzo;

import com.liulishuo.share.base.login.LoginListener;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

/**
 * @author Jack Tony
 * @date 2015/10/18
 */
public class MyLoginListener implements LoginListener {

    public static final String TAG = "LoginListener";

    private Context mContext;

    public MyLoginListener(Context context) {
        mContext = context;
    }

    @Override
    public void onSuccess(String uId, String accessToken, long expiresIn, String data) {
        Log.d(TAG, "uid = " + uId);
        Log.d(TAG, "accessToken = " + accessToken);
        Log.d(TAG, "expires_in = " + expiresIn);
        Log.d(TAG, "登录成功");
        Toast.makeText(mContext, "登录成功", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onError(String msg) {
        Toast.makeText(mContext, "登录失败,失败信息：" + msg, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onCancel() {
        Log.d(TAG, "取消登录");
        Toast.makeText(mContext, "取消登录", Toast.LENGTH_SHORT).show();
    }
}
