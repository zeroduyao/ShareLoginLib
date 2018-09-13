package com.liulishuo.share.utils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import android.app.Activity;
import android.app.Application;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.ThumbnailUtils;
import android.os.Environment;
import android.support.annotation.Nullable;

import com.liulishuo.share.ShareLoginLib;

/**
 * @author Kale
 * @date 2017/3/21
 */
public class SlUtils {

    public static String generateTempPicDir(Application application) {
        String tempPicDir = null;

        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            try {
                tempPicDir = application.getExternalCacheDir() + File.separator;
                File dir = new File(tempPicDir);
                if (!dir.exists()) {
                    dir.mkdirs();
                }
            } catch (Exception e) {
                e.printStackTrace();
                tempPicDir = null;
            }
        }
        return tempPicDir;
    }

    public static String getTempPicFilePath() {
        return ShareLoginLib.TEMP_PIC_DIR + "share_login_lib_large_pic.jpg";
    }

    /**
     * 将bitmap缩小到可以分享的大小，并变为byte数组
     *
     * Note:外部传入的bitmap可能会被用于其他的地方，所以这里不能做recycle()
     *
     * https://juejin.im/post/5b0bad475188251545422199
     * https://juejin.im/post/5b1a6b035188257d7102591a
     */
    @Nullable
    public static byte[] getImageThumbByteArr(@Nullable Bitmap src) {
        if (src == null) {
            return null;
        }

        final int WIDTH = 500;

        final Bitmap bitmap;
        if (src.getWidth() > WIDTH || src.getHeight() > WIDTH) {
            // 裁剪为正方形的图片
            bitmap = ThumbnailUtils.extractThumbnail(src, WIDTH, WIDTH);
        } else {
            bitmap = src;
        }

        final long SIZE = '耀'; // 最大的大小

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream(bitmap.getWidth() * bitmap.getHeight());

        int options = 100;
        bitmap.compress(Bitmap.CompressFormat.JPEG, options, outputStream);

        while (outputStream.size() > SIZE && options > 6) {
            outputStream.reset();
            options -= 6; // 不断的压缩图片的质量
            bitmap.compress(Bitmap.CompressFormat.JPEG, options, outputStream);
        }

//        bitmap.recycle();

        return outputStream.toByteArray();
    }

    /**
     * 将比特率存入本地磁盤
     */
    @Nullable
    public static String saveBytesToFile(byte[] bytes, String picPath) {
        if (bytes == null) {
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

    public static void startActivity(Activity activity, Intent intent,EventHandlerActivity.OnCreateListener listener) {
        ShareLoginLib.onCreateListener = listener;
        activity.startActivity(intent);
        activity.overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }

}