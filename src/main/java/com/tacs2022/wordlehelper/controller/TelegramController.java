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
import com.tacs2022.wordlehelper.service.TelegramTempService;
import io.github.cdimascio.dotenv.Dotenv;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class TelegramController {
    private Map<Long, String> usernameByChatId = new HashMap<>();
    private Map<Long, String> lastMessageSentByChatId = new HashMap<>();
    private TelegramBot bot;
    private TelegramTempService tempService = new TelegramTempService();
    /*private SessionService sessionService;*/
    public TelegramController(){
        /*sessionService = new SessionService();*/
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
            case "signin":
                this.handleSignin(query.message().chat().id());
                break;
        }
    }

    private void handleLogin(Long chatId){
        SendMessage sendMessage = new SendMessage(chatId, "Ingrese nombre de usuario");
        this.lastMessageSentByChatId.put(chatId, "setUsernameForLogin");
        this.bot.execute(sendMessage);
    }

    private void handleSignin(Long chatId){
        SendMessage sendMessage = new SendMessage(chatId, "Ingrese nombre de usuario");
        this.lastMessageSentByChatId.put(chatId, "setUsernameForSignin");
        this.bot.execute(sendMessage);
    }

    private void handleMessage(Message message){
        if (message != null) {
            long chatId = message.chat().id();

            if (this.lastMessageSentByChatId.containsKey(chatId)) {
                String lastMessage = this.lastMessageSentByChatId.get(chatId);

                switch (lastMessage){
                    case "setUsernameForLogin":
                        handleUsernameForLogin(chatId, message);
                        break;
                    case "setPasswordForLogin":
                        handlePasswordForLogin(chatId, message);
                        break;
                    case "setUsernameForSignin":
                        handleUsernameForSignin(chatId, message);
                        break;
                    case "setPasswordForSignin":
                        handlePasswordForSignin(chatId, message);
                        break;
                }
            } else if (Objects.equals(message.text(), "/start")){
                this.sendKeyboardForNotLogued(chatId);
            }
        }
    }

    private void handleUsernameForLogin(long chatId, Message message){
        this.usernameByChatId.put(chatId, message.text());

        SendMessage sendMessage = new SendMessage(chatId, "Ingrese contraseña");
        this.lastMessageSentByChatId.replace(chatId, "setPasswordForLogin");
        this.bot.execute(sendMessage);
    }

    private void handleUsernameForSignin(long chatId, Message message){
        this.usernameByChatId.put(chatId, message.text());

        SendMessage sendMessage = new SendMessage(chatId, "Ingrese contraseña");
        this.lastMessageSentByChatId.replace(chatId, "setPasswordForSignin");
        this.bot.execute(sendMessage);
    }

    private void handlePasswordForLogin(long chatId, Message message) {
        String username = this.usernameByChatId.get(chatId);
        String password = message.text();

        try {
            /*String token = this.sessionService.getToken(username, password);*/
            String token = this.tempService.getToken(chatId, username, password);

            if (token == null) {
                SendMessage sendMessage = new SendMessage(chatId, "Usuario o contraseña inválido.");
                this.bot.execute(sendMessage);
                this.cleanMaps(chatId);
                this.handleLogin(chatId);
            } else {
                SendMessage sendMessage = new SendMessage(chatId, "Logueado con éxito.");
                this.bot.execute(sendMessage);
                this.sendKeyboardForLogued(chatId);
            }
        }catch(Exception e){
            System.out.println(e);
        }
    }

    private void handlePasswordForSignin(long chatId, Message message){
        String username = this.usernameByChatId.get(chatId);
        String password = message.text();

        try {
            this.tempService.save(username, password);
            SendMessage sendMessage = new SendMessage(chatId, "El usuario ha sido creado correctamente.");
            bot.execute(sendMessage);
            this.sendKeyboardForNotLogued(chatId);
        } catch(Exception e){
            System.out.println(e);
        }
    }

    private void cleanMaps(long chatId){
        this.usernameByChatId.remove(chatId);
        this.lastMessageSentByChatId.remove(chatId);
    }

    private void sendKeyboardForNotLogued(long chatId){
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

    private void sendKeyboardForLogued(long chatId){
        InlineKeyboardButton tournamentButton = new InlineKeyboardButton("Torneo").callbackData("tournament");
        InlineKeyboardButton dictionaryButton = new InlineKeyboardButton("Diccionario").callbackData("dictionary");
        InlineKeyboardMarkup keyboardMarkup = new InlineKeyboardMarkup(tournamentButton, dictionaryButton);
        String buttonMessage = "Seleccionar categoría de acción a realizar";
        SendMessage sendMessage = new SendMessage(chatId, buttonMessage).replyMarkup(keyboardMarkup);
        SendResponse sendResponse = bot.execute(sendMessage);
    }
}
