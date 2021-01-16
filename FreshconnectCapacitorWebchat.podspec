require 'json'

package = JSON.parse(File.read(File.join(__dir__, 'package.json')))

Pod::Spec.new do |s|
  s.name = 'FreshconnectCapacitorWebchat'
  s.version = package['version']
  s.summary = package['description']
  s.license = package['license']
  s.homepage = package['repository']['url']
  s.author = package['author']
  s.source = { :git => package['repository']['url'], :tag => s.version.to_s }
  s.source_files = 'ios/SDKExport/*.h',  'ios/Plugin/**/*.{swift,h,m,c,cc,mm,cpp}'
  s.vendored_libraries  = 'ios/SDKExport/libWeChatSDK.a'
  s.public_header_files = 'ios/WechatKit/*.h', 'ios/SDKExport/*.h'

  s.frameworks = 'SystemConfiguration', 'Security', 'CoreTelephony', 'CFNetwork', 'UIKit'
  s.libraries = 'z', 'c++', 'sqlite3.0'
  s.xcconfig = { 'OTHER_LDFLAGS' => '-ObjC -all_load' }

  s.ios.deployment_target  = '11.0'
  s.dependency 'Capacitor'
  s.dependency 'Kingfisher'
  s.swift_version = '5.1'
end
