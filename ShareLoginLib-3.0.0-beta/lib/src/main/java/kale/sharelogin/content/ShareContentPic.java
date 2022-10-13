package kale.sharelogin.content;

import android.graphics.Bitmap;
import android.support.annotation.NonNull;

import kale.sharelogin.ShareLoginLib;
import kale.sharelogin.utils.SlUtils;

/**
 * Created by echo on 5/18/15.
 * 分享图片模式
 */
public class ShareContentPic implements ShareContent {

    private Bitmap largeBmp;

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

    @ShareContentType
    @Override
    public int getType() {
        return ShareContentType.PIC;
    }

    @Override
    public String getLargeBmpPath() {
        if (largeBmpPath != null) {
            return largeBmpPath;
        } else {
            return SlUtils.saveBitmapToFile(largeBmp, ShareLoginLib.TEMP_PIC_DIR + "share_login_lib_large_pic.jpg");
        }
    }

}