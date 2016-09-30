package com.liulishuo.share.content;

import com.liulishuo.share.type.ContentType;

import android.graphics.Bitmap;

/**
 * Created by echo on 5/18/15.
 * 音乐模式
 */
class ShareContentMusic extends ShareContentWebPage {

    private final String musicUrl;

    /**
     * @param title    标题
     * @param summary  副标题（描述）
     * @param url      击分享的内容后跳转到的网页
     * @param imageBmp 分享内容中的bitmap对象
     * @param musicUrl 音乐的url
     */
    public ShareContentMusic(String title, String summary, String url, 
            Bitmap imageBmp, String imageUrl, String musicUrl) {
        
        super(title, summary, url, imageBmp, imageUrl);
        this.musicUrl = musicUrl;
    }

    @Override
    public String getMusicUrl() {
        return musicUrl;
    }

    @Override
    public int getType() {
        return ContentType.MUSIC;
    }

}
