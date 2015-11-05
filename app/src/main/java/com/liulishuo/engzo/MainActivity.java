package com.liulishuo.engzo;

import com.liulishuo.share.ShareBlock;
import com.liulishuo.share.base.share.ShareStateListener;
import com.liulishuo.share.base.shareContent.ShareContent;
import com.liulishuo.share.base.shareContent.ShareContentPic;
import com.liulishuo.share.base.shareContent.ShareContentText;
import com.liulishuo.share.base.shareContent.ShareContentWebpage;
import com.liulishuo.share.qq.QQLoginManager;
import com.liulishuo.share.qq.QQShareManager;
import com.liulishuo.share.weibo.WeiboLoginManager;
import com.liulishuo.share.weibo.WeiboShareManager;
import com.liulishuo.share.weixin.WeiXinLoginManager;
import com.liulishuo.share.weixin.WeiXinShareManager;

import android.app.ActivityManager;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.RadioGroup;
import android.widget.TextView;

import java.io.File;
import java.io.FileOutputStream;

/**
 * 步骤：
 * 1.添加混淆参数
 * 2.在包中放入微信必须的activity
 * 3.配置manifest中的activity
 */
public class MainActivity extends AppCompatActivity {

    public static final String TAG = "MainActivity";

    private MyLoginListener mLoginListener;

    private ShareStateListener mShareListener = new MyShareListener(this);

    private Bitmap mBitmap;

    private ShareContent mShareContent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Drawable drawable = getResources().getDrawable(R.drawable.kale);
        mBitmap = ((BitmapDrawable) drawable).getBitmap();

        WeiboLoginManager.isWeiBoInstalled(this);
        WeiboShareManager.isWeiBoInstalled(this);

        WeiXinLoginManager.isWeiXinInstalled(this);
        WeiXinShareManager.isWeiXinInstalled(this);

        ShareBlock.getInstance()
                .initAppName("TestAppName")
                .initSharePicFile(getApplication())
                .initQQ(OAuthConstant.QQ_APPID, OAuthConstant.QQ_SCOPE)
                .initWeiXin(OAuthConstant.WECHAT_APPID, OAuthConstant.WECHAT_SECRET)
                .initWeibo(OAuthConstant.WEIBO_APPID, OAuthConstant.WEIBO_REDIRECT_URL, OAuthConstant.WEIBO_SCOPE);

        RadioGroup shareType = (RadioGroup) findViewById(R.id.share_type_rg);
        shareType.check(R.id.rich_text);

        mShareContent = new ShareContentWebpage("title", "hello world!，进入：http://www.baidu.com", "http://www.baidu.com", mBitmap);

        shareType.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.rich_text:
                        mShareContent = new ShareContentWebpage("title", "hello world!", "http://www.baidu.com", mBitmap);
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

        mLoginListener = new MyLoginListener(this, 0, ((TextView) findViewById(R.id.userinfo_tv)));

        ///////////////////////////// QQ ///////////////////////////////

        // QQ登录
        findViewById(R.id.login_qq_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mLoginListener.setType(MyLoginListener.QQ);
                new QQLoginManager().login(MainActivity.this, mLoginListener);
            }
        });

        findViewById(R.id.share_qq_friend_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new QQShareManager().share(MainActivity.this, mShareContent, ShareBlock.QQ_FRIEND, mShareListener);
            }
        });

        findViewById(R.id.share_qZone_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new QQShareManager().share(MainActivity.this, mShareContent, ShareBlock.QQ_ZONE, mShareListener);
            }
        });

        ///////////////////////////// weixin ///////////////////////////////

        // 微信登录
        findViewById(R.id.login_wechat_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mLoginListener.setType(MyLoginListener.WEIXIN);
                new WeiXinLoginManager().login(MainActivity.this, mLoginListener);
            }
        });

        // 微信分享到回话
        findViewById(R.id.share_wechat_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new WeiXinShareManager().share(MainActivity.this, mShareContent, ShareBlock.WEIXIN_FRIEND, mShareListener);
            }
        });

        // 微信分享到朋友圈
        findViewById(R.id.share_friends_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new WeiXinShareManager().share(MainActivity.this, mShareContent, ShareBlock.WEIXIN_FRIEND_ZONE, mShareListener);
            }
        });

        ///////////////////////////// Weibo ///////////////////////////////

        findViewById(R.id.login_weibo_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mLoginListener.setType(MyLoginListener.WEIBO);
                new WeiboLoginManager().login(MainActivity.this, mLoginListener);
            }
        });

        findViewById(R.id.share_weibo_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new WeiboShareManager().share(MainActivity.this, mShareContent, ShareBlock.WEIBO_TIME_LINE, mShareListener);
            }
        });

    }


    /**
     * @return 手机当前的activity
     */
    String getRunningActivityName() {
        ActivityManager activityManager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        return activityManager.getRunningTasks(1).get(0).topActivity.getClassName();
    }


    /**
     * 截取对象是普通view，得到这个view在本地存放的地址
     */
    private String getImagePath(View view) {
        String imagePath = getPathTemp() + File.separator + System.currentTimeMillis() + ".png";
        try {
            view.setDrawingCacheEnabled(true);
            Bitmap bitmap = view.getDrawingCache();
            if (bitmap != null) {
                FileOutputStream out = new FileOutputStream(imagePath);
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
                out.close();
            }
        } catch (OutOfMemoryError ignored) {
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return imagePath;
    }

    private String mPathTemp = "";

    /**
     * 临时文件地址 *
     */
    public String getPathTemp() {
        if (TextUtils.isEmpty(mPathTemp)) {
            mPathTemp = MainActivity.this.getExternalCacheDir() + File.separator + "temp";
            File dir = new File(mPathTemp);
            if (!dir.exists()) {
                dir.mkdirs();
            }
        }
        return mPathTemp;
    }

}

