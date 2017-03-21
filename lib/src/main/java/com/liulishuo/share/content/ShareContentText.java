package com.liulishuo.share.content;

import android.os.Parcel;
import android.support.annotation.NonNull;

import com.liulishuo.share.type.ShareContentType;

/**
 * Created by echo on 5/18/15.
 * 分享文本内容
 */
public class ShareContentText implements ShareContent {

    private final String summary;

    /**
     * 给QQ、微博、微信使用
     *
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
    public byte[] getThumbBmpBytes() {
        return null;
    }

    @Override
    public String getLargeBmpPath() {
        return null;
    }

    @Override
    public String getMusicUrl() {
        return null;
    }

    @Override
    public int getType() {
        return ShareContentType.TEXT;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.summary);
    }

    private ShareContentText(Parcel in) {
        this.summary = in.readString();
    }

    public static final Creator<ShareContentText> CREATOR = new Creator<ShareContentText>() {
        @Override
        public ShareContentText createFromParcel(Parcel source) {
            return new ShareContentText(source);
        }

        @Override
        public ShareContentText[] newArray(int size) {
            return new ShareContentText[size];
        }
    };
}