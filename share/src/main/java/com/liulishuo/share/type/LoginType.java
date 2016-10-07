package com.liulishuo.share.type;

import android.support.annotation.StringDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * @author Kale
 * @date 2016/3/30
 */
@Retention(RetentionPolicy.SOURCE)
@StringDef({LoginType.WEIXIN, LoginType.WEIBO, LoginType.QQ})
public @interface LoginType {

    String QQ = "QQ", WEIBO = "WEIBO", WEIXIN = "WEIXIN";
}
