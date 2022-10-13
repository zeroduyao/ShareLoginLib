package com.liulishuo.engzo.share.weixin;

import android.support.test.uiautomator.By;
import android.support.test.uiautomator.UiObjectNotFoundException;
import android.widget.ImageView;

import com.liulishuo.engzo.share.AbsShareTestCase;
import com.liulishuo.engzo.utils.With;

import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import static android.support.test.uiautomator.Until.findObject;
import static com.liulishuo.engzo.Constant.MAX_TIMEOUT;

/**
 * @author Kale
 * @date 2016/10/7
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class WxTimelineShareTest extends AbsShareTestCase {

    @Override
    protected void clickButton() throws UiObjectNotFoundException {
        device.findObject(With.text("分享到微信朋友圈")).clickAndWaitForNewWindow();
    }

    /**
     * 新版本的微信，取消也会变成成功的回调
     */
    @Test
    @Override
    public void shareCanceled_by_clickCancelButton() {
        device.findObject(By.clazz(ImageView.class).desc("返回")).click();
        device.wait(findObject(By.text("退出")), MAX_TIMEOUT).click();
        assertShareIsSucceed();
    }

    @Test
    @Override
    public void shareCanceled_by_pressBackButton() {
        device.pressBack();
        device.wait(findObject(By.text("退出")), MAX_TIMEOUT).click();
        assertShareIsSucceed();
    }

    @Test
    @Override
    public void shareSuccess_by_clickSendButton() throws UiObjectNotFoundException {
        device.findObject(By.clazz("android.widget.EditText").textContains("这一刻的想法"))
                .setText("test by kale");

        device.findObject(With.text("谁可以看")).clickAndWaitForNewWindow();
        device.findObject(With.text("私密")).click();
        device.findObject(With.text("完成")).clickAndWaitForNewWindow();

        device.findObject(By.text("发表")).click();
        assertShareIsSucceed();
    }

}