package com.freshconnect.capacitor.webchat;
import android.content.Intent;
import android.util.Log;

import com.freshconnect.capacitor.plugins.webchat.freshconnectcapacitorwebchat.R;
import com.freshconnect.capacitor.webchat.wxapi.WXEntryActivity;
import com.getcapacitor.JSObject;
import com.getcapacitor.NativePlugin;
import com.getcapacitor.Plugin;
import com.getcapacitor.PluginCall;
import com.getcapacitor.PluginMethod;
import com.tencent.mm.opensdk.modelmsg.SendAuth.Req;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;

import java.util.UUID;

/**
 * 微信插件主入口
 */
@NativePlugin
public class FreshconnectWebChat extends Plugin {

    /**
     * 移动应用从微信官方网站申请到的合法appID
     */
    public static String APP_ID = "";

    /**
     * 移动应用从微信官方网站申请到的合法app秘钥
     */
    public static String APP_SECRET = "";

    /**
     * 日志前缀
     */
    public static String LOG_TAG = "freshconnect-capacitor-wechat:";

    @PluginMethod
    public void echo(PluginCall call) {
        String value = call.getString("value");

        JSObject ret = new JSObject();
        ret.put("value", value);
        call.success(ret);
    }

    /**
     * 加载插件参数
     */
    @Override
    public void load() {
        FreshconnectWebChat.APP_ID = this.getContext().getString(R.string.app_id);
        FreshconnectWebChat.APP_SECRET = this.getContext().getString(R.string.app_secret);
    }

    /**
     * 将程序注册到微信,接口调用的必备条件
     *
     * @param call
     */
    @PluginMethod()
    public void registerWx(PluginCall call) {
        IWXAPI api = WXAPIFactory.createWXAPI(getContext(), FreshconnectWebChat.APP_ID, true);
        api.registerApp(FreshconnectWebChat.APP_ID);
        Log.i(this.LOG_TAG, "register to wechat");
    }

    /**
     * 微信认证
     *
     * @param call
     */
    @PluginMethod()
    public void authLogin(PluginCall call) {
        UUID uuid = UUID.randomUUID();
        String state = uuid.toString();
        WXEntryActivity.getPluginCallCache().addPluginCallCache(state, call);
        final Req req = new Req();
        req.scope = "snsapi_userinfo";
        req.state = state;
        IWXAPI api = WXAPIFactory.createWXAPI(getContext(), FreshconnectWebChat.APP_ID, true);
        api.sendReq(req);
        Log.i(this.LOG_TAG, "send auth request to wechat");
    }

    @Override
    protected void handleOnActivityResult(int requestCode, int resultCode, Intent data) {
        super.handleOnActivityResult(requestCode, resultCode, data);
    }
}
