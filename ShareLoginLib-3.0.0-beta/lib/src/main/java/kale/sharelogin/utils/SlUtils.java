package kale.sharelogin.utils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import android.app.Application;
import android.graphics.Bitmap;
import android.media.ThumbnailUtils;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.annotation.RestrictTo;
import android.util.Log;

import kale.sharelogin.IPlatform;
import kale.sharelogin.ShareLoginLib;
import kale.sharelogin.content.ShareContent;

/**
 * @author Kale
 * @date 2017/3/21
 */
@RestrictTo(RestrictTo.Scope.LIBRARY)
public class SlUtils {

    private static final String TAG = "ShareLoginLib";

    public static void printLog(String message) {
        if (ShareLoginLib.DEBUG) {
            Log.i(TAG, "======> " + message);
        }
    }

    public static void printErr(String message) {
        if (ShareLoginLib.DEBUG) {
            Log.e(TAG, "======>" + message);
        }
    }

    /**
     * 得到缓存图片的本地目录
     */
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

        final int WIDTH = 500, HEIGHT = 500;
        final long SIZE = '耀'; // 最大的图片大小

        final Bitmap bitmap;
        if (src.getWidth() > WIDTH || src.getHeight() > WIDTH) {
            // 裁剪为正方形的图片
            bitmap = ThumbnailUtils.extractThumbnail(src, WIDTH, HEIGHT);
            printLog("预览图过大，进行了裁剪");
        } else {
            bitmap = src;
        }

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream(bitmap.getWidth() * bitmap.getHeight());

        int options = 100;
        bitmap.compress(Bitmap.CompressFormat.JPEG, options, outputStream);

        if (outputStream.size() > SIZE) {
            printLog("裁剪后的预览图仍旧过大，需要进一步压缩");
        }

        while (outputStream.size() > SIZE && options > 6) {
            outputStream.reset();
            options -= 6; // 不断的压缩图片的质量
            bitmap.compress(Bitmap.CompressFormat.JPEG, options, outputStream);
        }

        printLog("最终的预览图大小：" + outputStream.size() + " ,目标大小：" + SIZE);

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
            printErr("save thumb picture error");
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
            printErr("bitmap is null");
            return null;
        }

        try (FileOutputStream fos = new FileOutputStream(imagePath)) {
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            return imagePath;
        } catch (Exception e) {
            e.printStackTrace();
            printErr("save bitmap picture error");
            return null;
        }
    }

    public static IPlatform createPlatform(Class<? extends IPlatform> platformClz) {
        try {
            return platformClz.newInstance();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }

        throw new RuntimeException("platform create error");
    }

    @RestrictTo(RestrictTo.Scope.LIBRARY)
    public interface Function<T> {

        T apply(ShareContent content);
    }

}