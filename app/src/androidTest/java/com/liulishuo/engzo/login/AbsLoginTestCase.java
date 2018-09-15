package com.liulishuo.engzo.login;

import android.support.test.uiautomator.By;
import android.support.test.uiautomator.SearchCondition;
import android.support.test.uiautomator.UiObject2;

import com.liulishuo.engzo.BaseTestCase;

import static android.support.test.uiautomator.Until.findObject;
import static com.liulishuo.engzo.Constant.APP_PACKAGE_NAME;
import static com.liulishuo.engzo.Constant.MAX_TIMEOUT;
import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

/**
 * @author Kale
 * @date 2018/9/15
 */
public abstract class AbsLoginTestCase extends BaseTestCase {

    public abstract void loginCanceled_by_clickCancelButton();

    public abstract void loginCanceled_by_pressBackButton();

    public abstract void loginSuccess_by_click();

    void assertLoginIsSucceed() {
        SearchCondition<UiObject2> infoTv = findObject(By.res(APP_PACKAGE_NAME, "user_info_tv"));
        assertThat(device.wait(infoTv, MAX_TIMEOUT).getText(), is(containsString("nickname")));
        assertThat(device.findObject(By.res(APP_PACKAGE_NAME, "result")).getText(), is(equalTo("登录成功")));
    }

    void assertLoginIsCanceled() {
        SearchCondition<UiObject2> infoTv = findObject(By.res(APP_PACKAGE_NAME, "result"));
        assertThat(device.wait(infoTv, MAX_TIMEOUT).getText(), is(equalTo("取消登录")));
    }

}
