## 配置（库版本1.+）

### 1. 在build.gradle中配置QQ的key  

```java
defaultConfig {
    applicationId "xxx.xxx.xxx" // 你的app包名
    
     // tencent+你的AppId
    manifestPlaceholders = ["tencentAuthId": "tencent123456"]
}
```

### 2. 在使用功能前配置常量

```java
SlConfig config = new SlConfig.Builder()
            .debug(false)
            .appName("Your App Name")
            .picTempFile(null) // 缓存缩略图的目录，如无特殊要求可以是null
            .qq(qq_app_id, qq_scope)
            .weiBo(weibo_app_key, weibo_redirect_url, weibo_scope)
            .weiXin(weixin_app_id, weixin_secret)
            .build();

ShareLoginSDK.init(this, config);
```

常量的具体值请和第三方平台进行申请，不要随意填写：

```java
/**
 * 初始化一些常量，这里的scope请根据第三方的文档进行定制
*/ 
protected void initConstant() {
    qq_app_id = "xxxxxxxxxxxx";
    qq_scope = "get_user_info,"
    + "get_simple_userinfo,"
    + "add_share,"
    + "add_topic,"
    + "add_pic_t";    
    
    weibo_app_key = "xxxxxxxxxxxx";
    weibo_redirect_url = "xxxxxxxxxxxx";
    weibo_scope = "friendships_groups_read,"
    + "friendships_groups_write,"
    + "statuses_to_me_read,"
    + "follow_app_official_microblog";    
    
    weixin_app_id = "xxxxxxxxxxxx";
    weixin_secret = "xxxxxxxxxxxx"; 
}
```

## 使用（库版本1.+）

### 判断是否已安装第三方客户端  

```java
ShareLoginSDK.isQQInstalled(this);
ShareLoginSDK.isWeiBoInstalled(this);
ShareLoginSDK.isWeiXinInstalled(this);
```

### 登录

登录操作会根据`SsoLoginType.XX`来区分平台，可选的类型有：

```java
public @interface SsoLoginType {

    String QQ = "QQ", WEIBO = "WEIBO", WEIXIN = "WEIXIN"; 
}
```

执行QQ登录（举例）：

```java
SsoLoginManager.login(this, SsoLoginType.QQ, new SsoLoginManager.LoginListener() {
    @Override
  public void onSuccess(String accessToken, String uId, long expiresIn, @Nullable String wholeData) {
        super.onSuccess(accessToken, uId, expiresIn, wholeData);
  }
});
```

*这里的监听器可以监听到登录成功、登录失败、取消登录等情况，请根据需要进行实现。*

得到QQ用户的详细信息（举例）：

在**1.+版本**中用户登录和获得用户信息是两个流程，登录成功后会得到token，我们需要用这个token来得到用户的具体信息。

```java
SsoUserInfoManager.getUserInfo(context, SsoLoginType.QQ, accessToken, userId,
    new UserInfoListener() {

        public void onSuccess(@NonNull OAuthUserInfo userInfo) {
            // 可以得到：昵称、性别、头像、用户id
        }

        public void onError(String errorMsg) {
        }
    });
```

更多详细的操作请参考adapter分支中的app项目。

### 分享

目前分享操作支持分享纯文字，单张图片和富文本网页三种类型，分别对应的类为：

1. ShareContentText
2. ShareContentPic
3. ShareContentWebPage

说明：上述类型都是ShareContent的子类。

执行分享给QQ好友的操作（举例）：

```java
ShareContent shareContent = new ShareContentWebPage("title", "summary", "http://www.kale.com", bitmap); 

SsoShareManager.share(this, SsoShareType.QQ_FRIEND, shareContent, new SsoShareManager.ShareStateListener(){
    @Override
  protected void onComplete() {
        super.onComplete();
  }
});
```

*这里的监听器可以监听到用户分享完成、取消分享和分享异常等情况，请根据需要进行实现。*