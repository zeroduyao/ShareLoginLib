package com.liulishuo.engzo;

import com.liulishuo.share.base.AuthUserInfo;
import com.liulishuo.share.base.login.LoginListener;
import com.liulishuo.share.base.login.UserInfoListener;
import com.liulishuo.share.qq.QQLoginManager;
import com.liulishuo.share.weibo.WeiboLoginManager;
import com.liulishuo.share.weixin.WeiXinLoginManager;

import android.app.Activity;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

/**
 * @author Jack Tony
 * @date 2015/10/18
 */
public class MyLoginListener implements LoginListener {

    public static final String TAG = "LoginListener";

    public static final int QQ = 0;

    public static final int WEIBO = 1;

    public static final int WEIXIN = 2;

    private Activity mActivity;

    private int mType;

    private TextView mTextView;

    private UserInfoListener mUserInfoListener = new UserInfoListener() {
        @Override
        public void onSuccess(AuthUserInfo userInfo) {
            String str = " nickname = " + userInfo.nickName
                    + "\n sex = " + userInfo.sex
                    + "\n imageUrl = " + userInfo.headImgUrl
                    + "\n id = " + userInfo.userId;
            mTextView.setText(str);
        }

        @Override
        public void onError(String msg) {
            String type;
            switch (mType) {
                case QQ:
                    type = "qq";
                    break;
                case WEIBO:
                    type = "weibo";
                    break;
                case WEIXIN:
                    type = "weixin";
                    break;
                
                default:
                    type = "qq"; 
            }
            String text = type + " 出错了！\n" + msg;
            mTextView.setText(text);
        }
    };

    public void setType(int type) {
        mType = type;
    }

    public MyLoginListener(Activity activity, int type, TextView textView) {
        mActivity = activity;
        mType = type;
        mTextView = textView;
    }

    @Override
    public void onSuccess(String accessToken, String userId, long expiresIn, String data) {
        Log.d(TAG, "accessToken = " + accessToken);
        Log.d(TAG, "uid = " + userId);
        Log.d(TAG, "expires_in = " + expiresIn);
        Log.d(TAG, "登录成功");
        Toast.makeText(mActivity, "登录成功", Toast.LENGTH_SHORT).show();

        switch (mType) {
            case QQ:
                QQLoginManager.getUserInfo(accessToken, userId, mUserInfoListener);
                break;
            case WEIXIN:
                WeiXinLoginManager.getUserInfo(accessToken, userId, mUserInfoListener);
                break;
            case WEIBO:
                WeiboLoginManager.getUserInfo(accessToken, userId, mUserInfoListener);
                break;
            default:
        }
    }

    @Override
    public void onError(String msg) {
        Toast.makeText(mActivity, "登录失败,失败信息：" + msg, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onCancel() {
        Log.d(TAG, "取消登录");
        Toast.makeText(mActivity, "取消登录", Toast.LENGTH_SHORT).show();
    }
}
