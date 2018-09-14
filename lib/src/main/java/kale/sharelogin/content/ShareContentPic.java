package kale.sharelogin.content;

import android.graphics.Bitmap;
import android.os.Parcel;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

/**
 * Created by echo on 5/18/15.
 * 分享图片模式
 */
public class ShareContentPic implements ShareContent {

    private Bitmap thumbBmp, largeBmp;

    private byte[] thumbBmpBytes;

    private String largeBmpPath;

    /**
     * @param largeBmp 大图的bitmap，必须在10m以内
     */
    public ShareContentPic(@NonNull Bitmap largeBmp) {
        this.largeBmp = largeBmp;
    }

    /**
     * @param largeBmpPath 分享图片的本地存储地址
     */
    public ShareContentPic(@NonNull String largeBmpPath) {
        this.largeBmpPath = largeBmpPath;
    }

    ShareContentPic(@Nullable Bitmap thumbBmp, @Nullable Bitmap largeBmp) {
        this.thumbBmp = thumbBmp;
        this.largeBmp = largeBmp;
    }

    @Override
    public String getSummary() {
        return null;
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
        return thumbBmpBytes;
    }

    @Override
    public String getLargeBmpPath() {
        return largeBmpPath;
    }

    @Override
    public String getMusicUrl() {
        return null;
    }

    @ShareContentType
    @Override
    public int getType() {
        return ShareContentType.PIC;
    }

    @Nullable
    public Bitmap getThumbBmp() {
        return thumbBmp;
    }

    @Nullable
    public Bitmap getLargeBmp() {
        return largeBmp;
    }

    public void setThumbBmpBytes(byte[] thumbBmpBytes) {
        this.thumbBmpBytes = thumbBmpBytes;
    }

    public void setLargeBmpPath(String largeBmpPath) {
        this.largeBmpPath = largeBmpPath;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeByteArray(this.thumbBmpBytes);
        dest.writeString(this.largeBmpPath);
    }

    ShareContentPic(Parcel in) {
        this.thumbBmpBytes = in.createByteArray();
        this.largeBmpPath = in.readString();
    }

    public static final Creator<ShareContentPic> CREATOR = new Creator<ShareContentPic>() {
        @Override
        public ShareContentPic createFromParcel(Parcel source) {
            return new ShareContentPic(source);
        }

        @Override
        public ShareContentPic[] newArray(int size) {
            return new ShareContentPic[size];
        }
    };
}