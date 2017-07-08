package cane.brothers.google.utils;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gdata.client.spreadsheet.SpreadsheetQuery;
import com.google.gdata.client.spreadsheet.SpreadsheetService;
import com.google.gdata.data.spreadsheet.ListEntry;
import com.google.gdata.data.spreadsheet.ListFeed;
import com.google.gdata.data.spreadsheet.SpreadsheetEntry;
import com.google.gdata.data.spreadsheet.SpreadsheetFeed;
import com.google.gdata.data.spreadsheet.WorksheetEntry;
import com.google.gdata.data.spreadsheet.WorksheetFeed;
import com.google.gdata.util.ServiceException;

public class GSUtils {

    private static final Logger log = LoggerFactory.getLogger(GSUtils.class);

    private static final String SPREADSHEET_SERVICE_URL = "https://spreadsheets.google.com/feeds/spreadsheets/private/full";

    /**
     * Получить goolge таблицу по имени
     *
     * @param service
     *            google сервис для таблиц
     * @param sheetName
     *            имя таблицы google
     * @return
     */
    public static SpreadsheetEntry getSpreadsheet(SpreadsheetService service,
            String sheetName) {
        try {
            URL spreadSheetFeedUrl = new URL(SPREADSHEET_SERVICE_URL);

            if (log.isDebugEnabled()) {
                log.debug("Подгружаемся к таблице google через запрос по ссылке");
            }
            SpreadsheetQuery spreadsheetQuery = new SpreadsheetQuery(
                    spreadSheetFeedUrl);
            spreadsheetQuery.setTitleQuery(sheetName);
            spreadsheetQuery.setTitleExact(true);

            SpreadsheetFeed spreadsheet = service.getFeed(
                    spreadsheetQuery,
                    SpreadsheetFeed.class);

            if (spreadsheet.getEntries() != null) {
                if (log.isDebugEnabled()) {
                    log.debug(
                            "Google сервис предоставил {} таблиц",
                            spreadsheet.getEntries().size());
                }
                if (spreadsheet.getEntries().size() == 1) {
                    return spreadsheet.getEntries().get(0);
                }
            }
            else {
                log.warn("Google сервис не предоставил таблиц");
                return null;
            }
        }
        catch (Exception ex) {
            log.error("Проблемы при запросе таблицы с google.", ex);
        }

        return null;
    }

    public static Map<String, Object> getRowData(ListEntry row) {
        Map<String, Object> rowValues = new HashMap<>();
        for (String tag : row.getCustomElements().getTags()) {
            Object value = row.getCustomElements().getValue(tag);
            rowValues.put(tag, value);
        }
        return rowValues;
    }

    public static List<ListEntry> getRowList(SpreadsheetService googleService,
            SpreadsheetEntry ss) {
        List<ListEntry> rows = null;

        try {
            if (ss != null) {
                WorksheetFeed worksheetFeed = googleService.getFeed(
                        ss.getWorksheetFeedUrl(), WorksheetFeed.class);
                List<WorksheetEntry> worksheets = worksheetFeed.getEntries();

                // берем только первую страницу
                WorksheetEntry worksheet = null;
                if (worksheets != null) {
                    worksheet = worksheets.get(0);
                    if (log.isDebugEnabled()) {
                        log.debug("Считываем только первую вкладку");
                    }
                }

                if (worksheet != null) {
                    URL listFeedUrl = worksheet.getListFeedUrl();

                    ListFeed listFeed = googleService.getFeed(
                            listFeedUrl,
                            ListFeed.class);

                    rows = listFeed.getEntries();
                }
            }
        }
        catch (IOException ex1) {
            log.error("Проблемы с получением строковых даных: ", ex1);
        }
        catch (ServiceException ex2) {
            log.error("Проблемы c google сервисом: ", ex2);
        }

        return rows;
    }

    public static List<Map<String, Object>> getRowMap(
            SpreadsheetService googleService, SpreadsheetEntry ss)
            throws IOException, ServiceException {
        List<Map<String, Object>> rows = new ArrayList<>();

        if (ss != null) {
            WorksheetFeed worksheetFeed = googleService.getFeed(
                    ss.getWorksheetFeedUrl(), WorksheetFeed.class);
            List<WorksheetEntry> worksheets = worksheetFeed.getEntries();

            WorksheetEntry worksheet = null;
            if (worksheets != null && worksheets.size() == 1) {
                worksheet = worksheets.get(0);
            }

            if (worksheet != null) {
                URL listFeedUrl = worksheet.getListFeedUrl();

                ListFeed listFeed = googleService.getFeed(
                        listFeedUrl,
                        ListFeed.class);

                for (ListEntry row : listFeed.getEntries()) {
                    Map<String, Object> rowValues = GSUtils.getRowData(row);
                    rows.add(rowValues);
                }

                for (Map<String, Object> row : rows) {
                    if (log.isDebugEnabled()) {
                        log.debug(row.toString());
                    }
                }
            }

        }

        return rows;

    }

}
