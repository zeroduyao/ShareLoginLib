package com.liulishuo.engzo;

import com.liulishuo.share.base.share.ShareStateListener;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

/**
 * @author Jack Tony
 * @date 2015/10/23
 */
public class MyShareListener implements ShareStateListener {

    private String TAG = "ShareListener";

    private Context mContext;

    public MyShareListener(Context context) {
        mContext = context;
    }

    @Override
    public void onSuccess() {
        Log.d(TAG, "分享成功");
        Toast.makeText(mContext, "分享成功", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onError(String msg) {
        Log.d(TAG, "分享失败，出错信息：" + msg);
        Toast.makeText(mContext, "分享失败，出错信息：" + msg, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onCancel() {
        Log.d(TAG, "取消分享");
        Toast.makeText(mContext, "取消分享", Toast.LENGTH_SHORT).show();
    }
}
