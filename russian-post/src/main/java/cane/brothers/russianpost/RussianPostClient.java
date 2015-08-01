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
import cane.brothers.russianpost.utils.PostUtils;

public class RussianPostClient {

	private static final Logger log = LoggerFactory
			.getLogger(RussianPostClient.class);

	public static void main(String[] args) {
		log.info("стартовали");

		List<String> msg = new ArrayList<String>();

		// считываем все баркоды с google-таблицы
		SpreadsheetInterrogator googleService = new SpreadsheetInterrogator();
		msg.addAll(googleService.getMessage());
		Set<? extends PostEntry> inputEntries = PostUtils.transformToWork(googleService.getPostEntries());

		if (inputEntries != null && !inputEntries.isEmpty()) {
			// set up output
			Set<PostEntry> outputEntries = new TreeSet<PostEntry>();

			// main post handling
			PostInterrogator postService = new PostInterrogator(inputEntries,
					outputEntries);
			
			if (postService.authorize()) {
				
				// прошли авторизацию - читаем историю
				postService.checkHistory();
				msg.addAll(postService.getMessage());

				// TODO в отдельном потоке
				// очищаем файл баркодов до
				if (Config.doCleanUp()) {
					googleService.removeOldPostEntries(outputEntries);
				}
			}
			
			// send e-mail
			EmailSender.sendEmail(inputEntries, outputEntries, msg);
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
