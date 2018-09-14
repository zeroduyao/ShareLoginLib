package kale.sharelogin.qq;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Map;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.util.ArrayMap;

import kale.sharelogin.ShareLoginLib;
import kale.sharelogin.content.ShareContent;
import kale.sharelogin.content.ShareContentType;
import kale.sharelogin.utils.SlUtils;
import com.tencent.connect.share.QQShare;
import com.tencent.connect.share.QzonePublish;
import com.tencent.connect.share.QzoneShare;

/**
 * @author Kale
 * @date 2018/9/11
 */
class ShareHelper {

    /**
     * "http://wiki.open.qq.com/wiki/mobile/API%E8%B0%83%E7%94%A8%E8%AF%B4%E6%98%8E#1.13_.E5.88.86.E4.BA.AB.E6.B6.88.E6.81.AF.E5.88.B0QQ.EF.BC.88.E6.97.A0.E9.9C.80QQ.E7.99.BB.E5.BD.95.EF.BC.89"
     */
    static Bundle qqFriendBundle(ShareContent shareContent) {
        Map<Integer, SlUtils.Function<Intent>> map = new ArrayMap<>();

        map.put(ShareContentType.WEBPAGE, ShareHelper::getWebPageObj);
        map.put(ShareContentType.PIC, ShareHelper::getImageObj);
        map.put(ShareContentType.MUSIC, ShareHelper::getMusicObj);

        Intent intent = map.get(shareContent.getType()).apply(shareContent);
        return buildQQParams(intent, shareContent).getExtras();
    }

    /**
     * QQShare.SHARE_TO_QQ_KEY_TYPE 	必填 	Int 	分享的类型。图文分享(普通分享)填：Tencent.SHARE_TO_QQ_TYPE_DEFAULT
     * QQShare.PARAM_TARGET_URL 	必填 	String 	这条分享消息被好友点击后的跳转URL，必须为真实可用的url才行
     */
    private static Intent getWebPageObj(ShareContent shareContent) {
        return new Intent()
                .putExtra(QQShare.SHARE_TO_QQ_KEY_TYPE, QQShare.SHARE_TO_QQ_TYPE_DEFAULT)
                .putExtra(QQShare.SHARE_TO_QQ_TARGET_URL, shareContent.getURL())
                .putExtra(QQShare.SHARE_TO_QQ_IMAGE_URL, getThumbImageUri(shareContent));
    }

    /**
     * QQShare.SHARE_TO_QQ_KEY_TYPE	必填	Int	分享的类型。分享音乐填Tencent.SHARE_TO_QQ_TYPE_AUDIO。
     * QQShare.PARAM_TARGET_URL		必选	这条分享消息被好友点击后的跳转URL，必须为真实可用的url才行
     * QQShare.SHARE_TO_QQ_AUDIO_URL	必填	String	音乐文件的远程链接, 以URL的形式传入, 不支持本地音乐。
     */
    private static Intent getMusicObj(ShareContent shareContent) {
        return new Intent()
                .putExtra(QQShare.SHARE_TO_QQ_KEY_TYPE, QQShare.SHARE_TO_QQ_TYPE_AUDIO)
                .putExtra(QQShare.SHARE_TO_QQ_AUDIO_URL, shareContent.getMusicUrl())
                .putExtra(QQShare.SHARE_TO_QQ_IMAGE_URL, getThumbImageUri(shareContent))
                .putExtra(QQShare.SHARE_TO_QQ_TARGET_URL, shareContent.getURL());
    }

    /**
     * QQShare.SHARE_TO_QQ_KEY_TYPE		必选	分享类型，分享纯图片时填写：QQShare.SHARE_TO_QQ_TYPE_IMAGE
     * QQShare.SHARE_TO_QQ_IMAGE_LOCAL_URL	必选	String	需要分享的本地图片路径
     */
    private static Intent getImageObj(ShareContent shareContent) {
        Intent intent = new Intent();
        intent.putExtra(QQShare.SHARE_TO_QQ_KEY_TYPE, QQShare.SHARE_TO_QQ_TYPE_IMAGE);
        String uri = shareContent.getLargeBmpPath();
        if (uri != null) {
            if (uri.startsWith("http")) {
                intent.putExtra(QQShare.SHARE_TO_QQ_IMAGE_URL, uri); // net image
            } else {
                intent.putExtra(QQShare.SHARE_TO_QQ_IMAGE_LOCAL_URL, uri); // local image
            }
        }
        return intent;
    }

    /**
     * 将分享的参数做最后一次组装
     *
     * QQShare.PARAM_TITLE 	        必填 	String 	分享的标题, 最长30个字符（对图片的分享无效）
     *
     * ---------------------------------------------------------------------------------
     *
     * QQShare.SHARE_TO_QQ_IMAGE_URL 	可选 	String 	分享图片的URL或者本地路径
     *
     * QQShare.PARAM_SUMMARY 	        可选 	String 	分享的消息摘要，最长40个字
     *
     * QQShare.SHARE_TO_QQ_APP_NAME 	可选 	String 	手Q客户端顶部，替换“返回”按钮文字，如果为空则用返回代替
     *
     * ---------------------------------------------------------------------------------
     *
     * QQShare.SHARE_TO_QQ_EXT_INT 	可选 	Int 	分享额外选项，两种类型可选（默认是不隐藏分享到QZone按钮且不自动打开分享到QZone的对话框）
     * QQShare.SHARE_TO_QQ_EXT_INT可选的值如下：
     * QQShare.SHARE_TO_QQ_FLAG_QZONE_AUTO_OPEN：分享时自动打开分享到QZone的对话框。
     * QQShare.SHARE_TO_QQ_FLAG_QZONE_ITEM_HIDE：分享时隐藏分享到QZone按钮
     */
    private static Intent buildQQParams(@Nullable Intent intent, ShareContent shareContent) {
        assert intent != null;
        return intent
                .putExtra(QQShare.SHARE_TO_QQ_TITLE, shareContent.getTitle())
                .putExtra(QQShare.SHARE_TO_QQ_SUMMARY, shareContent.getSummary())
                .putExtra(QQShare.SHARE_TO_QQ_APP_NAME, ShareLoginLib.APP_NAME);
    }

    /**
     * 分享到QQ空间（目前支持图文分享、发表说说、视频或上传图片），不用用户授权
     * 调用后将打开手机QQ内QQ空间的界面，或者用浏览器打开QQ空间页面进行分享操作。
     *
     * ---------------------------------------------------------------------------------
     *
     * QzoneShare.SHARE_TO_QQ_KEY_TYPE 	    必填      Int 	SHARE_TO_QZONE_TYPE_IMAGE_TEXT（图文）
     * QzoneShare.SHARE_TO_QQ_TITLE 	    必填      Int 	分享的标题，最多200个字符
     * QzoneShare.SHARE_TO_QQ_TARGET_URL    必填      String 	跳转URL，URL字符串
     *
     * ---------------------------------------------------------------------------------
     *
     * QzoneShare.SHARE_TO_QQ_SUMMARY 	    选填      String 	分享的摘要，最多600字符
     * QzoneShare.SHARE_TO_QQ_IMAGE_URL     选填      String     图片链接ArrayList
     *
     * 注意:QZone接口暂不支持发送多张图片的能力，若传入多张图片，则会自动选入第一张图片作为预览图，多图的能力将会在以后支持。
     *
     * 如果分享的图片url是本地的图片地址那么在分享时会显示图片，如果分享的是图片的网址，那么就不会在分享时显示图片
     */
    static Bundle zoneBundle(ShareContent shareContent) {
        // 分享的图片, 以ArrayList<String>的类型传入，以便支持多张图片 （注：图片最多支持9张图片，多余的图片会被丢弃）。
        ArrayList<String> list = new ArrayList<>(Collections.singletonList(getThumbImageUri(shareContent)));

        return new Intent()
                .putExtra(QzoneShare.SHARE_TO_QZONE_KEY_TYPE, QzoneShare.SHARE_TO_QZONE_TYPE_IMAGE_TEXT)
                .putExtra(QzoneShare.SHARE_TO_QQ_TITLE, shareContent.getTitle())
                .putExtra(QzoneShare.SHARE_TO_QQ_SUMMARY, shareContent.getSummary())
                .putExtra(QzoneShare.SHARE_TO_QQ_TARGET_URL, shareContent.getURL())
                .putExtra(QzoneShare.SHARE_TO_QQ_IMAGE_URL, list)
                .getExtras();
    }

    /**
     * PUBLISH_TO_QZONE_KEY_TYP	必填	Int	QzonePublish.PUBLISH_TO_QZONE_TYPE_PUBLISHMOOD（发表说说、上传图片）
     * PUBLISH_TO_QZONE_SUMMARY	选填	String	说说正文（传图和传视频接口会过滤第三方传过来的自带描述，目的为了鼓励用户自行输入有价值信息）
     * PUBLISH_TO_QZONE_IMAGE_URL	选填	说说的图片, 以ArrayList<String>的类型传入，以便支持多张图片（注：<=9张图片为发表说说，>9张为上传图片到相册），只支持本地图片
     * PUBLISH_TO_QZONE_VIDEO_PATH	选填	发表的视频，只支持本地地址，发表视频时必填；上传视频的大小最好控制在100M以内（因为QQ普通用户上传视频必须在100M以内，黄钻用户可上传1G以内视频，大于1G会直接报错。）
     */
    static Bundle publishToQzoneBundle(ShareContent shareContent) {
        ArrayList<String> list = new ArrayList<>();
        list.add(shareContent.getLargeBmpPath());

        return new Intent()
                .putExtra(QzoneShare.SHARE_TO_QZONE_KEY_TYPE, QzonePublish.PUBLISH_TO_QZONE_TYPE_PUBLISHMOOD)
                .putExtra(QzoneShare.SHARE_TO_QQ_SUMMARY, shareContent.getSummary())
                .putExtra(QzoneShare.SHARE_TO_QQ_IMAGE_URL, list)
                .getExtras();
    }

    /**
     * 得到分享时的预览小图
     */
    @Nullable
    private static String getThumbImageUri(@NonNull ShareContent content) {
        return SlUtils.saveBytesToFile(content.getThumbBmpBytes(), ShareLoginLib.TEMP_PIC_DIR + "share_login_lib_thumb_pic");
    }

}