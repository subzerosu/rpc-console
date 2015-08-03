package cane.brothers.russianpost;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cane.brothers.google.SpreadsheetInterrogator;
import cane.brothers.mail.EmailSender;
import cane.brothers.mail.MessageContext;
import cane.brothers.russianpost.client.PostInterrogator;
import cane.brothers.russianpost.client.data.PostEntry;
import cane.brothers.russianpost.config.Config;
import cane.brothers.russianpost.utils.MessageBuilder;
import cane.brothers.russianpost.utils.PostUtils;

public class RussianPostClient {

	private static final Logger log = LoggerFactory.getLogger(RussianPostClient.class);

	public static void main(String[] args) {
		log.info("стартовали");

		// считываем все баркоды с google-таблицы
		SpreadsheetInterrogator googleService = new SpreadsheetInterrogator(MessageContext.getContext());
		Set<? extends PostEntry> inputEntries = PostUtils.transformToWork(googleService.getPostEntries());

		if (inputEntries != null && !inputEntries.isEmpty()) {
			// set up output
			Set<PostEntry> outputEntries = new TreeSet<PostEntry>();

			// main post handling
			PostInterrogator postService = new PostInterrogator(inputEntries, outputEntries,
					MessageContext.getContext());

			if (postService.authorize()) {

				// прошли авторизацию - читаем историю
				postService.checkHistory();

				// TODO в отдельном потоке ???

				// очищаем файл баркодов до
				if (Config.doCleanUp()) {
					googleService.removeOldPostEntries(outputEntries);
				}
			}

			// build message
			MessageBuilder messages = new MessageBuilder(MessageContext.getContext(), inputEntries, outputEntries)
					.build();

			// send e-mail
			EmailSender.sendEmail(messages);
		}

		log.info("работу закончили");
	}
}
