package com.unfbx.chatgptsteamoutput;

import com.unfbx.chatgpt.OpenAiStreamClient;
import org.springframework.beans.factory.annotation.Value;
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

    @Value("${chatgpt.apiKey}")
    private String apiKey;
    @Value("${chatgpt.apiHost}")
    private String apiHost;

    public static void main(String[] args) {
        SpringApplication.run(ChatgptSteamOutputApplication.class, args);
    }


    @Bean
    public OpenAiStreamClient openAiStreamClient() {
        return OpenAiStreamClient.builder().apiHost(apiHost).apiKey(apiKey).build();
    }

}
