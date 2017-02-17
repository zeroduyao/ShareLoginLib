# ShareLoginLib   
[![](https://jitpack.io/v/tianzhijiexian/ShareLoginLib.svg)](https://jitpack.io/#tianzhijiexian/ShareLoginLib)  

ShareLoginLib likes simple sharesdk or umeng in China . It is a tool to help developers to share their content (image , text or music ) to WeChat,Weibo and QQ.  

![](./screenshot/logo.png)

## 示例
![](./screenshot/login.png) ![](./screenshot/share.png) ![](./screenshot/wechat.png)

## 添加依赖

1.在项目外层的build.gradle中添加JitPack仓库

```
repositories {
	maven {
		url "https://jitpack.io"
	}
}
```

2.在用到的项目中添加依赖  
>	compile 'com.github.tianzhijiexian:ShareLoginLib:[Latest release](https://github.com/tianzhijiexian/ShareLoginLib/releases)(<-click it)'  

**举例：**
```
compile 'com.github.tianzhijiexian:ShareLoginLib:1.3.7'
```


## 使用

### 在Application上配置注解

```java
@ShareLoginApp(packageName = BuildConfig.APPLICATION_ID)
public class AppApplication extends Application {
	 // ...
}
```
这里写上你的项目包名或者你可以通过`BuildConfig.APPLICATION_ID`的值来代替。

### 初始化第三方sdk的参数
```java  
Config config = Config.getInstance()
            .debug(true)
            .appName("Test App")
            .picTempFile(this)
            .qq(QQ_APPID, QQ_SCOPE)
            .weiXin(WEIXIN_APPID, WEIXIN_SECRET)
            .weiBo(WEIBO_APPID, WEIBO_REDIRECT_URL, WEIBO_SCOPE);

ShareBlock.init(config);
```

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

### 通过token和id得到用户信息
```JAVA
UserInfoManager.getUserInfo(context, LoginType.【WeiBo,WeiXin,QQ】, accessToken, userId,
    new UserInfoManager.UserInfoListener() {

        public void onSuccess(@NonNull AuthUserInfo userInfo) {
            // 可以得到：昵称，性别，头像url，用户id
        }

        public void onError(String msg) {
        }
    });
```  

更多详细的操作请参考项目的demo。

## 配置工作

### 1. 在build.gradle中配置QQ的key  

```JAVA
defaultConfig {
    applicationId "xxx.xxx.xxx" // 你的包名
    minSdkVersion 15
    targetSdkVersion 23

    manifestPlaceholders = [
            // 这里换成:tencent+你的AppId
            "tencentAuthId": "tencent123456",
    ]
}
```

### 2. 在gradle.properties中配置常量

这里分两种情况：  
**1.** 如果你要运行该项目给出的demo，那么请先在本地建立一个`gradle.properties`文件，然后配置下下列必要的信息   

```
STORE_FILE_PATH xxxxx
STORE_PASSWORD xxxxx
KEY_ALIAS xxxxx
KEY_PASSWORD xxxxx
TENCENT_AUTHID tencentxxxx
```

**2.** 如果你是在自己项目中通过gradle依赖了本库，只需要保证可签名即可
```
signingConfigs {
    release {
        // 这里换成你自己的签名、密码等信息
        storeFile file(STORE_FILE_PATH)
        storePassword STORE_PASSWORD
        keyAlias KEY_ALIAS
        keyPassword KEY_PASSWORD
    }
}
```

最后运行签名后的apk即可。

## 重要说明
因为本项目需要签名和第三方认证，所以使用者要在第三方（qq/weibo/weixin）网站进行注册后才可测试。**本库作者是不会提供任何和签名、密码、AppId等有关信息的。**

**注意：第三方的登录和分享功能均需要在【已签名】的app中进行测试**

## 测试环境  
1. 开启不保留活动
2. 未安装第三方应用  
3. 安装第三方应用，但第三方应用未登录  
4. 未开启不保留活动，并且第三方应用已经登录

目前需要在上述四种不同的环境中执行项目的测试用例，以保证整个项目的健壮性。

## 已知的第三方SDK的bug（本lib无法解决）
- 首先不能信任第三方的回调，比如你分享到了微信，然后用户停在了微信，那么你就永远接收不到回调了。停留在他们的app一阵后，可能会因为内存不足等奇葩情况，你的应用被杀死。死了后怎么接收回调？  
- 如果你手机中安装了微信，并且微信已经登录。直接从你的应用分享到微信是没有任何回调的，只有在你用微信登录你的应用（无论登录是否成功，取消也行）后，才能有回调。   
- 当开启不保留活动后，有可能会出现界面的显示异常，这个和第三方的应用有密切关系，微博尤其明显。  

## LICENCE

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
