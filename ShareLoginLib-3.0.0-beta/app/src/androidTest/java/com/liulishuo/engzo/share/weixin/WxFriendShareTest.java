package com.liulishuo.engzo.share.weixin;

import android.support.test.uiautomator.By;
import android.support.test.uiautomator.UiObjectNotFoundException;
import android.support.test.uiautomator.UiSelector;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.liulishuo.engzo.share.AbsShareTestCase;
import com.liulishuo.engzo.utils.With;

import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

/**
 * @author Kale
 * @date 2016/10/5
 *
 * Note:
 * 如果用户选择留在微信，那么应用则无法接收到回调，这点需要开发者知晓一下
 * 最新版本的微信，无论是否取消分享，都会是分享成功的回调
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class WxFriendShareTest extends AbsShareTestCase {

    @Override
    protected void clickButton() throws UiObjectNotFoundException {
        device.findObject(With.text("分享到微信")).clickAndWaitForNewWindow();
    }

    /**
     * 最新版本的微信，无论是否取消分享，都会是分享成功的回调
     */
    @Test
    @Override
    public void shareCanceled_by_clickCancelButton() {
        device.findObject(By.clazz(ImageView.class).desc("返回")).click();
        assertShareIsSucceed();
    }

    /**
     * 最新版本的微信，无论是否取消分享，都会是分享成功的回调
     */
    @Override
    public void shareCanceled_by_pressBackButton() {
        device.pressBack();
        assertShareIsSucceed();
    }

    @Test
    @Override
    public void shareSuccess_by_clickSendButton() throws UiObjectNotFoundException {
        // 找到最近联系的第一个好友
        UiSelector firstFriendItem = With.clazz(ListView.class).childSelector(With.index(2));
        device.findObject(firstFriendItem).click();
        device.findObject(With.text("取消")).click();

        device.findObject(firstFriendItem).click();
        device.findObject(By.clazz(EditText.class)).setText("test by kale");
        device.findObject(With.text("分享")).click();

        device.findObject(By.clazz(Button.class).textContains("返回")).click();
        assertShareIsSucceed();
    }

    /**
     * 如果用户选择留在微信，那么应用则无法接收到回调
     */
    @Test
    public void shareSuccess_and_stayInWeiXin() throws Exception {
        device.findObject(With.text("创建新聊天")).clickAndWaitForNewWindow();
        // 定位到某个微信好友
        device.findObject(With.clazz(ListView.class).childSelector(With.text("小闲"))).click();
        device.findObject(With.clazz(TextView.class).textStartsWith("确定").clickable(true)).clickAndWaitForNewWindow();

        device.findObject(By.text("分享")).click();

        device.findObject(With.clazz(Button.class).text("留在微信")).clickAndWaitForNewWindow();

        device.pressBack(); // 返回应用
    }

    public static class SingleTxtShareTest extends WxFriendShareTest {

        @Override
        protected void clickButton() throws UiObjectNotFoundException {
            device.findObject(With.text("仅文字")).click(); // 测试分享单个文案
            super.clickButton();
        }
    }

    public static class SinglePicShareTest extends WxFriendShareTest {

        @Override
        protected void clickButton() throws UiObjectNotFoundException {
            device.findObject(With.text("仅图片")).click(); // 测试分享单个图片
            super.clickButton();
        }
    }

}
