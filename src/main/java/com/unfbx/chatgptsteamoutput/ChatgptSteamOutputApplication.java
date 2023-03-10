package com.unfbx.chatgptsteamoutput;

import com.unfbx.chatgpt.OpenAiStreamClient;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

/**
 * 描述：ChatgptSteamOutputApplication
 *
 * @author https:www.unfbx.com
 * @date 2023-02-28
 */
@SpringBootApplication
public class ChatgptSteamOutputApplication {

    public static void main(String[] args) {
        SpringApplication.run(ChatgptSteamOutputApplication.class, args);
    }


    @Bean
    public OpenAiStreamClient openAiStreamClient() {
        return OpenAiStreamClient.builder().apiHost("https://*************/").apiKey("sk-***************************").build();
    }

}
