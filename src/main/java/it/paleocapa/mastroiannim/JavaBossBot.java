package it.paleocapa.mastroiannim;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;


@Service
public class JavaBossBot extends TelegramLongPollingBot {

	private static final Logger LOG = LoggerFactory.getLogger(JavaBossBot.class);

	private String botUsername;
	private static String botToken;
	private static JavaBossBot instance;

	public static JavaBossBot getJavaBossBotInstance(String botUsername, String botToken){
		if(instance == null) {
			instance = new JavaBossBot();
			instance.botUsername = botUsername;
			JavaBossBot.botToken = botToken;
		}
		return instance;
	}
	/*
	private JavaBossBot(){
		super(botToken);
	}
	*/
	
	@Override
	public String getBotToken() {
		return botToken;
	}
	
	@Override
	public String getBotUsername() {
		return botUsername;
	}

	private Map<String, Double> menu = new HashMap<>();

	private double soldi;

	public JavaBossBot() {
		super(botToken);
		// Aggiungi i prodotti e i prezzi al menu
		menu.put("paneCotoletta", 5.0);
		menu.put("paneWurstel", 4.5);
		menu.put("pizzaPiegata", 8.0);	
	}

	@Override
	public void onUpdateReceived(Update update) {
		if (update.hasMessage() && update.getMessage().hasText()) {
			String messageText = update.getMessage().getText();
			long chatId = update.getMessage().getChatId();

			if (messageText.equals("/run")) {
				String message = "Ciao";
				SendMessage response = new SendMessage();
				response.setChatId(chatId);
				response.setText(message);
				try {
					execute(response);
				} catch (TelegramApiException e) {
					e.printStackTrace();
				}
			} else if (messageText.startsWith("/soldi ")) {
				try {
					String soldiString = messageText.substring(7); // estrae la quantità di denaro dal messaggio
					double soldi = Double.parseDouble(soldiString.replace(",", ".")); // converte la quantità di denaro in un numero decimale
					this.soldi = soldi; // assegna la quantità di denaro alla variabile soldi
					String message = "Hai inserito " + soldi + " €";
					SendMessage response = new SendMessage();
					response.setChatId(chatId);
					response.setText(message);
					try {
						execute(response);
					} catch (TelegramApiException e) {
						e.printStackTrace();
					}
				} catch (NumberFormatException e) {
					String message = "Formato denaro non valido. Inserisci la quantità di denaro nel formato xx,xx €";
					SendMessage response = new SendMessage();
					response.setChatId(chatId);
					response.setText(message);
					try {
						execute(response);
					} catch (TelegramApiException ex) {
						ex.printStackTrace();
					}
				}
			}
		}
	}

}
