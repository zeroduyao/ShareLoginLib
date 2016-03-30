package com.liulishuo.share.model.shareContent;

import com.liulishuo.share.model.Constants;

import android.graphics.Bitmap;
import android.os.Parcel;

/**
 * Created by echo on 5/18/15.
 * 音乐模式
 */
class ShareContentMusic extends ShareContentWebpage {

    private final String musicUrl;

    /**
     * @param title    标题
     * @param summary  副标题（描述）
     * @param url      击分享的内容后跳转到的网页
     * @param imageBmp 分享内容中的bitmap对象
     * @param musicUrl 音乐的url
     */
    public ShareContentMusic(String title, String summary, String url, Bitmap imageBmp, String musicUrl) {
        super(title, summary, url, imageBmp);
        this.musicUrl = musicUrl;
    }

    @Override
    public String getMusicUrl() {
        return musicUrl;
    }

    @Override
    public int getType() {
        return Constants.SHARE_TYPE_MUSIC;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeString(this.musicUrl);
    }

    protected ShareContentMusic(Parcel in) {
        super(in);
        this.musicUrl = in.readString();
    }

    public static final Creator<ShareContentMusic> CREATOR = new Creator<ShareContentMusic>() {
        public ShareContentMusic createFromParcel(Parcel source) {
            return new ShareContentMusic(source);
        }

        public ShareContentMusic[] newArray(int size) {
            return new ShareContentMusic[size];
        }
    };
}
