package com.liulishuo.engzo.share.qq;

import android.support.test.uiautomator.By;
import android.support.test.uiautomator.UiObjectNotFoundException;
import android.widget.TextView;

import com.liulishuo.engzo.Constant;
import com.liulishuo.engzo.share.AbsShareTestCase;
import com.liulishuo.engzo.utils.With;

import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

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

    // TODO: 2018/9/15 留在qq

}
