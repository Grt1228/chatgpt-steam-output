# 简介
Open AI ChatGPT流式输出。Open AI Stream output. ChatGPT Stream output.
# SSE
主要是基于[SSE](https://developer.mozilla.org/en-US/docs/Web/API/Server-sent_events/Using_server-sent_events#event_stream_format) 实现的（可以百度下这个技术）。也是最近在了解到SSE。OpenAI官网在接受Completions接口的时候，有提到过这个技术。
Completion对象本身有一个stream属性，当stream为true时候Api的Response返回就会变成Http长链接。
具体可以看下文档：https://platform.openai.com/docs/api-reference/completions/create
![实例2](https://user-images.githubusercontent.com/27008803/224496355-76e94a21-a346-4260-93bf-9088bcb31a18.gif)

## 依赖
最新版参考：https://github.com/Grt1228/chatgpt-java
目前是1.0.5版本
```
<dependency>
    <groupId>com.unfbx</groupId>
    <artifactId>chatgpt-java</artifactId>
    <version>1.0.5</version>
</dependency>
```
# 项目部署

## 拉取源代码
```
git clone https://github.com/Grt1228/chatgpt-steam-output
```
## 修改配置
修改application.properties文件
默认8000端口，可以自己修改，修改端口的哈记得将：1.html文件的8000端口也替换掉
```
server.port=8000
chatgpt.apiKey=配置自己的key
chatgpt.apiHost=配置opai的Api Host地址
```
## 运行
运行ChatgptSteamOutputApplication
```
com.unfbx.chatgptsteamoutput.ChatgptSteamOutputApplication
```
运行成功后打开浏览器：

```
http://localhost:8000/
```
能打开此页面表示运行成功
<img width="1080" alt="1" src="https://user-images.githubusercontent.com/27008803/224496424-b75465a0-32fb-491a-934c-c9c524cf5be7.png">


代码其实很简单，小伙伴们可以下载代码来看下。

