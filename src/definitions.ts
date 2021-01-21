declare module '@capacitor/core' {
  interface PluginRegistry {  
    FreshconnectWebChat: FreshconnectWebchatPlugin;
  }
}

export interface AuthLoginResponse {
  errCode:  0;
  errMsg: '';
  code: '';
  openId: '';
}

export interface SubscribeMessageResp {
  errCode:  0;
  errMsg: '';
  openId: '';
  opetemplateIDnId: '';
  action: '';
  scene: '';
  reserved: '';
}

export interface LaunchWxMiniprogramResp {
  extraData: '';
}

export interface Resp {
  errCode:  0;
  errMsg: '';
}

export interface ShareTextRequest { 
  text:string, // 文本内容
  description:string, // 文本描述
  scene:number //0:分享到会话 1:分享到朋友圈 2:分享到收藏
} 

export interface SharePictureRequest { 
  imgData:string, // 图片路径或者base64编码图片或者手机相对路径
  description:string,  // 文本描述
  scene:number //0:分享到会话 1:分享到朋友圈 2:分享到收藏
} 

export interface SubscribeMessageRequest { 
  templateID:string, // 消息模板ID
  scene:number //重定向后会带上 scene 参数，开发者可以填 0-10000 的整形值，用来标识订阅场值
} 

export interface ShareMiniProgramRequest { 
  webpageUrl:string, // 兼容低版本的网页链接
  userName:string, // 小程序原始 ID 获取方法：登录小程序管理后台-设置-基本设置-帐号信息
  path:string, //小程序页面路径；对于小游戏，可以只传入 query 部分，来实现传参效果，如：传入 "?foo=bar"
  title:string, // 小程序消息标题
  description:string, // 小程序描述
  thumbData:string, // 小程序缩略图
  miniprogramType:number //0:正式版小程序 1:测试版小程序 2:预览版小程序
} 

export interface LaunchMiniProgramRequest { 
  userName:string,  // 小程序原始 ID 获取方法：登录小程序管理后台-设置-基本设置-帐号信息
  path:string, //小程序页面路径；对于小游戏，可以只传入 query 部分，来实现传参效果，如：传入 "?foo=bar"
  miniprogramType:number //0:正式版小程序 1:测试版小程序 2:预览版小程序
} 

export interface DownloadImgRequest { 
  imgData:string, // 图片路径或者base64编码图片或者手机相对路径
  type:number //0:base64
} 

export interface DownloadImgResp {
  errCode:  0;
  errMsg: '';
  data: '';
}

export interface HasPermissionResp {
  errCode:  0;
  errMsg: '';
  data: '';
}

export interface FreshconnectWebchatPlugin {
  echo(options: { value: string }): Promise<{ value: string }>;
  authLogin(): Promise<AuthLoginResponse>;
  shareText(request: ShareTextRequest): Promise<Resp>;
  sharePicture(request: SharePictureRequest): Promise<Resp>;
  shareMiniProgram(request: ShareMiniProgramRequest): Promise<Resp>;
  launchMiniProgram(request: LaunchMiniProgramRequest): Promise<LaunchWxMiniprogramResp>;
  subscribeMessage(request: SubscribeMessageRequest): Promise<SubscribeMessageResp>;
  downloadImg(request: DownloadImgRequest): Promise<DownloadImgResp>;
  hasPermission(): Promise<HasPermissionResp>;
}
