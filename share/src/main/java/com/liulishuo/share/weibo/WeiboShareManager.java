package com.liulishuo.share.weibo;

import com.liulishuo.share.ShareBlock;
import com.liulishuo.share.base.Constants;
import com.liulishuo.share.base.share.IShareManager;
import com.liulishuo.share.base.share.ShareStateListener;
import com.liulishuo.share.base.shareContent.ShareContent;
import com.sina.weibo.sdk.api.ImageObject;
import com.sina.weibo.sdk.api.MusicObject;
import com.sina.weibo.sdk.api.TextObject;
import com.sina.weibo.sdk.api.VideoObject;
import com.sina.weibo.sdk.api.WebpageObject;
import com.sina.weibo.sdk.api.WeiboMultiMessage;
import com.sina.weibo.sdk.api.share.IWeiboShareAPI;
import com.sina.weibo.sdk.api.share.SendMultiMessageToWeiboRequest;
import com.sina.weibo.sdk.api.share.WeiboShareSDK;
import com.sina.weibo.sdk.constant.WBConstants;
import com.sina.weibo.sdk.utils.Utility;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;

/**
 * Created by echo on 5/18/15.
 */
public class WeiboShareManager implements IShareManager {

    private static ShareStateListener mShareStateListener;

    private static SendMultiMessageToWeiboRequest mRequest;

    /**
     * 启动新的activity进行分享
     */
    public void share(@NonNull Activity activity, @NonNull ShareContent shareContent, @ShareBlock.ShareType int shareType,
            @Nullable ShareStateListener listener) {

        mShareStateListener = listener;
        // 建立请求体
        mRequest = new SendMultiMessageToWeiboRequest();
        mRequest.transaction = buildTransaction("tag");// 用transaction唯一标识一个请求
        mRequest.multiMessage = getShareObject(shareContent);

        // 启动activity进行分享
        activity.startActivity(new Intent(activity, SL_WeiBoShareActivity.class));
    }

    /**
     * 在启动的activity中发送分享的信息
     */
    protected static void sendShareMsg(Activity activity) {
        String appId = ShareBlock.getInstance().weiboAppId;
        if (TextUtils.isEmpty(appId)) {
            throw new NullPointerException("请通过shareBlock初始化weiboAppId");
        }

        IWeiboShareAPI api = WeiboShareSDK.createWeiboAPI(activity, appId);
        api.registerApp();  // 将应用注册到微博客户端
        api.sendRequest(activity, mRequest);
    }

    /**
     * 处理分享的回调
     */
    protected static void parseShareResp(int respCode, String errorMsg) {
        if (mShareStateListener != null) {
            switch (respCode) {
                case WBConstants.ErrorCode.ERR_OK:
                    mShareStateListener.onSuccess();
                    break;

                case WBConstants.ErrorCode.ERR_CANCEL:
                    mShareStateListener.onCancel();
                    break;

                case WBConstants.ErrorCode.ERR_FAIL:
                    mShareStateListener.onError(errorMsg);
                    break;
                default:
                    mShareStateListener.onError("未知错误");
            }
        }
    }

    // --------------------------

    private WeiboMultiMessage getShareObject(@NonNull ShareContent shareContent) {
        WeiboMultiMessage weiboMultiMessage = new WeiboMultiMessage();
        switch (shareContent.getType()) {
            case Constants.SHARE_TYPE_TEXT:
                // 纯文字
                weiboMultiMessage.textObject = getTextObj(shareContent.getSummary());
                break;
            case Constants.SHARE_TYPE_PIC:
                // 纯图片
                weiboMultiMessage.imageObject = getImageObj(shareContent);
                break;
            case Constants.SHARE_TYPE_WEBPAGE:
                // 网页
                if (shareContent.getURL() == null) {
                    weiboMultiMessage.imageObject = getImageObj(shareContent);
                    weiboMultiMessage.textObject = getTextObj(shareContent.getSummary());
                } else {
                    weiboMultiMessage.mediaObject = getWebPageObj(shareContent);
                }
                break;
            case Constants.SHARE_TYPE_MUSIC:
                // 音乐
                weiboMultiMessage.mediaObject = getMusicObj(shareContent);
                break;
            default:
                throw new UnsupportedOperationException("不支持的分享内容");
        }
        if (!weiboMultiMessage.checkArgs()) {
            throw new IllegalArgumentException("分享信息的参数类型不正确");
        }
        return weiboMultiMessage;
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
        musicObject.dataUrl = ShareBlock.getInstance().weiboRedirectUrl;
        musicObject.dataHdUrl = ShareBlock.getInstance().weiboRedirectUrl;
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
        videoObject.dataUrl = ShareBlock.getInstance().weiboRedirectUrl;
        videoObject.dataHdUrl = ShareBlock.getInstance().weiboRedirectUrl;
        videoObject.duration = 10;
        videoObject.defaultText = shareContent.getSummary(); // 默认文案
        return videoObject;
    }

    public static boolean isWeiBoInstalled(@NonNull Context context) {
        IWeiboShareAPI shareAPI = WeiboShareSDK.createWeiboAPI(context, ShareBlock.getInstance().weiboAppId);
        return shareAPI.isWeiboAppInstalled();
    }


    public static String buildTransaction(final String type) {
        return (type == null) ? String.valueOf(System.currentTimeMillis())
                : type + System.currentTimeMillis();
    }

}
