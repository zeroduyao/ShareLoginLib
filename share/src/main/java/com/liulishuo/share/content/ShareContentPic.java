package com.liulishuo.share.content;

import com.liulishuo.share.type.ContentType;

import android.graphics.Bitmap;
import android.os.Parcel;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.io.ByteArrayOutputStream;

/**
 * Created by echo on 5/18/15.
 * 分享图片模式
 */
public class ShareContentPic implements ShareContent {

    protected byte[] bitmapBytes;

    /**
     * @param bitmap 分享的bitmap
     */
    public ShareContentPic(@Nullable Bitmap bitmap) {
        if (bitmap != null) {
            this.bitmapBytes = getThumbImageByteArr(bitmap);
        }
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
    public byte[] getImageBmpBytes() {
        return bitmapBytes;
    }


    @Override
    public String getMusicUrl() {
        return null;
    }

    @ContentType
    @Override
    public int getType() {
        return ContentType.PIC;
    }

    public void setBitmap(@NonNull Bitmap bitmap) {
        this.bitmapBytes = getThumbImageByteArr(bitmap);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeByteArray(this.bitmapBytes);
    }

    protected ShareContentPic(Parcel in) {
        this.bitmapBytes = in.createByteArray();
    }

    public static final Creator<ShareContentPic> CREATOR = new Creator<ShareContentPic>() {
        public ShareContentPic createFromParcel(Parcel source) {
            return new ShareContentPic(source);
        }

        public ShareContentPic[] newArray(int size) {
            return new ShareContentPic[size];
        }
    };


    private
    @Nullable
    byte[] getThumbImageByteArr(@NonNull Bitmap bitmap) {
        byte[] thumbData = null;
        try {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 85, outputStream);
            thumbData = outputStream.toByteArray();
            outputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return thumbData;
    }
}
