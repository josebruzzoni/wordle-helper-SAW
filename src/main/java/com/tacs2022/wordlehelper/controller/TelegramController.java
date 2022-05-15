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
import com.tacs2022.wordlehelper.utils.LanguageUtils;
import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
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
    private List<Command> commands = new ArrayList<>(
            Arrays.asList(
                    new Command("/start", false),
                    new Command("/login", false),
                    new Command("/logout", false)
            )
    );

    List<String> messagesIdsToNotCheckAuthorization = new ArrayList<>(
            Arrays.asList(
                    "setUsernameForLogin",
                    "setPasswordForLogin",
                    "setUsernameForSignin",
                    "setPasswordForSignin"));

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
        String optionId = query.data();

        List<String> optionsToNotCheckIfUserIsLogged = new ArrayList<>(Arrays.asList("login", "logout", "signin"));

        if(!optionsToNotCheckIfUserIsLogged.contains(optionId)){
            boolean isUserLogged = this.checkLoggedUser(chatId);

            if(!isUserLogged){
                return;
            }
        }

        switch (optionId){
            case "login":
                this.handleLogin(chatId);
                break;
            case "logout":
                this.handleLogout(chatId);
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

        if(optionId.startsWith("tournament-")){
            String tournamentId = optionId.substring("tournament-".length());
            this.handleShowTournament(chatId, tournamentId);
        } else if(optionId.startsWith("join-tournament-")){
            String tournamentId = optionId.substring("join-tournament-".length());
            this.handleJoinTournament(chatId, tournamentId);
        }
    }

    // Begin handle query methods

    private void handleLogin(Long chatId){
        this.lastMessageSentByChatId.put(chatId, "setUsernameForLogin");
        this.sendSimpleMessageAndExecute(chatId, "What's your username?");
    }

    private void handleLogout(Long chatId){
        this.lastMessageSentByChatId.remove(chatId);
        this.telegramSecurityService.logout(chatId);
        this.sendSimpleMessageAndExecute(chatId, "Logged out successfuly");
        this.sendKeyboardForNotLogued(chatId);
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
                tournamentInProcess.getEndDate().format(this.formatter), tournamentInProcess.getVisibility().getCapitalized(),
                LanguageUtils.format(tournamentInProcess.getLanguages()));

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
                tournament.getName(), tournament.getStartDate(), tournament.getEndDate(),
                tournament.getVisibility().getCapitalized(), LanguageUtils.format(tournament.getLanguages()),
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

    private void handleMessage(Message message){
        if(message == null){
            return;
        }

        long chatId = message.chat().id();
        String text = message.text();
        System.out.printf("text: %s\n", text);

        List<String> commandNames = this.commands.stream().map(Command::getName).collect(Collectors.toList());
        System.out.println(commandNames);
        boolean isCommand = commandNames.contains(text);

        // if is not a message that belongs to a flow and is not a command, then the bot doesn't understand the message.
        if (!this.lastMessageSentByChatId.containsKey(chatId) && !isCommand) {
            this.sendSimpleMessageAndExecute(chatId, "I don't understand your message");
            this.sendKeyboard(chatId);
            return;
        }


        boolean isAuthorizationRequiredForCommand;
        String lastMessageId = null;

        if(isCommand){
            Command selectedCommand = this.commands.stream().filter(command -> command.getName().equals(text)).findFirst().get();

            isAuthorizationRequiredForCommand = selectedCommand.isNeedsAuthorization();
        } else {
            lastMessageId = this.lastMessageSentByChatId.get(chatId);
            isAuthorizationRequiredForCommand = !messagesIdsToNotCheckAuthorization.contains(lastMessageId);
        }

        if(isAuthorizationRequiredForCommand){
            boolean isUserLogged = this.checkLoggedUser(chatId);

            if(!isUserLogged){
                return;
            }
        }

        if(isCommand) {
            switch(text){
                case "/login":
                    if(this.telegramSecurityService.isUserLogged(chatId)){
                        this.sendSimpleMessageAndExecute(chatId, "User is already logged");
                        return;
                    }

                    this.handleLogin(chatId);
                    break;
                case "/logout":
                    if(!this.telegramSecurityService.isUserLogged(chatId)){
                        this.sendSimpleMessageAndExecute(chatId, "User is not logged");
                        this.sendKeyboard(chatId);
                        return;
                    }

                    this.handleLogout(chatId);
                    break;
                case "/start":
                    this.sendKeyboard(chatId);
                    break;
            }
        } else {
            switch (lastMessageId){
                case "setUsernameForLogin":
                    handleUsernameForLogin(chatId, text);
                    break;
                case "setPasswordForLogin":
                    handlePasswordForLogin(chatId, text);
                    break;
                case "setUsernameForSignin":
                    handleUsernameForSignin(chatId, text);
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
        }
    }

    // Begin handle message methods

    private void handleUsernameForLogin(long chatId, String username){
        this.usernameByChatId.put(chatId, username);

        SendMessage sendMessage = new SendMessage(chatId, "What is your password?");
        this.lastMessageSentByChatId.replace(chatId, "setPasswordForLogin");
        this.bot.execute(sendMessage);
    }

    private void handleUsernameForSignin(long chatId, String username){
        this.usernameByChatId.put(chatId, username);

        SendMessage sendMessage = new SendMessage(chatId, "What will be your password?");
        this.lastMessageSentByChatId.replace(chatId, "setPasswordForSignin");
        this.bot.execute(sendMessage);
    }

    private void handlePasswordForLogin(long chatId, String password) {
        String username = this.usernameByChatId.get(chatId);

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
        } catch(NotFoundException e){
            this.sendMessageAndExecute(chatId, "Invalid user or password", null);
            this.cleanMaps(chatId);
            this.handleLogin(chatId);
        } catch(Exception e){
            System.out.printf("error trying to login: %s", e);
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
        boolean wasHandleDateSuccessful = this.handleDateForTournament(chatId, startDate);

        if(wasHandleDateSuccessful){
            this.lastMessageSentByChatId.replace(chatId, "setEndDateForTournament");
            this.sendSimpleMessageAndExecute(chatId, "Great. Now please send me the end date of the tournament in this format: 20/05/2022");
        }
    }

    private void handleEndDateForTournament(long chatId, String endDate){
        boolean wasHandleDateSuccessful = this.handleDateForTournament(chatId, endDate);

        if(wasHandleDateSuccessful){
            this.lastMessageSentByChatId.replace(chatId, "setVisibilityForTournament");
            InlineKeyboardButton publicButton = new InlineKeyboardButton("Public").callbackData("public-tournament");
            InlineKeyboardButton privateButton = new InlineKeyboardButton("Private").callbackData("private-tournament");
            InlineKeyboardMarkup keyboardMarkup = new InlineKeyboardMarkup(publicButton, privateButton);
            this.sendMessageAndExecute(chatId, "Perfect. Will it be a public or a private tournament?", keyboardMarkup);
        }
    }

    // Returns if parse date was successful
    private boolean handleDateForTournament(long chatId, String date){
        NewTournamentDto tournamentInProcess = this.tournamentBeingCreatedByChatId.get(chatId);

        try {
            LocalDate localDate = LocalDate.parse(date, this.formatter);
            tournamentInProcess.setStartDate(localDate);

            return true;
        } catch (DateTimeParseException e) {
            this.sendSimpleMessageAndExecute(chatId, "Date is not in correct format. Please try again.");

            return false;
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

    private void sendKeyboard(long chatId){
        if(this.telegramSecurityService.isUserLogged(chatId)){
            this.sendKeyboardForLogued(chatId);
        } else {
            this.sendKeyboardForNotLogued(chatId);
        }
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
        InlineKeyboardButton logoutButton = new InlineKeyboardButton("Logout").callbackData("logout");
        InlineKeyboardMarkup keyboardMarkup = new InlineKeyboardMarkup(tournamentButton, dictionaryButton, logoutButton);
        String buttonMessage = "Select action's category to perform";
        SendMessage sendMessage = new SendMessage(chatId, buttonMessage).replyMarkup(keyboardMarkup);
        this.executeMessage(sendMessage);
    }

    private <T extends BaseRequest<T, R>, R extends BaseResponse> void executeMessage(BaseRequest<T, R> message){
        R response = bot.execute(message);
        System.out.println("Response | is ok: " + response.isOk() + " | error code: " + response.errorCode() + " description: " + response.description());
    }

    private boolean checkLoggedUser(Long chatId){
        boolean isUserLogged = this.telegramSecurityService.isUserLogged(chatId);

        if(!isUserLogged){
            this.sendSimpleMessageAndExecute(chatId, "You must be logged to perform this action");
            this.sendKeyboardForNotLogued(chatId);
        }

        return isUserLogged;
    }
}
