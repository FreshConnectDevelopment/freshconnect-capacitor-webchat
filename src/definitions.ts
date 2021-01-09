declare module '@capacitor/core' {
  interface PluginRegistry {  
    FreshconnectWebchat: FreshconnectWebchatPlugin;
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
  text:string, 
  description:string, 
  scene:number
} 

export interface SharePictureRequest { 
  imgData:any[], 
  description:string, 
  scene:number
} 

export interface SubscribeMessageRequest { 
  templateID:string, 
  scene:number
} 

export interface ShareMiniProgramRequest { 
  webpageUrl:string, 
  userName:string, 
  path:string, 
  title:string, 
  description:string, 
  thumbData:string, 
  miniprogramType:number
} 

export interface LaunchMiniProgramRequest { 
  userName:string, 
  path:string, 
  miniprogramType:number
} 

export interface FreshconnectWebchatPlugin {
  echo(options: { value: string }): Promise<{ value: string }>;
  authLogin(): Promise<AuthLoginResponse>;
  shareText(request: ShareTextRequest): Promise<Resp>;
  sharePicture(request: SharePictureRequest): Promise<Resp>;
  shareMiniProgram(request: ShareMiniProgramRequest): Promise<Resp>;
  subscribeMessage(request: SubscribeMessageRequest): Promise<SubscribeMessageResp>;
  launchMiniProgram(request: LaunchMiniProgramRequest): Promise<LaunchWxMiniprogramResp>;
}
