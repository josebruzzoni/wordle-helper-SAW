package com.tacs2022.wordlehelper.controller;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.UpdatesListener;
import com.pengrad.telegrambot.model.CallbackQuery;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.model.request.InlineKeyboardButton;
import com.pengrad.telegrambot.model.request.InlineKeyboardMarkup;
import com.pengrad.telegrambot.request.SendMessage;
import com.pengrad.telegrambot.response.SendResponse;
import com.tacs2022.wordlehelper.service.SessionService;
import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.stereotype.Controller;

import java.util.HashMap;
import java.util.Map;

public class TelegramController {
    private Map<Long, String> usernameByChatId = new HashMap<>();
    private Map<Long, String> lastMessageSentByChatId = new HashMap<>();
    private TelegramBot bot;
    private SessionService sessionService;
    public TelegramController(){
        sessionService = new SessionService();
        Dotenv dotenv = Dotenv.configure().load();
        String key = dotenv.get("TELEGRAM_BOT_AUTH_TOKEN");
        bot = new TelegramBot(key);

        bot.setUpdatesListener(updates -> {
            Update update = updates.get(0);
            System.out.println(update);
            CallbackQuery query = update.callbackQuery();

            if(query != null){
                this.handleQuery(query);
            } else {
                this.handleMessage(update.message());
            }

            return UpdatesListener.CONFIRMED_UPDATES_ALL;
        });
    }

    private void handleQuery(CallbackQuery query){
        switch (query.data()){
            case "login":
                this.handleLogin(query.message().chat().id());
                break;
        }
    }

    private void handleLogin(Long chatId){
        SendMessage sendMessage = new SendMessage(chatId, "Ingrese nombre de usuario");
        this.lastMessageSentByChatId.put(chatId, "setUsername");
        this.bot.execute(sendMessage);
    }

    private void handleMessage(Message message){
        if (message != null) {
            long chatId = message.chat().id();

            if (this.lastMessageSentByChatId.containsKey(chatId)) {
                String lastMessage = this.lastMessageSentByChatId.get(chatId);

                switch (lastMessage){
                    case "setUsername":
                        handleUsername(chatId, message);
                        break;
                    case "setPassword":
                        handlePassword(chatId, message);
                        break;
                }
            } else {
                InlineKeyboardButton loginButton = new InlineKeyboardButton("Login").callbackData("login");
                InlineKeyboardButton createUserButton = new InlineKeyboardButton("Sign in").callbackData("signin");
                InlineKeyboardMarkup keyboardMarkup = new InlineKeyboardMarkup(loginButton, createUserButton);
                String buttonMessage = "Seleccionar acción a realizar";
                SendMessage sendMessage = new SendMessage(chatId, buttonMessage).replyMarkup(keyboardMarkup);
                SendResponse sendResponse = bot.execute(sendMessage);
                /*System.out.println(sendResponse.isOk());
                System.out.println(sendResponse.errorCode());
                System.out.println(sendResponse.description());*/
            }
        }
    }

    private void handleUsername(long chatId, Message message){
        this.usernameByChatId.put(chatId, message.text());

        SendMessage sendMessage = new SendMessage(chatId, "Ingrese contraseña");
        this.lastMessageSentByChatId.replace(chatId, "setPassword");
        this.bot.execute(sendMessage);
    }

    private void handlePassword(long chatId, Message message) {
        String username = this.usernameByChatId.get(chatId);
        String password = message.text();

        try {
            String token = this.sessionService.getToken(username, password);

            if (token == null) {
                SendMessage sendMessage = new SendMessage(chatId, "Usuario o contraseña inválido.");
                this.bot.execute(sendMessage);
                this.cleanMaps(chatId);
                this.handleLogin(chatId);
            }
        }catch(Exception e){
            System.out.println(e);
        }
    }

    private void cleanMaps(long chatId){
        this.usernameByChatId.remove(chatId);
        this.lastMessageSentByChatId.remove(chatId);
    }
}
