package com.liulishuo.share.content;

import android.os.Parcelable;

import com.liulishuo.share.type.ShareContentType;

/**
 * Created by echo on 5/18/15.
 */

public interface ShareContent extends Parcelable {

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
