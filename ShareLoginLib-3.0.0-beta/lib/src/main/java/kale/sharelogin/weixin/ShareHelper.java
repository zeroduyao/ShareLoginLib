package kale.sharelogin.weixin;

import java.util.Map;

import android.support.annotation.NonNull;
import android.support.v4.util.ArrayMap;

import kale.sharelogin.ShareListener;
import kale.sharelogin.content.ShareContent;
import kale.sharelogin.content.ShareContentType;
import kale.sharelogin.utils.SlUtils;
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
        // 1. 建立信息体
        WXMediaMessage msg = new WXMediaMessage();
        msg.title = shareContent.getTitle();
        msg.description = shareContent.getSummary();
        msg.thumbData = shareContent.getThumbBmpBytes();
        msg.mediaObject = createMediaObject(shareContent);

        // 2. 发送信息
        SendMessageToWX.Req req = new SendMessageToWX.Req();
        req.transaction = String.valueOf(System.currentTimeMillis());
        req.message = msg;
        req.scene = Integer.valueOf(shareType.substring(shareType.length() - 1));
        return req;
    }

    private static WXMediaMessage.IMediaObject createMediaObject(@NonNull ShareContent shareContent) {
        Map<Integer, SlUtils.Function<WXMediaMessage.IMediaObject>> map = new ArrayMap<>();

        map.put(ShareContentType.WEBPAGE, ShareHelper::getWebPageObj);
        map.put(ShareContentType.TEXT, ShareHelper::getTextObj);
        map.put(ShareContentType.PIC, ShareHelper::getImageObj);
        map.put(ShareContentType.MUSIC, ShareHelper::getMusicObj);

        return map.get(shareContent.getType()).apply(shareContent);
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
            default:
                listener.onError("未知错误，code：" + resp.errCode + ", message：" + resp.errStr);
        }
    }

}