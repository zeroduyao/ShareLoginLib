package com.liulishuo.engzo;

import com.liulishuo.engzo.utils.TestUtil;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import android.support.test.InstrumentationRegistry;
import android.support.test.filters.LargeTest;
import android.support.test.filters.SdkSuppress;
import android.support.test.runner.AndroidJUnit4;
import android.support.test.uiautomator.UiDevice;

import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertThat;

/**
 * @author Kale
 * @date 2016/10/6
 * 
 * https://github.com/googlesamples/android-testing/blob/master/ui/uiautomator/BasicSample/app/src/androidTest/java/com/example/android/testing/uiautomator/BasicSample/ChangeTextBehaviorTest.java
 * 
 * adb shell uiautomator events
 */
@RunWith(AndroidJUnit4.class)
@LargeTest
@SdkSuppress(minSdkVersion = 18)
public class BaseEvnTest {

    private UiDevice uiDevice;

    @Before
    public void startMainActivityFromHomeScreen() {
        // Initialize UiDevice instance
        uiDevice = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation());
        TestUtil.maybeStartTestApp(uiDevice);
    }

    @Test
    public void checkPreconditions() throws Exception {
        assertThat(uiDevice, notNullValue());
    }

}
