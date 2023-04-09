package com.unfbx.chatgptsteamoutput.controller.request;

import lombok.Data;
import org.jetbrains.annotations.NotNull;

/**
 * 描述：
 *
 * @author grt
 * @date 2023-04-08
 */
@Data
public class ChatRequest {
    /**
     * 客户端发送的问题参数
     */
    @NotNull
    private String msg;
}
