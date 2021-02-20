import Foundation
import Capacitor
import Kingfisher


/**
 * Please read the Capacitor iOS Plugin Development Guide
 * here: https://capacitorjs.com/docs/plugins/ios
 */
@objc(FreshconnectWebChat)
public class FreshconnectWebChat: CAPPlugin {
    
    static var authCall = CAPPluginCall()
    
    static var subscribeMsgCall = CAPPluginCall()
    
    public func load(appid: String, universalLink: String) {
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
        FreshconnectWebChat.authCall = call
    }
    
    public func authResponse(_ resp: BaseResp) {
        
        if let temp = resp as? SendAuthResp {
            let call = FreshconnectWebChat.authCall
            
            if 0 != resp.errCode {
                print(resp) //
                call.success([
                    "errCode": resp.errCode,
                    "data": resp.errStr
                ])
            } else {
                call.success([
                    "errCode": resp.errCode,
                    "errMsg": resp.errStr,
                    "code": temp.code ?? ""
                ])
            }
        } else if let temp = resp as? WXSubscribeMsgResp {
            
            let call = FreshconnectWebChat.subscribeMsgCall
            
            if 0 != resp.errCode {
                print(resp) //
                call.success([
                    "errCode": resp.errCode,
                    "data": resp.errStr
                ])
            } else {
                call.success([
                    "errCode": resp.errCode,
                    "errMsg": resp.errStr,
                    "openId": temp.openId ?? "",
                    "opetemplateIDnId": temp.templateId,
                    "action": temp.action,
                    "scene": temp.scene,
                    "reserved": temp.reserved
                ])
            }
        }
        
        
    }
    
    @objc func shareText(_ call: CAPPluginCall) {

        let req = SendMessageToWXReq()
        req.bText = true
        req.text = call.getString("text")!
        let scene:Int = call.getInt("scene")!
        req.scene = Int32(scene)
        WXApi.send(req)
    }
    
    @objc func downloadImg(_ call: CAPPluginCall) {
        
        let imgData = call.getString("imgData")!

        if !imgData.starts(with: "data:image"){

            let url = URL.init(string: imgData)!
            KingfisherManager.shared.retrieveImage(with: url) { result in
                switch result {
                case .success(let value):
                    
                    let cgImage = value.image.cgImage
                    let uiImage = UIImage.init(cgImage: cgImage!)
                    let imageData:Data =  uiImage.pngData()!
                    let base64String = imageData.base64EncodedString()
                    
                    call.success([
                        "errCode": 0,
                        "data": base64String
                    ])

                case .failure(let error):
                    print(error) // The error happens
                    call.success([
                        "errCode": error.errorCode,
                        "data": error.errorDescription ?? ""
                    ])
                }

            }
        }else{
            call.success([
                "errCode": 0,
                "data": imgData
            ])
        }
    }
    
    fileprivate func downloadThumbImage(_ url: URL, _ call: CAPPluginCall) {
        KingfisherManager.shared.retrieveImage(with: url) { result in
            switch result {
            case .success(let value):
                
                let cgImage = value.image.cgImage
                let uiImage = UIImage.init(cgImage: cgImage!)
                let imageData:Data =  uiImage.pngData()!
                let base64String = imageData.base64EncodedString()
                
                call.success([
                    "errCode": 0,
                    "data": base64String
                ])
                
                
            case .failure(let error):
                print(error) // The error happens
            }
            
        }
    }
    
    fileprivate func sharePictureForBase64(_ imgData: String, _ call: CAPPluginCall) {
        
        let imageData = Data(base64Encoded: imgData, options: Data.Base64DecodingOptions.ignoreUnknownCharacters)!
        let uiImage = UIImage(data: imageData)!
        
        let req = SendMessageToWXReq()
        req.bText = false
        
        let imageObject =  WXImageObject()
        imageObject.imageData = uiImage.pngData()!;
        
        
        let mediaMessage = WXMediaMessage()
        mediaMessage.setThumbImage(uiImage)
        mediaMessage.mediaObject =  imageObject
        
        let scene:Int = call.getInt("scene")!
        req.scene = Int32(scene)
        req.message = mediaMessage;
        WXApi.send(req)
    }
    
    @objc func sharePicture(_ call: CAPPluginCall) {
        
        let imgData = call.getString("imgData")!

        if !imgData.starts(with: "data:image"){

            let url = URL.init(string: imgData)!
            KingfisherManager.shared.retrieveImage(with: url) { result in
                switch result {
                case .success(let value):

                    let cgImage = value.image.cgImage
                    let uiImage = UIImage.init(cgImage: cgImage!)
                    let imageData:Data =  uiImage.pngData()!
                    let base64String = imageData.base64EncodedString()

                    self.sharePictureForBase64(base64String, call)

                case .failure(let error):
                    print(error) // The error happens
                }

            }
        }else{
            sharePictureForBase64(imgData, call)
        }
    }
    
    @objc func subscribeMessage(_ call: CAPPluginCall) {

        let req = WXSubscribeMsgReq()
        req.templateId = call.getString("templateID")!
        req.reserved = call.getString("reserved")
        let scene:Int = call.getInt("scene")!
        req.scene = UInt32(scene)
        WXApi.send(req)
        
        FreshconnectWebChat.subscribeMsgCall = call
    }
    
    @objc func launchMiniProgram(_ call: CAPPluginCall) {

        let req = WXLaunchMiniProgramReq()
        req.userName = call.getString("userName")!; // 填小程序原始id
        req.path = call.getString("path")!;                  ////拉起小程序页面的可带参路径，不填默认拉起小程序首页，对于小游戏，可以只传入 query 部分，来实现传参效果，如：传入 "?foo=bar"。
        let miniprogramType:Int = call.getInt("miniprogramType")!
        req.miniProgramType = WXMiniProgramType.init(rawValue: UInt(UInt32(miniprogramType)))!;// 可选打开 开发版，体验版和正式版
        WXApi.send(req)
    }
    
    @objc func shareMiniProgram(_ call: CAPPluginCall) {
        
        let imgData = call.getString("thumbData")!

        if !imgData.starts(with: "data:image"){

            let url = URL.init(string: imgData)!
            KingfisherManager.shared.retrieveImage(with: url) { result in
                switch result {
                case .success(let value):
                    
                    let cgImage = value.image.cgImage
                    let uiImage = UIImage.init(cgImage: cgImage!)
                    let imageData:Data =  uiImage.pngData()!
                    let base64String = imageData.base64EncodedString()

                    self.shareMiniProgramForBase64(base64String, call)

                case .failure(let error):
                    print(error) // The error happens
                }

            }
        }else{
            shareMiniProgramForBase64(imgData, call)
        }
    }
    
    fileprivate func shareMiniProgramForBase64(_ imgData: String, _ call: CAPPluginCall) {
        
        let webpageUrl = call.getString("webpageUrl")
        let miniprogramType = call.getInt("miniprogramType")
        let userName = call.getString("userName")
        let path = call.getString("path")
        let title = call.getString("title")
        let description = call.getString("description")

        
        let imageData = Data(base64Encoded: imgData, options: Data.Base64DecodingOptions.ignoreUnknownCharacters)!
        let uiImage = UIImage(data: imageData)!
        
        let imageObject =  WXImageObject()
        let thumbImage = self.resizeImage(uiImage, newWidth: 144)
        imageObject.imageData = thumbImage.pngData()!
        
        
        
        let miniProgramObject =  WXMiniProgramObject()
        miniProgramObject.webpageUrl = webpageUrl!
        miniProgramObject.userName = userName!
        miniProgramObject.path = path
        miniProgramObject.hdImageData = thumbImage.pngData()!
        miniProgramObject.withShareTicket = true
        miniProgramObject.miniProgramType = WXMiniProgramType(rawValue: WXMiniProgramType.RawValue(miniprogramType!))!
        
        let message = WXMediaMessage()
        message.title = title!
        message.description = description ?? ""
        message.thumbData = thumbImage.pngData()!
        message.mediaObject = miniProgramObject;
        
        let req = SendMessageToWXReq()
        req.bText = false
        
        req.scene = Int32(WXSceneSession.rawValue)
        req.message = message;
        WXApi.send(req)
    }
    
    fileprivate func resizeImage(_ image: UIImage, newWidth: CGFloat) -> UIImage {

        let newHeight = image.size.height / image.size.width * newWidth
        UIGraphicsBeginImageContext( CGSize(width: newWidth, height: newHeight) )
        image.draw(in: CGRect(x: 0, y: 0, width: newWidth, height: newHeight))
        let newImage = UIGraphicsGetImageFromCurrentImageContext()
        UIGraphicsEndImageContext()

        return newImage!
    }
    
    
    
    @objc func hasPermission(_ call: CAPPluginCall) {
        
        call.success([
            "errCode": 0
        ])
    }
    
}
