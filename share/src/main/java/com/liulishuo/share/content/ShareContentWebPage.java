package com.liulishuo.share.content;

import com.liulishuo.share.type.ContentType;

import android.graphics.Bitmap;
import android.os.Parcel;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

/**
 * Created by echo on 5/18/15.
 * 分享网页模式
 */
public class ShareContentWebPage extends ShareContentPic {

    protected final String title;

    protected final String summary;

    protected final String url;

    /**
     * @param title   标题
     * @param summary 描述
     * @param url     点击分享的内容后跳转的链接
     * @param bitmap  图片的bitmap（请用缩略图）
     */
    public ShareContentWebPage(@NonNull String title, @NonNull String summary, String url, @Nullable Bitmap bitmap) {
        super(bitmap);
        this.title = title;
        this.summary = summary;
        this.url = url;
    }

    @Override
    public String getSummary() {
        return summary;
    }

    @Override
    public String getTitle() {
        return title;
    }

    @Override
    public String getURL() {
        return url;
    }

    @Override
    public int getType() {
        return ContentType.WEBPAGE;
    }

    @Override
    public String getMusicUrl() {
        return null;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeString(this.title);
        dest.writeString(this.summary);
        dest.writeString(this.url);
    }

    protected ShareContentWebPage(Parcel in) {
        super(in);
        this.title = in.readString();
        this.summary = in.readString();
        this.url = in.readString();
    }

    public static final Creator<ShareContentWebPage> CREATOR = new Creator<ShareContentWebPage>() {
        public ShareContentWebPage createFromParcel(Parcel source) {
            return new ShareContentWebPage(source);
        }

        public ShareContentWebPage[] newArray(int size) {
            return new ShareContentWebPage[size];
        }
    };
}
