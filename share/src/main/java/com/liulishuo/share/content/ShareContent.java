package com.liulishuo.share.content;

import com.liulishuo.share.type.ContentType;

import java.io.Serializable;

/**
 * Created by echo on 5/18/15.
 */

public interface ShareContent extends Serializable {

    /**
     * @return 分享的方式
     */
    @ContentType int getType();

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
     * 分享的图片url
     */
    String getImagePicUrl();

    /**
     * 音频url
     */
    String getMusicUrl();

}
