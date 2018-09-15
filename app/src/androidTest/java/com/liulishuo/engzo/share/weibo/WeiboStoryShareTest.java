package com.liulishuo.engzo.share.weibo;

import android.support.test.uiautomator.UiObjectNotFoundException;

import com.liulishuo.engzo.share.AbsShareTestCase;
import com.liulishuo.engzo.utils.With;

import org.junit.Assert;
import org.junit.FixMethodOrder;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runners.MethodSorters;

/**
 * @author Kale
 * @date 2018/9/15
 */
@Ignore
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class WeiboStoryShareTest extends AbsShareTestCase {

    @Override
    protected void clickButton() throws UiObjectNotFoundException {
        device.findObject(With.text("仅图片")).click();
        device.findObject(With.text("分享到微博故事")).clickAndWaitForNewWindow();
    }

    @Test
    @Override
    public void shareCanceled_by_clickCancelButton() {
        Assert.assertNull(null);
    }

    @Test
    @Override
    public void shareCanceled_by_pressBackButton() {

    }

    @Test
    @Override
    public void shareSuccess_by_clickSendButton() throws UiObjectNotFoundException {

    }
    
}
