package it.paleocapa.mastroiannim;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
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

	private JavaBossBot(){
		super(botToken);
	}

	@Override
	public String getBotToken() {
		return botToken;
	}
	
	@Override
	public String getBotUsername() {
		return botUsername;
	}

	private Map<String, Double> menu = new HashMap<>();

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

			if (command.equals("/run")) {
				String message = "Ciao";
				SendMessage response = new SendMessage();
				response.setChatId(update.getMessage().getChatId().toString());
				response.setText(message);
				try{
					execute(response);
				} catch (TelegramApiException E){
					E.printStackTrace();
				}
			} else if (command.equals("/ordina")) {
				SendMessage response = new SendMessage();
				response.setChatId(update.getMessage().getChatId().toString());
				response.setText("Scegli il tuo ordine:");

				ReplyKeyboardMarkup keyboardMarkup = new ReplyKeyboardMarkup();
				keyboardMarkup.setSelective(true);
				keyboardMarkup.setResizeKeyboard(true);
				keyboardMarkup.setOneTimeKeyboard(true);

				List<KeyboardRow> keyboard = new ArrayList<>();
				for (String item : menu.keySet()) {
					KeyboardRow row = new KeyboardRow();
					row.add(item);
					keyboard.add(row);
				}

				keyboardMarkup.setKeyboard(keyboard);
				response.setReplyMarkup(keyboardMarkup);

				try{
					execute(response);
				} catch (TelegramApiException E){
					E.printStackTrace();
				}
			}else if (messageText.startsWith("/soldi")) {
				try {
					double amount = Double.parseDouble(messageText.split(" ")[1].replace(",", "."));
					soldi += amount;
					SendMessage response = new SendMessage(chatId, "Soldi aggiunti correttamente!");
					execute(response);
				} catch (Exception e) {
					SendMessage response = new SendMessage(chatId, "Formato denaro non valido!");
					execute(response);
				}
        	} else if (menu.containsKey(command)) {
				double price = menu.get(command);

				SendMessage response = new SendMessage();
				response.setChatId(update.getMessage().getChatId().toString());
				response.setText(String.format("Hai ordinato %s per %.2f euro.", command, price));

				try{
					execute(response);
				} catch (TelegramApiException E){
					E.printStackTrace();
				}
			}
		}
	}
}
