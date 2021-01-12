#import <Foundation/Foundation.h>
#import <Capacitor/Capacitor.h>

// Define the plugin using the CAP_PLUGIN Macro, and
// each method the plugin supports using the CAP_PLUGIN_METHOD macro.
CAP_PLUGIN(FreshconnectWebchat, "FreshconnectWebchat",
           CAP_PLUGIN_METHOD(echo, CAPPluginReturnPromise);
           CAP_PLUGIN_METHOD(authLogin, CAPPluginReturnPromise);
           CAP_PLUGIN_METHOD(shareText, CAPPluginReturnPromise);
           CAP_PLUGIN_METHOD(sharePicture, CAPPluginReturnPromise);
           CAP_PLUGIN_METHOD(subscribeMessage, CAPPluginReturnPromise);
           CAP_PLUGIN_METHOD(launchMiniProgram, CAPPluginReturnPromise);
           CAP_PLUGIN_METHOD(shareMiniProgram, CAPPluginReturnPromise);
)
