package com.liulishuo.share.weibo;

import android.support.annotation.NonNull;

import com.liulishuo.share.ShareListener;
import com.liulishuo.share.ShareLoginLib;
import com.liulishuo.share.content.ShareContent;
import com.liulishuo.share.content.ShareContentType;
import com.sina.weibo.sdk.api.BaseMediaObject;
import com.sina.weibo.sdk.api.ImageObject;
import com.sina.weibo.sdk.api.MusicObject;
import com.sina.weibo.sdk.api.TextObject;
import com.sina.weibo.sdk.api.WebpageObject;
import com.sina.weibo.sdk.api.WeiboMultiMessage;
import com.sina.weibo.sdk.api.share.BaseResponse;
import com.sina.weibo.sdk.constant.WBConstants;
import com.sina.weibo.sdk.utils.Utility;

import static com.liulishuo.share.weibo.WeiBoPlatform.KEY_REDIRECT_URL;

/**
 * @author Kale
 * @date 2018/9/10
 */
class ShareHelper {

    WeiboMultiMessage createShareObject(@NonNull ShareContent shareContent) {
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
                // 音乐
                weiboMultiMessage.mediaObject = getMusicObj(shareContent);
                break;
        }
        if (!weiboMultiMessage.checkArgs()) {
            throw new IllegalArgumentException("分享信息的参数类型不正确");
        }
        return weiboMultiMessage;
    }

    /**
     * 创建文本消息对象
     */
    private TextObject getTextObj(ShareContent shareContent) {
        TextObject textObject = new TextObject();
        textObject.text = shareContent.getSummary();
        return textObject;
    }

    /**
     * 创建图片消息对象
     */
    private ImageObject getImageObj(ShareContent shareContent) {
        ImageObject imageObject = new ImageObject();
        imageObject.imagePath = shareContent.getLargeBmpPath();
        return imageObject;
    }

    /**
     * 创建多媒体（网页）消息对象
     */
    private WebpageObject getWebPageObj(ShareContent shareContent) {
        WebpageObject mediaObject = new WebpageObject();
        buildMediaObj(mediaObject, shareContent);

        mediaObject.defaultText = shareContent.getSummary();
        mediaObject.actionUrl = shareContent.getURL();
        return mediaObject;
    }

    /**
     * 创建多媒体（音乐）消息对象。
     *
     * @return 多媒体（音乐）消息对象。
     */
    private MusicObject getMusicObj(ShareContent shareContent) {
        // 创建媒体消息
        MusicObject musicObject = new MusicObject();
        buildMediaObj(musicObject, shareContent);

        musicObject.defaultText = shareContent.getSummary();
        musicObject.actionUrl = shareContent.getMusicUrl();
        musicObject.dataUrl = ShareLoginLib.getValue(KEY_REDIRECT_URL);
        musicObject.dataHdUrl = ShareLoginLib.getValue(KEY_REDIRECT_URL);
        musicObject.duration = 10;
        return musicObject;
    }

    private void buildMediaObj(BaseMediaObject mediaObject, ShareContent shareContent) {
        mediaObject.identify = Utility.generateGUID();
        mediaObject.title = shareContent.getTitle();
        mediaObject.description = shareContent.getSummary();
        mediaObject.thumbData = shareContent.getThumbBmpBytes();
    }

    static void parseShareResp(BaseResponse resp, ShareListener listener) {
        if (listener == null) {
            return;
        }

        switch (resp.errCode) {
            case WBConstants.ErrorCode.ERR_OK:
                listener.onSuccess();
                break;
            case WBConstants.ErrorCode.ERR_CANCEL:
                listener.onCancel();
                break;
            case WBConstants.ErrorCode.ERR_FAIL:
                listener.onError(resp.errMsg);
                break;
            default:
                listener.onError("未知错误");
        }
    }
    
}
