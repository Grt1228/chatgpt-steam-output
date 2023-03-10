package com.unfbx.chatgptsteamoutput.entity;

import com.unfbx.chatgpt.entity.chat.Message;
import lombok.Data;

import java.util.List;

@Data
public class Chat {

    private String uid;

    private List<Message> message;
}
