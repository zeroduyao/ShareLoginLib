package com.liulishuo.engzo.utils;

import java.util.Random;
import java.util.function.Function;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.support.test.InstrumentationRegistry;
import android.support.test.uiautomator.By;
import android.support.test.uiautomator.BySelector;
import android.support.test.uiautomator.UiDevice;
import android.support.test.uiautomator.Until;
import android.util.Log;

import com.liulishuo.engzo.Constant;

import static com.liulishuo.engzo.Constant.APP_PACKAGE_NAME;
import static org.junit.Assert.assertNotNull;

/**
 * @author Kale
 * @date 2016/10/6
 * 
 * 辅助测试的类，主要处理基本的登录、分享断言
 */

public class TestUtils {

    private static final int LAUNCH_TIMEOUT = 2000;

    public static void startTestApp(UiDevice uiDevice) {
        if (uiDevice.getCurrentPackageName().equals(APP_PACKAGE_NAME)) {
            return;
        }
        startTargetApp(uiDevice, false);
    }
    
    public static void startTestAppAndWait(UiDevice uiDevice) {
        if (uiDevice.getCurrentPackageName().equals(APP_PACKAGE_NAME)) {
            return;
        }
        startTargetApp(uiDevice, true);
    }

    private static void startTargetApp(UiDevice uiDevice,boolean wait) {
        // 1. Start from the home screen
//        uiDevice.pressHome();

        // 2. Wait for launcher
        final String launcherPackage = getLauncherPackageName();
        uiDevice.wait(Until.hasObject(By.pkg(launcherPackage).depth(0)), LAUNCH_TIMEOUT);

        // 3. Launch the test app
        Context context = InstrumentationRegistry.getContext();//获取上下文
        final Intent intent = context.getPackageManager().getLaunchIntentForPackage(APP_PACKAGE_NAME);
        assertNotNull(intent);

        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        context.startActivity(intent);

        // 4. Wait for the app to appear
        BySelector depth = By.pkg(APP_PACKAGE_NAME).depth(0); //找到满足条件的包，取第一个
        
        if (wait) {
            uiDevice.wait(Until.hasObject(depth), LAUNCH_TIMEOUT);
        }
    }

    /**
     * Uses package manager to find the package name of the device launcher. Usually this package
     * is "com.android.launcher" but can be different at times. This is a generic solution which
     * works on all platforms.
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

    public static void randomExecuteFunction(UiDevice device, int methodCount, Function<Integer, Void> function) {
        for (int i = 0; i < 10; i++) {
            int randomNum = new Random().nextInt(methodCount);

            Log.i("ddd", "==> 随机数为：" + randomNum);
            System.out.println("==> 随机数为：" + randomNum);

            function.apply(randomNum);

            // wait
            device.waitForWindowUpdate(Constant.APP_PACKAGE_NAME, 1500);
        }
    }

}
