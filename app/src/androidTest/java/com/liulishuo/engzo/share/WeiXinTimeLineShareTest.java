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
import android.widget.ImageView;

import static android.support.test.uiautomator.Until.findObject;
import static com.liulishuo.engzo.utils.Constant.MAX_TIMEOUT;

/**
 * @author Kale
 * @date 2016/10/7
 */
@RunWith(AndroidJUnit4.class)
@LargeTest
@SdkSuppress(minSdkVersion = 18)
public class WeiXinTimeLineShareTest {

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
        
        device.findObject(With.text("分享到微信朋友圈")).clickAndWaitForNewWindow();
        // 微信界面有延迟，所以需要等待界面显示完毕后再进行操作
        device.wait(findObject(By.text("发送")), MAX_TIMEOUT);
    }

    @Test
    public void testShareWithoutText() throws Exception {
        device.findObject(By.text("发送")).click();
        TestUtil.assertShareSucceed(device);
    }

    @Test
    public void testShareWithText() throws Exception {
        device.findObject(By.clazz("android.widget.EditText").textContains("这一刻的想法"))
                .setText("test by kale");
        device.findObject(By.text("发送")).click();
        TestUtil.assertShareSucceed(device);
    }

    @Test
    public void testShareCancelByClient() throws InterruptedException {
        device.findObject(By.clazz(ImageView.class).desc("返回")).click();
        device.wait(findObject(By.text("退出")), MAX_TIMEOUT).click();
        TestUtil.assertShareCanceled(device);
    }

    @Test
    public void testShareCancelByPressBack() {
        device.pressBack();
        device.wait(findObject(By.text("退出")), MAX_TIMEOUT).click();
        TestUtil.assertShareCanceled(device);
    }

}
