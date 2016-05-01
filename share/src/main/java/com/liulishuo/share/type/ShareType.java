package com.liulishuo.share.type;

import android.support.annotation.StringDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * @author Kale
 * @date 2016/3/30
 */
@Retention(RetentionPolicy.CLASS)
@StringDef({ShareType.WEIXIN_FRIEND, ShareType.WEIXIN_FRIEND_ZONE, ShareType.WEIBO_TIME_LINE, ShareType.QQ_ZONE, ShareType.QQ_FRIEND})
public @interface ShareType {

    String
            WEIXIN_FRIEND = "WEIXIN_FRIEND",
            WEIXIN_FRIEND_ZONE = "WEIXIN_FRIEND_ZONE",
            WEIBO_TIME_LINE = "WEIBO_TIME_LINE",
            QQ_ZONE = "QQ_ZONE",
            QQ_FRIEND = "QQ_FRIEND";
}