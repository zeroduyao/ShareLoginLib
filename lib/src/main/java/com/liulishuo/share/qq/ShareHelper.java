package com.liulishuo.share.qq;

import java.util.ArrayList;
import java.util.Collections;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.liulishuo.share.ShareLoginLib;
import com.liulishuo.share.content.ShareContent;
import com.liulishuo.share.content.ShareContentType;
import com.liulishuo.share.utils.SlUtils;
import com.tencent.connect.share.QQShare;
import com.tencent.connect.share.QzoneShare;

/**
 * @author Kale
 * @date 2018/9/11
 */
class ShareHelper {

    @NonNull
    Bundle createQQFriendBundle(ShareContent shareContent) {
        Bundle bundle;
        switch (shareContent.getType()) {
            case ShareContentType.PIC:
                // 纯图片
                bundle = getImageObj(shareContent);
                break;
            case ShareContentType.WEBPAGE:
                // 网页
                bundle = getWebPageObj();
                break;
            case ShareContentType.MUSIC:
                // 音乐
                bundle = getMusicObj(shareContent);
                break;
            case ShareContentType.TEXT:
            default:
                throw new UnsupportedOperationException("不支持的分享内容");
        }
        return getQQFriendParams(bundle, shareContent);
    }

    /**
     * @see "http://wiki.open.qq.com/wiki/mobile/API%E8%B0%83%E7%94%A8%E8%AF%B4%E6%98%8E#1.13_.E5.88.86.E4.BA.AB.E6.B6.88.E6.81.AF.E5.88.B0QQ.EF.BC.88.E6.97.A0.E9.9C.80QQ.E7.99.BB.E5.BD.95.EF.BC.89"
     * QQShare.PARAM_TITLE 	        必填 	String 	分享的标题, 最长30个字符。
     * QQShare.SHARE_TO_QQ_KEY_TYPE 	必填 	Int 	分享的类型。图文分享(普通分享)填Tencent.SHARE_TO_QQ_TYPE_DEFAULT
     * QQShare.PARAM_TARGET_URL 	必填 	String 	这条分享消息被好友点击后的跳转URL。
     * QQShare.PARAM_SUMMARY 	        可选 	String 	分享的消息摘要，最长40个字。
     * QQShare.SHARE_TO_QQ_IMAGE_URL 	可选 	String 	分享图片的URL或者本地路径
     * QQShare.SHARE_TO_QQ_APP_NAME 	可选 	String 	手Q客户端顶部，替换“返回”按钮文字，如果为空，用返回代替
     * QQShare.SHARE_TO_QQ_EXT_INT 	可选 	Int 	分享额外选项，两种类型可选（默认是不隐藏分享到QZone按钮且不自动打开分享到QZone的对话框）：
     * QQShare.SHARE_TO_QQ_FLAG_QZONE_AUTO_OPEN，分享时自动打开分享到QZone的对话框。
     * QQShare.SHARE_TO_QQ_FLAG_QZONE_ITEM_HIDE，分享时隐藏分享到QZone按钮
     *
     * target必须是真实的可跳转链接才能跳到QQ = =！
     *
     * 发送给QQ好友
     */
    private Bundle getQQFriendParams(Bundle params, ShareContent shareContent) {
        params.putString(QQShare.SHARE_TO_QQ_TITLE, shareContent.getTitle()); // 标题
        params.putString(QQShare.SHARE_TO_QQ_SUMMARY, shareContent.getSummary()); // 描述
        params.putString(QQShare.SHARE_TO_QQ_TARGET_URL, shareContent.getURL()); // 这条分享消息被好友点击后的跳转URL
        params.putString(QQShare.SHARE_TO_QQ_APP_NAME, ShareLoginLib.APP_NAME); // 手Q客户端顶部，替换“返回”按钮文字，如果为空，用返回代替 (可选)
        return params;
    }

    private Bundle getImageObj(ShareContent shareContent) {
        final Bundle params = new Bundle();
        params.putInt(QQShare.SHARE_TO_QQ_KEY_TYPE, QQShare.SHARE_TO_QQ_TYPE_IMAGE); // 标识分享的是纯图片 (必填)
        String uri = getImageUri(shareContent, true);
        if (uri != null) {
            if (uri.startsWith("http")) {
                params.putString(QQShare.SHARE_TO_QQ_IMAGE_URL, uri); // net uri
            } else {
                params.putString(QQShare.SHARE_TO_QQ_IMAGE_LOCAL_URL, uri); // local uri
            }
        }
        return params;
    }

    private Bundle getWebPageObj() {
        final Bundle params = new Bundle();
        params.putInt(QQShare.SHARE_TO_QQ_KEY_TYPE, QQShare.SHARE_TO_QQ_TYPE_DEFAULT);
        return params;
    }

    private Bundle getMusicObj(ShareContent shareContent) {
        final Bundle params = new Bundle();
        params.putInt(QQShare.SHARE_TO_QQ_KEY_TYPE, QQShare.SHARE_TO_QQ_TYPE_AUDIO); // 标识分享的是音乐 (必填)
        params.putString(QQShare.SHARE_TO_QQ_AUDIO_URL, shareContent.getMusicUrl()); //  音乐链接 (必填)
        return params;
    }

    /**
     * 分享到QQ空间（目前支持图文分享）
     *
     * @see "http://wiki.open.qq.com/wiki/Android_API%E8%B0%83%E7%94%A8%E8%AF%B4%E6%98%8E#1.14_.E5.88.86.E4.BA.AB.E5.88.B0QQ.E7.A9.BA.E9.97.B4.EF.BC.88.E6.97.A0.E9.9C.80QQ.E7.99.BB.E5.BD.95.EF.BC.89"
     * QzoneShare.SHARE_TO_QQ_KEY_TYPE 	    选填      Int 	SHARE_TO_QZONE_TYPE_IMAGE_TEXT（图文）
     * QzoneShare.SHARE_TO_QQ_TITLE 	    必填      Int 	分享的标题，最多200个字符。
     * QzoneShare.SHARE_TO_QQ_SUMMARY 	    选填      String 	分享的摘要，最多600字符。
     * QzoneShare.SHARE_TO_QQ_TARGET_URL    必填      String 	跳转URL，URL字符串。
     * QzoneShare.SHARE_TO_QQ_IMAGE_URL     选填      String     图片链接ArrayList
     *
     * 注意:QZone接口暂不支持发送多张图片的能力，若传入多张图片，则会自动选入第一张图片作为预览图。多图的能力将会在以后支持。
     *
     * 如果分享的图片url是本地的图片地址那么在分享时会显示图片，如果分享的是图片的网址，那么就不会在分享时显示图片
     */
    Bundle createQZoneBundle(ShareContent shareContent) {
        Bundle params = new Bundle();
        params.putInt(QzoneShare.SHARE_TO_QZONE_KEY_TYPE, QzoneShare.SHARE_TO_QZONE_TYPE_IMAGE_TEXT);
        String title = shareContent.getTitle();
        params.putString(QzoneShare.SHARE_TO_QQ_TITLE, title); // 标题
        params.putString(QzoneShare.SHARE_TO_QQ_SUMMARY, shareContent.getSummary()); // 描述
        params.putString(QzoneShare.SHARE_TO_QQ_TARGET_URL, shareContent.getURL()); // 点击后跳转的url

        // 分享的图片, 以ArrayList<String>的类型传入，以便支持多张图片 （注：图片最多支持9张图片，多余的图片会被丢弃）。
        ArrayList<String> value = new ArrayList<>(Collections.singletonList(getImageUri(shareContent, false)));
        params.putStringArrayList(QzoneShare.SHARE_TO_QQ_IMAGE_URL, value);
        return params;
    }

    @Nullable
    private String getImageUri(@NonNull ShareContent content, boolean isLargePic) {
        if (isLargePic) {
            return content.getLargeBmpPath();
        } else {
            return SlUtils.saveBytesToFile(content.getThumbBmpBytes(), ShareLoginLib.TEMP_PIC_DIR + "share_login_lib_thumb_pic");
        }
    }

}