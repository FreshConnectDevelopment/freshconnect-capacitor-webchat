package com.freshconnect.capacitor.webchat;

import android.Manifest;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.util.Log;

import com.freshconnect.capacitor.webchat.freshconnectcapacitorwebchat.R;
import com.freshconnect.capacitor.webchat.util.Util;
import com.freshconnect.capacitor.webchat.wxapi.WXEntryActivity;
import com.getcapacitor.JSObject;
import com.getcapacitor.NativePlugin;
import com.getcapacitor.Plugin;
import com.getcapacitor.PluginCall;
import com.getcapacitor.PluginMethod;
import com.getcapacitor.PluginRequestCodes;
import com.tencent.mm.opensdk.modelbiz.SubscribeMessage;
import com.tencent.mm.opensdk.modelbiz.WXLaunchMiniProgram;
import com.tencent.mm.opensdk.modelmsg.SendAuth.Req;
import com.tencent.mm.opensdk.modelmsg.SendMessageToWX;
import com.tencent.mm.opensdk.modelmsg.WXImageObject;
import com.tencent.mm.opensdk.modelmsg.WXMediaMessage;
import com.tencent.mm.opensdk.modelmsg.WXMiniProgramObject;
import com.tencent.mm.opensdk.modelmsg.WXTextObject;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
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
     * 缩略图大小
     */
    private static final int THUMB_SIZE = 150;

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

    /**
     * 微信分享文本
     *
     * @param call
     */
    @PluginMethod()
    public void shareText(PluginCall call) {
        UUID uuid = UUID.randomUUID();
        String transaction = uuid.toString();
        WXEntryActivity.getPluginCallCache().addPluginCallCache(transaction, call);
        IWXAPI api = WXAPIFactory.createWXAPI(getContext(), FreshconnectWebChat.APP_ID, true);

        String text = call.getString("text");
        String description = call.getString("description");
        int mTargetScene = call.getInt("scene");

        //初始化一个 WXTextObject 对象，填写分享的文本内容
        WXTextObject textObj = new WXTextObject();
        textObj.text = text;

        //用 WXTextObject 对象初始化一个 WXMediaMessage 对象
        WXMediaMessage msg = new WXMediaMessage();
        msg.mediaObject = textObj;
        msg.description = description;

        SendMessageToWX.Req req = new SendMessageToWX.Req();
        req.transaction = transaction;
        req.message = msg;
        req.scene = mTargetScene;
        //调用api接口，发送数据到微信
        api.sendReq(req);
        Log.i(this.LOG_TAG, "send shareText request to wechat");
    }

    /**
     * 微信分享图片
     *
     * @param call
     */
    @PluginMethod()
    public void sharePicture(PluginCall call) {

        if (this.hasPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            this.pluginRequestPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE, PluginRequestCodes.FILESYSTEM_REQUEST_WRITE_FILE_PERMISSIONS);
        }

        UUID uuid = UUID.randomUUID();
        String transaction = uuid.toString();
        WXEntryActivity.getPluginCallCache().addPluginCallCache(transaction, call);

        IWXAPI api = WXAPIFactory.createWXAPI(getContext(), FreshconnectWebChat.APP_ID, true);
        int mTargetScene = call.getInt("scene");

        String imgData = call.getString("imgData");

        Bitmap bmp = BitmapFactory.decodeStream(Util.getFileInputStream(imgData, getContext()));

        //初始化 WXImageObject 和 WXMediaMessage 对象
        WXImageObject imgObj = new WXImageObject(bmp);
        WXMediaMessage msg = new WXMediaMessage();
        msg.mediaObject = imgObj;

        //设置缩略图
        Bitmap thumbBmp = Bitmap.createScaledBitmap(bmp, THUMB_SIZE, THUMB_SIZE, true);
        bmp.recycle();
        msg.thumbData = Util.bmpToByteArray(thumbBmp, true);

        //构造一个Req
        SendMessageToWX.Req req = new SendMessageToWX.Req();
        req.transaction = transaction;
        req.message = msg;
        req.scene = mTargetScene;
        //调用api接口，发送数据到微信
        api.sendReq(req);
        Log.i(this.LOG_TAG, "send sharePicture request to wechat");
    }

    /**
     * 微信消息订阅
     *
     * @param call
     */
    @PluginMethod()
    public void subscribeMessage(PluginCall call) {
        UUID uuid = UUID.randomUUID();
        String reserved = uuid.toString();
        WXEntryActivity.getPluginCallCache().addPluginCallCache(reserved, call);

        IWXAPI api = WXAPIFactory.createWXAPI(getContext(), FreshconnectWebChat.APP_ID, true);

        SubscribeMessage.Req req = new SubscribeMessage.Req();
        req.scene = call.getInt("scene");
        req.templateID = call.getString("templateID");
        req.reserved = reserved;
        //调用api接口，发送数据到微信
        api.sendReq(req);
        Log.i(this.LOG_TAG, "send sharePicture request to wechat");
    }

    /**
     * 拉起微信小程序
     *
     * @param call
     */
    @PluginMethod()
    public void launchMiniProgram(PluginCall call) {

        IWXAPI api = WXAPIFactory.createWXAPI(getContext(), FreshconnectWebChat.APP_ID, true);

        String appId = FreshconnectWebChat.APP_ID; // 填移动应用(App)的 AppId，非小程序的 AppID

        WXEntryActivity.getPluginCallCache().addPluginCallCache(appId, call);

        WXLaunchMiniProgram.Req req = new WXLaunchMiniProgram.Req();
        req.userName = call.getString("userName"); // 填小程序原始id
        req.path = call.getString("path");                  ////拉起小程序页面的可带参路径，不填默认拉起小程序首页，对于小游戏，可以只传入 query 部分，来实现传参效果，如：传入 "?foo=bar"。
        req.miniprogramType = call.getInt("miniprogramType");// 可选打开 开发版，体验版和正式版

        //调用api接口，发送数据到微信
        api.sendReq(req);
        Log.i(this.LOG_TAG, "send sharePicture request to wechat");
    }

    /**
     * 分享微信小程序
     *
     * @param call
     */
    @PluginMethod()
    public void shareMiniProgram(PluginCall call) {

        if (this.hasPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            this.pluginRequestPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE, PluginRequestCodes.FILESYSTEM_REQUEST_WRITE_FILE_PERMISSIONS);
        }
        UUID uuid = UUID.randomUUID();
        String transaction = uuid.toString();
        WXEntryActivity.getPluginCallCache().addPluginCallCache(transaction, call);

        IWXAPI api = WXAPIFactory.createWXAPI(getContext(), FreshconnectWebChat.APP_ID, true);

        WXMiniProgramObject miniProgramObj = new WXMiniProgramObject();
        String webpageUrl = call.getString("webpageUrl");
        int miniprogramType = call.getInt("miniprogramType");
        String userName = call.getString("userName");
        String path = call.getString("path");
        String title = call.getString("title");
        String description = call.getString("description");
        String imgData = call.getString("thumbData");

        Bitmap bmp = BitmapFactory.decodeStream(Util.getFileInputStream(imgData, getContext()));
        Bitmap thumbBmp = Bitmap.createScaledBitmap(bmp, THUMB_SIZE, THUMB_SIZE, true);
        bmp.recycle();
        byte[] thumbData = Util.bmpToByteArray(thumbBmp, true);

        miniProgramObj.webpageUrl = webpageUrl; // 兼容低版本的网页链接
        miniProgramObj.miniprogramType = miniprogramType;// 正式版:0，测试版:1，体验版:2
        miniProgramObj.userName = userName;     // 小程序原始id
        miniProgramObj.path = path;            //小程序页面路径；对于小游戏，可以只传入 query 部分，来实现传参效果，如：传入 "?foo=bar"
        WXMediaMessage msg = new WXMediaMessage(miniProgramObj);
        msg.title = title;                    // 小程序消息title
        msg.description = description;               // 小程序消息desc
        msg.thumbData = thumbData;                      // 小程序消息封面图片，小于128k

        SendMessageToWX.Req req = new SendMessageToWX.Req();
        req.transaction = transaction;
        req.message = msg;
        req.scene = SendMessageToWX.Req.WXSceneSession;  // 目前只支持会话
        api.sendReq(req);
        Log.i(this.LOG_TAG, "send shareMiniProgram request to wechat");
    }

    /**
     * 下载图片
     *
     * @param call
     */
    @PluginMethod()
    public void downloadImg(PluginCall call) throws IOException {

        if (!this.hasPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            this.pluginRequestPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE, PluginRequestCodes.FILESYSTEM_REQUEST_WRITE_FILE_PERMISSIONS);
        }

        String imgData = call.getString("imgData");

        InputStream fileInputStream = Util.getFileInputStream(imgData, getContext());
        byte[] array = Util.inputStreamToByte(fileInputStream);

        if(array == null){
            JSObject ret = new JSObject();
            ret.put("errCode", 1);
            ret.put("errMsg", "文件路径有误：" + imgData);
            call.resolve(ret);
            Log.i(this.LOG_TAG, "文件路径有误：" + imgData);
            return;
        }
        String base64 = Base64.encodeToString(array, Base64.DEFAULT);
        fileInputStream.close();

        JSObject ret = new JSObject();
        ret.put("errCode", 0);
        ret.put("errMsg", "");
        ret.put("data", base64);
        call.resolve(ret);
        Log.i(this.LOG_TAG, "download img");

    }



    /**
     * 下载图片
     *
     * @param call
     */
    @PluginMethod()
    public void hasPermission(PluginCall call) throws IOException {

        JSObject ret = new JSObject();
        if (!this.hasPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            this.pluginRequestPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE, PluginRequestCodes.FILESYSTEM_REQUEST_WRITE_FILE_PERMISSIONS);
            ret.put("errCode", -1);
            ret.put("errMsg", "plugin request permission");
        }
        call.resolve(ret);

    }
}
