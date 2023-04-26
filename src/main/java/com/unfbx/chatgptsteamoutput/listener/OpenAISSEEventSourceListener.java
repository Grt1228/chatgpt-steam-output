package com.unfbx.chatgptsteamoutput.listener;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.unfbx.chatgpt.entity.chat.ChatCompletionResponse;
import com.unfbx.chatgpt.entity.chat.Message;
import com.unfbx.chatgptsteamoutput.config.LocalCache;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okhttp3.sse.EventSource;
import okhttp3.sse.EventSourceListener;

/**
 * 描述：OpenAIEventSourceListener
 *
 * @author https:www.unfbx.com
 * @date 2023-02-22
 */
@Slf4j
public class OpenAISSEEventSourceListener extends EventSourceListener {

    private long tokens;

    private SseEmitter sseEmitter;

    private String uid;

    private StringBuffer answerBuffer;

    public OpenAISSEEventSourceListener(SseEmitter sseEmitter, String uid) {
        this.sseEmitter = sseEmitter;
        this.uid = uid;
        this.answerBuffer = new StringBuffer();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onOpen(EventSource eventSource, Response response) {
        log.info("OpenAI建立sse连接...");
    }

    /**
     * {@inheritDoc}
     */
    @SneakyThrows
    @Override
    public void onEvent(EventSource eventSource, String id, String type, String data) {
        log.info("OpenAI返回数据：{}", data);
        tokens += 1;
        if (data.equals("[DONE]")) {
            log.info("OpenAI返回数据结束了");
            sseEmitter.send(SseEmitter.event()
                    .id("[TOKENS]")
                    .data("<br/><br/>tokens：" + tokens())
                    .reconnectTime(3000));
            sseEmitter.send(SseEmitter.event()
                    .id("[DONE]")
                    .data("[DONE]")
                    .reconnectTime(3000));
            saveAnswer();
            // 传输完成后自动关闭sse
            sseEmitter.complete();
            return;
        }
        ObjectMapper mapper = new ObjectMapper();
        ChatCompletionResponse completionResponse = mapper.readValue(data, ChatCompletionResponse.class); // 读取Json
        try {
            Message delta = completionResponse.getChoices().get(0).getDelta();
            if (StrUtil.isNotBlank(delta.getContent())) {
                answerBuffer.append(delta.getContent());
            }
            sseEmitter.send(SseEmitter.event()
                    .id(completionResponse.getId())
                    .data(delta)
                    .reconnectTime(3000));
        } catch (Exception e) {
            log.error("sse信息推送失败！");
            eventSource.cancel();
            e.printStackTrace();
        }
    }

    /**
     * 保存AI的回答
     */
    private void saveAnswer() {
        // 从缓存中获取已有问答列表
        String messageContext = (String)LocalCache.CACHE.get("msg" + uid);
        List<Message> messages = new ArrayList<>();
        if (StrUtil.isNotBlank(messageContext)) {
            messages = JSONUtil.toList(messageContext, Message.class);
        }
        // 添加本轮对话AI的回答
        Message currentMessage =
            Message.builder().content(answerBuffer.toString()).role(Message.Role.ASSISTANT).build();
        messages.add(currentMessage);
        // 加入缓存
        LocalCache.CACHE.put("msg" + uid, JSONUtil.toJsonStr(messages), LocalCache.TIMEOUT);
    }

    @Override
    public void onClosed(EventSource eventSource) {
        log.info("流式输出返回值总共{}tokens", tokens() - 2);
        log.info("OpenAI关闭sse连接...");
    }

    @SneakyThrows
    @Override
    public void onFailure(EventSource eventSource, Throwable t, Response response) {
        if (Objects.isNull(response)) {
            return;
        }
        ResponseBody body = response.body();
        if (Objects.nonNull(body)) {
            log.error("OpenAI  sse连接异常data：{}，异常：{}", body.string(), t);
        } else {
            log.error("OpenAI  sse连接异常data：{}，异常：{}", response, t);
        }
        eventSource.cancel();
    }

    /**
     * tokens
     * 
     * @return
     */
    public long tokens() {
        return tokens;
    }
}
