package com.liulishuo.engzo.share;

import android.support.test.uiautomator.By;
import android.support.test.uiautomator.SearchCondition;
import android.support.test.uiautomator.UiObject2;
import android.support.test.uiautomator.UiObjectNotFoundException;

import com.liulishuo.engzo.BaseTestCase;

import static android.support.test.uiautomator.Until.findObject;
import static com.liulishuo.engzo.Constant.APP_PACKAGE_NAME;
import static com.liulishuo.engzo.Constant.MAX_TIMEOUT;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

/**
 * @author Kale
 * @date 2018/9/15
 */
public abstract class AbsShareTestCase extends BaseTestCase {

    public abstract void shareCanceled_by_clickCancelButton();

    public abstract void shareCanceled_by_pressBackButton();

    public abstract void shareSuccess_by_clickSendButton() throws UiObjectNotFoundException;

    public void assertShareIsSucceed() {
        SearchCondition<UiObject2> infoTv = findObject(By.res(APP_PACKAGE_NAME, "result"));
        assertThat(device.wait(infoTv, MAX_TIMEOUT).getText(), is(equalTo("分享成功")));
    }

    public void assertShareIsCanceled() {
        SearchCondition<UiObject2> infoTv = findObject(By.res(APP_PACKAGE_NAME, "result"));
        assertThat(device.wait(infoTv, MAX_TIMEOUT).getText(), is(equalTo("取消分享")));
    }
}
