package com.unfbx.chatgptsteamoutput.controller.request;

import lombok.Data;

/**
 * 描述：
 *
 * @author https:www.unfbx.com
 * @sine 2023-04-08
 */
@Data
public class ChatRequest {
    /**
     * 客户端发送的问题参数
     */
    private String msg;
}
