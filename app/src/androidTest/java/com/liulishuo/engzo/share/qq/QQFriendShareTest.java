package com.liulishuo.engzo.share.qq;

import android.support.test.uiautomator.By;
import android.support.test.uiautomator.SearchCondition;
import android.support.test.uiautomator.UiObject2;
import android.support.test.uiautomator.UiObjectNotFoundException;
import android.widget.TextView;

import com.liulishuo.engzo.BaseTestCase;
import com.liulishuo.engzo.Constant;
import com.liulishuo.engzo.share.AbsShareTestCase;
import com.liulishuo.engzo.utils.With;

import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import static android.support.test.uiautomator.Until.findObject;
import static com.liulishuo.engzo.Constant.APP_PACKAGE_NAME;
import static com.liulishuo.engzo.Constant.MAX_TIMEOUT;
import static org.hamcrest.CoreMatchers.containsString;
import static org.junit.Assert.assertThat;

/**
 * @author Kale
 * @date 2016/10/7
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class QQFriendShareTest extends AbsShareTestCase {

    @Override
    protected void clickButton() throws UiObjectNotFoundException {
        device.findObject(With.text("分享给QQ好友")).clickAndWaitForNewWindow();
    }

    @Override
    public void shareCanceled_by_clickCancelButton() {
        device.findObject(By.res("com.tencent.mobileqq:id/ivTitleBtnRightText").text("取消")).click();
        assertShareIsCanceled();
    }

    @Test
    public void shareCanceled_by_clickCancelButton_inDialog() throws UiObjectNotFoundException {
        device.findObject(With.text("我的电脑")).clickAndWaitForNewWindow();
        device.findObject(By.res("com.tencent.mobileqq:id/dialogLeftBtn")
                .clazz(TextView.class).text("取消")).click();

        device.waitForWindowUpdate(Constant.APP_PACKAGE_NAME, 500);

        device.pressBack();
        assertShareIsCanceled();
    }

    @Test
    @Override
    public void shareCanceled_by_pressBackButton() {
        device.pressBack();
        assertShareIsCanceled();
    }

    @Test
    @Override
    public void shareSuccess_by_clickSendButton() throws UiObjectNotFoundException {
        device.findObject(By.text("我的电脑")).click();
        device.findObject(With.text("发送")).clickAndWaitForNewWindow();
        device.waitForIdle();
        device.findObject(By.textContains("返回" + "登录分享Demo")).click();
        assertShareIsSucceed();
    }

    @Test
    public void shareSuccess_and_stayInQQ() throws UiObjectNotFoundException {
        device.findObject(By.text("我的电脑")).click();
        device.findObject(With.text("发送")).clickAndWaitForNewWindow();
        device.findObject(With.clazz(TextView.class).text("留在QQ")).click();

        device.waitForIdle();
        device.pressBack();

        assertShareIsSucceed();
    }

    public static class SingleTxtShareTest extends BaseTestCase {

        @Override
        protected void clickButton() throws UiObjectNotFoundException {
            device.findObject(With.text("仅文字")).click();
            device.findObject(With.text("分享给QQ好友")).click();
        }

        @Test
        public void support_shareText_toFriend() {
            SearchCondition<UiObject2> infoTv = findObject(By.res(APP_PACKAGE_NAME, "result"));
            assertThat(device.wait(infoTv, MAX_TIMEOUT).getText(),
                    containsString("目前不支持分享纯文本信息给QQ好友"));
        }
    }

    public static class SinglePicShareTest extends QQFriendShareTest {

        @Override
        protected void clickButton() throws UiObjectNotFoundException {
            device.findObject(With.text("仅图片")).click();
            super.clickButton();
        }
    }

}
