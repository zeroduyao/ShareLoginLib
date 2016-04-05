package com.liulishuo.share.content;

import android.os.Parcelable;

/**
 * Created by echo on 5/18/15.
 */

public interface ShareContent extends Parcelable {

    /**
     * @return 分享的方式
     */
    int getType();

    /**
     * 分享的描述信息(摘要)
     */
    String getSummary();

    /**
     * 分享的标题
     */
    String getTitle();

    /**
     * 获取跳转的链接
     */
    String getURL();

    /**
     * 分享的图片
     */
    byte[] getImageBmpBytes();

    /**
     * 音频url
     */
    String getMusicUrl();

}
