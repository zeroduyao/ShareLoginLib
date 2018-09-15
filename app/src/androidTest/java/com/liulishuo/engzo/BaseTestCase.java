package com.liulishuo.engzo;

import android.support.test.InstrumentationRegistry;
import android.support.test.filters.LargeTest;
import android.support.test.filters.SdkSuppress;
import android.support.test.runner.AndroidJUnit4;
import android.support.test.uiautomator.UiDevice;
import android.support.test.uiautomator.UiObjectNotFoundException;

import com.liulishuo.engzo.utils.TestUtils;

import org.junit.After;
import org.junit.Before;
import org.junit.runner.RunWith;

/**
 * @author Kale
 * @date 2018/9/15
 */
@RunWith(AndroidJUnit4.class)
@LargeTest
@SdkSuppress(minSdkVersion = 18)
public abstract class BaseTestCase {

    protected UiDevice device;

    @Before
    public void setup() throws UiObjectNotFoundException {
        device = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation());
        TestUtils.startTestApp(device);
        clickButton();
        device.waitForWindowUpdate(Constant.APP_PACKAGE_NAME, 500);
    }

    public void setDevice(UiDevice device) {
        this.device = device;
    }

    @After
    public void end() {
        device = null;
    }

    protected abstract void clickButton() throws UiObjectNotFoundException;
    
}