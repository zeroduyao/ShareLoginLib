package com.liulishuo.share.base.shareContent;

import com.liulishuo.share.base.Constants;

import android.graphics.Bitmap;
import android.os.Parcel;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

/**
 * Created by echo on 5/18/15.
 * 分享网页模式
 */
public class ShareContentWebpage extends ShareContentPic {

    protected final String title;

    protected final String summary;

    protected final String url;

    /**
     * 给微信使用
     *
     * @param title   标题
     * @param summary 描述
     * @param url     点击分享的内容后跳转的链接
     * @param bitmap  图片的bitmap（请用缩略图）
     */
    public ShareContentWebpage(@NonNull String title, @NonNull String summary, String url, @Nullable Bitmap bitmap) {
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
        return Constants.SHARE_TYPE_WEBPAGE;
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

    protected ShareContentWebpage(Parcel in) {
        super(in);
        this.title = in.readString();
        this.summary = in.readString();
        this.url = in.readString();
    }

    public static final Creator<ShareContentWebpage> CREATOR = new Creator<ShareContentWebpage>() {
        public ShareContentWebpage createFromParcel(Parcel source) {
            return new ShareContentWebpage(source);
        }

        public ShareContentWebpage[] newArray(int size) {
            return new ShareContentWebpage[size];
        }
    };
}
