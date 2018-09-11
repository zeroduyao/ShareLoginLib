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
import android.view.View;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.liulishuo.demo.R;
import com.liulishuo.share.ShareListener;
import com.liulishuo.share.ShareLoginLib;
import com.liulishuo.share.content.ShareContent;
import com.liulishuo.share.content.ShareContentPic;
import com.liulishuo.share.content.ShareContentText;
import com.liulishuo.share.content.ShareContentWebPage;
import com.liulishuo.share.qq.QQPlatform;
import com.liulishuo.share.weibo.WeiBoPlatform;
import com.liulishuo.share.weixin.WeiXinPlatform;
import com.squareup.picasso.Picasso;

/**
 * 步骤：
 * 1.添加混淆参数
 * 2.在包中放入微信必须的activity
 */
public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    public static final String URL = "https://www.zhihu.com/question/22913650";

    public static final String TITLE = "这里是标题";

    public static final String MSG = "这是描述信息";

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
        final Bitmap largeBmp = ((BitmapDrawable) getResources().getDrawable(R.drawable.large_pic)).getBitmap();

        shareTypeRg.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.rich_text) {
                mShareContent = new ShareContentWebPage(TITLE, MSG, URL, thumbBmp, largeBmp);
            } else if (checkedId == R.id.only_image) {
                mShareContent = new ShareContentPic(thumbBmp, largeBmp);
            } else if (checkedId == R.id.only_text) {
                mShareContent = new ShareContentText("doShare text");
            }
        });
        shareTypeRg.check(R.id.rich_text);

        loadPicFromTempFile();
        Toast.makeText(MainActivity.this, getPackageName(), Toast.LENGTH_SHORT).show();
    }

    private void loadPicFromTempFile() {
        try {
            String path = ShareLoginLib.TEMP_PIC_PATH + "sharePic_temp";
            File file = new File(path);
            if (file.exists()) {
                FileInputStream fis = new FileInputStream(file);
                tempPicIv.setImageBitmap(BitmapFactory.decodeStream(fis));
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public void onClick(View v) {
        ShareListener shareListener = new MyShareListener(this);

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
                ShareLoginLib.doShare(this, QQPlatform.FRIEND, mShareContent, shareListener);
                break;
            case R.id.分享到QQ空间:
                ShareLoginLib.doShare(this, QQPlatform.ZONE, mShareContent, shareListener);
                break;
            case R.id.分享到微博:
                ShareLoginLib.doShare(this, WeiBoPlatform.TIME_LINE, mShareContent, shareListener);
                break;
            case R.id.分享到微博_不带跳转链接:
                // 解开注释即可测试
                if (mShareContent instanceof ShareContentWebPage) {
//                    ((ShareContentWebPage) mShareContent).setUrl(null);
                }
                ShareLoginLib.doShare(this, WeiBoPlatform.TIME_LINE, mShareContent, shareListener);
                break;
            case R.id.分享给微信好友:
                ShareLoginLib.doShare(this, WeiXinPlatform.FRIEND, mShareContent, shareListener);
                break;
            case R.id.分享到微信朋友圈:
                ShareLoginLib.doShare(this, WeiXinPlatform.FRIEND_ZONE, mShareContent, shareListener);
                break;
            case R.id.分享到微信收藏:
                ShareLoginLib.doShare(this, WeiXinPlatform.FAVORITE, mShareContent, shareListener);
                break;
        }
        userInfoTv.setText("");
        userPicIv.setImageResource(0);
        resultTv.setText("");
    }

    public void onGotUserInfo(@Nullable String text, @Nullable String imageUrl) {
        userInfoTv.setText(text);
        Picasso.with(this).load(imageUrl).into(userPicIv);
    }

    public void handResult(String result) {
        resultTv.setText(result);
        Toast.makeText(this, result, Toast.LENGTH_SHORT).show();
    }
}
