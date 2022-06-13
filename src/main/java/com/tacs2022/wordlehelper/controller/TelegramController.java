package com.tacs2022.wordlehelper.controller;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.UpdatesListener;
import com.pengrad.telegrambot.model.CallbackQuery;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.model.request.*;
import com.pengrad.telegrambot.request.BaseRequest;
import com.pengrad.telegrambot.request.EditMessageReplyMarkup;
import com.pengrad.telegrambot.request.SendMessage;
import com.pengrad.telegrambot.response.BaseResponse;
import com.tacs2022.wordlehelper.domain.Language;
import com.tacs2022.wordlehelper.domain.dictionary.Word;
import com.tacs2022.wordlehelper.domain.play.TempHelperInfo;
import com.tacs2022.wordlehelper.domain.play.WordPlay;
import com.tacs2022.wordlehelper.domain.tournaments.Tournament;
import com.tacs2022.wordlehelper.domain.tournaments.TournamentStatus;
import com.tacs2022.wordlehelper.domain.tournaments.Visibility;
import com.tacs2022.wordlehelper.domain.user.Result;
import com.tacs2022.wordlehelper.domain.user.User;
import com.tacs2022.wordlehelper.dtos.tournaments.NewTournamentDto;
import com.tacs2022.wordlehelper.exceptions.ExistingUserException;
import com.tacs2022.wordlehelper.exceptions.LetterMismatchException;
import com.tacs2022.wordlehelper.exceptions.NotFoundException;
import com.tacs2022.wordlehelper.service.*;
import com.tacs2022.wordlehelper.utils.LanguageUtils;
import io.github.cdimascio.dotenv.Dotenv;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.lang.Strings;
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
    @Autowired
    private HelperService helperService;

    private Map<String, String> usernameByChatId = new HashMap<>();
    private Map<String, String> lastMessageSentByChatId = new HashMap<>();
    private Map<String, NewTournamentDto> tournamentBeingCreatedByChatId = new HashMap<>();
    private Map<String, Result> resultsBeingCreatedByChatId = new HashMap<>();

    private User currentUser;
    private Language currentDictionaryLanguage;
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private List<Command> commands = new ArrayList<>(
            Arrays.asList(
                    new Command("/start", false),
                    new Command("/login", false),
                    new Command("/logout", false),
                    new Command("/tournament", true),
                    new Command("/dictionary", true),
                    new Command("/show_public_tournaments", true),
                    new Command("/show_my_tournaments", true),
                    new Command("/create_tournament", true),
                    new Command("/submit_results", true)
            )
    );

    List<String> messagesIdsToNotCheckAuthorization = new ArrayList<>(
            Arrays.asList(
                    "setUsernameForLogin",
                    "setPasswordForLogin",
                    "setUsernameForSignin",
                    "setPasswordForSignin"));
    private HashMap<String, List<String>> colourByCharacterForLastWordByChatId = new HashMap<>();
    private HashMap<String, TempHelperInfo> tempHelperInfoByChatId = new HashMap<>();

    private HashMap<String, Integer> helperWordsSentByChatId = new HashMap<>();
    private String whiteCircleEmoji = "\u26AA";
    private String yellowCircleEmoji = "\uD83D\uDFE1";
    private String greenCircleEmoji = "\uD83D\uDFE2";
    private static final char whiteCharacter = 'W';
    private static final char yellowCharacter = 'Y';
    private static final char greenCharacter = 'G';

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
            case "helper":
                this.handleHelper(chatId);
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
            case "keyboardForLogued":
                this.sendKeyboardForLogued(chatId);
                break;
            case "englishLanguageHelper":
                this.handleEnglishLanguageHelper(chatId);
                break;
            case "spanishLanguageHelper":
                this.handleSpanishLanguageHelper(chatId);
                break;
            case "writeNextWord":
                this.handleWriteNextWord(chatId);
                break;
            case "finishHelper":
                this.handleFinishHelper(chatId);
                break;
        }

        if(optionId.startsWith("showMyTournament-")){
            String tournamentId = optionId.substring("showMyTournament-".length());
            this.handleShowTournament(chatId, tournamentId, "My");
        } else if(optionId.startsWith("showPublicTournament-")) {
            String tournamentId = optionId.substring("showPublicTournament-".length());
            this.handleShowTournament(chatId, tournamentId, "Public");
        } else if(optionId.startsWith("joinTournament-")){
            String tournamentId = optionId.substring("joinTournament-".length());
            this.handleJoinTournament(chatId, tournamentId);
        } else if(optionId.startsWith("attempts-")){
            String attempts = optionId.substring("attempts-".length());
            this.handleAttempts(chatId, attempts);
        } else if(optionId.startsWith("changeCharacter-")){
            String characterPosition = optionId.substring("changeCharacter-".length());
            int messageId = query.message().messageId();
            this.handleCharacter(chatId, characterPosition, messageId);
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
        InlineKeyboardButton backwardsButton = new InlineKeyboardButton("Backwards").callbackData("keyboardForLogued");
        InlineKeyboardMarkup keyboardMarkup = new InlineKeyboardMarkup();
        keyboardMarkup.addRow(myTournamentsButton, showTournamentsButton);
        keyboardMarkup.addRow(createTournamentButton, submitResultsButton);
        keyboardMarkup.addRow(backwardsButton);
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

    private void handleHelper(String chatId){
        InlineKeyboardButton englishButton = new InlineKeyboardButton("English").callbackData("englishLanguageHelper");
        InlineKeyboardButton spanishButton = new InlineKeyboardButton("Spanish").callbackData("spanishLanguageHelper");
        InlineKeyboardMarkup keyboardMarkup = new InlineKeyboardMarkup(englishButton, spanishButton);
        this.sendMessageAndExecute(chatId, "Select the language to get help.", keyboardMarkup);
    }

    private void handleEnglishLanguageHelper(String chatId){
        this.handleLanguageHelper(chatId, Language.EN);
    }

    private void handleSpanishLanguageHelper(String chatId){
        this.handleLanguageHelper(chatId, Language.ES);
    }

    private void handleLanguageHelper(String chatId, Language language){
        TempHelperInfo tempHelperInfo = new TempHelperInfo();
        tempHelperInfo.setLanguage(language);
        this.tempHelperInfoByChatId.put(chatId, tempHelperInfo);

        this.lastMessageSentByChatId.put(chatId, "wordHelper");
        this.sendSimpleMessageAndExecute(chatId, "Good. Now send me the first word.");
    }

    public void handleShowMyTournaments(String chatId){
        List<Tournament> tournaments = this.tournamentService.findTournamentsInWhichUserIsRegistered(this.currentUser);

        if(tournaments.isEmpty()){
            String buttonsMessage = "You haven't tournaments yet.";
            this.sendSimpleMessageAndExecute(chatId, buttonsMessage);
            this.handleTournaments(chatId);
        } else {
            this.handleShowTournaments(chatId, tournaments, "My");
        }
    }

    private void handleShowPublicTournaments(String chatId){
        List<Tournament> tournaments = this.tournamentService.findPublicTournaments();

        if(tournaments.isEmpty()){
            String buttonsMessage = "There aren't public tournaments yet.";
            this.sendSimpleMessageAndExecute(chatId, buttonsMessage);
            this.sendKeyboard(chatId);
        } else {
            this.handleShowTournaments(chatId, tournaments, "Public");
        }
    }

    public void handleShowTournaments(String chatId,  List<Tournament> tournaments, String comesFrom){
        InlineKeyboardMarkup keyboardMarkup = new InlineKeyboardMarkup();

        tournaments.forEach(tournament -> {
            String label = tournament.getName();
            String callbackData = String.format("show%sTournament-%s", comesFrom, tournament.getId());
            InlineKeyboardButton tournamentButton = new InlineKeyboardButton(label).callbackData(callbackData);
            keyboardMarkup.addRow(tournamentButton);
        });

        InlineKeyboardButton backwardsButton = new InlineKeyboardButton("Backwards").callbackData("tournament");
        keyboardMarkup.addRow(backwardsButton);

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
        Result result = this.resultsBeingCreatedByChatId.get(chatId);

        this.userService.addResult(currentUser.getId(), result);

        this.sendSimpleMessageAndExecute(chatId, "Result successfuly saved.");
        this.sendKeyboard(chatId);
    }

    private void handleCancelResult(String chatId){
        this.resultsBeingCreatedByChatId.remove(chatId);

        this.sendKeyboard(chatId);
    }

    private void handleResultLanguage(String chatId, Language language){
        Result result = new Result();

        result.setLanguage(language);
        this.resultsBeingCreatedByChatId.put(chatId, result);

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

        this.sendMessageAndExecute(chatId, "Great. Now please select amount of attempts", keyboardMarkup);
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

    public void handleShowTournament(String chatId, String tournamentId, String comesFrom){
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

        if(tournament.getStatus() == TournamentStatus.NOT_STARTED && !tournament.isAParticipant(this.currentUser)) {
            String data = String.format("joinTournament-%s", tournament.getId());
            InlineKeyboardButton tournamentButton = new InlineKeyboardButton("Join").callbackData(data);
            keyboardMarkup = new InlineKeyboardMarkup(tournamentButton);

        }

        String callbackData = String.format("show%sTournaments", comesFrom);
        InlineKeyboardButton backwardsButton = new InlineKeyboardButton("Backwards").callbackData(callbackData);

        if(keyboardMarkup == null){
            keyboardMarkup = new InlineKeyboardMarkup(backwardsButton);
        } else {
            keyboardMarkup.addRow(backwardsButton);
        }

        this.sendMessageAndExecute(chatId, message, keyboardMarkup);
    }

    private void handleJoinTournament(String chatId, String tournamentId) {
        Tournament tournament = this.tournamentService.findById(tournamentId);

        this.tournamentService.addParticipant(tournament.getId(), this.currentUser, this.currentUser);
        this.sendMessageAndExecute(chatId, "Joined successfuly", null);
        this.handleShowPublicTournaments(chatId);
    }

    private void handleAttempts(String chatId, String attemptsStr){
        Integer attempts = Integer.parseInt(attemptsStr);
        Result resultInProcess = this.resultsBeingCreatedByChatId.get(chatId);

        resultInProcess.setAttempts(attempts);

        String messageText = String.format("Perfect. This is your result:\n\nLanguage: %s\nAttempts: %s\n\n Is everything ok?",
                resultInProcess.getLanguage().getLanguage(), resultInProcess.getAttempts().toString());

        InlineKeyboardButton confirmButton = new InlineKeyboardButton("Confirm").callbackData("confirmResult");
        InlineKeyboardButton cancelButton = new InlineKeyboardButton("Cancel").callbackData("cancelResult");
        InlineKeyboardMarkup keyboardMarkup = new InlineKeyboardMarkup(confirmButton, cancelButton);

        this.sendMessageAndExecute(chatId, messageText, keyboardMarkup);
    }

    private void handleCharacter(String chatId, String characterPosition, int messageId){
        int position = Integer.parseInt(characterPosition);
        List<String> colourByCharacterForLastWord = this.colourByCharacterForLastWordByChatId.get(chatId);
        String colourByCharacter = colourByCharacterForLastWord.get(position);
        String[] characterAndColour = colourByCharacter.split("-");

        char newColour = this.getNextColour(characterAndColour[1].charAt(0));
        String newColourByCharacter = String.format("%c-%c", characterAndColour[0].charAt(0), newColour);
        colourByCharacterForLastWord.remove(position);
        colourByCharacterForLastWord.add(position, newColourByCharacter);
        this.colourByCharacterForLastWordByChatId.put(chatId, colourByCharacterForLastWord);

        ArrayList<InlineKeyboardButton> colouredButtons = this.getColouredButtons(chatId);
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        inlineKeyboardMarkup.addRow(colouredButtons.toArray(new InlineKeyboardButton[0]));
        InlineKeyboardButton nextWord = new InlineKeyboardButton("Write next word").callbackData("writeNextWord");
        InlineKeyboardButton finish = new InlineKeyboardButton("Finish").callbackData("finishHelper");
        inlineKeyboardMarkup.addRow(nextWord);
        inlineKeyboardMarkup.addRow(finish);
        EditMessageReplyMarkup editMessageReplyMarkup = new EditMessageReplyMarkup(chatId, messageId).replyMarkup(inlineKeyboardMarkup);
        this.executeMessage(editMessageReplyMarkup);
    }

    private ArrayList<InlineKeyboardButton> getColouredButtons(String chatId){
        ArrayList<InlineKeyboardButton> buttons = new ArrayList<>();
        List<String> colourByCharacterForLastWord = this.colourByCharacterForLastWordByChatId.get(chatId);
        for (int i = 0; i < colourByCharacterForLastWord.size(); i++) {
            String colourByCharacter = colourByCharacterForLastWord.get(i);
            String[] characterAndColour = colourByCharacter.split("-");
            String label = String.format("%c %s", characterAndColour[0].charAt(0), this.getEmoji(characterAndColour[1].charAt(0)));
            String callbackData = String.format("changeCharacter-%d", i);
            InlineKeyboardButton tournamentButton = new InlineKeyboardButton(label).callbackData(callbackData);
            buttons.add(tournamentButton);
        };

        return buttons;
    }

    private char getNextColour(Character actualColour){
        switch(actualColour){
            case whiteCharacter:
                return yellowCharacter;
            case yellowCharacter:
                return greenCharacter;
            case greenCharacter:
                return whiteCharacter;
            default:
                return 'N';
        }
    }

    private String getEmoji(char colour){
        switch(colour){
            case whiteCharacter:
                return this.whiteCircleEmoji;
            case yellowCharacter:
                return this.yellowCircleEmoji;
            case greenCharacter:
                return this.greenCircleEmoji;
            default:
                return "";
        }
    }

    // End handle query methods

    private void handleMessage(Message message){
        if(message == null){
            return;
        }

        String chatId = message.chat().id().toString();
        String text = message.text();

        List<String> commandNames = this.commands.stream().map(Command::getName).collect(Collectors.toList());

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
                case "/tournament":
                    this.handleTournaments(chatId);
                    break;
                case "/dictionary":
                    this.handleDictionary(chatId);
                    break;
                case "/show_public_tournaments":
                    this.handleShowPublicTournaments(chatId);
                    break;
                case "/show_my_tournaments":
                    this.handleShowMyTournaments(chatId);
                    break;
                case "/create_tournament":
                    this.handleCreateTournament(chatId);
                    break;
                case "/submit_result":
                    this.handleSubmitResults(chatId);
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
                case "wordHelper":
                    this.handleWordHelper(chatId, text);
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
            this.sendSimpleMessageAndExecute(chatId, "User has been created successfuly");
            this.sendKeyboardForNotLogued(chatId);
        } catch(ExistingUserException e){
            this.sendSimpleMessageAndExecute(chatId, "User already exists.");
            this.handleSignin(chatId);
        } catch(Exception e){
            System.out.println(e);
            this.sendSimpleMessageAndExecute(chatId, "An error ocurred.");
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
        InlineKeyboardButton englishButton = new InlineKeyboardButton("English").callbackData("englishLanguageResult");
        InlineKeyboardButton spanishButton = new InlineKeyboardButton("Spanish").callbackData("spanishLanguageResult");
        InlineKeyboardMarkup keyboardMarkup = new InlineKeyboardMarkup(englishButton, spanishButton);

        this.sendMessageAndExecute(chatId, "Select the language.", keyboardMarkup);
    }

    private void handleDictionaryWord(String chatId, String word){
        try {
            Word resultWord = this.dictionaryService.findByNameAndLanguage(word, this.currentDictionaryLanguage);
            this.sendSimpleMessageAndExecute(chatId, resultWord.getDefinition());
        } catch(NotFoundException e){
            String messageText = String.format("No results found for %s", word);
            this.sendSimpleMessageAndExecute(chatId, messageText);
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

    private void handleWordHelper(String chatId, String word){
        if(word.length() != 5){
            this.sendSimpleMessageAndExecute(chatId, "Word must be five characters long. Try again.");
            this.handleHelper(chatId);
            return;
        }

        Integer wordsSent = this.helperWordsSentByChatId.get(chatId);

        if(wordsSent == null){
            wordsSent = 0;
        }

        this.helperWordsSentByChatId.put(chatId, wordsSent+1);

        InlineKeyboardMarkup keyboardMarkup = new InlineKeyboardMarkup();
        char[] upperWordArray = word.toUpperCase().toCharArray();
        List<String> colourByCharacterForLastWord = new ArrayList<>();

        for (Character character : upperWordArray) {
            colourByCharacterForLastWord.add(String.format("%c-%c", character, whiteCharacter));
        }

        this.colourByCharacterForLastWordByChatId.put(chatId, colourByCharacterForLastWord);

        ArrayList<InlineKeyboardButton> buttons = this.getColouredButtons(chatId);
        keyboardMarkup.addRow(buttons.toArray(new InlineKeyboardButton[0]));

        wordsSent = this.helperWordsSentByChatId.get(chatId);
        String writeNextWord = "";

        if(wordsSent != 5){
            InlineKeyboardButton nextWord = new InlineKeyboardButton("Write next word").callbackData("writeNextWord");
            keyboardMarkup.addRow(nextWord);
            writeNextWord = ", write next word";
        }

        InlineKeyboardButton finish = new InlineKeyboardButton("Finish").callbackData("finishHelper");
        keyboardMarkup.addRow(finish);

        this.sendMessageAndExecute(chatId, String.format("Change colours%s or finish.", writeNextWord), keyboardMarkup);
    }

    private void handleWriteNextWord(String chatId){
        TempHelperInfo tempHelperInfo = this.readLastWord(chatId);

        if(tempHelperInfo == null){
            return;
        }

        this.tempHelperInfoByChatId.put(chatId, tempHelperInfo);
        this.lastMessageSentByChatId.put(chatId, "wordHelper");
        this.sendSimpleMessageAndExecute(chatId, "Send me the next word please.");
    }

    private TempHelperInfo readLastWord(String chatId){
        TempHelperInfo tempHelperInfo = this.tempHelperInfoByChatId.get(chatId);
        List<String> colourByCharacterForLastWord = this.colourByCharacterForLastWordByChatId.get(chatId);

        for(int i = 0; i < colourByCharacterForLastWord.size(); i++){
            String colourByCharacter = colourByCharacterForLastWord.get(i);
            String[] characterAndColour = Strings.split(colourByCharacter, "-");
            char colour = characterAndColour[1].charAt(0);
            char letter = characterAndColour[0].charAt(0);

            switch(colour){
                case whiteCharacter:
                    tempHelperInfo.addGreyLetterPlayed(letter);
                    break;
                case yellowCharacter:
                    tempHelperInfo.addYellowLetterPlayed(i, letter);
                    break;
                case greenCharacter:
                    try {
                        tempHelperInfo.addGreenLetterPlayed(i, letter);
                    }catch(LetterMismatchException e){
                        this.sendSimpleMessageAndExecute(chatId, e.getMessage());
                        return null;
                    }
                    break;
            }
        }

        return tempHelperInfo;
    }

    private void handleFinishHelper(String chatId){
        TempHelperInfo tempHelperInfo = this.readLastWord(chatId);

        if(tempHelperInfo == null){
            return;
        }

        WordPlay attemptedPlay = new WordPlay(tempHelperInfo.getGrayLettersPlayed(), tempHelperInfo.getYellowLettersPlayed(),
                tempHelperInfo.getGreenLettersPlayed());
        List<String> possibleWords = this.helperService.getWordsByPlay(attemptedPlay, tempHelperInfo.getLanguage());
        String message = "Possible words are:\n" + String.join("\n", possibleWords);

        this.sendSimpleMessageAndExecute(chatId, message);
        this.sendKeyboard(chatId);
    }

    // End handle message methods

    private void sendSimpleMessageAndExecute(String chatId, String message){
        this.sendMessageAndExecute(chatId, message, null);
    }

    private void sendMessageAndExecute(String chatId, String message, InlineKeyboardMarkup markup){
        SendMessage sendMessage = new SendMessage(chatId, message).parseMode(ParseMode.HTML);

        if(markup != null){
            sendMessage.replyMarkup(markup);
        }

        this.executeMessage(sendMessage);
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
        InlineKeyboardButton helperButton = new InlineKeyboardButton("Helper").callbackData("helper");
        InlineKeyboardButton logoutButton = new InlineKeyboardButton("Logout").callbackData("logout");
        InlineKeyboardMarkup keyboardMarkup = new InlineKeyboardMarkup(tournamentButton, dictionaryButton, helperButton, logoutButton);
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

        try {
            if (this.currentUser == null) {
                this.currentUser = this.telegramSecurityService.getUserFromToken(chatId);
            }
        } catch(ExpiredJwtException e){
            this.sendSimpleMessageAndExecute(chatId, "Your token has expired. Please, login again.");
            this.handleLogin(chatId);

            return false;
        }

        return isUserLogged;
    }
}
