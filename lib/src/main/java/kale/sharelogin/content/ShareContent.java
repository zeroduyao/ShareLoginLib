package kale.sharelogin.content;

/**
 * Created by echo on 5/18/15.
 */

public interface ShareContent {

    int NO_CONTENT = 31415926;

    /**
     * @return 分享的方式
     */
    @ShareContentType
    int getType();

    /**
     * 分享的描述信息(摘要)
     */
    default String getSummary() {
        return null;
    }

    /**
     * 分享的标题
     */
    default String getTitle() {
        return null;
    }

    /**
     * 获取跳转的链接
     */
    default String getURL() {
        return null;
    }

    /**
     * 分享的缩略图片
     */
    default byte[] getThumbBmpBytes() {
        return null;
    }

    /**
     * 分享的大图地址
     */
    default String getLargeBmpPath() {
        return null;
    }

    /**
     * 音频url
     */
    default String getMusicUrl() {
        return null;
    }

}