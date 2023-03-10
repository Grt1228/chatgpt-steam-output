package com.unfbx.chatgptsteamoutput.controller;

import cn.hutool.cache.Cache;
import cn.hutool.cache.CacheUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.http.server.HttpServerRequest;
import cn.hutool.json.JSONUtil;
import com.unfbx.chatgpt.OpenAiStreamClient;
import com.unfbx.chatgpt.entity.chat.Message;
import com.unfbx.chatgpt.exception.BaseException;
import com.unfbx.chatgpt.exception.CommonError;
import com.unfbx.chatgptsteamoutput.config.LocalCache;
import com.unfbx.chatgptsteamoutput.listener.OpenAIEventSourceListener;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 描述：
 *
 * @author https:www.unfbx.com
 * @date 2023-03-01
 */
@Controller
@Slf4j
public class ChatController {

    private final OpenAiStreamClient openAiStreamClient;

    public ChatController(OpenAiStreamClient openAiStreamClient) {
        this.openAiStreamClient = openAiStreamClient;
    }

    @GetMapping("/chat")
    @CrossOrigin
    public SseEmitter chat(@RequestParam("message") String msg, @RequestHeader Map<String, String> headers) throws IOException {
        SseEmitter sseEmitter = new SseEmitter(20000l);
        String cookie = headers.get("cookie");
        if (StrUtil.isBlank(cookie)) {
            throw new BaseException(CommonError.SYS_ERROR);
        }
        String messageContext = (String) LocalCache.CACHE.get(cookie);
        List<Message> messages = new ArrayList<>();
        if (StrUtil.isNotBlank(messageContext)) {
            messages = JSONUtil.toList(messageContext, Message.class);
            if (messages.size() >= 10) {
                messages = messages.subList(1, 10);
            }
            Message currentMessage = Message.builder().content(msg).role(Message.Role.USER).build();
            messages.add(currentMessage);
        } else {
            Message currentMessage = Message.builder().content(msg).role(Message.Role.USER).build();
            messages.add(currentMessage);
        }
        sseEmitter.send(SseEmitter.event().id(cookie).name("连接成功！！！！").data(LocalDateTime.now()).reconnectTime(3000));
        sseEmitter.onCompletion(() -> {
            log.info(LocalDateTime.now() + ", uid#" + cookie + ", on completion");
        });
        sseEmitter.onTimeout(() -> log.info(LocalDateTime.now() + ", uid#" + cookie + ", on timeout#" + sseEmitter.getTimeout()));
        sseEmitter.onError(
                throwable -> {
                    try {
                        log.info(LocalDateTime.now() + ", uid#" + "765431" + ", on error#" + throwable.toString());
                        sseEmitter.send(SseEmitter.event().id("765431").name("发生异常！").data(throwable.getMessage()).reconnectTime(3000));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
        );
        OpenAIEventSourceListener openAIEventSourceListener = new OpenAIEventSourceListener(sseEmitter);
        openAiStreamClient.streamChatCompletion(messages, openAIEventSourceListener);
        LocalCache.CACHE.put(cookie, JSONUtil.toJsonStr(messages), LocalCache.TIMEOUT);
        return sseEmitter;
    }

    @GetMapping("")
    public String index() {
        return "1.html";
    }

}
