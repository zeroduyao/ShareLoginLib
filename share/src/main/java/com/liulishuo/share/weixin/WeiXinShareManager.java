package com.liulishuo.share.weixin;

import com.liulishuo.share.ShareBlock;
import com.liulishuo.share.base.Constants;
import com.liulishuo.share.base.share.IShareManager;
import com.liulishuo.share.base.share.ShareStateListener;
import com.liulishuo.share.base.shareContent.ShareContent;
import com.liulishuo.share.util.ShareUtil;
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

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.widget.Toast;

import static com.tencent.mm.sdk.modelmsg.WXMediaMessage.IMediaObject;

/**
 * Created by echo on 5/18/15.
 */
public class WeiXinShareManager implements IShareManager {

    private static ShareStateListener mShareStateListener;

    /**
     * 进行分享
     */
    @Override
    public void share(@NonNull Activity activity, @NonNull ShareContent shareContent, @ShareBlock.ShareType int shareType, 
            @Nullable ShareStateListener listener) {
        
        String weChatAppId = ShareBlock.getInstance().weiXinAppId;
        if (TextUtils.isEmpty(weChatAppId)) {
            throw new NullPointerException("请通过shareBlock初始化WeChatAppId");
        }
        IWXAPI IWXAPI = WXAPIFactory.createWXAPI(activity, weChatAppId, true);
        if (!IWXAPI.isWXAppInstalled()) {
            Toast.makeText(activity, "请安装微信哦~", Toast.LENGTH_SHORT).show();
        } else {
            IWXAPI.registerApp(weChatAppId);
        }
        
        mShareStateListener = listener;
        SendMessageToWX.Req req = getReq(shareContent, shareType);
        IWXAPI.sendReq(req); 
    }


    /**
     * 解析分享到微信的结果
     */
    protected static void onShareResp(BaseResp resp) {
        if (mShareStateListener != null) {
            switch (resp.errCode) {
                case BaseResp.ErrCode.ERR_OK:
                    // 分享成功
                    mShareStateListener.onSuccess();
                    break;
                case BaseResp.ErrCode.ERR_USER_CANCEL:
                    // 用户取消
                    mShareStateListener.onCancel();
                    break;
                case BaseResp.ErrCode.ERR_AUTH_DENIED:
                    // 用户拒绝授权
                    mShareStateListener.onError("用户拒绝授权");
                    break;
                case BaseResp.ErrCode.ERR_SENT_FAILED:
                    // 发送失败
                    mShareStateListener.onError("发送失败");
                    break;
                case BaseResp.ErrCode.ERR_COMM:
                    // 一般错误
                    mShareStateListener.onError("一般错误");
                    break;
                default:
                    mShareStateListener.onError("未知错误");
            }
        }
    }

    // --------------------------

    @NonNull
    private SendMessageToWX.Req getReq(@NonNull ShareContent shareContent, @ShareBlock.ShareType int shareType) {
        // 建立信息体
        WXMediaMessage msg = new WXMediaMessage(getShareObject(shareContent));
        msg.title = shareContent.getTitle();
        msg.description = shareContent.getSummary();
        msg.thumbData = shareContent.getImageBmpBytes();

        // 发送信息
        SendMessageToWX.Req req = new SendMessageToWX.Req();
        req.transaction = ShareUtil.buildTransaction("tag");
        req.message = msg;
        req.scene = shareType;
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
        WXWebpageObject webpage = new WXWebpageObject();
        webpage.webpageUrl = shareContent.getURL();
        return webpage;
    }

    private IMediaObject getMusicObj(ShareContent shareContent) {
        WXMusicObject music = new WXMusicObject();
        //Str1+"#wechat_music_url="+str2 ;str1是网页地址，str2是音乐地址。
        music.musicUrl = shareContent.getURL() + "#wechat_music_url=" + shareContent.getMusicUrl();
        return music;
    }

    private IMediaObject getAppObj(ShareContent shareContent) {
        WXAppExtendObject app = new WXAppExtendObject();
       /* Log.d("ddd", "exinfo = " + ((ShareContentApp) shareContent).getAppInfo());
        app.extInfo = ((ShareContentApp) shareContent).getAppInfo();*/
        return app;
    }

    

    /**
     * @return 是否已经安装微信
     */
    public static boolean isWeiXinInstalled(Context context) {
        IWXAPI api = WXAPIFactory.createWXAPI(context, ShareBlock.getInstance().weiXinAppId, true);
        return api.isWXAppInstalled();
    }

}
