package cane.brothers.google;

import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cane.brothers.google.utils.GSUtils;
import cane.brothers.russianpost.client.data.PostEntry;
import cane.brothers.russianpost.config.Config;
import cane.brothers.russianpost.utils.PostUtils;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.gdata.client.spreadsheet.SpreadsheetService;
import com.google.gdata.data.spreadsheet.ListEntry;
import com.google.gdata.data.spreadsheet.SpreadsheetEntry;

public class SpreadsheetInterrogator {

	private static final Logger log = LoggerFactory
			.getLogger(SpreadsheetInterrogator.class);

	private static final List<String> SCOPES = Arrays
			.asList("https://spreadsheets.google.com/feeds");

	private static final java.io.File DATA_STORE_DIR = new java.io.File(
			System.getProperty("user.home"), Config.getDataStoreDir());

	private static final String CLIENT_SECRETS = Config.getClientSecrets();

	private static FileDataStoreFactory dataStoreFactory;

	private static HttpTransport httpTransport;

	private static final JsonFactory JSON_FACTORY = JacksonFactory
			.getDefaultInstance();

	private static GoogleClientSecrets clientSecrets;

	private static Credential authorize() throws Exception {

		if (log.isDebugEnabled()) {
			log.debug("Google авторизация:");
		}

		// load client secrets
		InputStreamReader clientSecretsReader = new InputStreamReader(
				Files.newInputStream(Paths.get(CLIENT_SECRETS)));

		clientSecrets = GoogleClientSecrets.load(JSON_FACTORY,
				clientSecretsReader);
		if (log.isDebugEnabled()) {
			log.debug("Клиентские авторизационные данные из файла client_secrets.json подгрузили");
		}

		if (clientSecrets.getDetails().getClientId().startsWith("Enter")
				|| clientSecrets.getDetails().getClientSecret()
						.startsWith("Enter ")) {
			log.warn("Enter Client ID and Secret from https://code.google.com/apis/console/ "
					+ "into client_secrets.json");

			return null;
		}

		httpTransport = GoogleNetHttpTransport.newTrustedTransport();
		dataStoreFactory = new FileDataStoreFactory(DATA_STORE_DIR);

		// set up authorization code flow
		GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(
				httpTransport, JSON_FACTORY, clientSecrets, SCOPES)
				.setDataStoreFactory(dataStoreFactory).build();
		//clientSecrets.getDetails().get
		// 
		//LocalServerReceiver receiver = new LocalServerReceiver.Builder().setHost("127.0.0.1").setPort(8080).build();
		LocalServerReceiver receiver = new LocalServerReceiver();
		AuthorizationCodeInstalledApp app = new AuthorizationCodeInstalledApp(
				flow, receiver);

		if (log.isDebugEnabled()) {
			log.debug("Авторизуемся приложением для доступа в Google API");
		}

		// authorize
		return app.authorize("user");
	}

	public Set<PostEntry> getPostEntries() {
		Set<PostEntry> barcodes = new TreeSet<PostEntry>();

		try {
			// authorization
			Credential credential = authorize();
			if (credential != null) {
//				if(credential.refreshToken()) {
//					if (log.isDebugEnabled()) {
//						log.debug("Token был обновлен");
//					}	
//				}
				if (log.isDebugEnabled()) {
					log.debug("Авторизация в Google есть");
				}

				// connect to google service with credential
				SpreadsheetService googleService = new SpreadsheetService(
						Config.getGoogleAppName());
				googleService.setOAuth2Credentials(credential);
				if (log.isDebugEnabled()) {
					log.debug("К Google сервису таблиц с помощью OAuth2 подключились");
				}

				// get table
				SpreadsheetEntry ss = GSUtils.getSpreadsheet(googleService,
						Config.getGoogleSpreadSheetName());
				if (log.isDebugEnabled()) {
					log.debug("Доступ к Google таблице {} есть",
							Config.getGoogleSpreadSheetName());
				}

				if (log.isDebugEnabled()) {
					log.debug("Берем данные из таблицы в виде списка");
				}
				List<ListEntry> inputRowList = GSUtils.getRowList(
						googleService, ss);

				if (inputRowList != null) {
					if (log.isDebugEnabled()) {
						log.debug("Трансформируем в почтовые записи");
					}
					barcodes.addAll(PostUtils.transformToPost(inputRowList));
				} 
				else {
					log.warn("Нет исходных данных");	
				}
			}

			else {
				log.error("Нет авторизации в Google");
			}

		} catch (Exception ex) {
			log.error("Не могу прочитать таблицу баркодов", ex);
		}

		// read barcodes spreadsheet
		return barcodes;
	}

}
