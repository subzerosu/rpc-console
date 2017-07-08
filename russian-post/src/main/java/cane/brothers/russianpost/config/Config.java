package cane.brothers.russianpost.config;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Date;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cane.brothers.russianpost.utils.DateUtils;

public class Config {

	private static final Logger log = LoggerFactory.getLogger(Config.class);
	
	private static String propFileName = "resources/config.properties";
	private static String clientSecrets = "resources/client_secrets.json";
	
	private static String dataStoreDir = ".store/rpc16";

	private static String postLogin;
	private static String postPassword;
	private static int postDelay;
	private static int deliveryDelay;
	private static int operationDelay;

	private static String charset;
	private static boolean cleanUp;

	private static String mailUser;
	private static String mailFrom;
	private static String mailPassword;
	private static String mailHostName;
	private static int mailPort;
	private static String mailTo;
	private static String mailSubjecy;
	
	/** google */
	private static String googleAppName;
	private static String googleSpreadSheetName;
	//

	private static Date time;

	static {
		try {
			String properiesPath = new File(propFileName).getAbsolutePath();
			if(log.isDebugEnabled()) {
				log.debug("конфигурационный файл: " + properiesPath);
			}

			Properties prop = new Properties();
			prop.load(new FileInputStream(properiesPath));

			time = new Date(System.currentTimeMillis());
			log.info(DateUtils.getDateTime(time));

			// get the property value and print it out
			postLogin = prop.getProperty("login");
			postPassword = prop.getProperty("password");
			postDelay = Integer.valueOf(prop.getProperty("delay", "5"));
			deliveryDelay = Integer.valueOf(prop.getProperty("deliveryDelay",
					"35"));
			operationDelay = Integer.valueOf(prop.getProperty("operationDelay",
					"10")); 

			charset = prop.getProperty("charset", "utf-8");
			cleanUp = Boolean.parseBoolean(prop.getProperty("cleanup", "true"));

			mailUser = prop.getProperty("sender_user");
			mailFrom = prop.getProperty("sender_email");
			mailPassword = prop.getProperty("sender_password");
			mailHostName = prop.getProperty("smtp_host_name");
			mailPort = Integer.valueOf(prop.getProperty("smtp_port", "465"));

			mailTo = prop.getProperty("mail_to");
			mailSubjecy = prop.getProperty("mail_subject");
			
			googleAppName = prop.getProperty("google_app_name", "CaneBrothers-RPC/1.7");
			googleSpreadSheetName = prop.getProperty("google_spreadsheet_name", "barcodes-sample");
			dataStoreDir = prop.getProperty("google_datastore_dir");
			
			log.info("настройки считаны");

		} catch (IOException ex) {
			log.error("Не могу прочитать файл с настройками...", ex);
		}
	}

	public static String getPostLogin() {
		return postLogin;
	}

	public static String getPostPassword() {
		return postPassword;
	}

	public static int getPostDelay() {
		return postDelay;
	}

	public static int getDeliveryDelay() {
		return deliveryDelay;
	}

	public static int getOperationDelay() {
		return operationDelay;
	}
	

	public static Date getDate() {
		return time;
	}

	public static String getMailUser() {
		return mailUser;
	}

	public static String getMailFrom() {
		return mailFrom;
	}

	public static String getMailPassword() {
		return mailPassword;
	}

	public static String getMailHostName() {
		return mailHostName;
	}

	public static int getMailPort() {
		return mailPort;
	}

	public static String getMailTo() {
		return mailTo;
	}

	public static String getMailSubjecy() {
		return mailSubjecy;
	}

	public static Charset getCharset() {
		return Charset.forName(charset);
	}

	public static boolean doCleanUp() {
		return cleanUp;
	}
	
	public static String getGoogleAppName() {
		return googleAppName;
	}
	
	public static String getGoogleSpreadSheetName() {
		return googleSpreadSheetName;
	}
	
	public static String getDataStoreDir() {
		return dataStoreDir;
	}
	
	public static String getClientSecrets() {
		return clientSecrets;
	}

}
