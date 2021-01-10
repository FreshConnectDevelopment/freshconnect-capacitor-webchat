import { WebPlugin } from '@capacitor/core';
import { ShareTextRequest, AuthLoginResponse, FreshconnectWebchatPlugin, LaunchWxMiniprogramResp, Resp, SubscribeMessageResp, LaunchMiniProgramRequest, ShareMiniProgramRequest, SharePictureRequest, SubscribeMessageRequest } from './definitions';

export class FreshconnectWebchatWeb extends WebPlugin implements FreshconnectWebchatPlugin {

  constructor() {
    super({
      name: 'FreshconnectWebchat',
      platforms: ['web'],
    });
  }
  authLogin(): Promise<AuthLoginResponse> {
    throw new Error('Method not implemented.');
  }
  shareText(request: ShareTextRequest): Promise<Resp> {
    console.log('shareText', request);
    throw new Error('Method not implemented.');
  }
  sharePicture(request: SharePictureRequest): Promise<Resp> {
    console.log('sharePicture', request);
    throw new Error('Method not implemented.');
  }
  shareMiniProgram(request: ShareMiniProgramRequest): Promise<Resp> {
    console.log('sharePicture', request);
    throw new Error('Method not implemented.');
  }
  subscribeMessage(request: SubscribeMessageRequest): Promise<SubscribeMessageResp> {
    console.log('shareMiniProgram', request);
    throw new Error('Method not implemented.');
  }
  launchMiniProgram(request: LaunchMiniProgramRequest): Promise<LaunchWxMiniprogramResp> {
    console.log('launchMiniProgram', request);
    throw new Error('Method not implemented.');
  }

  async echo(options: { value: string }): Promise<{ value: string }> {
    console.log('ECHO', options);
    return options;
  }
}

const FreshconnectWebChat = new FreshconnectWebchatWeb();

export { FreshconnectWebChat };

import { registerWebPlugin } from '@capacitor/core';
registerWebPlugin(FreshconnectWebChat);
