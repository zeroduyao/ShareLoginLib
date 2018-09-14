package com.liulishuo.share;

import android.support.annotation.CallSuper;

import com.liulishuo.share.utils.ISsoListener;
import com.liulishuo.share.utils.SlUtils;

/**
 * @author Kale
 * @date 2018/9/10
 */
public class ShareListener implements ISsoListener {

    @CallSuper
    public void onSuccess() {
        SlUtils.printLog("share success");

        onComplete();
    }

}