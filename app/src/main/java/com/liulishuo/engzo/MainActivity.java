package com.liulishuo.engzo;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.liulishuo.demo.R;
import com.squareup.picasso.Picasso;

import kale.sharelogin.ShareLoginLib;
import kale.sharelogin.content.ShareContent;
import kale.sharelogin.content.ShareContentPic;
import kale.sharelogin.content.ShareContentText;
import kale.sharelogin.content.ShareContentWebPage;
import kale.sharelogin.qq.QQPlatform;
import kale.sharelogin.utils.SlUtils;
import kale.sharelogin.weibo.WeiBoPlatform;
import kale.sharelogin.weixin.WeiXinPlatform;

/**
 * 步骤：
 * 1.添加混淆参数
 * 2.在包中放入微信必须的activity
 */
public class MainActivity extends AppCompatActivity {

    private static final String TAG = "[ MainActivity ] ";

    public static final String URL = "https://www.zhihu.com/question/22913650";

    private ShareContent mShareContent;

    private ImageView tempPicIv;

    private TextView userInfoTv;

    private ImageView userPicIv;

    private TextView resultTv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tempPicIv = findViewById(R.id.temp_pic_iv);
        RadioGroup shareTypeRg = findViewById(R.id.share_type_rg);
        userInfoTv = findViewById(R.id.user_info_tv);
        userPicIv = findViewById(R.id.user_pic_iv);
        resultTv = findViewById(R.id.result);

        final Bitmap thumbBmp = ((BitmapDrawable) getResources().getDrawable(R.drawable.kale)).getBitmap();
        final Bitmap largeBmp = ((BitmapDrawable) getResources().getDrawable(R.drawable.pic_large_01)).getBitmap();

        shareTypeRg.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.rich_text) {
                mShareContent = new ShareContentWebPage("这里是我们的标题", "这是一段描述信息……", URL, thumbBmp);
            } else if (checkedId == R.id.only_image) {
                mShareContent = new ShareContentPic(largeBmp);
            } else if (checkedId == R.id.only_text) {
                mShareContent = new ShareContentText("这里是纯文本的文案……");
            }
        });
        shareTypeRg.check(R.id.rich_text);

        getSupportActionBar().setTitle(getPackageName());
    }

    @Override
    protected void onResume() {
        super.onResume();
        SlUtils.printLog(TAG + "onResume() :" + this);

        loadPicFromTempFile();

//        ShareLoginLib.checkLeak(this); // TODO: 2018/9/14 delete this
    }

    public void onClick(View v) {
        int i = v.getId();
        switch (i) {
            case R.id.QQ登录:
                ShareLoginLib.doLogin(this, QQPlatform.LOGIN, new MyLoginListener(this));
                break;
            case R.id.微博登录:
                ShareLoginLib.doLogin(this, WeiBoPlatform.LOGIN, new MyLoginListener(this));
                break;
            case R.id.微信登录:
                ShareLoginLib.doLogin(this, WeiXinPlatform.LOGIN, new MyLoginListener(this));
                break;
            case R.id.分享给QQ好友:
                ShareLoginLib.doShare(this, QQPlatform.FRIEND, mShareContent, new MyShareListener(this));
                break;
            case R.id.分享到QQ空间:
                ShareLoginLib.doShare(this, QQPlatform.ZONE, mShareContent, new MyShareListener(this));
                break;
            case R.id.分享到微博:
                ShareLoginLib.doShare(this, WeiBoPlatform.TIMELINE, mShareContent, new MyShareListener(this));
                break;
            case R.id.分享到微博故事:
                ShareLoginLib.doShare(this, WeiBoPlatform.STORY, mShareContent, new MyShareListener(this));
                break;
            case R.id.发送图文微博:
                if (mShareContent instanceof ShareContentWebPage) {
                    // 如果不带外链则会变成单图的分享，即url是null
                    ShareContentWebPage webPage = new ShareContentWebPage(
                            mShareContent.getTitle(), // title
                            mShareContent.getSummary(), // summary
                            null, // url
                            BitmapFactory.decodeByteArray(mShareContent.getThumbBmpBytes(), 0, mShareContent.getThumbBmpBytes().length));

                    ShareLoginLib.doShare(this, WeiBoPlatform.TIMELINE, webPage, new MyShareListener(this));
                } else {
                    ShareLoginLib.doShare(this, WeiBoPlatform.TIMELINE, mShareContent, new MyShareListener(this));
                }
                break;
            case R.id.分享给微信好友:
                ShareLoginLib.doShare(this, WeiXinPlatform.FRIEND, mShareContent, new MyShareListener(this));
                break;
            case R.id.分享到微信朋友圈:
                ShareLoginLib.doShare(this, WeiXinPlatform.TIMELINE, mShareContent, new MyShareListener(this));
                break;
            case R.id.分享到微信收藏:
                ShareLoginLib.doShare(this, WeiXinPlatform.FAVORITE, mShareContent, new MyShareListener(this));
                break;
        }
        userInfoTv.setText("");
        userPicIv.setImageResource(0);
        resultTv.setText("");
    }

    public void onGotUserInfo(@Nullable String text, @Nullable String imageUrl) {
        SlUtils.printLog(TAG + "gotUserInfo() :" + this);
        userInfoTv.setText(text);
        Picasso.with(this).load(imageUrl).into(userPicIv);
    }

    public void handResult(String result) {
        SlUtils.printLog(TAG + "handResult() :" + this);
        resultTv.postDelayed(() -> resultTv.setText(result), 100);
    }

    private void loadPicFromTempFile() {
        try {
            File file = new File(ShareLoginLib.TEMP_PIC_DIR + "share_login_lib_large_pic.jpg");
            if (file.exists()) {
                FileInputStream fis = new FileInputStream(file);
                tempPicIv.setImageBitmap(BitmapFactory.decodeStream(fis));
            } else {
                Log.e(TAG, "loadPicFromTempFile: no file");
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            Log.e(TAG, "loadPicFromTempFile: FileNotFoundException" + e.getMessage());
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        SlUtils.printLog(TAG + "onStop() :" + this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        SlUtils.printLog(TAG + "onDestroy() :" + this);
    }

}