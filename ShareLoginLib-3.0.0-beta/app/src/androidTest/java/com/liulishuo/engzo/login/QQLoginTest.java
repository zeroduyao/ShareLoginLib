package com.liulishuo.engzo.login;

import android.support.test.uiautomator.By;
import android.support.test.uiautomator.UiObjectNotFoundException;
import android.widget.Button;

import com.liulishuo.engzo.utils.With;

import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

/**
 * @author Kale
 * @date 2016/10/5
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING) // 按照方法的先后进行执行，而非通过hash值
public class QQLoginTest extends AbsLoginTestCase {

    @Override
    protected void clickButton() throws UiObjectNotFoundException {
        device.findObject(With.text("QQ登录")).clickAndWaitForNewWindow();
    }

    @Test
    @Override
    public void loginCanceled_by_pressBackButton() {
        device.pressBack();
        assertLoginIsCanceled();
    }

    @Test
    @Override
    public void loginCanceled_by_clickCancelButton() {
        // 点击返回按钮，下面是左上角返回按钮的id值
        device.findObject(By.res("com.tencent.mobileqq:id/ivTitleBtnLeft")).click();
        assertLoginIsCanceled();
    }

    @Test
    @Override
    public void loginSuccess_by_click() {
        device.findObject(By.text("授权并登录").clazz(Button.class)).click(1000);
        assertLoginIsSucceed();
    }

}
