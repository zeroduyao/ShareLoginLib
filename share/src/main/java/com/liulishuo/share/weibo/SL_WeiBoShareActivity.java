package com.liulishuo.share.weibo;

import com.liulishuo.share.ShareBlock;
import com.liulishuo.share.ShareManager;
import com.liulishuo.share.content.ShareContent;
import com.liulishuo.share.type.ContentType;
import com.sina.weibo.sdk.api.ImageObject;
import com.sina.weibo.sdk.api.MusicObject;
import com.sina.weibo.sdk.api.TextObject;
import com.sina.weibo.sdk.api.WebpageObject;
import com.sina.weibo.sdk.api.WeiboMultiMessage;
import com.sina.weibo.sdk.api.share.BaseResponse;
import com.sina.weibo.sdk.api.share.IWeiboHandler;
import com.sina.weibo.sdk.api.share.IWeiboShareAPI;
import com.sina.weibo.sdk.api.share.SendMultiMessageToWeiboRequest;
import com.sina.weibo.sdk.api.share.WeiboShareSDK;
import com.sina.weibo.sdk.constant.WBConstants;
import com.sina.weibo.sdk.utils.Utility;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.TextUtils;

/**
 * @author Jack Tony
 * @date 2015/10/14
 */
public class SL_WeiBoShareActivity extends Activity implements IWeiboHandler.Response {

    private boolean isFirstIn = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState == null) {
            // 防止不保留活动情况下activity被重置后直接进行操作的情况
            // 建立请求体
            SendMultiMessageToWeiboRequest request = new SendMultiMessageToWeiboRequest();
            request.transaction = String.valueOf(System.currentTimeMillis());// 用transaction唯一标识一个请求
            ShareContent content = getIntent().getParcelableExtra(ShareManager.KEY_CONTENT);
            request.multiMessage = getShareObject(content);

            doShare(this, request);
        } else {
            /**
             * 当 Activity 被重新初始化时（该 Activity 处于后台时，可能会由于内存不足被杀掉了），
             * 需要调用 {@link IWeiboShareAPI#handleWeiboResponse} 来接收微博客户端返回的数据。
             * 执行成功，返回 true，并调用 {@link IWeiboHandler.Response#onResponse}；
             * 失败返回 false，不调用上述回调
             */
            IWeiboShareAPI API = WeiboShareSDK.createWeiboAPI(getApplicationContext(),
                    ShareBlock.getInstance().weiBoAppId);
            API.handleWeiboResponse(getIntent(), this);
        }
    }

    /**
     * 因为微博客户端在用户取消分享后，用户点击保存到草稿箱后就不能接收到回调。
     * 因此，在这里必须进行强制关闭，不能依赖回调来关闭。
     */
    @Override
    protected void onResume() {
        super.onResume();
        if (isFirstIn) {
            isFirstIn = false;
        } else {
            // 这里处理保存到草稿箱的逻辑
            parseShareResp(WBConstants.ErrorCode.ERR_CANCEL, "weibo cancel");
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        /**
         * 从当前应用唤起微博并进行分享后，返回到当前应用时，需要在此处调用该函数
         * 来接收微博客户端返回的数据；执行成功，返回 true，并调用
         * {@link IWeiboHandler.Response#onResponse}；失败返回 false，不调用上述回调
         */
        IWeiboShareAPI API = WeiboShareSDK.createWeiboAPI(getApplicationContext(), ShareBlock.getInstance().weiBoAppId);
        API.handleWeiboResponse(intent, this); // 当前应用唤起微博分享后，返回当前应用
    }

    @Override
    public void onResponse(BaseResponse baseResponse) {
        parseShareResp(baseResponse.errCode, baseResponse.errMsg);
    }

    private void doShare(Activity activity, SendMultiMessageToWeiboRequest request) {
        String appId = ShareBlock.getInstance().weiBoAppId;
        if (TextUtils.isEmpty(appId)) {
            throw new NullPointerException("请通过shareBlock初始化weiBoAppId");
        }
        IWeiboShareAPI api = WeiboShareSDK.createWeiboAPI(activity, appId);
        api.registerApp();  // 将应用注册到微博客户端
        api.sendRequest(activity, request);
    }

    /**
     * 处理分享的回调
     */
    private void parseShareResp(int respCode, String errorMsg) {
        ShareManager.ShareStateListener listener = ShareManager.listener;
        if (listener == null) {
            return;
        }

        switch (respCode) {
            case WBConstants.ErrorCode.ERR_OK:
                listener.onSuccess();
                break;

            case WBConstants.ErrorCode.ERR_CANCEL:
                listener.onCancel();
                break;

            case WBConstants.ErrorCode.ERR_FAIL:
                listener.onError(errorMsg);
                break;
            default:
                listener.onError("未知错误");
        }
        finish();
    }

    // --------------------------

    private WeiboMultiMessage getShareObject(@NonNull ShareContent shareContent) {
        WeiboMultiMessage weiboMultiMessage = new WeiboMultiMessage();
        switch (shareContent.getType()) {
            case ContentType.TEXT:
                // 纯文字
                weiboMultiMessage.textObject = getTextObj(shareContent.getSummary());
                break;
            case ContentType.PIC:
                // 纯图片
                weiboMultiMessage.imageObject = getImageObj(shareContent);
                break;
            case ContentType.WEBPAGE:
                // 网页
                if (shareContent.getURL() == null) {
                    weiboMultiMessage.imageObject = getImageObj(shareContent);
                    weiboMultiMessage.textObject = getTextObj(shareContent.getSummary());
                } else {
                    weiboMultiMessage.mediaObject = getWebPageObj(shareContent);
                }
                break;
            case ContentType.MUSIC:
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
        musicObject.dataUrl = ShareBlock.getInstance().weiBoRedirectUrl;
        musicObject.dataHdUrl = ShareBlock.getInstance().weiBoRedirectUrl;
        musicObject.duration = 10;
        musicObject.defaultText = shareContent.getSummary();
        return musicObject;
    }

   /* *//**
     * 创建多媒体（视频）消息对象。
     *
     * @return 多媒体（视频）消息对象。
     *//*
    private VideoObject getVideoObj(ShareContent shareContent) {
        VideoObject videoObject = new VideoObject();
        videoObject.identify = Utility.generateGUID();
        videoObject.title = shareContent.getTitle();
        videoObject.description = shareContent.getSummary();
        // 设置 Bitmap 类型的图片到视频对象里        
        // 设置缩略图。 注意：最终压缩过的缩略图大小不得超过 32kb。
        videoObject.thumbData = shareContent.getImageBmpBytes();
        videoObject.actionUrl = shareContent.getURL();
        videoObject.dataUrl = ShareBlock.getInstance().weiBoRedirectUrl;
        videoObject.dataHdUrl = ShareBlock.getInstance().weiBoRedirectUrl;
        videoObject.duration = 10;
        videoObject.defaultText = shareContent.getSummary(); // 默认文案
        return videoObject;
    }*/

    /*public static String buildTransaction(final String type) {
        return (type == null) ? String.valueOf(System.currentTimeMillis())
                : type + System.currentTimeMillis();
    }*/

}
