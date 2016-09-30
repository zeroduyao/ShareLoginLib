package com.liulishuo.share.content;

import com.liulishuo.share.type.ContentType;

import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.io.ByteArrayOutputStream;

/**
 * Created by echo on 5/18/15.
 * 分享图片模式
 */
public class ShareContentPic implements ShareContent {

    /**
     * 图片的byte数组
     */
    protected byte[] bitmapBytes;

    /**
     * 图片的url
     */
    protected String imageUrl;

    /**
     * @param bitmap      如果需要分享图片，则必传
     * @param imagePicUrl 分享图片的url，能传则传，仅供QQ分享使用
     *                    目前不支持https的图片！
     */
    public ShareContentPic(@Nullable Bitmap bitmap, @Nullable String imagePicUrl) {
        if (bitmap != null) {
            this.bitmapBytes = getThumbImageByteArr(bitmap);
        }
        this.imageUrl = imagePicUrl;
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
    public String getImagePicUrl() {
        return imageUrl;
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
