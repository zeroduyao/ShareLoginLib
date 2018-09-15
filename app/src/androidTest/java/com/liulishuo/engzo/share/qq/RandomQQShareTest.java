package com.liulishuo.engzo.share.qq;

import android.support.test.uiautomator.UiObjectNotFoundException;

import com.liulishuo.engzo.BaseTestCase;
import com.liulishuo.engzo.utils.TestUtils;
import com.liulishuo.engzo.utils.With;

import org.junit.Ignore;
import org.junit.Test;

/**
 * @author Kale
 * @date 2018/9/15
 */
public class RandomQQShareTest extends BaseTestCase {

    private static final String TAG = "RandomQQShareTest";

    private QQFriendShareTest friendTest = new QQFriendShareTest();

    private QZoneShareTest zoneShareTest = new QZoneShareTest();

    @Override
    protected void clickButton() {
        // ignore
    }

    @Ignore
    @Test
    public void testShareToFriend() {
        friendTest.setDevice(device);
        TestUtils.randomExecuteFunction(device, 4, this::executeFriendCase);
    }

    @Ignore
    @Test
    public void testShareToQZone() {
        zoneShareTest.setDevice(device);
        TestUtils.randomExecuteFunction(device, 2, this::executeQZoneCase);
    }

    private Void executeFriendCase(Integer index) {
        try {
            device.findObject(With.text("分享给QQ好友")).clickAndWaitForNewWindow();
            switch (index) {
                case 0:
                    friendTest.shareCanceled_by_clickCancelButton();
                    break;
                case 1:
                    friendTest.shareCanceled_by_clickCancelButton_inDialog();
                    break;
                case 2:
                    friendTest.shareCanceled_by_pressBackButton();
                    break;
                case 3:
                    friendTest.shareSuccess_by_clickSendButton();
                    break;
                case 4:
                    friendTest.shareSuccess_and_stayInQQ();
                    break;
                default:
                    throw new IllegalAccessError("error");
            }
        } catch (UiObjectNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    private Void executeQZoneCase(Integer index) {
        try {
            device.findObject(With.text("分享到QQ空间")).clickAndWaitForNewWindow();
            switch (index) {
                case 0:
                    zoneShareTest.shareCanceled_by_clickCancelButton();
                    break;
                case 1:
                    zoneShareTest.shareCanceled_by_pressBackButton();
                    break;
                case 2:
                    zoneShareTest.shareSuccess_by_clickSendButton();
                    break;
                default:
                    throw new IllegalAccessError("error");
            }
        } catch (UiObjectNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }
    
}
