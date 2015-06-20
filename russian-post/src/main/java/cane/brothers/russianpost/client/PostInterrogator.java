package cane.brothers.russianpost.client;

import java.util.HashSet;
import java.util.Set;

import javax.xml.ws.WebServiceException;

import org.russianpost.operationhistory.AuthorizationFault;
import org.russianpost.operationhistory.LanguageFault;
import org.russianpost.operationhistory.OperationHistoryClient;
import org.russianpost.operationhistory.OperationHistoryFault;
import org.russianpost.operationhistory.OperationHistoryService;
import org.russianpost.operationhistory.data.AuthorizationHeader;
import org.russianpost.operationhistory.data.OperationHistoryData;
import org.russianpost.operationhistory.data.OperationHistoryRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cane.brothers.russianpost.client.data.PostEntry;
import cane.brothers.russianpost.config.Config;
import cane.brothers.russianpost.utils.PostUtils;

public class PostInterrogator {

	private static final Logger log = LoggerFactory
			.getLogger(PostInterrogator.class);

	/**
	 * набор входных записей ПО
	 */
	private Set<PostEntry> inputEntries = null;

	/**
	 * набор выходных записей ПО
	 */
	private Set<PostEntry> outputEntries = null;

	/**
	 * список баркодов с истекшим сроком, посылки по которые возвращаются, уже
	 * вернулись или совсем древние
	 */
	// List<String> oldBarcodes = new ArrayList<String>();
	//
	// public List<String> getOldBarcodes() {
	// return oldBarcodes;
	// }

	/**
	 * сервис ПО
	 */
	private OperationHistoryService service = null;
	private AuthorizationHeader authorizationHeader = null;

	/**
	 * 
	 * @param input
	 * @param output
	 */
	public PostInterrogator(Set<PostEntry> input, Set<PostEntry> output) {
		this.inputEntries = input;
		this.outputEntries = output;
	}

	public boolean authorize() {
		if (log.isDebugEnabled()) {
			log.debug("Подключаюсь к сервису почтовых отправлений...");
		}

		// get service
		try {
			OperationHistoryClient client = new OperationHistoryClient();
			service = client.getOperationHistoryService();

			if (log.isDebugEnabled()) {
				log.debug("Подключились");
			}
		} catch (WebServiceException ex) {
			log.error("Проблемы с доступом к сервису почтовых отправлений", ex);
			return false;
		}

		log.debug("Настраиваем заголовки авторизации");

		// set up authorization
		authorizationHeader = new AuthorizationHeader();
		authorizationHeader.setLogin(Config.getPostLogin());
		authorizationHeader.setPassword(Config.getPostPassword());
		authorizationHeader.setMustUnderstand(Boolean.TRUE);

		// get Languages
		// LanguageData langData = service
		// .getLanguages(authorizationHeader);
		// List<LanguageData.Language> langs = langData.getLanguage();
		// for (LanguageData.Language lang : langs) {
		// System.out.println(lang.getName());
		// }

		return true;
	}

	/**
	 * проверяем историю посылок. Ищем зависшие посылки
	 */
	public void checkHistory() {
		if (inputEntries == null || inputEntries.isEmpty()) {
			log.error("Исходный список пуст или не существует.");
			return;
		}

		if (outputEntries == null) {
			log.error("Выходной список не существует.");
			return;
		}

		// cleanup
		outputEntries.clear();
		// oldBarcodes.clear();
		Set<PostEntry> delEntries = new HashSet<PostEntry>();
		Set<PostEntry> oldEntries = new HashSet<PostEntry>();

		if (log.isDebugEnabled()) {
			log.debug("Делаю запрос по каждому из почтовых отправлений:");
		}

		// get operation history
		OperationHistoryRequest operHistoryRequest = new OperationHistoryRequest();

		for (PostEntry mailing : inputEntries) {

			operHistoryRequest.setBarcode(mailing.getBarcode());
			operHistoryRequest.setMessageType(0);

			try {
				log.info("Запрос по : " + mailing.getBarcode());

				OperationHistoryData operHistory = service.getOperationHistory(
						operHistoryRequest, authorizationHeader);

				if (log.isDebugEnabled()) {
					log.debug("Ищем зависшие посылки:");
				}
				PostEntry delayedPost = PostUtils.getDelayedPost(mailing,
						operHistory);
				if (delayedPost != null) {
					// outputEntries.add(delayedPost);
					delEntries.add(delayedPost);
					if (log.isDebugEnabled()) {
						log.debug("Добавляем в зависшие посылки: "
								+ delayedPost);
					}
				}

				if (log.isDebugEnabled()) {
					log.debug("Ищем старые посылки:");
				}
				PostEntry oldPost = PostUtils.getOldPost(mailing, operHistory);
				if (oldPost != null) {
					if (oldEntries.add(oldPost)) {
						if (log.isDebugEnabled()) {
							log.debug("Добавляем в список на удаление: "
									+ oldPost);
						}
					} else {
						if (log.isDebugEnabled()) {
							log.debug("Не могу добавить в список на удаление: "
									+ oldPost);
						}
					}
				}
			} catch (AuthorizationFault ae) {
				log.error("Проблемы с авторизацией. ", ae);
			} catch (LanguageFault le) {
				log.error("Проблемы с языком запроса. ", le);
			} catch (OperationHistoryFault he) {
				log.error(
						"Проблемы с запросом истории почтового отправления. ",
						he);
			} catch (WebServiceException we) {
				log.error("Проблемы с доступом к web-сервису. ", we);
			}
			if (log.isDebugEnabled()) {
				log.debug("");
			}
		}

		if (log.isDebugEnabled()) {
			log.debug("Набор входных данных обработали.");
		}

		outputEntries.addAll(delEntries);
		if (delEntries.size() > 0) {
			log.info("Зависших посылок: {}", delEntries.size());
		} else {
			log.info("Зависших посылок нет.");
		}

		if (Config.doCleanUp()) {
			if (oldEntries.size() > 0) {
				log.info("Посылок на удаление: {}", oldEntries.size());
			} else {
				log.info("Посылок для удаления нет.");
			}

			outputEntries.addAll(oldEntries);
		}
	}
}
