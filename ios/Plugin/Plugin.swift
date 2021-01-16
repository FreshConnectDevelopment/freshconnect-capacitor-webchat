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
        
        let appid = ""
        let universalLink = ""
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
    
    @objc func sharePicture(_ call: CAPPluginCall) {
        let imgData = call.getString("imgData")!
        guard let url = URL.init(string: imgData) else {
            return
        }
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
    
    @objc func subscribeMessage(_ call: CAPPluginCall) {
        let uuid = NSUUID().uuidString

        let req = SendMessageToWXReq()
        req.bText = false
        req.text = call.getString("text")!
        let scene:Int = call.getInt("scene")!
        req.scene = Int32(scene)
        WXApi.send(req)
    }
    
    @objc func launchMiniProgram(_ call: CAPPluginCall) {
        let uuid = NSUUID().uuidString

        let req = SendMessageToWXReq()
        req.bText = false
        req.text = call.getString("text")!
        let scene:Int = call.getInt("scene")!
        req.scene = Int32(scene)
        WXApi.send(req)
    }
    
    @objc func shareMiniProgram(_ call: CAPPluginCall) {
        let uuid = NSUUID().uuidString

        let req = SendMessageToWXReq()
        req.bText = false
        req.text = call.getString("text")!
        let scene:Int = call.getInt("scene")!
        req.scene = Int32(scene)
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
}
