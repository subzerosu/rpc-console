package cane.brothers.russianpost;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cane.brothers.google.SpreadsheetInterrogator;
import cane.brothers.mail.EmailSender;
import cane.brothers.russianpost.client.PostInterrogator;
import cane.brothers.russianpost.client.data.PostEntry;
import cane.brothers.russianpost.config.Config;

public class RussianPostClient {

	private static final Logger log = LoggerFactory
			.getLogger(RussianPostClient.class);

	public static void main(String[] args) {
		log.info("стартовали");

		List<String> msg = new ArrayList<String>();
		// read input
		// InputData input = new InputData();
		SpreadsheetInterrogator googleService = new SpreadsheetInterrogator();
		Set<PostEntry> inputEntries = googleService.getPostEntries();
		msg.addAll(googleService.getMessage());

		if (inputEntries != null && !inputEntries.isEmpty()) {
			// set up output
			Set<PostEntry> outputEntries = new TreeSet<PostEntry>();

			// log.info(String.format(
			// "Считано: %1$d баркодов, ошибок %2$d, дублей %3$d",
			// input.getBarcodeAmount(), input.getErrorAmount(),
			// input.getDuplBarcodeAmount()));

			// main handling
			PostInterrogator postService = new PostInterrogator(inputEntries,
					outputEntries);
			
			if (postService.authorize()) {
				
				// прошли авторизацию - читаем историю
				postService.checkHistory();
				msg.addAll(postService.getMessage());

				// очищаем файл баркодов до
				if (Config.doCleanUp()) {
					googleService.removeOldPostEntries(outputEntries);
				}

				// добавляем в выходной набор повторяющиеся записи
				// if (input.getDuplBarcodeAmount() > 0) {
				// if (log.isDebugEnabled()) {
				// log.debug("Добавляю дубли в выходной набор. {} записей",
				// input.getDuplBarcodeAmount());
				// }
				// outputEntries.addAll(input.getDuplEntries());
				// }

				// send e-mail
				EmailSender.sendEmail(outputEntries, msg);
			}
		}

		log.info("работу закончили");
	}

	// private static void cleanup(InputData input, List<String> oldBarcodes) {
	// log.info("Делаю очистку старых отправлений...");
	//
	// if (oldBarcodes != null && !oldBarcodes.isEmpty()) {
	// input.cleanupPostEntries(oldBarcodes);
	// } else {
	// log.info("Нечего чистить. Cтарых отправлений нет.");
	// }
	//
	// }
}
