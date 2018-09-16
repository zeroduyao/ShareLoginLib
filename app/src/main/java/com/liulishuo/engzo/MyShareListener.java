package com.liulishuo.engzo;

import android.widget.Toast;

import kale.sharelogin.ShareListener;

/**
 * @author Jack Tony
 * @date 2015/10/23
 */
class MyShareListener extends ShareListener {

    private MainActivity activity;

    MyShareListener(MainActivity context) {
        activity = context;
    }

    @Override
    public void onSuccess() {
        super.onSuccess();
        String result = "分享成功";

        Toast.makeText(activity, result, Toast.LENGTH_SHORT).show();
        activity.handResult(result);
    }

    @Override
    public void onCancel() {
        super.onCancel();
        String result = "取消分享";

        Toast.makeText(activity, result, Toast.LENGTH_SHORT).show();
        activity.handResult(result);
    }

    @Override
    public void onError(String msg) {
        super.onError(msg);
        String result = "分享失败，出错信息：" + msg;

        Toast.makeText(activity, result, Toast.LENGTH_SHORT).show();
        activity.handResult(result);
    }

}