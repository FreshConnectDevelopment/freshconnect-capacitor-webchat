package com.freshconnect.capacitor.webchat.wxapi;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.freshconnect.capacitor.webchat.FreshconnectWebChat;
import com.freshconnect.capacitor.webchat.cache.PluginCallCache;
import com.getcapacitor.JSObject;
import com.getcapacitor.PluginCall;
import com.tencent.mm.opensdk.constants.ConstantsAPI;
import com.tencent.mm.opensdk.modelbase.BaseReq;
import com.tencent.mm.opensdk.modelbase.BaseResp;
import com.tencent.mm.opensdk.modelbiz.SubscribeMessage;
import com.tencent.mm.opensdk.modelbiz.WXLaunchMiniProgram;
import com.tencent.mm.opensdk.modelmsg.SendAuth;
import com.tencent.mm.opensdk.modelmsg.SendAuth.Resp;
import com.tencent.mm.opensdk.modelmsg.SendMessageToWX;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;

/**
 * 微信消息处理
 * <p>
 * <li>接收微信发送的请求</li>
 * <li>接收发送到微信请求的响应结果</li>
 * </p>
 */
public class WXEntryActivity extends Activity implements IWXAPIEventHandler {

    /**
     * 缓存
     */
    private static PluginCallCache pluginCallCache;

    public static PluginCallCache getPluginCallCache() {
        createIfNoPresent();
        return pluginCallCache;
    }

    private static void setPluginCallCache(PluginCallCache pluginCallCache) {
        WXEntryActivity.pluginCallCache = pluginCallCache;
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

        createIfNoPresent();

        IWXAPI api = WXAPIFactory.createWXAPI(this, FreshconnectWebChat.APP_ID, false);
        try {
            // WXEntryActivity 中将接收到的 intent 及实现了 IWXAPIEventHandler 接口的对象传递给 IWXAPI 接口的
            // handleIntent方法
            api.handleIntent(getIntent(), this);
        } catch (Exception e) {
            Log.e(FreshconnectWebChat.LOG_TAG, e.getMessage());
        }
    }

    private static void createIfNoPresent() {
        if (pluginCallCache == null) {
            PluginCallCache pluginCallCache = new PluginCallCache();
            pluginCallCache.init();
            WXEntryActivity.setPluginCallCache(pluginCallCache);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        createIfNoPresent();

        IWXAPI api = WXAPIFactory.createWXAPI(this, FreshconnectWebChat.APP_ID, false);
        try {
            // WXEntryActivity 中将接收到的 intent 及实现了 IWXAPIEventHandler 接口的对象传递给 IWXAPI 接口的
            // handleIntent方法
            api.handleIntent(getIntent(), this);
        } catch (Exception e) {
            Log.e(FreshconnectWebChat.LOG_TAG, e.getMessage());
        }
    }

    /**
     * 微信发送的请求将回调到 onReq 方法
     *
     * @param baseReq
     */
    @Override
    public void onReq(BaseReq baseReq) {
        finish();
    }

    /**
     * 发送到微信请求的响应结果将回调到 onResp 方法
     *
     * @param baseResp
     */
    @Override
    public void onResp(BaseResp baseResp) {
        if (BaseResp.ErrCode.ERR_OK == baseResp.errCode) {
            switch (baseResp.getType()) {
                case ConstantsAPI.COMMAND_SENDAUTH:
                    handleAuth(baseResp);
                    break;
                case ConstantsAPI.COMMAND_LAUNCH_WX_MINIPROGRAM:
                    handleLaunchWxMiniprogram(baseResp);
                    break;
                default:
                    handleCommonRequest(baseResp);
            }
        } else {
            switch (baseResp.getType()) {
                case ConstantsAPI.COMMAND_SENDAUTH:
                    handleAuthResponse(baseResp);
                    break;
                default:
                    handleCommonResponse(baseResp);
            }
            Log.e(FreshconnectWebChat.LOG_TAG, "errorCode:" + baseResp.errCode + " errMsg:" + baseResp.errStr);
        }

    }

    /**
     * 拉起小程序相应处理
     *
     * @param baseResp
     */
    private void handleLaunchWxMiniprogram(BaseResp baseResp) {
        String appId = FreshconnectWebChat.APP_ID; // 填移动应用(App)的 AppId，非小程序的 AppID
        PluginCall pluginCall = pluginCallCache.getPluginCall(transaction);
        if (pluginCall == null) {
            Log.e(FreshconnectWebChat.LOG_TAG, appId + ",pluginCall is null");
        } else {
            WXLaunchMiniProgram.Resp launchMiniProResp = (WXLaunchMiniProgram.Resp) resp;
            JSObject ret = new JSObject();
            ret.put("extraData", launchMiniProResp.extMsg);
            pluginCall.resolve(ret);
        }
    }

    private void handleCommonRequest(BaseResp baseResp) {
        String transaction = baseResp.transaction;
        PluginCall pluginCall = pluginCallCache.getPluginCall(transaction);
        if (pluginCall == null) {
            Log.e(FreshconnectWebChat.LOG_TAG, transaction + ",pluginCall is null");
        } else {
            JSObject ret = new JSObject();
            ret.put("errCode", baseResp.errCode);
            ret.put("errMsg", baseResp.errStr);
            pluginCall.resolve(ret);
        }
    }

    private void handleCommonResponse(BaseResp baseResp) {
        String transaction = baseResp.transaction;
        PluginCall pluginCall = pluginCallCache.getPluginCall(transaction);
        if (pluginCall == null) {
            Log.e(FreshconnectWebChat.LOG_TAG, transaction + ",pluginCall is null");
        } else {
            JSObject ret = new JSObject();
            ret.put("errCode", baseResp.errCode);
            ret.put("errMsg", baseResp.errStr);
            pluginCall.resolve(ret);
        }
    }

    private void handleAuthResponse(BaseResp baseResp) {
        Resp authResp = (Resp) baseResp;
        String state = authResp.state;
        PluginCall pluginCall = pluginCallCache.getPluginCall(state);
        if (pluginCall == null) {
            Log.e(FreshconnectWebChat.LOG_TAG, state + ",pluginCall is null");
        } else {
            JSObject ret = new JSObject();
            ret.put("errCode", authResp.errCode);
            ret.put("errMsg", authResp.errStr);
            pluginCall.resolve(ret);
        }
    }

    /**
     * 第三方发起微信授权登录请求，微信用户允许授权第三方应用后，微信会拉起应用或重定向到第三方网站，并且带上授权临时票据code参数
     * <p>
     * 授权回调有两种场景会触发，登录授权和订阅授权都会回调
     *
     * @param baseResp
     */
    private void handleAuth(BaseResp baseResp) {
        if (baseResp instanceof SendAuth.Resp) {
            //用户授权登录
            Resp authResp = (Resp) baseResp;
            String state = authResp.state;
            PluginCall pluginCall = pluginCallCache.getPluginCall(state);
            if (pluginCall == null) {
                Log.e(FreshconnectWebChat.LOG_TAG, state + ",pluginCall is null");
            } else {
                JSObject ret = new JSObject();
                ret.put("errCode", authResp.errCode);
                ret.put("errMsg", authResp.errStr);
                ret.put("code", authResp.code);
                ret.put("openId", authResp.openId);
                pluginCall.resolve(ret);
            }
        } else {
            //用户订阅授权
            SubscribeMessage.Resp subscribeMessageResp = (SubscribeMessage.Resp) baseResp;
            String reserved = subscribeMessageResp.reserved;
            PluginCall pluginCall = pluginCallCache.getPluginCall(reserved);
            if (pluginCall == null) {
                Log.e(FreshconnectWebChat.LOG_TAG, reserved + ",pluginCall is null");
            } else {
                JSObject ret = new JSObject();
                ret.put("errCode", subscribeMessageResp.errCode);
                ret.put("errMsg", subscribeMessageResp.errStr);
                ret.put("openId", subscribeMessageResp.openId);
                ret.put("templateID", subscribeMessageResp.templateID);
                ret.put("action", subscribeMessageResp.action);
                ret.put("scene", subscribeMessageResp.scene);
                ret.put("reserved", subscribeMessageResp.reserved);
                pluginCall.resolve(ret);
            }
        }

    }
}
