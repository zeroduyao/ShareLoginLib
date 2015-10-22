package com.liulishuo.share.weibo;

import com.liulishuo.share.ShareBlock;
import com.liulishuo.share.base.share.ShareConstants;
import com.liulishuo.share.base.share.ShareContent;
import com.liulishuo.share.util.ShareUtil;
import com.sina.weibo.sdk.api.ImageObject;
import com.sina.weibo.sdk.api.MusicObject;
import com.sina.weibo.sdk.api.TextObject;
import com.sina.weibo.sdk.api.VideoObject;
import com.sina.weibo.sdk.api.WebpageObject;
import com.sina.weibo.sdk.api.WeiboMultiMessage;
import com.sina.weibo.sdk.api.share.IWeiboShareAPI;
import com.sina.weibo.sdk.api.share.SendMultiMessageToWeiboRequest;
import com.sina.weibo.sdk.api.share.WeiboShareSDK;
import com.sina.weibo.sdk.utils.Utility;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.text.TextUtils;

/**
 * Created by echo on 5/18/15.
 */
class WeiboRealShareManager {

    private String mRedirectUrl;

    /**
     * 微博分享的接口实例
     */
    private IWeiboShareAPI mSinaAPI;

    public WeiboRealShareManager(Activity activity) {
        String sinaAppKey = ShareBlock.getInstance().weiboAppId;
        mRedirectUrl = ShareBlock.getInstance().weiboRedirectUrl;

        if (!TextUtils.isEmpty(sinaAppKey)) {
            // 创建微博 SDK 接口实例
            mSinaAPI = WeiboShareSDK.createWeiboAPI(activity, sinaAppKey);
            mSinaAPI.registerApp();  // 将应用注册到微博客户端
        } else {
            throw new NullPointerException("请通过shareBlock初始化SinaAppKey");
        }
    }

    public boolean share(Activity activity, @NonNull ShareContent shareContent) {
        WeiboMultiMessage weiboMultiMessage = new WeiboMultiMessage();
        switch (shareContent.getShareWay()) {
            case ShareConstants.SHARE_WAY_TEXT:
                // 纯文字
                weiboMultiMessage.textObject = getTextObj(shareContent.getSummary());
                break;
            case ShareConstants.SHARE_WAY_PIC:
                // 纯图片
                weiboMultiMessage.imageObject = getImageObj(shareContent);
                break;
            case ShareConstants.SHARE_WAY_WEBPAGE:
                // 网页
                if (shareContent.getURL() == null) {
                    weiboMultiMessage.imageObject = getImageObj(shareContent);
                    weiboMultiMessage.textObject = getTextObj(shareContent.getSummary());
                } else {
                    weiboMultiMessage.mediaObject = getWebPageObj(shareContent);
                }
                break;
            case ShareConstants.SHARE_WAY_MUSIC:
                // 音乐
                weiboMultiMessage.mediaObject = getMusicObj(shareContent);
                break;
            default:
                throw new UnsupportedOperationException("不支持的分享内容");
        }

        if (!weiboMultiMessage.checkArgs()) {
            throw new IllegalArgumentException("分享信息的参数类型不正确");
        }

        // 初始化从第三方到微博的消息请求
        SendMultiMessageToWeiboRequest request = new SendMultiMessageToWeiboRequest();
        // 用transaction唯一标识一个请求
        request.transaction = ShareUtil.buildTransaction("tag");
        request.multiMessage = weiboMultiMessage;
        return mSinaAPI.sendRequest(activity, request);
    }

    public IWeiboShareAPI getSinaAPI() {
        return mSinaAPI;
    }

    /**
     * 创建文本消息对象。
     *
     * @return 文本消息对象。
     */
    private TextObject getTextObj(String text) {
        TextObject textObject = new TextObject();
        textObject.text = text;
        return textObject;
    }

    /**
     * 创建图片消息对象。
     *
     * @return 图片消息对象。
     */
    private ImageObject getImageObj(ShareContent shareContent) {
        byte[] bmpBytes = shareContent.getImageBmpBytes();
        if (bmpBytes != null) {
            ImageObject imageObject = new ImageObject();
            imageObject.imageData = bmpBytes;
            return imageObject;
        }
        return null;
    }

    /**
     * 创建多媒体（网页）消息对象。
     *
     * @return 多媒体（网页）消息对象。
     */
    private WebpageObject getWebPageObj(ShareContent shareContent) {
        WebpageObject mediaObject = new WebpageObject();
        mediaObject.identify = Utility.generateGUID();
        mediaObject.title = shareContent.getTitle();
        mediaObject.description = shareContent.getSummary();
        // 设置 Bitmap 类型的图片到视频对象里        
        // 设置缩略图。 注意：最终压缩过的缩略图大小不得超过 32kb。
        mediaObject.thumbData = shareContent.getImageBmpBytes();
        mediaObject.actionUrl = shareContent.getURL();
        mediaObject.defaultText = shareContent.getSummary();
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
        musicObject.identify = Utility.generateGUID();
        musicObject.title = shareContent.getTitle();
        musicObject.description = shareContent.getSummary();
        // 设置 Bitmap 类型的图片到视频对象里        
        // 设置缩略图。 注意：最终压缩过的缩略图大小不得超过 32kb。
        musicObject.thumbData = shareContent.getImageBmpBytes();
        musicObject.actionUrl = shareContent.getMusicUrl();
        musicObject.dataUrl = mRedirectUrl;
        musicObject.dataHdUrl = mRedirectUrl;
        musicObject.duration = 10;
        musicObject.defaultText = shareContent.getSummary();
        return musicObject;
    }

    /**
     * 创建多媒体（视频）消息对象。
     *
     * @return 多媒体（视频）消息对象。
     */
    private VideoObject getVideoObj(ShareContent shareContent) {
        VideoObject videoObject = new VideoObject();
        videoObject.identify = Utility.generateGUID();
        videoObject.title = shareContent.getTitle();
        videoObject.description = shareContent.getSummary();
        // 设置 Bitmap 类型的图片到视频对象里        
        // 设置缩略图。 注意：最终压缩过的缩略图大小不得超过 32kb。
        videoObject.thumbData = shareContent.getImageBmpBytes();
        videoObject.actionUrl = shareContent.getURL();
        videoObject.dataUrl = mRedirectUrl;
        videoObject.dataHdUrl = mRedirectUrl;
        videoObject.duration = 10;
        videoObject.defaultText = shareContent.getSummary(); // 默认文案
        return videoObject;
    }

}
