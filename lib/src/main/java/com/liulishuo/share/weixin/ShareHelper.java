package com.liulishuo.share.weixin;

import android.support.annotation.NonNull;

import com.liulishuo.share.ShareListener;
import com.liulishuo.share.content.ShareContent;
import com.liulishuo.share.content.ShareContentType;
import com.tencent.mm.opensdk.modelbase.BaseResp;
import com.tencent.mm.opensdk.modelmsg.SendMessageToWX;
import com.tencent.mm.opensdk.modelmsg.WXImageObject;
import com.tencent.mm.opensdk.modelmsg.WXMediaMessage;
import com.tencent.mm.opensdk.modelmsg.WXMusicObject;
import com.tencent.mm.opensdk.modelmsg.WXTextObject;
import com.tencent.mm.opensdk.modelmsg.WXWebpageObject;

/**
 * @author Kale
 * @date 2018/9/10
 */
class ShareHelper {

    @NonNull
    static SendMessageToWX.Req createRequest(@NonNull ShareContent shareContent, String shareType) {
        // 建立信息体
        WXMediaMessage msg = new WXMediaMessage();
        msg.title = shareContent.getTitle();
        msg.description = shareContent.getSummary();
        msg.thumbData = shareContent.getThumbBmpBytes(); // 这里没有做缩略图的配置，缩略图和原图是同一个对象

        msg.mediaObject = createMediaObject(shareContent);

        // 发送信息
        SendMessageToWX.Req req = new SendMessageToWX.Req();
        req.transaction = String.valueOf(System.currentTimeMillis());
        req.message = msg;

        switch (shareType) {
            case WeiXinPlatform.FRIEND: // 好友
                req.scene = SendMessageToWX.Req.WXSceneSession;
                break;
            case WeiXinPlatform.FRIEND_ZONE: // 朋友圈
                req.scene = SendMessageToWX.Req.WXSceneTimeline;
                break;
            case WeiXinPlatform.FAVORITE: // 收藏
                req.scene = SendMessageToWX.Req.WXSceneFavorite;
                break;
        }
        return req;
    }

    private static WXMediaMessage.IMediaObject createMediaObject(@NonNull ShareContent shareContent) {
        WXMediaMessage.IMediaObject mediaObject;
        switch (shareContent.getType()) {
            case ShareContentType.TEXT:
                // 纯文字
                mediaObject = getTextObj(shareContent);
                break;
            case ShareContentType.PIC:
                // 纯图片
                mediaObject = getImageObj(shareContent);
                break;
            case ShareContentType.WEBPAGE:
                // 网页
                mediaObject = getWebPageObj(shareContent);
                break;
            case ShareContentType.MUSIC:
                // 音乐
                mediaObject = getMusicObj(shareContent);
                break;
            default:
                throw new UnsupportedOperationException("不支持的分享内容");
        }
        if (!mediaObject.checkArgs()) {
            throw new IllegalArgumentException("分享信息的参数类型不正确");
        }
        return mediaObject;
    }

    private static WXMediaMessage.IMediaObject getTextObj(ShareContent shareContent) {
        WXTextObject text = new WXTextObject();
        text.text = shareContent.getSummary();
        return text;
    }

    private static WXMediaMessage.IMediaObject getImageObj(ShareContent shareContent) {
        WXImageObject image = new WXImageObject();
        image.imagePath = shareContent.getLargeBmpPath();
        return image;
    }

    private static WXMediaMessage.IMediaObject getMusicObj(ShareContent shareContent) {
        WXMusicObject music = new WXMusicObject();
        //Str1+"#wechat_music_url="+str2（str1是跳转的网页地址，str2是音乐地址）
        music.musicUrl = shareContent.getURL() + "#wechat_music_url=" + shareContent.getMusicUrl();
        return music;
    }

    private static WXMediaMessage.IMediaObject getWebPageObj(ShareContent shareContent) {
        WXWebpageObject webPage = new WXWebpageObject();
        webPage.webpageUrl = shareContent.getURL();
        return webPage;
    }

    /**
     * 解析分享到微信的结果
     *
     * https://open.weixin.qq.com/cgi-bin/showdocument?action=dir_list&t=resource/res_list&verify=1&id=open1419318634&token=&lang=zh_CN
     */
    static void parseShareResp(BaseResp resp, @NonNull ShareListener listener) {
        switch (resp.errCode) {
            case BaseResp.ErrCode.ERR_OK:
                listener.onSuccess();
                break;
            case BaseResp.ErrCode.ERR_USER_CANCEL:
                listener.onCancel();
                break;
            case BaseResp.ErrCode.ERR_AUTH_DENIED:
                listener.onError("用户拒绝授权");
                break;
            case BaseResp.ErrCode.ERR_SENT_FAILED:
                listener.onError("发送失败");
                break;
            case BaseResp.ErrCode.ERR_COMM:
                listener.onError("一般错误");
                break;
            default:
                listener.onError("未知错误");
        }
    }

}