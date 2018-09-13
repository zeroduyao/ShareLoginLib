package com.liulishuo.share.utils;

import android.support.annotation.CallSuper;

import com.liulishuo.share.ShareLoginLib;

/**
 * @author Kale
 * @date 2018/9/10
 */
public interface ISsoListener {

    @CallSuper
    default void onError(String errorMsg){
        ShareLoginLib.printErr("login or share error:" + errorMsg);
        onComplete();
    }

    @CallSuper
    default void onCancel() {
        ShareLoginLib.printLog("login or share cancel:");
        onComplete();
    }

    default void onComplete() {
        
    }
}