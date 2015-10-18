package com.liulishuo.share.base.share;

import com.liulishuo.share.util.PicFileUtil;

import android.graphics.Bitmap;
import android.os.Parcel;
import android.support.annotation.NonNull;

/**
 * Created by echo on 5/18/15.
 * 分享图片模式
 */
class ShareContentPic implements ShareContent {

    protected byte[] bitmapBytes;

    /**
     * @param bitmap 分享的bitmap
     */
    public ShareContentPic(@NonNull Bitmap bitmap) {
        this.bitmapBytes = PicFileUtil.getThumbImageByteArr(bitmap);
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

    @Override
    public int getShareWay() {
        return ShareConstants.SHARE_WAY_PIC;
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
}
