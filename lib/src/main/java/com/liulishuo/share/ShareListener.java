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

    @CallSuper
    public void onCancel() {
        ShareLoginLib.printLog("share cancel");
        
        onComplete();
    }

    @CallSuper
    public void onError(String err) {
        ShareLoginLib.printErr("share error:" + err);
        
        onComplete();
    }

    @CallSuper
    public void onComplete() {
    }
}