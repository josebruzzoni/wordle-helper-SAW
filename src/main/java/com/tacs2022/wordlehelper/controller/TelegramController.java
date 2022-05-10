package com.tacs2022.wordlehelper.controller;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.UpdatesListener;
import com.pengrad.telegrambot.model.CallbackQuery;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.model.request.InlineKeyboardButton;
import com.pengrad.telegrambot.model.request.InlineKeyboardMarkup;
import com.pengrad.telegrambot.model.request.LoginUrl;
import com.pengrad.telegrambot.request.BaseRequest;
import com.pengrad.telegrambot.request.EditMessageText;
import com.pengrad.telegrambot.request.SendMessage;
import com.pengrad.telegrambot.response.BaseResponse;
import com.pengrad.telegrambot.response.SendResponse;
import com.tacs2022.wordlehelper.domain.tournaments.Tournament;
import com.tacs2022.wordlehelper.domain.tournaments.TournamentStatus;
import com.tacs2022.wordlehelper.domain.user.User;
import com.tacs2022.wordlehelper.exceptions.NotFoundException;
import com.tacs2022.wordlehelper.service.SessionService;
import com.tacs2022.wordlehelper.service.TelegramSecurityService;
import com.tacs2022.wordlehelper.service.TournamentService;
import com.tacs2022.wordlehelper.service.UserService;
import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import java.util.*;
import java.util.stream.Collectors;

@Controller
public class TelegramController {
    private Map<Long, String> usernameByChatId = new HashMap<>();
    private Map<Long, String> lastMessageSentByChatId = new HashMap<>();
    private TelegramBot bot;
    @Autowired
    private TelegramSecurityService telegramSecurityService;
    @Autowired
    private UserService userService;
    @Autowired
    private TournamentService tournamentService;
    private User currentUser;

    public TelegramController(){
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
        Long chatId = query.message().chat().id();
        String data = query.data();

        switch (data){
            case "login":
                this.handleLogin(chatId);
                break;
            case "signin":
                this.handleSignin(chatId);
                break;
            case "tournament":
                System.out.println("query.inlineMessageId(): " + query.inlineMessageId());
                System.out.println("message id: " + query.message().messageId());
                this.handleTournaments(chatId, query.message().messageId().toString());
                break;
            case "dictionary":
                /*this.handleDictionary(chatId);*/
                break;
            case "show-tournaments":
                this.handleShowTournaments(chatId);
                break;
        }

        if(data.startsWith("tournament-")){
            String tournamentId = data.substring("tournament-".length());
            this.handleShowTournament(chatId, tournamentId);
        } else if(data.startsWith("join-tournament-")){
            String tournamentId = data.substring("join-tournament-".length());
            this.handleJoinTournament(chatId, tournamentId);
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

    private void handleTournaments(Long chatId, String messageId){
        InlineKeyboardButton createTournamentButton = new InlineKeyboardButton("Ver torneos").callbackData("show-tournaments");
        InlineKeyboardMarkup keyboardMarkup = new InlineKeyboardMarkup(createTournamentButton);
        String buttonMessage = "Seleccione la acción a realizar";
        /*EditMessageText editMessageText = new EditMessageText(messageId, buttonMessage).replyMarkup(keyboardMarkup);
        System.out.println("parameters" + editMessageText.getParameters());
        this.executeMessage(editMessageText);*/
        SendMessage sendMessage = new SendMessage(chatId, buttonMessage).replyMarkup(keyboardMarkup);
        this.executeMessage(sendMessage);
    }

    private void handleShowTournaments(Long chatId){
        List<Tournament> tournaments = this.tournamentService.findAll();
        InlineKeyboardMarkup keyboardMarkup = new InlineKeyboardMarkup();

        tournaments.forEach(tournament -> {
            // TODO: Traducir los lenguajes.
            String label = String.format("%s | %s to %s | %s | %s", tournament.getName(), tournament.getStartDate(),
                    tournament.getEndDate(), tournament.getLanguages(), this.capitalize(tournament.getVisibility().toString()));
            String callbackData = String.format("tournament-%s", tournament.getId());
            InlineKeyboardButton tournamentButton = new InlineKeyboardButton(label).callbackData(callbackData);
            keyboardMarkup.addRow(tournamentButton);
        });

        String buttonsMessage = "Clickee un torneo para ver opciones";
        this.sendMessageAndExecute(chatId, buttonsMessage, keyboardMarkup);
    }

    public void handleShowTournament(long chatId, String tournamentId){
        Long tournamentIdCasted = Long.parseLong(tournamentId);

        Tournament tournament = this.tournamentService.findById(tournamentIdCasted);
        List<User> allParticipants = tournament.getParticipants();
        String participants = "";

        if(!allParticipants.isEmpty()) {
            List<String> allParticipantsUsernames = allParticipants.stream().map(User::getUsername).collect(Collectors.toList());
            participants = String.join(",", allParticipantsUsernames);
        }

        String message = String.format("Name: %s\nFrom: %s\nTo: %s\nVisibility: %s\nLanguages: %s\nOwner: %s\nParticipants: %s\n",
                tournament.getName(), tournament.getStartDate(), tournament.getEndDate(), this.capitalize(tournament.getVisibility().toString()), tournament.getLanguages(),
                tournament.getOwner().getUsername(), participants);

        InlineKeyboardMarkup keyboardMarkup = null;

        if(tournament.getStatus() == TournamentStatus.NOTSTARTED) {
            String data = String.format("join-tournament-%s", tournament.getId());
            InlineKeyboardButton tournamentButton = new InlineKeyboardButton("Join").callbackData(data);
            keyboardMarkup = new InlineKeyboardMarkup(tournamentButton);

        }

        this.sendMessageAndExecute(chatId, message, keyboardMarkup);
    }

    private void handleJoinTournament(long chatId, String tournamentId) {
        Long tournamentIdCasted = Long.parseLong(tournamentId);
        Tournament tournament = this.tournamentService.findById(tournamentIdCasted);

        this.tournamentService.addParticipant(tournament.getId(), this.currentUser, this.currentUser);
        this.sendMessageAndExecute(chatId, "Joined successfuly", null);
    }

    private String capitalize(String str) {
        if(str == null || str.isEmpty()) {
            return str;
        }

        String strLowerCase = str.toLowerCase();

        return strLowerCase.substring(0, 1).toUpperCase() + strLowerCase.substring(1);
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
            this.currentUser = this.telegramSecurityService.login(username, password, chatId);

            if(this.currentUser == null){
                this.sendMessageAndExecute(chatId, "Usuario o contraseña inválido.", null);
                this.cleanMaps(chatId);
                this.handleLogin(chatId);
            } else {
                this.sendMessageAndExecute(chatId, "Logueado con éxito.", null);
                this.sendKeyboardForLogued(chatId);
            }
        }catch(NotFoundException e){
            this.sendMessageAndExecute(chatId, "Usuario o contraseña inválido.", null);
            this.cleanMaps(chatId);
            this.handleLogin(chatId);
        } catch(Exception e){
            System.out.printf("error en logueo: %s", e);
            this.sendMessageAndExecute(chatId, "Ocurrió un error.", null);
            this.cleanMaps(chatId);
            this.handleLogin(chatId);
        }
    }

    private void handlePasswordForSignin(long chatId, Message message){
        String username = this.usernameByChatId.get(chatId);
        String password = message.text();

        try {
            this.userService.save(username, password);
            SendMessage sendMessage = new SendMessage(chatId, "El usuario ha sido creado correctamente.");
            bot.execute(sendMessage);
            /*this.sendKeyboardForNotLogued(chatId);*/
        } catch(Exception e){
            System.out.println(e);
        }
    }

    private void sendMessageAndExecute(Long chatId, String message, InlineKeyboardMarkup markup){
        SendMessage sendMessage = new SendMessage(chatId, message);

        if(markup != null){
            sendMessage.replyMarkup(markup);
        }

        bot.execute(sendMessage);
    }

    private void cleanMaps(long chatId){
        this.usernameByChatId.remove(chatId);
        this.lastMessageSentByChatId.remove(chatId);
    }

    private void sendKeyboardForNotLogued(long chatId){
        /*LoginUrl loginUrl = new LoginUrl("https://d9a8-83-165-97-130.eu.ngrok.io/sessions");*/
        InlineKeyboardButton loginButton = new InlineKeyboardButton("Login").callbackData("login");/*.loginUrl(loginUrl);*/
        InlineKeyboardButton createUserButton = new InlineKeyboardButton("Sign in").callbackData("signin");
        InlineKeyboardMarkup keyboardMarkup = new InlineKeyboardMarkup(loginButton, createUserButton);
        String buttonMessage = "Seleccionar acción a realizar";
        SendMessage sendMessage = new SendMessage(chatId, buttonMessage).replyMarkup(keyboardMarkup);
        this.executeMessage(sendMessage);
    }

    private void sendKeyboardForLogued(long chatId){
        InlineKeyboardButton tournamentButton = new InlineKeyboardButton("Torneo").callbackData("tournament");
        InlineKeyboardButton dictionaryButton = new InlineKeyboardButton("Diccionario").callbackData("dictionary");
        InlineKeyboardMarkup keyboardMarkup = new InlineKeyboardMarkup(tournamentButton, dictionaryButton);
        String buttonMessage = "Seleccionar categoría de acción a realizar";
        SendMessage sendMessage = new SendMessage(chatId, buttonMessage).replyMarkup(keyboardMarkup);
        this.executeMessage(sendMessage);
    }

    private <T extends BaseRequest<T, R>, R extends BaseResponse> void executeMessage(BaseRequest<T, R> message){
        R response = bot.execute(message);
        System.out.println("Response | is ok: " + response.isOk() + " | error code: " + response.errorCode() + " description: " + response.description());
    }
}
