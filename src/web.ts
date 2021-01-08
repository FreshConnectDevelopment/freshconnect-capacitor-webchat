import { WebPlugin } from '@capacitor/core';
import { FreshconnectWebchatPlugin } from './definitions';

export class FreshconnectWebchatWeb extends WebPlugin implements FreshconnectWebchatPlugin {
  constructor() {
    super({
      name: 'FreshconnectWebchat',
      platforms: ['web'],
    });
  }

  async echo(options: { value: string }): Promise<{ value: string }> {
    console.log('ECHO', options);
    return options;
  }
}

const FreshconnectWebchat = new FreshconnectWebchatWeb();

export { FreshconnectWebchat };

import { registerWebPlugin } from '@capacitor/core';
registerWebPlugin(FreshconnectWebchat);
