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
        device.findObject(With.text("分享到QQ空间")).clickAndWaitForNewWindow();
    }

    @Test
    public void testShareSuccess() throws Exception {
        device.findObject(With.text("发送")).clickAndWaitForNewWindow();
        device.findObject(By.textContains("返回")).click();
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
