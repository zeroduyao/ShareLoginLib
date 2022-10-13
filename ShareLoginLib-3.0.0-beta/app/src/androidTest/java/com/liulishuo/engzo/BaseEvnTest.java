package com.liulishuo.engzo;

import android.content.Context;
import android.content.Intent;
import android.support.test.InstrumentationRegistry;
import android.support.test.filters.LargeTest;
import android.support.test.filters.SdkSuppress;
import android.support.test.runner.AndroidJUnit4;
import android.support.test.uiautomator.By;
import android.support.test.uiautomator.UiDevice;
import android.support.test.uiautomator.Until;

import com.liulishuo.engzo.utils.TestUtils;

import org.junit.Test;
import org.junit.runner.RunWith;

import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertThat;

/**
 * @author Kale
 * @date 2016/10/6
 *
 * https://github.com/googlesamples/android-testing/blob/master/ui/uiautomator/BasicSample/app/src/androidTest/java/com/example/android/testing/uiautomator/BasicSample/ChangeTextBehaviorTest.java
 *
 * 测试当前设备上的基本环境是否正常，检测待测app是否已经被安装
 */
@RunWith(AndroidJUnit4.class)
@LargeTest
@SdkSuppress(minSdkVersion = 18)
public class BaseEvnTest {

    @Test
    public void runTargetApp() {
        UiDevice uiDevice = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation());
        Context context = InstrumentationRegistry.getInstrumentation().getContext();
        Intent intent = context.getPackageManager().getLaunchIntentForPackage(Constant.APP_PACKAGE_NAME);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);

        // Clear out any previous instances
        context.startActivity(intent);

        uiDevice.wait(Until.hasObject(By.pkg(Constant.APP_PACKAGE_NAME).depth(0)), 2000);
    }

    @Test
    public void checkPreconditions() {
        UiDevice uiDevice = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation());
        TestUtils.startTestAppAndWait(uiDevice);

        assertThat("uiDevice should exist", uiDevice, notNullValue());
    }

}
