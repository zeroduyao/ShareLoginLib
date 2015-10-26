package com.liulishuo.share.base.share;

import com.liulishuo.share.ShareBlock;
import com.liulishuo.share.base.shareContent.ShareContent;

/**
 * Created by echo on 5/21/15.
 */
public interface IShareManager {

    void share(ShareContent shareContent, @ShareBlock.ShareType int shareType, ShareStateListener listener);

}
