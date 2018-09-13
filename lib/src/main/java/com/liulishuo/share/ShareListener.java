package com.liulishuo.share;

import android.support.annotation.CallSuper;

import com.liulishuo.share.utils.ISsoListener;

/**
 * @author Kale
 * @date 2018/9/10
 */
public class ShareListener implements ISsoListener {

    @CallSuper
    public void onSuccess() {
        ShareLoginLib.printLog("share success");

        onComplete();
    }

}