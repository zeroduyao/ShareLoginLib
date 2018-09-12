package com.liulishuo.engzo.utils;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.support.test.InstrumentationRegistry;
import android.support.test.uiautomator.By;
import android.support.test.uiautomator.BySelector;
import android.support.test.uiautomator.SearchCondition;
import android.support.test.uiautomator.UiDevice;
import android.support.test.uiautomator.UiObject2;
import android.support.test.uiautomator.Until;

import com.liulishuo.demo.R;

import static android.support.test.uiautomator.Until.findObject;
import static com.liulishuo.engzo.Constant.APPLICATION_PACKAGE;
import static com.liulishuo.engzo.Constant.MAX_TIMEOUT;
import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;

/**
 * @author Kale
 * @date 2016/10/6
 */

public class TestUtil {

    private static final int LAUNCH_TIMEOUT = 2000;

    public static void maybeStartTestApp(UiDevice uiDevice) {
        if (uiDevice.getCurrentPackageName().equals(APPLICATION_PACKAGE)) {
            return;
        }
        startTestApp(uiDevice);
    }

    private static void startTestApp(UiDevice uiDevice) {
        // Start from the home screen
        uiDevice.pressHome();

        // Wait for launcher
        final String launcherPackage = getLauncherPackageName();
        uiDevice.wait(Until.hasObject(By.pkg(launcherPackage).depth(0)), LAUNCH_TIMEOUT);

        // Launch the test app
        Context context = InstrumentationRegistry.getContext();//获取上下文
        final Intent intent = context.getPackageManager().getLaunchIntentForPackage(APPLICATION_PACKAGE);
        assertNotNull(intent);

        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        context.startActivity(intent);

        // Wait for the app to appear
        //找到满足条件的包，取第一个
        BySelector depth = By.pkg(APPLICATION_PACKAGE).depth(0);
        uiDevice.wait(Until.hasObject(depth), LAUNCH_TIMEOUT);
    }

    /**
     * Uses package manager to find the package name of the device launcher. Usually this package
     * is "com.android.launcher" but can be different at times. This is a generic solution which
     * works on all platforms.`
     */
    private static String getLauncherPackageName() {
        // Create launcher Intent
        final Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);

        // Use PackageManager to get the launcher package name
        PackageManager pm = InstrumentationRegistry.getContext().getPackageManager();
        ResolveInfo resolveInfo = pm.resolveActivity(intent, PackageManager.MATCH_DEFAULT_ONLY);
        return resolveInfo.activityInfo.packageName;
    }

    public static void initEachTestEnvironment(UiDevice device) {
        Context context = InstrumentationRegistry.getTargetContext();
        String name = device.getCurrentActivityName();
        if (name == null ||
                (!name.equals(context.getString(R.string.app_name))
                        && !name.equals(context.getString(R.string.app_name2)))) {
            TestUtil.startTestApp(device);
        }
    }

    ///////////////////////////////////////////////////////////////////////////
    // assert
    ///////////////////////////////////////////////////////////////////////////

    public static void assertLoginSucceed(UiDevice uiDevice) {
        SearchCondition<UiObject2> infoTv = findObject(By.res(APPLICATION_PACKAGE, "user_info_tv"));
        assertThat(uiDevice.wait(infoTv, MAX_TIMEOUT).getText(), is(containsString("nickname")));
        assertThat(uiDevice.findObject(By.res(APPLICATION_PACKAGE, "result")).getText(),
                is(equalTo("登录成功")));
    }

    public static void assertLoginCanceled(UiDevice uiDevice) {
        SearchCondition<UiObject2> infoTv = findObject(By.res(APPLICATION_PACKAGE, "result"));
        assertThat(uiDevice.wait(infoTv, MAX_TIMEOUT).getText(), is(equalTo("取消登录")));
    }

    public static void assertShareSucceed(UiDevice uiDevice) {
        SearchCondition<UiObject2> infoTv = findObject(By.res(APPLICATION_PACKAGE, "result"));
        assertThat(uiDevice.wait(infoTv, MAX_TIMEOUT).getText(), is(equalTo("分享成功")));
    }

    public static void assertShareCanceled(UiDevice uiDevice) {
        SearchCondition<UiObject2> infoTv = findObject(By.res(APPLICATION_PACKAGE, "result"));
        assertThat(uiDevice.wait(infoTv, MAX_TIMEOUT).getText(), is(equalTo("取消分享")));
    }

}
