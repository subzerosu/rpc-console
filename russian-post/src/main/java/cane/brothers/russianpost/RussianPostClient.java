package cane.brothers.russianpost;

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

		// read input
		// InputData input = new InputData();
		SpreadsheetInterrogator googleInput = new SpreadsheetInterrogator();
		Set<PostEntry> inputEntries = googleInput.getPostEntries();

		if (inputEntries != null && !inputEntries.isEmpty()) {
			// set up output
			Set<PostEntry> outputEntries = new TreeSet<PostEntry>();

			// log.info(String.format(
			// "Считано: %1$d баркодов, ошибок %2$d, дублей %3$d",
			// input.getBarcodeAmount(), input.getErrorAmount(),
			// input.getDuplBarcodeAmount()));

			// main handling
			PostInterrogator service = new PostInterrogator(inputEntries,
					outputEntries);
			if (service.authorize()) {
				service.checkHistory();

				// очищаем файл баркодов до
				if (Config.doCleanUp()) {
					// cleanup(input, service.getOldBarcodes());
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
				EmailSender.sendEmail(outputEntries);
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
