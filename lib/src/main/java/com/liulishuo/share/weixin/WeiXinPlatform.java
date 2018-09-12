package com.liulishuo.share.weixin;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.text.TextUtils;

import com.liulishuo.share.LoginListener;
import com.liulishuo.share.ShareListener;
import com.liulishuo.share.ShareLoginLib;
import com.liulishuo.share.content.ShareContent;
import com.liulishuo.share.content.ShareContentType;
import com.liulishuo.share.utils.IPlatform;
import com.tencent.mm.opensdk.modelbase.BaseReq;
import com.tencent.mm.opensdk.modelbase.BaseResp;
import com.tencent.mm.opensdk.modelmsg.SendAuth;
import com.tencent.mm.opensdk.modelmsg.SendMessageToWX;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;

/**
 * @author Kale
 * @date 2018/9/10
 */
public class WeiXinPlatform implements IPlatform {

    public static final String KEY_APP_ID = "weixin_key_app_id";

    public static final String KEY_SECRET_KEY = "weixin_key_secret_key";

    // ---------------------------------------------------------------

    public static final String LOGIN = "weixin_login";

    public static final String FRIEND = "weixin_friend", FRIEND_ZONE = "weixin_friend_zone", FAVORITE = "weixin_favorite";

    private IWXAPIEventHandler wxEventHandler;

    @Override
    public String[] getSupportedTypes() {
        return new String[]{LOGIN, FRIEND, FRIEND_ZONE, FAVORITE};
    }

    @Override
    public boolean isAppInstalled(@NonNull Context context) {
        return getApi(context).isWXAppInstalled();
    }

    @Override
    public void checkEnvironment(Context context, String type, @ShareContentType int contentType) {
        String appId = ShareLoginLib.getValue(KEY_APP_ID);

        // 1. 检测id是否为空
        if (TextUtils.isEmpty(appId)) {
            throw new IllegalArgumentException("WeiXinAppId未被初始化，当前为空");
        }

        // 2. 检测是否安装了微信
        if (!isAppInstalled(context)) {
            throw new IllegalArgumentException("当设备上未安装微信");
        }

        // 3. 检测分享的目标渠道是否合法
        if (!type.equals(LOGIN)) {
            // 是分享操作
            if (!type.equals(FRIEND) && !type.equals(FRIEND_ZONE)) {
                throw new UnsupportedOperationException("不支持的分享渠道");
            }
        }
    }

    @Override
    public void doLogin(@NonNull final Activity activity, @NonNull final LoginListener listener) {
        SendAuth.Req request = new SendAuth.Req();
        request.scope = "snsapi_userinfo"; // 期望得到用户信息

        sendRequest(activity, request, new IWXAPIEventHandler() {
            @Override
            public void onReq(BaseReq baseReq) {
            }

            @Override
            public void onResp(BaseResp baseResp) {
                LoginHelper.parseLoginResp(activity, baseResp, listener);
                activity.finish();
            }
        });
    }

    public void doShare(@NonNull Activity activity, String shareType, @NonNull ShareContent shareContent, @NonNull ShareListener listener) {
        SendMessageToWX.Req request = ShareHelper.createRequest(shareContent, shareType);

        sendRequest(activity, request, new IWXAPIEventHandler() {
            @Override
            public void onReq(BaseReq baseReq) {
            }

            @Override
            public void onResp(BaseResp baseResp) {
                ShareHelper.parseShareResp(baseResp, listener);
                activity.finish();
            }
        });
    }

    private void sendRequest(@NonNull Activity activity, BaseReq request, IWXAPIEventHandler eventHandler) {
        IWXAPI api = getApi(activity);
        api.registerApp(ShareLoginLib.getValue(KEY_APP_ID));

        wxEventHandler = eventHandler;

        api.sendReq(request); // 这里的请求的回调会在handlerResp中收到
    }

    @Override
    public void onResponse(Activity activity, Intent data) {
        getApi(activity).handleIntent(data, wxEventHandler);
    }

    private static IWXAPI getApi(Context context) {
        return WXAPIFactory.createWXAPI(context.getApplicationContext(), ShareLoginLib.getValue(KEY_APP_ID), true);
    }

}
