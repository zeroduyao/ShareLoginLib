package com.liulishuo.share.util;

import android.graphics.Bitmap;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import static com.liulishuo.share.ShareBlock.getInstance;

/**
 * @author Jack Tony
 * @date 2015/10/15
 */
public class PicFileUtil {

    /**
     * 保存bitmap到磁盘，返回保存的目录
     *
     * @return 保存bitmap的目录
     */
    public static String saveBitmap(Bitmap bitmap) {
        String imagePath = getInstance().pathTemp + File.separator  + "sharePic_temp.png";
        try {
            if (bitmap != null) {
                FileOutputStream out = new FileOutputStream(imagePath);
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
                out.close();
            }
        } catch (OutOfMemoryError ignored) {
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return imagePath;
    }

    public static String saveByteArr(byte[] bytes) {
        String imagePath = getInstance().pathTemp + File.separator  + "sharePic_temp.png";
        try {
            FileOutputStream fos = new FileOutputStream(imagePath);
            fos.write(bytes);
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return imagePath;
    }

    public static byte[] getThumbImageByteArr(Bitmap bitmap) {
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
