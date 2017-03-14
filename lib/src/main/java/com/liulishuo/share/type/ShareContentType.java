package com.liulishuo.share.type;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import android.support.annotation.IntDef;

/**
 * Created by echo on 5/18/15.
 */
@Retention(RetentionPolicy.SOURCE)
@IntDef({ShareContentType.TEXT, ShareContentType.PIC, ShareContentType.WEBPAGE, ShareContentType.MUSIC})
public @interface ShareContentType {

    int TEXT = 1, PIC = 2, WEBPAGE = 3, MUSIC = 4;
}