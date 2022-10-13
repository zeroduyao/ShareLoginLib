package kale.sharelogin;

import android.support.annotation.CallSuper;

import kale.sharelogin.utils.IBaseListener;
import kale.sharelogin.utils.SlUtils;

/**
 * @author Kale
 * @date 2018/9/10
 */
public class ShareListener implements IBaseListener {

    @CallSuper
    public void onSuccess() {
        SlUtils.printLog("share success");

        onComplete();
    }

}