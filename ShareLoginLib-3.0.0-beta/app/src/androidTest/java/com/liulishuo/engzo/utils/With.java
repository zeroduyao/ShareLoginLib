package com.liulishuo.engzo.utils;

import android.support.test.uiautomator.UiSelector;

/**
 * @author Kale
 * @date 2016/10/7
 */

public final class With {

    private With() { }

    public static UiSelector text(String text){
        return new UiSelector().text(text);
    }
    
    public static UiSelector clazz(Class clz){
        return new UiSelector().className(clz);
    }

    public static UiSelector index(int index) {
        return new UiSelector().index(index);
    }

    public static UiSelector res(String id) {
        return new UiSelector().resourceId(id);
    }

    public static UiSelector desc(String desc) {
        return new UiSelector().description(desc);
    }
}
