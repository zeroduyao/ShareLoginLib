package com.liulishuo.engzo.login;

import android.support.test.uiautomator.By;
import android.support.test.uiautomator.UiObject2;
import android.support.test.uiautomator.UiObjectNotFoundException;
import android.widget.ImageView;

import com.liulishuo.engzo.utils.With;

import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

/**
 * @author Kale
 * @date 2016/10/5
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING) // 按照方法的先后进行执行，而非通过hash值
public class WeiXinLoginTest extends AbsLoginTestCase {

    @Override
    protected void clickButton() throws UiObjectNotFoundException {
        device.findObject(With.text("微信登录")).clickAndWaitForNewWindow();
    }

    /**
     * 微信无法取消了，直接成功
     */
    @Test
    @Override
    public void loginCanceled_by_clickCancelButton() {
        UiObject2 object = device.findObject(By.clazz(ImageView.class).desc("返回"));
        if (object == null) {
            assertLoginIsSucceed();
        }
    }

    @Test
    @Override
    public void loginCanceled_by_pressBackButton() {
        assertLoginIsSucceed();
    }

    @Test
    @Override
    public void loginSuccess_by_click() {
        device.waitForIdle();
        assertLoginIsSucceed();
    }

}
