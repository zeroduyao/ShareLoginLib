package com.liulishuo.share.weixin;

import com.liulishuo.share.ShareBlock;
import com.liulishuo.share.ShareManager;
import com.liulishuo.share.model.Constants;
import com.liulishuo.share.model.shareContent.ShareContent;
import com.liulishuo.share.type.ShareType;
import com.tencent.mm.sdk.modelbase.BaseResp;
import com.tencent.mm.sdk.modelmsg.SendMessageToWX;
import com.tencent.mm.sdk.modelmsg.WXAppExtendObject;
import com.tencent.mm.sdk.modelmsg.WXImageObject;
import com.tencent.mm.sdk.modelmsg.WXMediaMessage;
import com.tencent.mm.sdk.modelmsg.WXMusicObject;
import com.tencent.mm.sdk.modelmsg.WXTextObject;
import com.tencent.mm.sdk.modelmsg.WXWebpageObject;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.WXAPIFactory;

import android.content.Context;
import android.support.annotation.NonNull;
import android.text.TextUtils;

import static com.tencent.mm.sdk.modelmsg.WXMediaMessage.IMediaObject;

/**
 * Created by echo on 5/18/15.
 */
public class WeiXinShareManager {

    public void sendShareMsg(@NonNull Context context, @NonNull ShareContent shareContent,
            ShareType shareType) {
        String weChatAppId = ShareBlock.getInstance().weiXinAppId;
        if (TextUtils.isEmpty(weChatAppId)) {
            throw new NullPointerException("请通过shareBlock初始化WeChatAppId");
        }

        IWXAPI IWXAPI = WXAPIFactory.createWXAPI(context, weChatAppId, true);
        IWXAPI.registerApp(weChatAppId);

        SendMessageToWX.Req req = getReq(shareContent, shareType);
        IWXAPI.sendReq(req);
    }

    /**
     * 解析分享到微信的结果
     */
    protected static void parseShareResp(BaseResp resp, ShareManager.ShareStateListener listener) {
        if (listener != null) {
            switch (resp.errCode) {
                case BaseResp.ErrCode.ERR_OK:
                    // 分享成功
                    listener.onSuccess();
                    break;
                case BaseResp.ErrCode.ERR_USER_CANCEL:
                    // 用户取消
                    listener.onCancel();
                    break;
                case BaseResp.ErrCode.ERR_AUTH_DENIED:
                    // 用户拒绝授权
                    listener.onError("用户拒绝授权");
                    break;
                case BaseResp.ErrCode.ERR_SENT_FAILED:
                    // 发送失败
                    listener.onError("发送失败");
                    break;
                case BaseResp.ErrCode.ERR_COMM:
                    // 一般错误
                    listener.onError("一般错误");
                    break;
                default:
                    listener.onError("未知错误");
            }
        }
    }

    // --------------------------

    @NonNull
    private SendMessageToWX.Req getReq(@NonNull ShareContent shareContent, ShareType shareType) {
        // 建立信息体
        WXMediaMessage msg = new WXMediaMessage(getShareObject(shareContent));
        msg.title = shareContent.getTitle();
        msg.description = shareContent.getSummary();
        msg.thumbData = shareContent.getImageBmpBytes();

        // 发送信息
        SendMessageToWX.Req req = new SendMessageToWX.Req();
        req.transaction = String.valueOf(System.currentTimeMillis());
        req.message = msg;
        if (shareType == ShareType.WEIXIN_FRIEND) {
            req.scene = SendMessageToWX.Req.WXSceneSession;
        } else {
            req.scene = SendMessageToWX.Req.WXSceneTimeline;
        }
        return req;
    }

    private IMediaObject getShareObject(@NonNull ShareContent shareContent) {
        IMediaObject mediaObject;
        switch (shareContent.getType()) {
            case Constants.SHARE_TYPE_TEXT:
                // 纯文字
                mediaObject = getTextObj(shareContent);
                break;
            case Constants.SHARE_TYPE_PIC:
                // 纯图片
                mediaObject = getImageObj(shareContent);
                break;
            case Constants.SHARE_TYPE_WEBPAGE:
                // 网页
                mediaObject = getWebPageObj(shareContent);
                break;
            case Constants.SHARE_TYPE_MUSIC:
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

    private IMediaObject getTextObj(ShareContent shareContent) {
        WXTextObject text = new WXTextObject();
        text.text = shareContent.getSummary();
        return text;
    }

    private IMediaObject getImageObj(ShareContent shareContent) {
        WXImageObject image = new WXImageObject();
        image.imageData = shareContent.getImageBmpBytes();
        return image;
    }

    private IMediaObject getWebPageObj(ShareContent shareContent) {
        WXWebpageObject webPage = new WXWebpageObject();
        webPage.webpageUrl = shareContent.getURL();
        return webPage;
    }

    private IMediaObject getMusicObj(ShareContent shareContent) {
        WXMusicObject music = new WXMusicObject();
        //Str1+"#wechat_music_url="+str2 ;str1是网页地址，str2是音乐地址。
        music.musicUrl = shareContent.getURL() + "#wechat_music_url=" + shareContent.getMusicUrl();
        return music;
    }

    private IMediaObject getAppObj(ShareContent shareContent) {
        WXAppExtendObject app = new WXAppExtendObject();
        // TODO: 2016/2/22  
       /* Log.d("ddd", "exinfo = " + ((ShareContentApp) shareContent).getAppInfo());
        app.extInfo = ((ShareContentApp) shareContent).getAppInfo();*/
        return app;
    }

}
