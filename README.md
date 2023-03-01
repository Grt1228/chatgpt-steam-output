# chatgpt-steam-output
Open AI ChatGPT流式输出。Open AI Stream output. ChatGPT Stream output.

主要就是做了EventSourceListener的继承实现自定义的com.unfbx.chatgptsteamoutput.listener.OpenAIEventSourceListener
## SSE
主要是基于[SSE](https://developer.mozilla.org/en-US/docs/Web/API/Server-sent_events/Using_server-sent_events#event_stream_format) 实现的（可以百度下这个技术）。也是最近在了解到SSE。OpenAI官网在接受Completions接口的时候，有提到过这个技术。
Completion对象本身有一个stream属性，当stream为true时候Api的Response返回就会变成Http长链接。
具体可以看下文档：https://platform.openai.com/docs/api-reference/completions/create

## 引入依赖
参考：https://github.com/Grt1228/chatgpt-java
```
<dependency>
    <groupId>com.unfbx</groupId>
    <artifactId>chatgpt-java</artifactId>
    <version>1.0.2</version>
</dependency>
```

### 实现自定义的EventSourceListener

com.unfbx.chatgptsteamoutput.listener.OpenAIEventSourceListener并持有一个SseEmitter，通过SseEmitter进行数据的通信


### postman测试
发送请求：**Get http://localhost:8080/test/sse?uid=123**

看下response （需要新版本postman）我发现老版本不支持，就先不上图了，后续补上去

重点关注下header：**Content-Type：text/event-stream**


### **如果想结合前端显示自行百度sse前端相关实现**
