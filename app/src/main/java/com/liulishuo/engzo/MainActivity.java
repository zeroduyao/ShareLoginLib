package com.liulishuo.engzo;

import com.liulishuo.share.ShareBlock;
import com.liulishuo.share.base.login.ILoginManager;
import com.liulishuo.share.base.login.LoginListener;
import com.liulishuo.share.base.share.IShareManager;
import com.liulishuo.share.base.share.ShareContentWebpage;
import com.liulishuo.share.base.share.ShareStateListener;
import com.liulishuo.share.qq.QQLoginManager;
import com.liulishuo.share.qq.QQShareManager;
import com.liulishuo.share.wechat.WeiXinLoginManager;
import com.liulishuo.share.wechat.WeiXinShareManager;
import com.liulishuo.share.weibo.WeiboLoginManager;
import com.liulishuo.share.weibo.WeiboShareManager;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

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

    private ILoginManager mCurrentLoginManager;

    private IShareManager mCurrentShareManager;

    private LoginListener mLoginListener = new MyLoginListener(this);

    private Bitmap mBitmap;

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
                .initWechat(OAuthConstant.WECHAT_APPID, OAuthConstant.WECHAT_SECRET)
                .initWeibo(OAuthConstant.WEIBO_APPID, OAuthConstant.WEIBO_REDIRECT_URL, OAuthConstant.WEIBO_SCOPE);

        // 微信分享到回话
        findViewById(R.id.share_wechat_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mCurrentShareManager = new WeiXinShareManager(MainActivity.this);
                /*mCurrentShareManager.share(
                        new ShareContentPic(mBitmap),
                        WechatShareManager.WEIXIN_FRIEND
                        , mShareListener);*/
                mCurrentShareManager.share(
                        new ShareContentWebpage("title", "hello world!", "http://www.baidu.com", mBitmap)
                        , ShareBlock.WEIXIN_FRIEND
                        , mShareListener);
            }
        });

        // 微信分享到朋友圈
        findViewById(R.id.share_friends_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mCurrentShareManager = new WeiXinShareManager(MainActivity.this);
                mCurrentShareManager.share(
                        new ShareContentWebpage("title", "hello world!", "http://www.baidu.com", mBitmap)
                        , ShareBlock.WEIXIN_FRIEND_ZONE
                        , mShareListener);
            }
        });

        // 微信登录
        findViewById(R.id.login_wechat_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mCurrentLoginManager = new WeiXinLoginManager(MainActivity.this);
                mCurrentLoginManager.login(mLoginListener);
            }
        });

        ///////////////////////////// Weibo ///////////////////////////////

        findViewById(R.id.login_weibo_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mCurrentLoginManager = new WeiboLoginManager(MainActivity.this);
                mCurrentLoginManager.login(mLoginListener);
            }
        });

        findViewById(R.id.share_weibo_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mCurrentShareManager = new WeiboShareManager(MainActivity.this);
                //mCurrentShareManager.share(new ShareContentText("test"), WeiboShareManager.WEIBO_TIME_LINE, mShareListener);
                mCurrentShareManager.share(
                        new ShareContentWebpage("from weibo", "share content web page", "http://www.baidu.com", mBitmap)
                        , ShareBlock.WEIBO_TIME_LINE, mShareListener);

                //mCurrentShareManager.share(new ShareContentPic(picFile), WeiboShareManager.WEIBO_TIME_LINE, mShareListener);
            }
        });

        ///////////////////////////// QQ ///////////////////////////////

        // QQ登录
        findViewById(R.id.login_qq_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mCurrentLoginManager = new QQLoginManager(MainActivity.this);
                mCurrentLoginManager.login(mLoginListener);
            }
        });

        findViewById(R.id.share_qq_friend_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mCurrentShareManager = new QQShareManager(MainActivity.this);
                /*mCurrentShareManager.share(
                        new ShareContentWebpage("title", "test", "http://www.baidu.com",
                                "https://ss0.bdstatic.com/5aV1bjqh_Q23odCf/static/superplus/img/logo_white_ee663702.png")
                        , QQShareManager.QQ_FRIEND, mShareListener);*/
                
                mCurrentShareManager.share(
                        new ShareContentWebpage("share to qq friend", "hello world!", "http://www.baidu.com",
                                mBitmap)
                        , ShareBlock.QQ_FRIEND, mShareListener);
            }
        });

        findViewById(R.id.share_qZone_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCurrentShareManager = new QQShareManager(MainActivity.this);
//                ShareContentWebpage content = new ShareContentWebpage("title", "test", "http://www.baidu.com", getImagePath(rooView));
                ShareContentWebpage content = new ShareContentWebpage("title", "test", "http://www.baidu.com", mBitmap);
                mCurrentShareManager.share(content, ShareBlock.QQ_ZONE, mShareListener);
            }
        });

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        ShareBlock.handlerOnActivityResult(mCurrentLoginManager, mCurrentShareManager, requestCode, resultCode, data);
    }

    private ShareStateListener mShareListener = new ShareStateListener() {
        @Override
        public void onSuccess() {
            Log.d(TAG, "分享成功");
            Toast.makeText(getBaseContext(), "分享成功", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onError(String msg) {
            Log.d(TAG, "分享失败，出错信息：" + msg);
            Toast.makeText(getBaseContext(), "分享失败，出错信息：" + msg, Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onCancel() {
            Log.d(TAG, "取消分享");
            Toast.makeText(getBaseContext(), "取消分享", Toast.LENGTH_SHORT).show();
        }
    };


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
