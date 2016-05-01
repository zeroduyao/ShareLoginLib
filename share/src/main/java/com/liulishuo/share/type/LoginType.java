package com.liulishuo.share.type;

import android.support.annotation.IntDef;

/**
 * @author Kale
 * @date 2016/3/30
 */
@IntDef({LoginType.WEIXIN, LoginType.WEIBO, LoginType.QQ})
public @interface LoginType {

    int WEIXIN = 0, WEIBO = 1, QQ = 2;
}
