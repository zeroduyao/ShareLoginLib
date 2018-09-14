package kale.sharelogin.content;

import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import kale.sharelogin.ShareLoginLib;
import kale.sharelogin.utils.SlUtils;

/**
 * Created by echo on 5/18/15.
 * 分享网页模式
 */
public class ShareContentWebPage implements ShareContent {

    private String title, summary, url;

    private Bitmap thumbBmp;

    /**
     * @param title   标题
     * @param summary 描述
     * @param url     点击分享的内容后跳转的链接
     * @param thumb   图片的bitmap。保证在32kb以内,如果要分享图片，那么必传
     */
    public ShareContentWebPage(@NonNull String title, @NonNull String summary, String url, @Nullable Bitmap thumb) {
        this.title = title;
        this.summary = summary;
        this.url = url;
        this.thumbBmp = thumb;
    }

    @Override
    public int getType() {
        return ShareContentType.WEBPAGE;
    }

    @Override
    public String getSummary() {
        return summary;
    }

    @Override
    public String getTitle() {
        return title;
    }

    @Override
    public String getURL() {
        return url;
    }

    @Override
    public byte[] getThumbBmpBytes() {
        return SlUtils.getImageThumbByteArr(thumbBmp);
    }

    @Override
    public String getLargeBmpPath() {
        if (url == null && thumbBmp != null) {
            // 没有url是不合法的，这里对微博做了兼容处理
            return SlUtils.saveBitmapToFile(thumbBmp, ShareLoginLib.TEMP_PIC_DIR + "share_login_lib_large_pic.jpg");
        } else {
            return null;
        }
    }
    
}