package com.liulishuo.engzo.login;

import android.support.test.uiautomator.By;
import android.support.test.uiautomator.UiObject2;
import android.support.test.uiautomator.UiObjectNotFoundException;

import com.liulishuo.engzo.Constant;
import com.liulishuo.engzo.utils.With;

import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import static android.support.test.uiautomator.Until.findObject;

/**
 * @author Kale
 * @date 2016/10/5
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING) // 按照方法的先后进行执行，而非通过hash值
public class WeiBoLoginTest extends AbsLoginTestCase {

    @Override
    protected void clickButton() throws UiObjectNotFoundException {
        device.findObject(With.text("微博登录")).clickAndWaitForNewWindow();
    }

    /**
     * 新版微博几乎无法取消了，所以直接是登录成功
     */
    @Test
    @Override
    public void loginCanceled_by_clickCancelButton() {
        UiObject2 object = device.findObject(By.text("取消"));
        if (object == null) {
            assertLoginIsSucceed();
        } else {
            assertLoginIsCanceled();
        }
    }

    /**
     * 新版微博几乎无法取消了，所以直接是登录成功
     */
    @Test
    @Override
    public void loginCanceled_by_pressBackButton() {
        device.wait(findObject(By.text("微博登录")), Constant.MAX_TIMEOUT);
        assertLoginIsSucceed();
    }

    /**
     * 新版微博几乎无法取消了，所以直接是登录成功
     */
    @Test
    @Override
    public void loginSuccess_by_click() {
        UiObject2 login = device.findObject(By.res("com.sina.weibo", "bnLogin"));
        if (login != null) {
            login.click();
        }
        assertLoginIsSucceed();
    }

}
