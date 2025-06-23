package com.example.gemini_chat;

import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.googleai.GoogleAiGeminiChatModel;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class GeminiChatApplication {

	public static void main(String[] args) {

		SpringApplication.run(GeminiChatApplication.class, args);

	}

}
