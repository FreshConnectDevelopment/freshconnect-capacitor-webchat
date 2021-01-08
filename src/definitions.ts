declare module '@capacitor/core' {
  interface PluginRegistry {
    FreshconnectWebchat: FreshconnectWebchatPlugin;
  }
}

export interface FreshconnectWebchatPlugin {
  echo(options: { value: string }): Promise<{ value: string }>;
}
