import Foundation
import Capacitor
import Kingfisher


/**
 * Please read the Capacitor iOS Plugin Development Guide
 * here: https://capacitorjs.com/docs/plugins/ios
 */
@objc(FreshconnectWebchat)
public class FreshconnectWebchat: CAPPlugin {
    
    
    override public func load() {
        
        let appid = "wxc5eec58b5652c0f6"
        let universalLink = "https://galloped.cn/"
        WXApi.registerApp(appid, universalLink: universalLink)
    }
    
    @objc func echo(_ call: CAPPluginCall) {
        let value = call.getString("value") ?? ""
        call.success([
            "value": value
        ])
    }
    
    @objc func authLogin(_ call: CAPPluginCall) {
        let uuid = NSUUID().uuidString

        let req = SendAuthReq()
        req.scope = "snsapi_userinfo"
        req.state = uuid
        WXApi.send(req)
    }
    
    @objc func shareText(_ call: CAPPluginCall) {
        let uuid = NSUUID().uuidString

        let req = SendMessageToWXReq()
        req.bText = true
        req.text = call.getString("text")!
        let scene:Int = call.getInt("scene")!
        req.scene = Int32(scene)
        WXApi.send(req)
    }
    
    fileprivate func download(_ url: URL, _ call: CAPPluginCall) {
        let resource = ImageResource(downloadURL: url)
        KingfisherManager.shared.retrieveImage(with: url) { result in
            switch result {
            case .success(let value):
                
                let uuid = NSUUID().uuidString
                
                let req = SendMessageToWXReq()
                req.bText = false
                
                let imageObject =  WXImageObject()
                let cgImage = value.image.cgImage
                let uiImage = UIImage.init(cgImage: cgImage!)
                let thumbImage = self.resizeImage(uiImage, newWidth: 144)
                imageObject.imageData = uiImage.pngData()!;
                
                
                let mediaMessage = WXMediaMessage()
                mediaMessage.setThumbImage(uiImage)
                mediaMessage.mediaObject =  imageObject
                
                let scene:Int = call.getInt("scene")!
                req.scene = Int32(scene)
                req.message = mediaMessage;
                WXApi.send(req)
                
            case .failure(let error):
                print(error) // The error happens
            }
            
        }
    }
    
    @objc func sharePicture(_ call: CAPPluginCall) {
        let imgData = call.getString("imgData")!
        
        guard let url = URL.init(string: imgData) else {
            
            if imgData.starts(with: "data:image") {
                let imageData = Data(base64Encoded: imgData, options: Data.Base64DecodingOptions.ignoreUnknownCharacters)!
                    let uiImage = UIImage(data: imageData)!
                
                    let uuid = NSUUID().uuidString
                    
                    let req = SendMessageToWXReq()
                    req.bText = false
                    
                    let imageObject =  WXImageObject()
                    let thumbImage = self.resizeImage(uiImage, newWidth: 144)
                    imageObject.imageData = uiImage.pngData()!;
                    
                    
                    let mediaMessage = WXMediaMessage()
                    mediaMessage.setThumbImage(uiImage)
                    mediaMessage.mediaObject =  imageObject
                    
                    let scene:Int = call.getInt("scene")!
                    req.scene = Int32(scene)
                    req.message = mediaMessage;
                    WXApi.send(req)
            }
            return
        }
        download(url, call)
    }
    
    @objc func subscribeMessage(_ call: CAPPluginCall) {
        let uuid = NSUUID().uuidString

        let req = WXSubscribeMsgReq()
        req.templateId = call.getString("templateID")!
        req.reserved = call.getString("reserved")
        let scene:Int = call.getInt("scene")!
        req.scene = UInt32(scene)
        WXApi.send(req)
    }
    
    @objc func launchMiniProgram(_ call: CAPPluginCall) {
        let uuid = NSUUID().uuidString

        let req = WXLaunchMiniProgramReq()
        req.userName = call.getString("userName")!; // 填小程序原始id
        req.path = call.getString("path")!;                  ////拉起小程序页面的可带参路径，不填默认拉起小程序首页，对于小游戏，可以只传入 query 部分，来实现传参效果，如：传入 "?foo=bar"。
        let miniprogramType:Int = call.getInt("miniprogramType")!
        req.miniProgramType = WXMiniProgramType.init(rawValue: UInt(UInt32(miniprogramType)))!;// 可选打开 开发版，体验版和正式版
        WXApi.send(req)
    }
    
    @objc func shareMiniProgram(_ call: CAPPluginCall) {
            
        let transaction = NSUUID().uuidString
        
        let req = SendMessageToWXReq()
        req.bText = false
        
        let miniProgramObject =  WXMiniProgramObject()
        
        let webpageUrl = call.getString("webpageUrl")
        let miniprogramType = call.getInt("miniprogramType")
        let userName = call.getString("userName")
        let path = call.getString("path")
        let title = call.getString("title")
        let description = call.getString("description")
        let imgData = call.getString("thumbData")!
        
        
        guard let url = URL.init(string: imgData) else {
            
            if imgData.starts(with: "data:image") {
                let imageData = Data(base64Encoded: imgData, options: Data.Base64DecodingOptions.ignoreUnknownCharacters)!
                    let uiImage = UIImage(data: imageData)!
                
                    let uuid = NSUUID().uuidString
                    
                    let req = SendMessageToWXReq()
                    req.bText = false
                    
                    let imageObject =  WXImageObject()
                    let thumbImage = self.resizeImage(uiImage, newWidth: 144)
                    imageObject.imageData = uiImage.pngData()!;
                    
                    
                    let mediaMessage = WXMediaMessage()
                    mediaMessage.setThumbImage(uiImage)
                    mediaMessage.mediaObject =  imageObject
                    
                    let scene:Int = call.getInt("scene")!
                    req.scene = Int32(scene)
                    req.message = mediaMessage;
                    WXApi.send(req)
            }
            return
        }
        download(url, call)
    }
    
    fileprivate func resizeImage(_ image: UIImage, newWidth: CGFloat) -> UIImage {

        let newHeight = image.size.height / image.size.width * newWidth
        UIGraphicsBeginImageContext( CGSize(width: newWidth, height: newHeight) )
        image.draw(in: CGRect(x: 0, y: 0, width: newWidth, height: newHeight))
        let newImage = UIGraphicsGetImageFromCurrentImageContext()
        UIGraphicsEndImageContext()

        return newImage!
    }
}
