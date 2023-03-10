package com.unfbx.chatgptsteamoutput.controller;

import com.unfbx.chatgpt.OpenAiStreamClient;
import com.unfbx.chatgpt.entity.chat.Message;
import com.unfbx.chatgptsteamoutput.entity.Chat;
import com.unfbx.chatgptsteamoutput.listener.OpenAIEventSourceListener;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * 描述：
 *
 * @author https:www.unfbx.com
 * @date 2023-03-01
 */
@Controller
@Slf4j
public class TestController {

    private final OpenAiStreamClient openAiStreamClient;

    public TestController(OpenAiStreamClient openAiStreamClient) {
        this.openAiStreamClient = openAiStreamClient;
    }

    @GetMapping("/chat")
    @CrossOrigin
    public SseEmitter chat(@RequestParam("message") String message) throws IOException {
        List<Message> list = new ArrayList<>();
        Message build = Message.builder().content(message).role(Message.Role.USER).build();
        list.add(build);
        SseEmitter sseEmitter = new SseEmitter(-1L);
        sseEmitter.send(SseEmitter.event().id("765431").name("连接成功！！！！").data(LocalDateTime.now()).reconnectTime(3000));
        sseEmitter.onCompletion(() -> {
            log.info(LocalDateTime.now() + ", uid#" + "765431" + ", on completion");
        });
        sseEmitter.onTimeout(() -> log.info(LocalDateTime.now() + ", uid#" + "765431" + ", on timeout#" + sseEmitter.getTimeout()));
        sseEmitter.onError(throwable -> log.info(LocalDateTime.now() + ", uid#" + "765431" + ", on error#" + throwable.toString()));
        OpenAIEventSourceListener openAIEventSourceListener = new OpenAIEventSourceListener(sseEmitter);
        openAiStreamClient.streamChatCompletion(list, openAIEventSourceListener);
        return sseEmitter;
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
        openAiStreamClient.streamCompletions("写一句话描述下开心的心情", openAIEventSourceListener);
        return sseEmitter;
    }

    @GetMapping("")
    public String index() {
        return "1.html";
    }

}
