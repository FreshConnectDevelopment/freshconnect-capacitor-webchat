# freshconnect-capacitor-webchat

## 本地插件测试
- 运行 sudo npm link

  在需要添加插件的工程运行下面的命令

- 运行 sudo npm link freshconnect-capacitor-webchat
- 运行 sudo npm install freshconnect-capacitor-webchat

## 插件开发注意的事情
- 在android studio中debug的时候会在node_modules中新增xml文件，如果之前已经debug过生成的文件后面会增加数字，当数字增加到3之后debug会出现报错，提示文件名校验失败。需要根据Android studio报错路径删除带数字的文件，删除后需要rebuild一下才会起效。