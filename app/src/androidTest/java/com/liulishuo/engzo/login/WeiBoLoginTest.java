package com.liulishuo.engzo.login;

import com.liulishuo.engzo.utils.Constant;
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

import static android.support.test.uiautomator.Until.findObject;

/**
 * @author Kale
 * @date 2016/10/5
 */
@RunWith(AndroidJUnit4.class)
@LargeTest
@SdkSuppress(minSdkVersion = 18)
public class WeiBoLoginTest {

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
        device.findObject(With.text("微博登录")).clickAndWaitForNewWindow();
    }

    @Test
    public void testLoginSuccess() throws Exception {
        device.findObject(By.res("com.sina.weibo", "bnLogin")).click();
        TestUtil.assertLoginSucceed(device);
    }

    @Test
    public void testLoginCancelByClient() {
        device.findObject(By.res("com.sina.weibo:id/titleBack")).click();
        TestUtil.assertLoginCanceled(device);
    }

    @Test
    public void testLoginCancelByPressBack() {
        device.wait(findObject(By.text("微博登录")), Constant.MAX_TIMEOUT);
        device.pressBack();
        TestUtil.assertLoginCanceled(device);
    }

}
