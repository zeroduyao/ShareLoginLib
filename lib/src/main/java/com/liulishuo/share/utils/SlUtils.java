package com.liulishuo.share.utils;

import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import android.graphics.Bitmap;
import android.media.ThumbnailUtils;
import android.support.annotation.Nullable;

import com.liulishuo.share.ShareLoginLib;

/**
 * @author Kale
 * @date 2017/3/21
 */
public class SlUtils {

    @Nullable
    static byte[] getImageThumbByteAr1r(@Nullable Bitmap bitmap) {
        if (bitmap == null) {
            return null;
        }

        final long size = '耀';

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream(bitmap.getWidth() * bitmap.getHeight());

        int options = 100;
        bitmap.compress(Bitmap.CompressFormat.JPEG, options, outputStream);

        while (outputStream.size() > size && options > 6) {
            outputStream.reset();
            options -= 6;
            bitmap.compress(Bitmap.CompressFormat.JPEG, options, outputStream);
        }

//        bitmap.recycle();

        return outputStream.toByteArray();
    }

    /**
     * Note:外部传入的bitmap可能会被用于其他的地方，所以这里不能做recycle()
     */
    @Nullable
    public static byte[] getImageThumbByteArr(@Nullable Bitmap src) {
        if (src == null) {
            return null;
        }

        final Bitmap bitmap;
        if (src.getWidth() > 250 || src.getHeight() > 250) {
            bitmap = ThumbnailUtils.extractThumbnail(src, 250, 250);
        } else {
            bitmap = src;
        }

        byte[] thumbData = null;
        ByteArrayOutputStream outputStream = null;
        try {
            outputStream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 85, outputStream);
            thumbData = outputStream.toByteArray();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (outputStream != null) {
                try {
                    outputStream.flush();
                    outputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            /*if (bitmap != null && !bitmap.isRecycled()) {
                bitmap.recycle();
            }*/
        }
        return thumbData;
    }

    /**
     * 将比特率存入本地磁盤
     */
    @Nullable
    public static String saveBytesToFile(byte[] bytes, String picPath) {
        if (bytes == null) {
            ShareLoginLib.printErr("bytes is null");
            return null;
        }

        try (FileOutputStream fos = new FileOutputStream(picPath)) {
            fos.write(bytes);
            fos.close();
            return picPath;
        } catch (IOException e) {
            e.printStackTrace();
            ShareLoginLib.printErr("save thumb picture error");
            return null;
        }
    }

    /**
     * 此方法是耗时操作，如果对于特别大的图，那么需要做异步
     *
     * Note:外部传入的bitmap可能会被用于其他的地方，所以这里不能做recycle()
     */
    public static String saveBitmapToFile(Bitmap bitmap, String imagePath) {
        if (bitmap == null) {
            ShareLoginLib.printErr("bitmap is null");
            return null;
        }

        try (FileOutputStream fos = new FileOutputStream(imagePath)) {
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            return imagePath;
        } catch (Exception e) {
            e.printStackTrace();
            ShareLoginLib.printErr("save bitmap picture error");
            return null;
        }
    }

}