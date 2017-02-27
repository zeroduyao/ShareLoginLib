package com.liulishuo.share.content;

import java.io.ByteArrayOutputStream;

import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.liulishuo.share.type.ContentType;

/**
 * Created by echo on 5/18/15.
 * 分享图片模式
 */
public class ShareContentPic implements ShareContent {

    /**
     * 图片的byte数组
     */
    private byte[] thumbBmpBytes;

    private byte[] largeBmpBytes;

    /**
     * @param thumbBmp      如果需要分享图片，则必传
     */
    public ShareContentPic(@Nullable Bitmap thumbBmp, @Nullable Bitmap largeBmp) {
        if (thumbBmp != null) {
            thumbBmpBytes = getImageByteArr(thumbBmp);
        }
        if (largeBmp != null) {
            largeBmpBytes = getImageByteArr(largeBmp);
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
    public byte[] getThumbBmpBytes() {
        return thumbBmpBytes;
    }

    @Override
    public byte[] getLargeBmpBytes() {
        return largeBmpBytes;
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

    private
    @Nullable
    byte[] getImageByteArr(@NonNull Bitmap bitmap) {
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
