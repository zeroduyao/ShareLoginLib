package com.liulishuo.share.utils;

/**
 * @author Kale
 * @date 2018/9/10
 */
public interface ISsoListener {

    void onError(String errorMsg);

    void onCancel();
}
