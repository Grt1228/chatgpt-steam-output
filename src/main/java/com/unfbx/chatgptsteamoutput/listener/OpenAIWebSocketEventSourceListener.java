package com.unfbx.chatgptsteamoutput.listener;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import javax.websocket.Session;

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
 * 描述：OpenAI流式输出Socket接收
 *
 * @author https:www.unfbx.com
 * @date 2023-03-23
 */
@Slf4j
public class OpenAIWebSocketEventSourceListener extends EventSourceListener {

    private Session session;

    private String uid;

    private StringBuffer answerBuffer;

    public OpenAIWebSocketEventSourceListener(Session session, String uid) {
        this.session = session;
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
        if (data.equals("[DONE]")) {
            log.info("OpenAI返回数据结束了");
            session.getBasicRemote().sendText("[DONE]");
            saveAnswer();
            return;
        }
        ObjectMapper mapper = new ObjectMapper();
        ChatCompletionResponse completionResponse = mapper.readValue(data, ChatCompletionResponse.class); // 读取Json
        Message deltaBody = completionResponse.getChoices().get(0).getDelta();
        String delta = mapper.writeValueAsString(deltaBody);
        if (StrUtil.isNotBlank(deltaBody.getContent())) {
            answerBuffer.append(deltaBody.getContent());
        }
        session.getBasicRemote().sendText(delta);
    }

    /**
     * 保存AI的回答
     */
    private void saveAnswer() {
        // 从缓存中获取已有问答列表
        String messageContext = (String)LocalCache.CACHE.get(uid);
        List<Message> messages = new ArrayList<>();
        if (StrUtil.isNotBlank(messageContext)) {
            messages = JSONUtil.toList(messageContext, Message.class);
        }
        log.info("OpenAI返回的全部数据:{}", answerBuffer.toString());
        // 添加本轮对话AI的回答
        Message currentMessage =
            Message.builder().content(answerBuffer.toString()).role(Message.Role.ASSISTANT).build();
        messages.add(currentMessage);
        // 加入缓存
        LocalCache.CACHE.put(uid, JSONUtil.toJsonStr(messages), LocalCache.TIMEOUT);
    }

    @Override
    public void onClosed(EventSource eventSource) {
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
}
