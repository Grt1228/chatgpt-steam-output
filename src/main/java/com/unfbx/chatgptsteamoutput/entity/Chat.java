package com.unfbx.chatgptsteamoutput.entity;

import com.unfbx.chatgpt.entity.chat.Message;
import lombok.Data;

import java.util.List;
/**
 * 描述：
 *
 * @author https:www.unfbx.com
 * @date 2023-04-10
 */
@Data
public class Chat {

    private String uid;

    private List<Message> message;
}
