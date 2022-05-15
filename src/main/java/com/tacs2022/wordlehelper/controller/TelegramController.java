package com.tacs2022.wordlehelper.controller;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.UpdatesListener;
import com.pengrad.telegrambot.model.CallbackQuery;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.model.request.InlineKeyboardButton;
import com.pengrad.telegrambot.model.request.InlineKeyboardMarkup;
import com.pengrad.telegrambot.request.BaseRequest;
import com.pengrad.telegrambot.request.SendMessage;
import com.pengrad.telegrambot.response.BaseResponse;
import com.tacs2022.wordlehelper.domain.Language;
import com.tacs2022.wordlehelper.domain.tournaments.Tournament;
import com.tacs2022.wordlehelper.domain.tournaments.TournamentStatus;
import com.tacs2022.wordlehelper.domain.tournaments.Visibility;
import com.tacs2022.wordlehelper.domain.user.User;
import com.tacs2022.wordlehelper.dtos.tournaments.NewTournamentDto;
import com.tacs2022.wordlehelper.exceptions.NotFoundException;
import com.tacs2022.wordlehelper.service.TelegramSecurityService;
import com.tacs2022.wordlehelper.service.TournamentService;
import com.tacs2022.wordlehelper.service.UserService;
import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Controller
public class TelegramController {
    private Map<Long, String> usernameByChatId = new HashMap<>();
    private Map<Long, String> lastMessageSentByChatId = new HashMap<>();
    private Map<Long, NewTournamentDto> tournamentBeingCreatedByChatId = new HashMap<>();
    private TelegramBot bot;
    @Autowired
    private TelegramSecurityService telegramSecurityService;
    @Autowired
    private UserService userService;
    @Autowired
    private TournamentService tournamentService;
    private User currentUser;
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

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
                this.handleTournaments(chatId);
                break;
            case "dictionary":
                /*this.handleDictionary(chatId);*/
                break;
            case "show-tournaments":
                this.handleShowTournaments(chatId);
                break;
            case "create-tournament":
                this.handleCreateTournament(chatId);
                break;
            case "public-tournament":
                this.handlePublicTournament(chatId);
                break;
            case "private-tournament":
                this.handlePrivateTournament(chatId);
                break;
            case "confirm-tournament":
                this.handleConfirmTournament(chatId);
                break;
            case "cancel-tournament":
                this.handleCancelTournament(chatId);
                break;
            case "english-language":
                this.handleEnglishLanguage(chatId);
                break;
            case "spanish-language":
                this.handleSpanishLanguage(chatId);
                break;
            case "english-spanish-language":
                this.handleEnglishAndSpanishLanguage(chatId);
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

    // Begin handle query methods

    private void handleLogin(Long chatId){
        SendMessage sendMessage = new SendMessage(chatId, "What's your username?");
        this.lastMessageSentByChatId.put(chatId, "setUsernameForLogin");
        this.bot.execute(sendMessage);
    }

    private void handleSignin(Long chatId){
        SendMessage sendMessage = new SendMessage(chatId, "What will be your username?");
        this.lastMessageSentByChatId.put(chatId, "setUsernameForSignin");
        this.bot.execute(sendMessage);
    }

    private void handleTournaments(Long chatId){
        InlineKeyboardButton showTournamentsButton = new InlineKeyboardButton("Show tournaments").callbackData("show-tournaments");
        InlineKeyboardButton createTournamentButton = new InlineKeyboardButton("Create tournament").callbackData("create-tournament");
        InlineKeyboardMarkup keyboardMarkup = new InlineKeyboardMarkup(showTournamentsButton, createTournamentButton);
        String buttonMessage = "Select action to perform";
        SendMessage sendMessage = new SendMessage(chatId, buttonMessage).replyMarkup(keyboardMarkup);
        this.executeMessage(sendMessage);
    }

    private void handleShowTournaments(Long chatId){
        List<Tournament> tournaments = this.tournamentService.findAll();
        InlineKeyboardMarkup keyboardMarkup = new InlineKeyboardMarkup();

        tournaments.forEach(tournament -> {
            String label = tournament.getName();
            String callbackData = String.format("tournament-%s", tournament.getId());
            InlineKeyboardButton tournamentButton = new InlineKeyboardButton(label).callbackData(callbackData);
            keyboardMarkup.addRow(tournamentButton);
        });

        String buttonsMessage = "Click a tournament to show options";
        this.sendMessageAndExecute(chatId, buttonsMessage, keyboardMarkup);
    }

    private void handleCreateTournament(Long chatId){
        this.sendMessageAndExecute(chatId, "What will be the name of the tournament?", null);

        NewTournamentDto tournament = new NewTournamentDto();

        this.tournamentBeingCreatedByChatId.put(chatId, tournament);
        this.lastMessageSentByChatId.put(chatId, "setNameForTournament");
    }

    private void handlePublicTournament(Long chatId){
        handleTournamentVisibility(chatId, Visibility.PUBLIC);
    }
    private void handlePrivateTournament(Long chatId) {
        handleTournamentVisibility(chatId, Visibility.PRIVATE);
    }

    private void handleTournamentVisibility(Long chatId, Visibility visibility){
        NewTournamentDto tournamentInProcess = this.tournamentBeingCreatedByChatId.get(chatId);

        tournamentInProcess.setVisibility(visibility);

        InlineKeyboardButton englishButton = new InlineKeyboardButton("English").callbackData("english-language");
        InlineKeyboardButton spanishButton = new InlineKeyboardButton("Spanish").callbackData("spanish-language");
        InlineKeyboardButton englishAndSpanishButton = new InlineKeyboardButton("English & Spanish").callbackData("english-spanish-language");
        InlineKeyboardMarkup keyboardMarkup = new InlineKeyboardMarkup(englishButton, spanishButton, englishAndSpanishButton);

        this.sendMessageAndExecute(chatId, "Fine. Please select your tournament's languages", keyboardMarkup);
    }

    private void handleEnglishLanguage(Long chatId){
        this.handleTournamentLanguage(chatId, new ArrayList<>(Collections.singleton(Language.EN)));
    }

    private void handleSpanishLanguage(Long chatId){
        this.handleTournamentLanguage(chatId, new ArrayList<>(Collections.singleton(Language.ES)));
    }

    private void handleEnglishAndSpanishLanguage(Long chatId){
        this.handleTournamentLanguage(chatId, new ArrayList<>(Arrays.asList(Language.EN, Language.ES)));
    }

    private void handleTournamentLanguage(Long chatId, List<Language> languages){
        NewTournamentDto tournamentInProcess = this.tournamentBeingCreatedByChatId.get(chatId);

        tournamentInProcess.setLanguages(languages);

        InlineKeyboardButton yesButton = new InlineKeyboardButton("Confirm").callbackData("confirm-tournament");
        InlineKeyboardButton noButton = new InlineKeyboardButton("Cancel").callbackData("cancel-tournament");
        InlineKeyboardMarkup keyboardMarkup = new InlineKeyboardMarkup(yesButton, noButton);

        String messageText = String.format("Allright. This will be your tournament:\nName: %s\nStart date: %s\nEnd date: %s\nVisibility: %s\nLanguages: %s\n\n Is everything ok?",
                tournamentInProcess.getName(), tournamentInProcess.getStartDate().format(this.formatter),
                tournamentInProcess.getEndDate().format(this.formatter), this.capitalize(tournamentInProcess.getVisibility().toString()),
                tournamentInProcess.getLanguages().stream().map(Language::getLanguage).collect(Collectors.joining(", ")));

        this.lastMessageSentByChatId.replace(chatId, "confirmTournament");
        this.sendMessageAndExecute(chatId, messageText, keyboardMarkup);
    }

    private void handleConfirmTournament(Long chatId){
        User owner = this.telegramSecurityService.getUserFromToken(chatId);
        NewTournamentDto tournament = this.tournamentBeingCreatedByChatId.get(chatId);
        Tournament newTournament = new Tournament(tournament, owner);

        this.tournamentService.save(newTournament);

        this.sendSimpleMessageAndExecute(chatId, "Tournament created succesfuly!");
        this.sendKeyboardForLogued(chatId);
    }

    private void handleCancelTournament(Long chatId){
        this.tournamentBeingCreatedByChatId.remove(chatId);
        this.sendKeyboardForLogued(chatId);
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

    // End handle query methods

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
                String text = message.text();

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
                        handlePasswordForSignin(chatId, text);
                        break;
                    case "setNameForTournament":
                        handleNameForTournament(chatId, text);
                        break;
                    case "setStartDateForTournament":
                        handleStartDateForTournament(chatId, text);
                        break;
                    case "setEndDateForTournament":
                        handleEndDateForTournament(chatId, text);
                        break;
                }
            } else if (Objects.equals(message.text(), "/start")){
                this.sendKeyboardForNotLogued(chatId);
            }
        }
    }

    // Begin handle message methods

    private void handleUsernameForLogin(long chatId, Message message){
        this.usernameByChatId.put(chatId, message.text());

        SendMessage sendMessage = new SendMessage(chatId, "What is your password?");
        this.lastMessageSentByChatId.replace(chatId, "setPasswordForLogin");
        this.bot.execute(sendMessage);
    }

    private void handleUsernameForSignin(long chatId, Message message){
        this.usernameByChatId.put(chatId, message.text());

        SendMessage sendMessage = new SendMessage(chatId, "What will be your password?");
        this.lastMessageSentByChatId.replace(chatId, "setPasswordForSignin");
        this.bot.execute(sendMessage);
    }

    private void handlePasswordForLogin(long chatId, Message message) {
        String username = this.usernameByChatId.get(chatId);
        String password = message.text();

        try {
            this.currentUser = this.telegramSecurityService.login(username, password, chatId);

            if(this.currentUser == null){
                this.sendMessageAndExecute(chatId, "Invalid user or password", null);
                this.cleanMaps(chatId);
                this.handleLogin(chatId);
            } else {
                this.sendMessageAndExecute(chatId, "Logged successfuly", null);
                this.sendKeyboardForLogued(chatId);
            }
        }catch(NotFoundException e){
            this.sendMessageAndExecute(chatId, "Invalid user or password", null);
            this.cleanMaps(chatId);
            this.handleLogin(chatId);
        } catch(Exception e){
            System.out.printf("error en logueo: %s", e);
            this.sendMessageAndExecute(chatId, "An error occurred", null);
            this.cleanMaps(chatId);
            this.handleLogin(chatId);
        }
    }

    private void handlePasswordForSignin(long chatId, String password){
        String username = this.usernameByChatId.get(chatId);

        try {
            this.userService.save(username, password);
            SendMessage sendMessage = new SendMessage(chatId, "User has been created successfuly");
            bot.execute(sendMessage);
            this.sendKeyboardForNotLogued(chatId);
        } catch(Exception e){
            System.out.println(e);
        }
    }

    private void handleNameForTournament(long chatId, String name){
        NewTournamentDto tournamentInProcess = this.tournamentBeingCreatedByChatId.get(chatId);
        tournamentInProcess.setName(name);

        this.lastMessageSentByChatId.replace(chatId, "setStartDateForTournament");
        this.sendMessageAndExecute(chatId, "Right. Now please send me the start date of the tournament in this format: 20/05/2022", null);
    }

    private void handleStartDateForTournament(long chatId, String startDate){
        NewTournamentDto tournamentInProcess = this.tournamentBeingCreatedByChatId.get(chatId);

        try {
            LocalDate localDate = LocalDate.parse(startDate, this.formatter);
            tournamentInProcess.setStartDate(localDate);
            this.lastMessageSentByChatId.replace(chatId, "setEndDateForTournament");
            this.sendSimpleMessageAndExecute(chatId, "Great. Now please send me the end date of the tournament in this format: 20/05/2022");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void handleEndDateForTournament(long chatId, String endDate){
        NewTournamentDto tournamentInProcess = this.tournamentBeingCreatedByChatId.get(chatId);

        try {
            LocalDate localDate = LocalDate.parse(endDate, this.formatter);
            tournamentInProcess.setEndDate(localDate);
            this.lastMessageSentByChatId.replace(chatId, "setVisibilityForTournament");
            InlineKeyboardButton publicButton = new InlineKeyboardButton("Public").callbackData("public-tournament");
            InlineKeyboardButton privateButton = new InlineKeyboardButton("Private").callbackData("private-tournament");
            InlineKeyboardMarkup keyboardMarkup = new InlineKeyboardMarkup(publicButton, privateButton);
            this.sendMessageAndExecute(chatId, "Perfect. Will it be a public or a private tournament?", keyboardMarkup);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // End handle message methods

    private void sendSimpleMessageAndExecute(Long chatId, String message){
        this.sendMessageAndExecute(chatId, message, null);
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
        InlineKeyboardButton loginButton = new InlineKeyboardButton("Login").callbackData("login");
        InlineKeyboardButton createUserButton = new InlineKeyboardButton("Sign in").callbackData("signin");
        InlineKeyboardMarkup keyboardMarkup = new InlineKeyboardMarkup(loginButton, createUserButton);
        String buttonMessage = "Select action to perform";
        SendMessage sendMessage = new SendMessage(chatId, buttonMessage).replyMarkup(keyboardMarkup);
        this.executeMessage(sendMessage);
    }

    private void sendKeyboardForLogued(long chatId){
        InlineKeyboardButton tournamentButton = new InlineKeyboardButton("Tournament").callbackData("tournament");
        InlineKeyboardButton dictionaryButton = new InlineKeyboardButton("Dictionary").callbackData("dictionary");
        InlineKeyboardMarkup keyboardMarkup = new InlineKeyboardMarkup(tournamentButton, dictionaryButton);
        String buttonMessage = "Select action's category to perform";
        SendMessage sendMessage = new SendMessage(chatId, buttonMessage).replyMarkup(keyboardMarkup);
        this.executeMessage(sendMessage);
    }

    private <T extends BaseRequest<T, R>, R extends BaseResponse> void executeMessage(BaseRequest<T, R> message){
        R response = bot.execute(message);
        System.out.println("Response | is ok: " + response.isOk() + " | error code: " + response.errorCode() + " description: " + response.description());
    }
}
