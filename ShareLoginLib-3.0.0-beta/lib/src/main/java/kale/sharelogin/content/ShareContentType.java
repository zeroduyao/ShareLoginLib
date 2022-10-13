package kale.sharelogin.content;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import android.support.annotation.IntDef;

/**
 * Created by echo on 5/18/15.
 */
@Retention(RetentionPolicy.SOURCE)
@IntDef({ShareContentType.TEXT, ShareContentType.PIC, ShareContentType.WEBPAGE, ShareContentType.MUSIC, ShareContent.NO_CONTENT})
public @interface ShareContentType {

    int TEXT = 1, PIC = 2, WEBPAGE = 3, MUSIC = 4;
}