# ShareLoginLib   
[![](https://jitpack.io/v/tianzhijiexian/ShareLoginLib.svg)](https://jitpack.io/#tianzhijiexian/ShareLoginLib)  

ShareLoginLib likes simple sharesdk or umeng in China . It is a tool to help developers to share their content (image , text or music ) to WeChat,Weibo and QQ.  

![](./screenshot/logo.png)

## 示例
![](./screenshot/login.png) ![](./screenshot/share.png) ![](./screenshot/wechat.png)

## 如何使用
### 登录、分享  
```JAVA  
// 登录
LoginManager.login(this, LoginType.【WeiBo,WeiXin,QQ】, new LoginManager.LoginListener() {

      public void onSuccess(String accessToken, String uId, long expiresIn, @Nullable String wholeData) {}

      public void onError(String msg) {}

      public void onCancel() {}
  });


// 分享
ShareManager.share(MainActivity.this，ShareType.【xxxx】
        new ShareContentWebpage("title", "hello world!", "http://www.baidu.com", mBitmap),
        new ShareManager.ShareStateListener() {

                  public void onSuccess() {}

                  public void onCancel() {}

                  public void onError(String msg) {}
              });

```   

### 判断是否已安装第三方客户端  
```JAVA
ShareBlock.isWeiXinInstalled(this);
ShareBlock.isWeiBoInstalled(this);
ShareBlock.isQQInstalled(this);
```

### 通过token和id得到用户的详细信息
```JAVA
UserInfoManager.getUserInfo(context, LoginType.【WeiBo,WeiXin,QQ】, accessToken, userId, new UserInfoManager.UserInfoListener() {
        @Override
        public void onSuccess(@NonNull AuthUserInfo userInfo) {
            // 可以得到：昵称，性别，头像url，用户id
        }

        @Override
        public void onError(String msg) {

        }
    });
```  

更多详细的操作请参考项目的源码。

## 配置工作
因为本项目需要签名和第三方认证，所以需要使用者在第三方（qq/weibo/weixin）网站进行注册后才可测试。**本库的作者是不会提供任何和签名和密码有关的信息的。**

注意：第三方的登录和分享功能均需要在已签名的app版本中进行测试

### 1. 添加混淆参数
```  
# ———————— 微信 ————————
-keep class com.tencent.mm.sdk.** { *;}

# ———————— 微博 ————————   
-keep class com.sina.weibo.sdk.api.* { *; }

# ———————— QQ ————————
-keep class com.tencent.open.TDialog$*
-keep class com.tencent.open.TDialog$* {*;}
-keep class com.tencent.open.PKDialog
-keep class com.tencent.open.PKDialog {*;}
-keep class com.tencent.open.PKDialog$*
-keep class com.tencent.open.PKDialog$* {*;}
```  

### 2. 在主包名下新建wxapi这个包，然后放入WXEntryActivity  
Activity的写法如下：  

```JAVA   
package 你自己的包名.wxapi;
import com.liulishuo.share.weixin.WeiXinHandlerActivity;

public class WXEntryActivity extends WeiXinHandlerActivity {}
```

### 3. 在build.gradle中配置QQ的key
```JAVA
defaultConfig {
    // 你的包名
    applicationId "xxx.xxx.xxx"
    minSdkVersion 15
    targetSdkVersion 23

    manifestPlaceholders = [
            // 这里需要换成:tencent+你的AppId
            "tencentAuthId": "tencent123456",
    ]
}
```

### 4. 在gradle.properties中配置常量
如果你要运行该库的demo，请先在本地建立一个gradle.properties，然后配置下签名信息。如果是在自己项目中用了本lib，只需要保证可签名即可。
```
STORE_FILE_PATH xxxxx
STORE_PASSWORD xxxxx
KEY_ALIAS xxxxx
KEY_PASSWORD xxxxx
```
### 5. 在使用功能前进行注册  
```java  
ShareBlock.Config config = ShareBlock.Config.getInstance()
                .debug(true)
                .appName("Test App")
                .picTempFile(this)
                .qq(QQ_APPID, QQ_SCOPE)
                .weiXin(WEIXIN_APPID, WEIXIN_SECRET)
                .weiBo(WEIBO_APPID, WEIBO_REDIRECT_URL, WEIBO_SCOPE);

ShareBlock.init(config);
```  

## 测试用例  
1. 开启不保留活动
2. 未安装第三方应用  
3. 安装第三方应用，但第三方应用未登录  
4. 未开启不保留活动，并且第三方应用已经登录

## 已知的第三方SDK的bug（本lib无法解决）
- 首先不能信任第三方的回调，比如你分享到了微信，然后用户停在了微信，那么你就永远接收不到回调了。停留在他们的app一阵后，可能会因为内存不足等奇葩情况，你的应用被杀死。死了后怎么接收回调？  
- 如果你手机中安装了微信，并且微信已经登录。直接从你的应用分享到微信是没有任何回调的，只有在你用微信登录你的应用（无论登录是否成功，取消也行）后，才能有回调。   
- 当开启不保留活动后，有可能会出现界面的显示异常，这个和第三方的应用有密切关系，微博尤其明显。  

## LICENCE
-------------------------
  The MIT License (MIT)

  Copyright (c) 2015 LingoChamp Inc.

  Permission is hereby granted, free of charge, to any person obtaining a copy
  of this software and associated documentation files (the "Software"), to deal
  in the Software without restriction, including without limitation the rights
  to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
  copies of the Software, and to permit persons to whom the Software is
  furnished to do so, subject to the following conditions:

  The above copyright notice and this permission notice shall be included in
  all copies or substantial portions of the Software.

  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
  IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
  FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
  AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
  LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
  OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
  THE SOFTWARE.
