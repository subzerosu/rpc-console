package cane.brothers.google;

import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
import com.google.gdata.util.ServiceException;

import cane.brothers.google.utils.GSUtils;
import cane.brothers.mail.MessageContext;
import cane.brothers.russianpost.client.data.OldPostEntry;
import cane.brothers.russianpost.client.data.PostEntry;
import cane.brothers.russianpost.config.Config;
import cane.brothers.russianpost.utils.PostUtils;

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

    private static Credential credential;

    private static SpreadsheetService googleService;

    private static SpreadsheetEntry table = null;

    private MessageContext messages = null;

    public SpreadsheetInterrogator(MessageContext context) {
        messages = context;
    }

    private static Credential authorize() throws Exception {

        if (log.isDebugEnabled()) {
            log.debug("Google авторизация:");
        }

        // load client secrets
        InputStreamReader clientSecretsReader = new InputStreamReader(
                Files.newInputStream(Paths.get(CLIENT_SECRETS)));

        clientSecrets = GoogleClientSecrets.load(
                JSON_FACTORY,
                clientSecretsReader);
        if (log.isDebugEnabled()) {
            log.debug("Клиентские авторизационные данные из файла client_secrets.json подгрузили");
        }

        if (clientSecrets.getDetails().getClientId().startsWith("Enter")
                || clientSecrets
                        .getDetails().getClientSecret()
                        .startsWith("Enter ")) {
            log.warn(
                    "Enter Client ID and Secret from https://code.google.com/apis/console/ "
                            + "into client_secrets.json");

            return null;
        }

        httpTransport = GoogleNetHttpTransport.newTrustedTransport();
        dataStoreFactory = new FileDataStoreFactory(DATA_STORE_DIR);

        // set up authorization code flow
        GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(
                httpTransport, JSON_FACTORY, clientSecrets, SCOPES)
                        .setDataStoreFactory(dataStoreFactory).build();
        // LocalServerReceiver receiver = new
        // LocalServerReceiver.Builder().setHost("127.0.0.1").setPort(8080).build();
        LocalServerReceiver receiver = new LocalServerReceiver();
        AuthorizationCodeInstalledApp app = new AuthorizationCodeInstalledApp(
                flow, receiver);

        if (log.isDebugEnabled()) {
            log.debug("Авторизуемся приложением для доступа в Google API");
        }

        // authorize
        return app.authorize("user");
    }

    private Credential getCredetial() throws Exception {
        if (credential == null) {
            credential = authorize();
        }
        return credential;
    }

    private SpreadsheetService getService() {
        if (googleService == null) {

            try {
                if (log.isInfoEnabled()) {
                    log.info("Подключаюсь к Google сервису...");
                }

                // authorization
                Credential credential = getCredetial();
                if (credential != null) {
                    // if(credential.refreshToken()) {
                    // if (log.isDebugEnabled()) {
                    // log.debug("Token был обновлен");
                    // }
                    // }
                    if (log.isInfoEnabled()) {
                        log.info("Авторизация в Google есть");
                    }

                    // connect to google service with credential
                    googleService = new SpreadsheetService(
                            Config.getGoogleAppName());
                    googleService.setOAuth2Credentials(credential);
                    if (log.isDebugEnabled()) {
                        log.debug("К Google сервису таблиц с помощью OAuth2 подключились");
                    }
                }

                else {
                    log.error("Нет авторизации в Google");
                    messages.addErrorMessage("Нет авторизации в Google");
                }

            }
            catch (Exception ex) {
                log.error("Не могу прочитать таблицу баркодов", ex);
            }
        }
        return googleService;
    }

    private SpreadsheetEntry getTable() {

        if (table == null) {

            // connect to google service with credential
            SpreadsheetService googleService = getService();

            if (googleService != null) {
                // get table
                table = GSUtils.getSpreadsheet(
                        googleService,
                        Config.getGoogleSpreadSheetName());
                if (log.isDebugEnabled()) {
                    log.debug(
                            "Доступ к Google таблице {} есть",
                            Config.getGoogleSpreadSheetName());
                }
            }
        }

        return table;
    }

    public Set<PostEntry> getPostEntries() {
        Set<PostEntry> barcodes = new TreeSet<>();

        if (log.isDebugEnabled()) {
            log.debug("Берем данные из таблицы в виде списка");
        }
        List<ListEntry> inputRowList = GSUtils.getRowList(
                getService(),
                getTable());

        int rows = (inputRowList == null ? 0 : inputRowList.size());
        if (log.isDebugEnabled()) {
            log.debug("Считали " + rows + " строк баркодов");
        }
        messages.addMessage1("В таблице баркодов " + rows + " строк.");

        if (inputRowList != null) {
            if (log.isDebugEnabled()) {
                log.debug("Трансформируем в почтовые записи");
            }
            barcodes.addAll(PostUtils.transformToPost(inputRowList, messages));
        }
        else {
            log.warn("Нет исходных данных");
        }

        messages.addMessage1(" Уникальных строк с баркодами: " + barcodes.size());

        // read barcodes spreadsheet
        return barcodes;
    }

    /**
     * @param outputEntries
     * @return true если все старые записи были удалены успешно или не удаленого
     *         ничего
     */
    public boolean removeOldPostEntries(Set<PostEntry> outputEntries) {

        if (outputEntries == null || outputEntries.isEmpty()) {
            log.warn("Нечего подчищать");
            return false;
        }

        boolean result = true;

        if (log.isDebugEnabled()) {
            log.debug("Удаляем старые записи");
        }

        // проходим по выходному списку
        for (PostEntry pe : outputEntries) {

            // интересуют только старые записи
            if (pe instanceof OldPostEntry) {
                OldPostEntry oldEntry = (OldPostEntry) pe;

                // get data list
                List<ListEntry> inputRowList = GSUtils.getRowList(
                        getService(),
                        getTable());
                if (inputRowList != null) {

                    for (ListEntry row : inputRowList) {
                        String barcode = row.getCustomElements().getValue(
                                "barcode");

                        // сверяем по баркоду
                        if (oldEntry.getBarcode().equals(barcode)) {

                            try {
                                // Delete the row using the API.
                                row.delete();

                                if (log.isDebugEnabled()) {
                                    log.debug(
                                            " Cтрока с баркодом {} была удалена",
                                            barcode);
                                }

                                // удалили одну строку, обновляем весь список
                                break;

                            }
                            catch (IOException ex) {
                                result &= false;
                                log.error(
                                        "Не могу удалить строку с баркодом "
                                                + barcode,
                                        ex);
                            }
                            catch (ServiceException ex2) {
                                result &= false;
                                log.error(
                                        "Не могу удалить строку с баркодом "
                                                + barcode);
                            }
                            catch (Exception ex3) {
                                result &= false;
                                log.error(
                                        "Не могу удалить строку с баркодом "
                                                + barcode,
                                        ex3);
                            }
                        }
                    }

                }
                else {
                    log.warn("Нет исходных данных");
                }
            }

            // TODO set days
        }

        return result;
    }

}
