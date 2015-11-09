package com.liulishuo.share.base.share;

/**
 * Created by echo on 5/20/15.
 */
public interface ShareStateListener {

    /**
     * 分享成功
     */
    void onSuccess();

    void onCancel();

    void onError(String msg);
}
