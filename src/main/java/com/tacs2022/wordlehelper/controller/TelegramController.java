package com.tacs2022.wordlehelper.controller;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.UpdatesListener;
import com.pengrad.telegrambot.model.request.InlineKeyboardButton;
import com.pengrad.telegrambot.model.request.InlineKeyboardMarkup;
import com.pengrad.telegrambot.model.request.ParseMode;
import com.pengrad.telegrambot.request.SendMessage;
import com.pengrad.telegrambot.response.SendResponse;
import io.github.cdimascio.dotenv.Dotenv;

public class TelegramController {
    public static void Init(){
        Dotenv dotenv = Dotenv.configure().load();
        String key = dotenv.get("TELEGRAM_BOT_AUTH_TOKEN");
        TelegramBot bot = new TelegramBot(key);

        bot.setUpdatesListener(updates -> {
            System.out.println(updates);
            long chatId = updates.get(0).message().chat().id();
            InlineKeyboardButton loginButton = new InlineKeyboardButton("Loguearse").callbackData("login");
            InlineKeyboardMarkup keyboardMarkup = new InlineKeyboardMarkup(loginButton);
            String message = "Seleccionar acción a realizar";
            SendMessage sendMessage = new SendMessage(chatId, message).replyMarkup(keyboardMarkup);
            SendResponse sendResponse = bot.execute(sendMessage);
            /*System.out.println(sendResponse.isOk());
            System.out.println(sendResponse.errorCode());
            System.out.println(sendResponse.description());*/
            return UpdatesListener.CONFIRMED_UPDATES_ALL;
        });
    }
}
