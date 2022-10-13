package com.liulishuo.engzo.share.qq;

import android.support.test.uiautomator.By;
import android.support.test.uiautomator.UiObjectNotFoundException;

import com.liulishuo.engzo.share.AbsShareTestCase;
import com.liulishuo.engzo.utils.With;

import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

/**
 * @author Kale
 * @date 2016/10/7
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class QZoneShareTest extends AbsShareTestCase {

    @Override
    protected void clickButton() throws UiObjectNotFoundException {
        device.findObject(With.text("分享到QQ空间")).clickAndWaitForNewWindow();
    }

    @Test
    @Override
    public void shareCanceled_by_clickCancelButton() {
        device.findObject(By.text("取消")).click();
        assertShareIsCanceled();
    }

    @Test
    @Override
    public void shareCanceled_by_pressBackButton() {
        device.pressBack(); // 关闭键盘
        device.pressBack(); // 返回应用
        assertShareIsCanceled();
    }

    @Test
    @Override
    public void shareSuccess_by_clickSendButton() throws UiObjectNotFoundException {
        device.findObject(With.text("发表")).click();
        assertShareIsSucceed();
    }

}
