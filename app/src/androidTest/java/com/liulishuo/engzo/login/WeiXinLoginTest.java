package com.liulishuo.engzo.login;

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
import android.widget.ImageView;

/**
 * @author Kale
 * @date 2016/10/5
 */
@RunWith(AndroidJUnit4.class)
@LargeTest
@SdkSuppress(minSdkVersion = 18)
public class WeiXinLoginTest {

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
    public void clickBtn() throws UiObjectNotFoundException {
        device.findObject(With.text("微信登录")).clickAndWaitForNewWindow();
    }

    @Test
    public void testLoginSuccess() throws Exception {
        device.waitForIdle();
        device.click(427, 789); // WebView
        TestUtil.assertLoginSucceed(device);
    }

    @Test
    public void testLoginCancelByClient() {
        device.findObject(By.clazz(ImageView.class).desc("返回")).click();
        TestUtil.assertLoginCanceled(device);
    }

    @Test
    public void testLoginCancelByPressBack() {
        device.pressBack();
        TestUtil.assertLoginCanceled(device);
    }

}
