package kale.sharelogin.utils;

import android.support.annotation.CallSuper;

/**
 * @author Kale
 * @date 2018/9/10
 */
public interface IBaseListener {

    @CallSuper
    default void onError(String errorMsg) {
        SlUtils.printErr("login or share error:" + errorMsg);
        onComplete();
    }

    @CallSuper
    default void onCancel() {
        SlUtils.printLog("login or share canceled");
        onComplete();
    }

    default void onComplete() {

    }
}