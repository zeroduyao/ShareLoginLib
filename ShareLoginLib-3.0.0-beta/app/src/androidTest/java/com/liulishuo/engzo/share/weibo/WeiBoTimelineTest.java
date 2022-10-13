package com.liulishuo.engzo.share.weibo;

import android.support.test.uiautomator.By;
import android.support.test.uiautomator.UiObjectNotFoundException;
import android.support.test.uiautomator.Until;

import com.liulishuo.engzo.share.AbsShareTestCase;
import com.liulishuo.engzo.utils.With;

import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import static com.liulishuo.engzo.Constant.MAX_TIMEOUT;

/**
 * @author Kale
 * @date 2016/10/6
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class WeiBoTimelineTest extends AbsShareTestCase {

    @Override
    protected void clickButton() throws UiObjectNotFoundException {
        device.findObject(With.text("分享到微博")).clickAndWaitForNewWindow();
    }

    @Test
    @Override
    public void shareCanceled_by_clickCancelButton() {
        device.findObject(By.res("com.sina.weibo:id/titleBack")).click();
        assertShareIsCanceled();
    }

    /**
     * 直接退出，不保存到草稿箱
     */
    @Test
    public void shareCanceled_withoutSave() throws UiObjectNotFoundException {
        device.findObject(By.res("com.sina.weibo:id/edit_view")).setText("test by kale @天之界线2010");
        device.findObject(With.res("com.sina.weibo:id/titleBack")).clickAndWaitForNewWindow();

        device.findObject(By.text("不保存")).click();
        assertShareIsCanceled();
    }

    @Test
    public void shareCanceled_and_saveContent() throws Exception {
        device.findObject(By.res("com.sina.weibo:id/edit_view")).setText("test by kale#test#");
        device.findObject(With.res("com.sina.weibo:id/titleBack")).clickAndWaitForNewWindow();

        device.wait(Until.findObject(By.text("保存")), MAX_TIMEOUT).click();

        assertShareIsCanceled();
    }

    @Test
    @Override
    public void shareCanceled_by_pressBackButton() {
        device.pressBack(); // 关闭输入法
        device.pressBack();
        assertShareIsCanceled();
    }

    @Test
    @Override
    public void shareSuccess_by_clickSendButton() throws UiObjectNotFoundException {
        device.findObject(With.text("公开")).clickAndWaitForNewWindow();
        device.findObject(With.text("仅自己可见")).clickAndWaitForNewWindow();

        device.findObject(By.res("com.sina.weibo:id/edit_view")).setText("#test#test by kale ");
        device.findObject(By.text("发送")).click();

        assertShareIsSucceed();
    }

}
