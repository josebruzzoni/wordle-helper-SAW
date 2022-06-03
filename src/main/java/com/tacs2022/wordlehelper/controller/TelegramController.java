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
import com.tacs2022.wordlehelper.domain.dictionary.Word;
import com.tacs2022.wordlehelper.domain.tournaments.Tournament;
import com.tacs2022.wordlehelper.domain.tournaments.TournamentStatus;
import com.tacs2022.wordlehelper.domain.tournaments.Visibility;
import com.tacs2022.wordlehelper.domain.user.User;
import com.tacs2022.wordlehelper.dtos.tournaments.NewTournamentDto;
import com.tacs2022.wordlehelper.dtos.user.NewResultDto;
import com.tacs2022.wordlehelper.exceptions.NotFoundException;
import com.tacs2022.wordlehelper.service.DictionaryService;
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
    private TelegramBot bot;

    @Autowired
    private TelegramSecurityService telegramSecurityService;
    @Autowired
    private UserService userService;
    @Autowired
    private TournamentService tournamentService;
    @Autowired
    private DictionaryService dictionaryService;

    private Map<String, String> usernameByChatId = new HashMap<>();
    private Map<String, String> lastMessageSentByChatId = new HashMap<>();
    private Map<String, NewTournamentDto> tournamentBeingCreatedByChatId = new HashMap<>();
    private Map<String, NewResultDto> resultsBeingCreatedByChatId = new HashMap<>();

    private User currentUser;
    private Language currentDictionaryLanguage;
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
        String chatId = query.message().chat().id().toString();
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
                this.handleDictionary(chatId);
                break;
            case "setEnglishDictionary":
                this.handleEnglishDictionary(chatId);
                break;
            case "setSpanishDictionary":
                this.handleSpanishDictionary(chatId);
                break;
            case "showPublicTournaments":
                this.handleShowPublicTournaments(chatId);
                break;
            case "showMyTournaments":
                this.handleShowMyTournaments(chatId);
                break;
            case "createTournament":
                this.handleCreateTournament(chatId);
                break;
            case "submitResults":
                this.handleSubmitResults(chatId);
                break;
            case "publicTournament":
                this.handlePublicTournament(chatId);
                break;
            case "privateTournament":
                this.handlePrivateTournament(chatId);
                break;
            case "confirmTournament":
                this.handleConfirmTournament(chatId);
                break;
            case "cancelTournament":
                this.handleCancelTournament(chatId);
                break;
            case "englishLanguageTournament":
                this.handleTournamentEnglishLanguage(chatId);
                break;
            case "spanishLanguageTournament":
                this.handleTournamentSpanishLanguage(chatId);
                break;
            case "englishSpanishLanguageTournament":
                this.handleTournamentEnglishAndSpanishLanguage(chatId);
                break;
            case "englishLanguageResult":
                this.handleEnglishLanguageResult(chatId);
                break;
            case "spanishLanguageResult":
                this.handleSpanishLanguageResult(chatId);
                break;
            case "confirmResult":
                this.handleConfirmResult(chatId);
                break;
            case "cancelResult":
                this.handleCancelResult(chatId);
                break;
            case "confirmSearchAgain":
                this.handleConfirmSearchAgain(chatId);
                break;
            case "cancelSearchAgain":
                this.handleCancelSearchAgain(chatId);
                break;
        }

        if(optionId.startsWith("tournament-")){
            String tournamentId = optionId.substring("tournament-".length());
            this.handleShowTournament(chatId, tournamentId);
        } else if(optionId.startsWith("joinTournament-")){
            String tournamentId = optionId.substring("joinTournament-".length());
            this.handleJoinTournament(chatId, tournamentId);
        } else if(optionId.startsWith("attempts-")){
            String attempts = optionId.substring("attempts-".length());
            this.handleAttempts(chatId, attempts);
        }
    }

    // Begin handle query methods

    private void handleLogin(String chatId){
        this.lastMessageSentByChatId.put(chatId, "setUsernameForLogin");
        this.sendSimpleMessageAndExecute(chatId, "What's your username?");
    }

    private void handleLogout(String chatId){
        this.lastMessageSentByChatId.remove(chatId);
        this.telegramSecurityService.logout(chatId);
        this.sendSimpleMessageAndExecute(chatId, "Logged out successfuly");
        this.sendKeyboardForNotLogued(chatId);
    }

    private void handleSignin(String chatId){
        this.lastMessageSentByChatId.put(chatId, "setUsernameForSignin");
        this.sendSimpleMessageAndExecute(chatId, "What will be your username?");
    }

    private void handleTournaments(String chatId){
        InlineKeyboardButton myTournamentsButton = new InlineKeyboardButton("My tournaments").callbackData("showMyTournaments");
        InlineKeyboardButton showTournamentsButton = new InlineKeyboardButton("Public tournaments").callbackData("showPublicTournaments");
        InlineKeyboardButton createTournamentButton = new InlineKeyboardButton("Create tournament").callbackData("createTournament");
        InlineKeyboardButton submitResultsButton = new InlineKeyboardButton("Submit results").callbackData("submitResults");
        InlineKeyboardMarkup keyboardMarkup = new InlineKeyboardMarkup();
        keyboardMarkup.addRow(myTournamentsButton, showTournamentsButton);
        keyboardMarkup.addRow(createTournamentButton, submitResultsButton);
        String messageText = "Select action to perform";

        this.sendMessageAndExecute(chatId, messageText, keyboardMarkup);
    }

    private void handleDictionary(String chatId){
        InlineKeyboardButton englishButton = new InlineKeyboardButton("English").callbackData("setEnglishDictionary");
        InlineKeyboardButton spanishButton = new InlineKeyboardButton("Spanish").callbackData("setSpanishDictionary");
        InlineKeyboardMarkup keyboardMarkup = new InlineKeyboardMarkup(englishButton, spanishButton);
        String messageText = "Select a language";

        this.sendMessageAndExecute(chatId, messageText, keyboardMarkup);
    }

    private void handleEnglishDictionary(String chatId){
        this.currentDictionaryLanguage = Language.EN;
        this.handleLanguageDictionary(chatId);
    }

    private void handleSpanishDictionary(String chatId){
        this.currentDictionaryLanguage = Language.ES;
        this.handleLanguageDictionary(chatId);
    }

    private void handleLanguageDictionary(String chatId){
        String messageText = String.format("Great. Now send me a word to search in %s.", this.currentDictionaryLanguage.getLanguage());

        this.lastMessageSentByChatId.put(chatId, "setDictionaryWord");
        this.sendSimpleMessageAndExecute(chatId, messageText);
    }

    public void handleShowMyTournaments(String chatId){
        List<Tournament> tournaments = this.tournamentService.findTournamentsInWhichUserIsRegistered(this.currentUser);

        this.handleShowTournaments(chatId, tournaments);
    }

    private void handleShowPublicTournaments(String chatId){
        List<Tournament> tournaments = this.tournamentService.findPublicTournaments();

        this.handleShowTournaments(chatId, tournaments);
    }

    public void handleShowTournaments(String chatId,  List<Tournament> tournaments){
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

    private void handleCreateTournament(String chatId){
        this.sendMessageAndExecute(chatId, "What will be the name of the tournament?", null);

        NewTournamentDto tournament = new NewTournamentDto();

        this.tournamentBeingCreatedByChatId.put(chatId, tournament);
        this.lastMessageSentByChatId.put(chatId, "setNameForTournament");
    }

    private void handlePublicTournament(String chatId){
        handleTournamentVisibility(chatId, Visibility.PUBLIC);
    }
    private void handlePrivateTournament(String chatId) {
        handleTournamentVisibility(chatId, Visibility.PRIVATE);
    }

    private void handleTournamentVisibility(String chatId, Visibility visibility){
        NewTournamentDto tournamentInProcess = this.tournamentBeingCreatedByChatId.get(chatId);

        tournamentInProcess.setVisibility(visibility);

        InlineKeyboardButton englishButton = new InlineKeyboardButton("English").callbackData("englishLanguageTournament");
        InlineKeyboardButton spanishButton = new InlineKeyboardButton("Spanish").callbackData("spanishLanguageTournament");
        InlineKeyboardButton englishAndSpanishButton = new InlineKeyboardButton("English & Spanish").callbackData("englishSpanishLanguageTournament");
        InlineKeyboardMarkup keyboardMarkup = new InlineKeyboardMarkup(englishButton, spanishButton, englishAndSpanishButton);

        this.sendMessageAndExecute(chatId, "Fine. Please select your tournament's languages", keyboardMarkup);
    }

    private void handleTournamentEnglishLanguage(String chatId){
        this.handleTournamentLanguage(chatId, new ArrayList<>(Collections.singleton(Language.EN)));
    }

    private void handleTournamentSpanishLanguage(String chatId){
        this.handleTournamentLanguage(chatId, new ArrayList<>(Collections.singleton(Language.ES)));
    }

    private void handleTournamentEnglishAndSpanishLanguage(String chatId){
        this.handleTournamentLanguage(chatId, new ArrayList<>(Arrays.asList(Language.EN, Language.ES)));
    }

    private void handleEnglishLanguageResult(String chatId){
        this.handleResultLanguage(chatId, Language.EN);
    }

    private void handleSpanishLanguageResult(String chatId){
        this.handleResultLanguage(chatId, Language.ES);
    }

    private void handleConfirmResult(String chatId){
        NewResultDto result = this.resultsBeingCreatedByChatId.get(chatId);

        this.userService.addResult(currentUser.getId(), result.fromDto());

        this.sendSimpleMessageAndExecute(chatId, "Result successfuly saved.");
        this.sendKeyboard(chatId);
    }

    private void handleCancelResult(String chatId){
        this.resultsBeingCreatedByChatId.remove(chatId);

        this.sendKeyboard(chatId);
    }

    private void handleResultLanguage(String chatId, Language language){
        NewResultDto resultInProcess = this.resultsBeingCreatedByChatId.get(chatId);

        resultInProcess.setLanguage(language);

        String messageText = String.format("Perfect. This is your result:\n\nAttempts: %s\nLanguage: %s\n\n Is everything ok?",
                resultInProcess.getAttempts().toString(), resultInProcess.getLanguage().getLanguage());

        InlineKeyboardButton confirmButton = new InlineKeyboardButton("Confirm").callbackData("confirmResult");
        InlineKeyboardButton cancelButton = new InlineKeyboardButton("Cancel").callbackData("cancelResult");
        InlineKeyboardMarkup keyboardMarkup = new InlineKeyboardMarkup(confirmButton, cancelButton);

        this.sendMessageAndExecute(chatId, messageText, keyboardMarkup);
    }

    private void handleTournamentLanguage(String chatId, List<Language> languages){
        NewTournamentDto tournamentInProcess = this.tournamentBeingCreatedByChatId.get(chatId);

        tournamentInProcess.setLanguages(languages);

        InlineKeyboardButton confirmButton = new InlineKeyboardButton("Confirm").callbackData("confirmTournament");
        InlineKeyboardButton cancelButton = new InlineKeyboardButton("Cancel").callbackData("cancelTournament");
        InlineKeyboardMarkup keyboardMarkup = new InlineKeyboardMarkup(confirmButton, cancelButton);

        String messageText = String.format("Allright. This will be your tournament:\n\nName: %s\nStart date: %s\nEnd date: %s\nVisibility: %s\nLanguages: %s\n\n Is everything ok?",
                tournamentInProcess.getName(), tournamentInProcess.getStartDate().format(this.formatter),
                tournamentInProcess.getEndDate().format(this.formatter), tournamentInProcess.getVisibility().getCapitalized(),
                LanguageUtils.format(tournamentInProcess.getLanguages()));

        this.sendMessageAndExecute(chatId, messageText, keyboardMarkup);
    }

    private void handleConfirmTournament(String chatId){
        NewTournamentDto tournament = this.tournamentBeingCreatedByChatId.get(chatId);
        Tournament newTournament = tournament.asTournamentWithOwner(this.currentUser);

        this.tournamentService.save(newTournament);

        this.sendSimpleMessageAndExecute(chatId, "Tournament created succesfuly!");
        this.sendKeyboardForLogued(chatId);
    }

    private void handleCancelTournament(String chatId){
        this.tournamentBeingCreatedByChatId.remove(chatId);
        this.sendKeyboardForLogued(chatId);
    }

    public void handleShowTournament(String chatId, String tournamentId){
        Tournament tournament = this.tournamentService.findById(tournamentId);
        List<User> allParticipants = tournament.getParticipants();
        String participants = "";

        if(!allParticipants.isEmpty()) {
            List<String> allParticipantsUsernames = allParticipants.stream().map(User::getUsername).collect(Collectors.toList());
            participants = String.join(", ", allParticipantsUsernames);
        }

        String message = String.format("Name: %s\nFrom: %s\nTo: %s\nVisibility: %s\nLanguages: %s\nOwner: %s\nParticipants: %s\n",
                tournament.getName(), tournament.getStartDate(), tournament.getEndDate(),
                tournament.getVisibility().getCapitalized(), LanguageUtils.format(tournament.getLanguages()),
                tournament.getOwner().getUsername(), participants);

        InlineKeyboardMarkup keyboardMarkup = null;

        if(tournament.getStatus() == TournamentStatus.NOT_STARTED) {
            String data = String.format("joinTournament-%s", tournament.getId());
            InlineKeyboardButton tournamentButton = new InlineKeyboardButton("Join").callbackData(data);
            keyboardMarkup = new InlineKeyboardMarkup(tournamentButton);

        }

        this.sendMessageAndExecute(chatId, message, keyboardMarkup);
    }

    private void handleJoinTournament(String chatId, String tournamentId) {
        Tournament tournament = this.tournamentService.findById(tournamentId);

        this.tournamentService.addParticipant(tournament.getId(), this.currentUser, this.currentUser);
        this.sendMessageAndExecute(chatId, "Joined successfuly", null);
    }

    private void handleAttempts(String chatId, String attemptsStr){
        Integer attempts = Integer.parseInt(attemptsStr);
        NewResultDto results = new NewResultDto();

        results.setAttempts(attempts);
        this.resultsBeingCreatedByChatId.put(chatId, results);

        InlineKeyboardButton englishButton = new InlineKeyboardButton("English").callbackData("englishLanguageResult");
        InlineKeyboardButton spanishButton = new InlineKeyboardButton("Spanish").callbackData("spanishLanguageResult");
        InlineKeyboardMarkup keyboardMarkup = new InlineKeyboardMarkup(englishButton, spanishButton);

        this.sendMessageAndExecute(chatId, "Great. Now please select the language.", keyboardMarkup);
    }

    // End handle query methods

    private void handleMessage(Message message){
        if(message == null){
            return;
        }

        String chatId = message.chat().id().toString();
        String text = message.text();

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
                case "setDictionaryWord":
                    this.handleDictionaryWord(chatId, text);
                    break;

            }
        }
    }

    // Begin handle message methods

    private void handleUsernameForLogin(String chatId, String username){
        this.usernameByChatId.put(chatId, username);
        this.lastMessageSentByChatId.put(chatId, "setPasswordForLogin");
        this.sendSimpleMessageAndExecute(chatId, "What is your password?");
    }

    private void handleUsernameForSignin(String chatId, String username){
        this.usernameByChatId.put(chatId, username);
        this.lastMessageSentByChatId.put(chatId, "setPasswordForSignin");
        this.sendSimpleMessageAndExecute(chatId, "What will be your password?");
    }

    private void handlePasswordForLogin(String chatId, String password) {
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

    private void handlePasswordForSignin(String chatId, String password){
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

    private void handleNameForTournament(String chatId, String name){
        NewTournamentDto tournamentInProcess = this.tournamentBeingCreatedByChatId.get(chatId);
        tournamentInProcess.setName(name);

        this.lastMessageSentByChatId.put(chatId, "setStartDateForTournament");
        this.sendMessageAndExecute(chatId, "Right. Now please send me the start date of the tournament in this format: 20/05/2022", null);
    }

    private void handleStartDateForTournament(String chatId, String startDate){
        LocalDate localDate = this.handleDateForTournament(chatId, startDate);

        if(localDate == null) {
            return;
        }

        NewTournamentDto tournamentInProcess = this.tournamentBeingCreatedByChatId.get(chatId);
        tournamentInProcess.setStartDate(localDate);

        this.lastMessageSentByChatId.put(chatId, "setEndDateForTournament");
        this.sendSimpleMessageAndExecute(chatId, "Great. Now please send me the end date of the tournament in this format: 20/05/2022");
    }

    private void handleEndDateForTournament(String chatId, String endDate){
        LocalDate localDate = this.handleDateForTournament(chatId, endDate);

        if(localDate == null) {
            return;
        }

        NewTournamentDto tournamentInProcess = this.tournamentBeingCreatedByChatId.get(chatId);
        tournamentInProcess.setEndDate(localDate);
        this.lastMessageSentByChatId.put(chatId, "setVisibilityForTournament");
        InlineKeyboardButton publicButton = new InlineKeyboardButton("Public").callbackData("publicTournament");
        InlineKeyboardButton privateButton = new InlineKeyboardButton("Private").callbackData("privateTournament");
        InlineKeyboardMarkup keyboardMarkup = new InlineKeyboardMarkup(publicButton, privateButton);
        this.sendMessageAndExecute(chatId, "Perfect. Will it be a public or a private tournament?", keyboardMarkup);
    }

    // Returns if parse date was successful
    private LocalDate handleDateForTournament(String chatId, String date){
        try {
            LocalDate localDate = LocalDate.parse(date, this.formatter);

            return localDate;
        } catch (DateTimeParseException e) {
            this.sendSimpleMessageAndExecute(chatId, "Date is not in correct format. Please try again.");

            return null;
        }
    }

    private void handleSubmitResults(String chatId){
        List<Integer> attemptsOptions = new ArrayList<>(
                Arrays.asList(
                        1, 2, 3, 4, 5, 6
                )
        );
        InlineKeyboardMarkup keyboardMarkup = new InlineKeyboardMarkup();
        List<InlineKeyboardButton> attemptsOptionsButtons = attemptsOptions.stream().map(attemptOption -> {
            String callbackData = String.format("attempts-%s", attemptOption.toString());

            return new InlineKeyboardButton(attemptOption.toString()).callbackData(callbackData);
        }).collect(Collectors.toList());

        keyboardMarkup.addRow(attemptsOptionsButtons.toArray(InlineKeyboardButton[]::new));

        this.sendMessageAndExecute(chatId, "Select amount of attempts", keyboardMarkup);
    }

    private void handleDictionaryWord(String chatId, String word){
        Word resultWord = this.dictionaryService.findByNameAndLanguage(word, this.currentDictionaryLanguage);

        if(resultWord == null){
            String messageText = String.format("No results found for %s", word);
            this.sendSimpleMessageAndExecute(chatId, messageText);
        } else {
            this.sendSimpleMessageAndExecute(chatId, resultWord.getDefinition());
        }

        InlineKeyboardButton yesButton = new InlineKeyboardButton("Yes").callbackData("confirmSearchAgain");
        InlineKeyboardButton noButton = new InlineKeyboardButton("No").callbackData("cancelSearchAgain");
        InlineKeyboardMarkup keyboardMarkup = new InlineKeyboardMarkup(yesButton, noButton);

        this.lastMessageSentByChatId.remove(chatId);
        this.sendMessageAndExecute(chatId, "Do you want to search again?", keyboardMarkup);
    }

    private void handleConfirmSearchAgain(String chatId){
        this.handleDictionary(chatId);
    }

    private void handleCancelSearchAgain(String chatId){
        this.sendKeyboard(chatId);
    }

    // End handle message methods

    private void sendSimpleMessageAndExecute(String chatId, String message){
        this.sendMessageAndExecute(chatId, message, null);
    }

    private void sendMessageAndExecute(String chatId, String message, InlineKeyboardMarkup markup){
        SendMessage sendMessage = new SendMessage(chatId, message);

        if(markup != null){
            sendMessage.replyMarkup(markup);
        }

        bot.execute(sendMessage);
    }

    private void cleanMaps(String chatId){
        this.usernameByChatId.remove(chatId);
        this.lastMessageSentByChatId.remove(chatId);
    }

    private void sendKeyboard(String chatId){
        if(this.telegramSecurityService.isUserLogged(chatId)){
            this.sendKeyboardForLogued(chatId);
        } else {
            this.sendKeyboardForNotLogued(chatId);
        }
    }

    private void sendKeyboardForNotLogued(String chatId){
        InlineKeyboardButton loginButton = new InlineKeyboardButton("Login").callbackData("login");
        InlineKeyboardButton createUserButton = new InlineKeyboardButton("Sign in").callbackData("signin");
        InlineKeyboardMarkup keyboardMarkup = new InlineKeyboardMarkup(loginButton, createUserButton);
        String messageText = "Select action to perform";

        this.sendMessageAndExecute(chatId, messageText, keyboardMarkup);
    }

    private void sendKeyboardForLogued(String chatId){
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

    private boolean checkLoggedUser(String chatId){
        boolean isUserLogged = this.telegramSecurityService.isUserLogged(chatId);

        if(!isUserLogged){
            this.sendSimpleMessageAndExecute(chatId, "You must be logged to perform this action");
            this.sendKeyboardForNotLogued(chatId);
        }

        return isUserLogged;
    }
}
