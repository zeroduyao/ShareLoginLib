package com.liulishuo.engzo.login;

import android.support.test.InstrumentationRegistry;
import android.support.test.filters.LargeTest;
import android.support.test.filters.SdkSuppress;
import android.support.test.runner.AndroidJUnit4;
import android.support.test.uiautomator.By;
import android.support.test.uiautomator.UiDevice;
import android.widget.Button;

import com.liulishuo.engzo.utils.TestUtil;
import com.liulishuo.engzo.utils.With;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Kale
 * @date 2016/10/5
 */
@RunWith(AndroidJUnit4.class)
@LargeTest
@SdkSuppress(minSdkVersion = 18)
public class QQLoginTest {

    private static UiDevice device;

    @BeforeClass
    public static void setup() {
        device = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation());
        TestUtil.maybeStartTestApp(device);
    }

    @AfterClass
    public static void end() {
        device = null;
    }

    @Before
    public void clickBtn() throws Exception {
        device.findObject(With.text("QQ登录")).clickAndWaitForNewWindow();
    }

    @Test
    public void testLoginSuccess() {
        device.findObject(By.text("授权并登录").clazz(Button.class)).click(1000);
        TestUtil.assertLoginSucceed(device);
    }

    @Test
    public void testLoginCancelByClient() {
        device.findObject(By.res("com.tencent.mobileqq:id/ivTitleBtnLeft")).click();
        TestUtil.assertLoginCanceled(device);
    }

    @Test
    public void testLoginCancelByPressBack() {
        device.pressBack();
        TestUtil.assertLoginCanceled(device);
    }

}
