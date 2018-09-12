package com.liulishuo.share.weibo;

import java.io.File;

import android.net.Uri;
import android.support.annotation.NonNull;

import com.liulishuo.share.content.ShareContent;
import com.liulishuo.share.content.ShareContentType;
import com.sina.weibo.sdk.api.BaseMediaObject;
import com.sina.weibo.sdk.api.ImageObject;
import com.sina.weibo.sdk.api.StoryMessage;
import com.sina.weibo.sdk.api.TextObject;
import com.sina.weibo.sdk.api.WebpageObject;
import com.sina.weibo.sdk.api.WeiboMultiMessage;
import com.sina.weibo.sdk.utils.Utility;

/**
 * @author Kale
 * @date 2018/9/10
 */
class ShareHelper {

    static StoryMessage createStoryMessage(ShareContent shareContent) {
        StoryMessage storyMessage = new StoryMessage();
        storyMessage.setImageUri(Uri.fromFile(new File(shareContent.getLargeBmpPath())));
        return storyMessage;
    }

    static WeiboMultiMessage createShareObject(@NonNull ShareContent shareContent) {
        WeiboMultiMessage weiboMultiMessage = new WeiboMultiMessage();
        switch (shareContent.getType()) {
            case ShareContentType.TEXT:
                // 纯文字
                weiboMultiMessage.textObject = getTextObj(shareContent);
                break;
            case ShareContentType.PIC:
                // 纯图片
                weiboMultiMessage.imageObject = getImageObj(shareContent);
                break;
            case ShareContentType.WEBPAGE:
                // 网页
                if (shareContent.getURL() == null) {
                    weiboMultiMessage.imageObject = getImageObj(shareContent);
                    weiboMultiMessage.textObject = getTextObj(shareContent);
                } else {
                    weiboMultiMessage.mediaObject = getWebPageObj(shareContent);
                }
                break;
            case ShareContentType.MUSIC:
            default:
                throw new UnsupportedOperationException("目前不能向微博分享音乐");
        }
        if (!weiboMultiMessage.checkArgs()) {
            throw new IllegalArgumentException("分享信息的参数类型不正确");
        }
        return weiboMultiMessage;
    }

    /**
     * 创建文本消息对象
     */
    private static TextObject getTextObj(ShareContent shareContent) {
        TextObject textObject = new TextObject();
        textObject.text = shareContent.getSummary();
        return textObject;
    }

    /**
     * 创建图片消息对象
     */
    private static ImageObject getImageObj(ShareContent shareContent) {
        ImageObject imageObject = new ImageObject();
        imageObject.imagePath = shareContent.getLargeBmpPath();
        return imageObject;
    }

    /**
     * 创建多媒体（网页）消息对象
     */
    private static WebpageObject getWebPageObj(ShareContent shareContent) {
        WebpageObject mediaObject = new WebpageObject();
        buildMediaObj(mediaObject, shareContent);

        mediaObject.defaultText = shareContent.getSummary();
        mediaObject.actionUrl = shareContent.getURL();
        return mediaObject;
    }

    private static void buildMediaObj(BaseMediaObject mediaObject, ShareContent shareContent) {
        mediaObject.identify = Utility.generateGUID();
        mediaObject.title = shareContent.getTitle();
        mediaObject.description = shareContent.getSummary();
        mediaObject.thumbData = shareContent.getThumbBmpBytes();
    }

}
