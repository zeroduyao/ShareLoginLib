package com.liulishuo.share.content;

import com.liulishuo.share.type.ContentType;

import android.support.annotation.NonNull;

/**
 * Created by echo on 5/18/15.
 * 分享文本内容
 */
public class ShareContentText implements ShareContent {

    private final String summary;

    /**
     * 给QQ、微博、微信使用
     * @param summary 分享的文字内容
     */
    public ShareContentText(@NonNull String summary) {
        this.summary = summary;
    }

    @Override
    public String getSummary() {
        return summary;
    }

    @Override
    public String getTitle() {
        return null;
    }

    @Override
    public String getURL() {
        return null;
    }

    @Override
    public byte[] getImageBmpBytes() {
        return null;
    }

    @Override
    public String getImagePicUrl() {
        return null;
    }

    @Override
    public String getMusicUrl() {
        return null;
    }

    @Override
    public int getType() {
        return ContentType.TEXT;
    }

}