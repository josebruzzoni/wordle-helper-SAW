package com.tacs2022.wordlehelper.controller;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.UpdatesListener;
import com.pengrad.telegrambot.request.SendMessage;
import io.github.cdimascio.dotenv.Dotenv;

public class TelegramController {
    public static void Init(){
        Dotenv dotenv = Dotenv.configure().load();
        String key = dotenv.get("TELEGRAM_BOT_AUTH_TOKEN");
        TelegramBot bot = new TelegramBot(key);

        bot.setUpdatesListener(updates -> {
            // ... process updates
            // return id of last processed update or confirm them all
            System.out.println(updates);
            long chatId = updates.get(0).message().chat().id();
            bot.execute(new SendMessage(chatId, "Hello!"));
            return UpdatesListener.CONFIRMED_UPDATES_ALL;
        });
    }
}
