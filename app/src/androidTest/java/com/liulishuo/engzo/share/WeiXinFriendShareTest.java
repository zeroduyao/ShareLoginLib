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
import android.support.test.uiautomator.UiSelector;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

/**
 * @author Kale
 * @date 2016/10/5
 *
 * Note:如果用户选择留在微信，那么应用则无法接收到回调，这点需要开发者知情
 */
@RunWith(AndroidJUnit4.class)
@LargeTest
@SdkSuppress(minSdkVersion = 18)
public class WeiXinFriendShareTest {

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
        device.findObject(With.text("分享到微信")).clickAndWaitForNewWindow();
    }

    @Test
    public void testShareSuccess() throws Exception {
        // find item by index
        UiSelector firstFriendItem = With.clazz(ListView.class).childSelector(With.index(2));
        device.findObject(firstFriendItem).click();
        device.findObject(With.text("取消")).click();

        device.findObject(firstFriendItem).click();
        device.findObject(With.text("分享")).click();

        device.findObject(By.clazz(Button.class).textContains("返回")).click();
        TestUtil.assertShareSucceed(device);
    }

    /**
     * 如果用户选择留在微信，那么应用则无法接收到回调
     */
    @Test
    public void testShareSuccessStayInWeiXin() throws Exception {
        device.findObject(With.text("创建新聊天")).clickAndWaitForNewWindow();
        device.findObject(With.clazz(ListView.class).childSelector(With.index(2))).click();
        device.findObject(With.clazz(TextView.class).textStartsWith("确定").clickable(true))
                .clickAndWaitForNewWindow();

        device.findObject(By.clazz(EditText.class)).setText("test by kale");
        device.findObject(By.text("分享")).click();

        device.findObject(With.clazz(Button.class).text("留在微信")).clickAndWaitForNewWindow();
        device.pressBack();
    }

    @Test
    public void testShareCancelByClient() {
        device.findObject(By.clazz(ImageView.class).desc("返回")).click();
        TestUtil.assertShareCanceled(device);
    }

    @Test
    public void testShareCancelByPressBack() {
        device.pressBack();
        TestUtil.assertShareCanceled(device);
    }

}
