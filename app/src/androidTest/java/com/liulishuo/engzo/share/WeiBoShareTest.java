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
import android.support.test.uiautomator.Until;

import static com.liulishuo.engzo.Constant.MAX_TIMEOUT;

/**
 * @author Kale
 * @date 2016/10/6
 */
@RunWith(AndroidJUnit4.class)
@LargeTest
@SdkSuppress(minSdkVersion = 18)
public class WeiBoShareTest {

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
        device.findObject(With.text("分享到微博")).clickAndWaitForNewWindow();
    }

    @Test
    public void testShareSuccess() throws Exception {
        device.findObject(With.text("公开")).clickAndWaitForNewWindow();
        device.findObject(With.text("仅自己可见")).clickAndWaitForNewWindow();

        device.findObject(By.res("com.sina.weibo:id/edit_view")).setText("test by kale#test#");
        device.findObject(By.text("发送")).click();
        TestUtil.assertShareSucceed(device);
    }

    @Test
    public void testShareCancelWithSave() throws Exception {
        device.findObject(By.res("com.sina.weibo:id/edit_view")).setText("test by kale#test#");
        device.findObject(By.res("com.sina.weibo:id/titleBack")).click();

        device.waitForIdle();
        device.wait(Until.findObject(By.text("保存")), MAX_TIMEOUT).click();
        TestUtil.assertShareCanceled(device);
    }

    @Test
    public void testShareCancelWithoutSave() throws UiObjectNotFoundException {
        device.findObject(By.res("com.sina.weibo:id/edit_view")).setText("test by kale@天之界线2010");
        device.findObject(By.res("com.sina.weibo:id/titleBack")).click();

        // 这个不保存有时候点击后无效，所以加了wait
        device.waitForIdle();
        device.wait(Until.findObject(By.text("不保存")), MAX_TIMEOUT).click();
        TestUtil.assertShareCanceled(device);
    }

    @Test
    public void testShareCancelByClient() {
        device.findObject(By.res("com.sina.weibo:id/titleBack")).click();
        TestUtil.assertShareCanceled(device);
    }

    @Test
    public void testShareCancelByPressBack() {
        device.pressBack();
        device.pressBack();
        TestUtil.assertShareCanceled(device);
    }

}
