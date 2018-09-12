package com.liulishuo.share.content;

import android.os.Parcelable;

/**
 * Created by echo on 5/18/15.
 */

public interface ShareContent extends Parcelable {

    int NO_CONTENT = 31415926;
    
    /**
     * @return 分享的方式
     */
    @ShareContentType
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
     * 分享的缩略图片
     */
    byte[] getThumbBmpBytes();

    /**
     * 分享的大图
     */
    String getLargeBmpPath();

    /**
     * 音频url
     */
    String getMusicUrl();

}