package com.liulishuo.engzo.share;

import com.liulishuo.engzo.utils.TestUtil;
import com.liulishuo.engzo.utils.With;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

import android.support.test.InstrumentationRegistry;
import android.support.test.filters.LargeTest;
import android.support.test.filters.SdkSuppress;
import android.support.test.runner.AndroidJUnit4;
import android.support.test.uiautomator.By;
import android.support.test.uiautomator.UiDevice;
import android.support.test.uiautomator.UiObjectNotFoundException;
import android.support.test.uiautomator.UiSelector;
import android.view.View;
import android.widget.TextView;

/**
 * @author Kale
 * @date 2016/10/7
 */
@RunWith(AndroidJUnit4.class)
@LargeTest
@SdkSuppress(minSdkVersion = 18)
public class QQFriendShareTest {

    private static UiDevice device;

    @BeforeClass
    public static void setup() {
        device = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation());
        TestUtil.maybeStartTestApp(device);
    }

    @AfterClass
    public static void teardown() {
        device = null;
    }

    @Before
    public void clickBtn() throws Exception {
        device.findObject(With.text("分享给QQ好友")).clickAndWaitForNewWindow();
    }

    @Test
    public void testShareSuccess() throws Exception {
        UiSelector firstFriendItem = With.clazz(View.class).childSelector(With.index(5));
        device.findObject(firstFriendItem).click();
        device.findObject(By.res("com.tencent.mobileqq:id/input")).setText("test by kale");
        device.findObject(With.text("发送")).clickAndWaitForNewWindow();
        device.waitForIdle();
        device.findObject(By.textContains("返回")).click();
        TestUtil.assertShareSucceed(device);
    }

    @Test
    public void testShareSuccessAndStayInQQ() throws UiObjectNotFoundException {
        device.findObject(By.text("我的电脑")).click();
        device.findObject(With.text("发送")).click();
        device.findObject(With.clazz(TextView.class).text("留在QQ")).click();
        device.pressBack();
        TestUtil.assertShareSucceed(device);
    }

    @Test
    public void testShareCancelByClient() {
        device.findObject(By.res("com.tencent.mobileqq:id/ivTitleBtnRightText").text("取消")).click();
        TestUtil.assertShareCanceled(device);
    }

    @Test
    public void testShareCancelByPressBack() {
        device.pressBack();
        TestUtil.assertShareCanceled(device);
    }

}
