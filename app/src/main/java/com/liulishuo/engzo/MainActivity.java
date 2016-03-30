package com.liulishuo.engzo;

import com.liulishuo.share.LoginManager;
import com.liulishuo.share.ShareBlock;
import com.liulishuo.share.ShareManager;
import com.liulishuo.share.UserInfoListener;
import com.liulishuo.share.model.AuthUserInfo;
import com.liulishuo.share.model.shareContent.ShareContent;
import com.liulishuo.share.model.shareContent.ShareContentPic;
import com.liulishuo.share.model.shareContent.ShareContentText;
import com.liulishuo.share.model.shareContent.ShareContentWebpage;
import com.liulishuo.share.type.LoginType;
import com.liulishuo.share.type.ShareType;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * 步骤：
 * 1.添加混淆参数
 * 2.在包中放入微信必须的activity
 * 3.配置manifest中的activity
 */
public class MainActivity extends AppCompatActivity {

    public static final String URL = "https://www.zhihu.com/question/22913650";

    public static final String TITLE = "标题";

    public static final String MSG = "描述信息";

    private MyLoginListener mLoginListener;

    private ShareManager.ShareStateListener mShareListener = new MyShareListener(this);

    private ShareContent mShareContent;

    private TextView mTextView;

    private ImageView mImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final Bitmap mBitmap = ((BitmapDrawable) getResources().getDrawable(R.drawable.kale)).getBitmap();

        LoginManager.isWeiBoInstalled(this);
        ShareManager.isWeiBoInstalled(this);

        LoginManager.isWeiXinInstalled(this);
        ShareManager.isWeiXinInstalled(this);

        ShareBlock.getInstance()
                .initAppName("TestAppName")
                .initSharePicFile(getApplication())
                .initQQ(OAuthConstant.QQ_APPID, OAuthConstant.QQ_SCOPE)
                .initWeiXin(OAuthConstant.WEIXIN_APPID, OAuthConstant.WEIXIN_SECRET)
                .initWeiBo(OAuthConstant.WEIBO_APPID, OAuthConstant.WEIBO_REDIRECT_URL, OAuthConstant.WEIBO_SCOPE);

        RadioGroup shareType = (RadioGroup) findViewById(R.id.share_type_rg);
        shareType.check(R.id.rich_text);

        mShareContent = new ShareContentWebpage(TITLE, MSG, URL, mBitmap);

        shareType.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.rich_text:
                        mShareContent = new ShareContentWebpage(TITLE, MSG, URL, mBitmap);
                        break;
                    case R.id.only_image:
                        mShareContent = new ShareContentPic(mBitmap);
                        break;
                    case R.id.only_text:
                        mShareContent = new ShareContentText("share text");
                        break;
                }
            }
        });
        mTextView = (TextView) findViewById(R.id.userinfo_tv);
        mImageView = (ImageView) findViewById(R.id.user_img_iv);

        mLoginListener = new MyLoginListener();
    }

    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.QQ登录:
                mLoginListener.setType(LoginType.QQ);
                LoginManager.login(this, mLoginListener, LoginType.QQ);
                break;
            case R.id.微信登录:
                mLoginListener.setType(LoginType.WEIXIN);
                LoginManager.login(this, mLoginListener, LoginType.WEIXIN);
                break;
            case R.id.微博登录:
                mLoginListener.setType(LoginType.WEIBO);
                LoginManager.login(this, mLoginListener, LoginType.WEIBO);
                break;

            case R.id.分享给QQ好友:
                ShareManager.share(this, mShareContent, ShareType.QQ_FRIEND, mShareListener);
                break;
            case R.id.分享到QQ空间:
                ShareManager.share(this, mShareContent, ShareType.QQ_ZONE, mShareListener);
                break;
            case R.id.分享给微信好友:
                ShareManager.share(this, mShareContent, ShareType.WEIXIN_FRIEND, mShareListener);
                break;
            case R.id.分享到微信朋友圈:
                ShareManager.share(this, mShareContent, ShareType.WEIXIN_FRIEND_ZONE, mShareListener);
                break;
            case R.id.分享到微博:
                ShareManager.share(this, mShareContent, ShareType.WEIBO_TIME_LINE, mShareListener);
                break;
        }
    }

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            mImageView.setImageBitmap((Bitmap) msg.obj);
        }
    };

    public class MyLoginListener implements LoginManager.LoginListener {

        public static final String TAG = "LoginListener";

        private LoginType mType;

        private UserInfoListener mUserInfoListener = new UserInfoListener() {
            @Override
            public void onSuccess(final AuthUserInfo userInfo) {
                String str = " nickname = " + userInfo.nickName
                        + "\n sex = " + userInfo.sex
                        + "\n id = " + userInfo.userId;
                mTextView.setText(str);

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            HttpURLConnection conn = (HttpURLConnection) new URL(userInfo.headImgUrl).openConnection();
                            Bitmap bmp = BitmapFactory.decodeStream(conn.getInputStream());
                            Message msg = new Message();
                            msg.obj = bmp;
                            mHandler.sendMessage(msg);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }).start();
            }

            @Override
            public void onError(String msg) {
                mTextView.setText(" 出错了！\n" + msg);
            }
        };

        public void setType(LoginType type) {
            mType = type;
        }

        @Override
        public void onSuccess(String accessToken, String userId, long expiresIn, String data) {
            Log.d(TAG, "accessToken = " + accessToken);
            Log.d(TAG, "uid = " + userId);
            Log.d(TAG, "expires_in = " + expiresIn);
            Log.d(TAG, "登录成功");
            Toast.makeText(MainActivity.this, "登录成功", Toast.LENGTH_SHORT).show();

            LoginManager.getUserInfo(accessToken, userId, mUserInfoListener, mType);
        }

        @Override
        public void onError(String msg) {
            Toast.makeText(getApplicationContext(), "登录失败,失败信息：" + msg, Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onCancel() {
            Log.d(TAG, "取消登录");
            Toast.makeText(getApplicationContext(), "取消登录", Toast.LENGTH_SHORT).show();
        }
    }

}
