package com.unfbx.chatgptsteamoutput.controller;

import com.unfbx.chatgpt.OpenAiStreamClient;
import com.unfbx.chatgptsteamoutput.listener.OpenAIEventSourceListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.time.LocalDateTime;

/**
 * 描述：
 *
 * @author https:www.unfbx.com
 * @date 2023-03-01
 */
@Controller
public class TestController {

    private final OpenAiStreamClient openAiStreamClient;

    public TestController(OpenAiStreamClient openAiStreamClient) {
        this.openAiStreamClient = openAiStreamClient;
    }

    @GetMapping("/test/sse")
    @CrossOrigin
    public SseEmitter sseEmitter(@RequestParam("uid") String uid) throws IOException {
        SseEmitter sseEmitter = new SseEmitter(-1L);
        sseEmitter.send(SseEmitter.event().id("root").name("连接成功！！！！").data(LocalDateTime.now()).reconnectTime(3000));
        sseEmitter.onCompletion(() -> {
            System.out.println(LocalDateTime.now() + ", uid#" + uid + ", on completion");
        });
        sseEmitter.onTimeout(() -> System.out.println(LocalDateTime.now() + ", uid#" + uid + ", on timeout#" + sseEmitter.getTimeout()));
        sseEmitter.onError(throwable -> System.out.println(LocalDateTime.now() + ", uid#" + uid + ", on error#" + throwable.toString()));
        OpenAIEventSourceListener openAIEventSourceListener = new OpenAIEventSourceListener(sseEmitter);
        openAiStreamClient.streamCompletions("写一句话描述下开心的心情",openAIEventSourceListener);
        return sseEmitter;
    }
}
