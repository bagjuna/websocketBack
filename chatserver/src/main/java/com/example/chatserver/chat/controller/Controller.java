package com.example.chatserver.chat.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.chatserver.chat.service.ChatService;

@RestController
@RequestMapping("/chat")
public class Controller {
	private final ChatService chatService;

	public Controller(ChatService chatService) {
		this.chatService = chatService;
	}


}
