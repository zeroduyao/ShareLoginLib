package com.liulishuo.share.type;

import android.support.annotation.IntDef;

/**
 * @author Kale
 * @date 2016/3/30
 */
@IntDef({ShareType.WEIXIN_FRIEND, ShareType.WEIXIN_FRIEND_ZONE, ShareType.WEIBO_TIME_LINE, ShareType.QQ_ZONE, ShareType.QQ_FRIEND})
public @interface ShareType {

    int
            WEIXIN_FRIEND = 0,
            WEIXIN_FRIEND_ZONE = 1,
            WEIBO_TIME_LINE = 2,
            QQ_ZONE = 3,
            QQ_FRIEND = 4;
}