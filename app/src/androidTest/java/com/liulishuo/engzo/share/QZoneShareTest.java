package com.liulishuo.engzo.share;

import com.liulishuo.engzo.Constant;
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
import android.support.test.uiautomator.UiObject2;
import android.support.test.uiautomator.Until;
import android.widget.TextView;

/**
 * @author Kale
 * @date 2016/10/7
 */
@RunWith(AndroidJUnit4.class)
@LargeTest
@SdkSuppress(minSdkVersion = 18)
public class QZoneShareTest {

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
        TestUtil.initEachTestEnvironment(device);
        device.findObject(With.text("分享到QQ空间")).clickAndWaitForNewWindow();
    }

    @Test
    public void testShareSuccess() throws Exception {
        UiObject2 send = device.wait(Until.findObject((By.text("发送"))), 500);
        if (send != null) {
            send.click();
            device.wait(Until.findObject(By.textContains("返回")), Constant.MAX_TIMEOUT).click();
        } else {
            send = device.findObject(By.res("com.tencent.mobileqq:id/ivTitleBtnRightText"));
            send.click();
        }
        TestUtil.assertShareSucceed(device);
    }

    @Test
    public void testShareSuccessStayInQQ() throws Exception {
        device.findObject(With.text("发送")).click();
        device.findObject(With.clazz(TextView.class).text("留在好友动态")).click();
        device.pressBack();
        TestUtil.assertShareSucceed(device);
    }

    @Test
    public void testShareCancelByClient() {
        device.findObject(By.text("返回")).click();
        TestUtil.assertShareCanceled(device);
    }

    @Test
    public void testShareCancelByPressBack() {
        device.pressBack();
        TestUtil.assertShareCanceled(device);
    }

}
