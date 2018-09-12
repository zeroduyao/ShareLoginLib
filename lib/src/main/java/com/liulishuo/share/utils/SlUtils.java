package com.liulishuo.share.utils;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import android.graphics.Bitmap;
import android.media.ThumbnailUtils;
import android.support.annotation.Nullable;
import android.text.TextUtils;

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
     * 此方法是耗时操作，如果对于特别大的图，那么需要做异步
     *
     * Note:外部传入的bitmap可能会被用于其他的地方，所以这里不能做recycle()
     */
    public static String saveLargeBitmap(final Bitmap bitmap) {
        if (bitmap == null) {
            return null;
        }

        String path = ShareLoginLib.TEMP_PIC_PATH;
        if (!TextUtils.isEmpty(path)) {
            String imagePath = path + "sl_large_pic";
            FileOutputStream fos = null;
            try {
                fos = new FileOutputStream(imagePath);
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } finally {
                if (fos != null) {
                    try {
                        fos.flush();
                        fos.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                /*if (bitmap != null && !bitmap.isRecycled()) {
                    bitmap.recycle();
                }*/
            }
            return imagePath;
        } else {
            return null;
        }
    }
}