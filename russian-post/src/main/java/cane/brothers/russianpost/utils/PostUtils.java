package cane.brothers.russianpost.utils;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import javax.xml.datatype.XMLGregorianCalendar;

import org.russianpost.operationhistory.data.Address;
import org.russianpost.operationhistory.data.AddressParameters;
import org.russianpost.operationhistory.data.OperationHistoryData;
import org.russianpost.operationhistory.data.OperationHistoryRecord;
import org.russianpost.operationhistory.data.OperationParameters;
import org.russianpost.operationhistory.data.Rtm02Parameter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cane.brothers.russianpost.client.data.UndeliveredPostEntry;
import cane.brothers.russianpost.client.data.DelayedPostEntry;
import cane.brothers.russianpost.client.data.InvalidPostEntry;
import cane.brothers.russianpost.client.data.InvalidReasons;
import cane.brothers.russianpost.client.data.OldPostEntry;
import cane.brothers.russianpost.client.data.PostEntry;
import cane.brothers.russianpost.client.data.TreatmentPostEntry;
import cane.brothers.russianpost.config.Config;

import com.google.gdata.data.spreadsheet.ListEntry;

public class PostUtils {

	private static final Logger log = LoggerFactory.getLogger(PostUtils.class);

	/**
	 * 
	 * @param mailing
	 * @param operHistory
	 * @return
	 */
	public static PostEntry getOldPost(PostEntry mailing,
			OperationHistoryData operHistory) {

		List<OperationHistoryRecord> operHistoryList = operHistory
				.getHistoryRecord();

		// is parcel delivered to the Post Department
		boolean isDepDelivered = false;
		Date depDeliveredDate = null;
		String address = null;

		// проходим по всем записям
		for (OperationHistoryRecord operRecord : operHistoryList) {
			OperationParameters operParams = operRecord
					.getOperationParameters();

			// тип операции
			Rtm02Parameter operType = operParams.getOperType();

			// атрибут операции
			Rtm02Parameter operAttr = operParams.getOperAttr();

			// get date
			XMLGregorianCalendar calendar = operParams.getOperDate();
			Date date = calendar.toGregorianCalendar().getTime();

			if (log.isDebugEnabled()) {
				log.debug(" " + mailing + ": [" + DateUtils.getDateTime(date)
						+ "] (" + operType.getId() + ") " + operType.getName()
						+ "\t(" + operAttr.getId() + ")" + operAttr.getName());
			}

			// вручение адресату
			if (isPresented(operType, operAttr)) {
				if (log.isDebugEnabled()) {
					log.debug("\tПосылка была вручена. " + mailing.getBarcode()
							+ " [" + operAttr.getName() + "]");
				}
				// return mailing.getBarcode();
				return new OldPostEntry(mailing.getBarcode(),
						mailing.getArticle(), "Посылка была вручена.", -1);
			}

			// Проверяем была ли посылка доставленна в почтовое отделение
			if (isDelivered(operType, operAttr)) {
				if (log.isDebugEnabled()) {
					log.debug(mailing.getBarcode() + ": ["
							+ DateUtils.getDateTime(date) + "] "
							+ operAttr.getName());
				}

				isDepDelivered = true;
				depDeliveredDate = date;
				address = getDestinationAddress(operRecord);
				if (log.isDebugEnabled()) {
					log.debug("\tПосылка доставлена {} по адресу: {}",
							depDeliveredDate, address);
				}
			}

			// возврат: истек срок хранения
			if (isComeback(operType, operAttr)) {
				if (log.isDebugEnabled()) {
					log.debug(mailing.getBarcode() + " [" + operType.getName()
							+ ": " + operAttr.getName() + "]");
				}
				log.warn("Возврат. Истек срок хранения");
				// return mailing.getBarcode();
				return new OldPostEntry(mailing.getBarcode(),
						mailing.getArticle(), "Возврат. Истек срок хранения.",
						-1);
			}

			// отказ
		}

		// если доставили в отделение, и вышел срок
		// (скорее всего вручили, но по истории не видно)
		if (isDepDelivered) {

			// System.out.println(DateUtils.getLongDate(depDeliveredDate));

			Calendar controlCalendar = Calendar.getInstance();
			controlCalendar.setTime(depDeliveredDate);
			controlCalendar.add(Calendar.DAY_OF_MONTH,
					Config.getDeliveryDelay());

			if (log.isDebugEnabled()) {
				log.debug(" Контрольная дата вручения: "
						+ DateUtils.getLongDate(controlCalendar.getTime()));
			}

			// задержка хранения
			int delay = DateUtils.getDayDifference(depDeliveredDate,
					Config.getDate());
			
			if(log.isDebugEnabled()) {
				log.debug(" Задержка хранения: " + delay);
			}

			if (delay >= Config.getDeliveryDelay()) {
				log.warn(" От даты поступления посылки в почтовое отделение прошло уже "
						+ delay + " дней");

				// return mailing.getBarcode();
				return new OldPostEntry(mailing.getBarcode(),
						mailing.getArticle(), "Старое ПО.", delay);
			}
		}

		return null;
	}

	/**
	 * @param mailing
	 * @param operHistory
	 * @return
	 */
	public static PostEntry getDelayedPost(PostEntry mailing,
			OperationHistoryData operHistory) {

		if (log.isDebugEnabled()) {
			log.debug("Cчитываем историю запросов:");
		}
		List<OperationHistoryRecord> operHistoryList = operHistory
				.getHistoryRecord();

		if (log.isDebugEnabled()) {
			log.debug("Проверяет наличие истории");
		}
		if (operHistoryList.isEmpty()) {
			if (mailing != null) {
				log.error("нет истории запросов по: " + mailing.getBarcode());
				return new InvalidPostEntry(mailing, InvalidReasons.EMPTY);
			}
		}

		// is parcel delivered to the Post Department
		boolean isDepDelivered = false;
		Date depDeliveredDate = null;
		String depDeliveryAddress = null;

		if (log.isDebugEnabled()) {
			log.debug(" Проходим по всем записям: ");
		}
		
		OperationHistoryRecord lastOperRecord = null;
		
		//
		for (OperationHistoryRecord operRecord : operHistoryList) {

			lastOperRecord = operRecord;
			
			// get operation parameters
			OperationParameters operParams = operRecord
					.getOperationParameters();

			// get type and attribute
			Rtm02Parameter operType = operParams.getOperType();
			Rtm02Parameter operAttr = operParams.getOperAttr();

			// get date
			XMLGregorianCalendar calendar = operParams.getOperDate();
			Date date = calendar.toGregorianCalendar().getTime();
			
			if (log.isDebugEnabled()) {
				log.debug(" " + mailing + ": [" + DateUtils.getDateTime(date)
						+ "] Type(" + operType.getId() + ") " + operType.getName()
						+ "\t Attr(" + operAttr.getId() + ")" + operAttr.getName());
			}

			// Проверяем была ли посылка доставленна в почтовое отделение
			if (isDelivered(operType, operAttr)) {
				if (log.isDebugEnabled()) {
					log.debug(mailing.getBarcode() + ": ["
							+ DateUtils.getDateTime(date) + "] "
							+ operAttr.getName());
				}

				isDepDelivered = true;
				depDeliveredDate = date;
				depDeliveryAddress = getDestinationAddress(operRecord);
				if (log.isDebugEnabled()) {
					log.debug(" Посылка доставлена {} по адресу: {}",
							depDeliveredDate, depDeliveryAddress);
				}
			}

			// Проверяем была ли посылка вручена адресату
			if (isPresented(operType, operAttr)) {
				if (log.isDebugEnabled()) {
					log.debug(" Посылка была вручена");
				}
				return null;
			}

			// возврат: истек срок хранения
			if (isComeback(operType, operAttr)) {
				if (log.isDebugEnabled()) {
					log.debug(" У посылки истек срок хранения");
				}
				return new InvalidPostEntry(mailing, InvalidReasons.EXPIRED);
			}
		}

		PostEntry delayedPostEntry = null;
		
		// если доставили в отделение, но не вручили
		if (isDepDelivered) {
			delayedPostEntry = verifyDeliveryDelay(mailing, depDeliveredDate, depDeliveryAddress);
		}
		
		// проверяем последнюю существующую запись в истории отправлени, 
		// если посылка точно не зависла в почтовом отделении
		if(delayedPostEntry == null && lastOperRecord != null) {
			delayedPostEntry = verifyOperationDelay(mailing, lastOperRecord);
		}
		
		return delayedPostEntry;
	}


	public static String getDestinationAddress(OperationHistoryRecord operRecord) {
		StringBuilder address = new StringBuilder("");

		// get address parameters
		AddressParameters addrParams = operRecord.getAddressParameters();

		// get destination address
		Address operAddress = addrParams.getOperationAddress();

		// add address index
		address.append(operAddress.getIndex());
		address.append(", ");

		// add address desc
		address.append(operAddress.getDescription());

		return address.toString();
	}

	/**
	 * Возвращает количество зависших ПО среди всех.
	 * 
	 * @param output
	 * @return
	 */
	public static int getDelayed(Set<PostEntry> output) {
		int result = 0;

		if (output != null && output.size() > 0) {
			for (PostEntry e : output) {
				if (e instanceof UndeliveredPostEntry) {
					result++;
				}
			}
		} else {
			if (log.isWarnEnabled()) {
				log.warn("Выходной набор пуст или не существует.");
			}
		}

		return result;
	}

	public static int getInvalid(Set<PostEntry> output) {
		int result = 0;

		if (output != null && output.size() > 0) {
			for (PostEntry e : output) {
				if (e instanceof InvalidPostEntry) {
					result++;
				}
			}
		} else {
			if (log.isWarnEnabled()) {
				log.warn("Выходной набор пуст или не существует.");
			}
		}

		return result;
	}

	public static int getOld(Set<PostEntry> output) {
		int result = 0;

		if (output != null && output.size() > 0) {
			for (PostEntry e : output) {
				if (e instanceof OldPostEntry) {
					result++;
				}
			}
		} else {
			if (log.isWarnEnabled()) {
				log.warn("Выходной набор пуст или не существует.");
			}
		}

		return result;
	}

	// возврат: истек срок хранения
	private static boolean isComeback(Rtm02Parameter operType,
			Rtm02Parameter operAttr) {
		if (operType.getId() == 3) {
			return true;
		}
		return false;
	}

	// посылка была вручена
	private static boolean isPresented(Rtm02Parameter operType,
			Rtm02Parameter operAttr) {
		if (operType.getId() == 2) {
			// if (operAttr.getId() == 1 && operType.getId() == 2) {
			return true;
		}
		return false;
	}

	// Проверяем была ли посылка доставленна в почтовое отделение
	private static boolean isDelivered(Rtm02Parameter operType,
			Rtm02Parameter operAttr) {
		if (operAttr.getId() == 2) {
			// if (operAttr.getId() == 1 && operType.getId() == 2) {
			return true;
		}
		return false;
	}

	// преобразуем списочные данные к набору ПО
	public static Set<PostEntry> transformToPost(List<ListEntry> dataRows, List<String> messages) {
		Set<PostEntry> barcodes = new TreeSet<PostEntry>();
		List<InvalidPostEntry> doubledBarcodes = new ArrayList<InvalidPostEntry>(); 

		if (dataRows == null) {
			log.error("нет исходного списка данных");
			return null;
		}

		for (ListEntry row : dataRows) {
			String barcode = row.getCustomElements().getValue("barcode");
			String article = row.getCustomElements().getValue("article");
			String date = row.getCustomElements().getValue("date");

			if (barcode != null && !barcode.isEmpty()) {
				//TreatmentPostEntry pe = new TreatmentPostEntry(new PostEntry(barcode, article, date));
				//barcodes.add( new PostEntry(null));
				PostEntry pe = new PostEntry(barcode, article, date);
				if(!barcodes.add(pe)) {
					doubledBarcodes.add(new InvalidPostEntry(pe, InvalidReasons.DUPLICATE));
					log.error("баркод " + barcode + " в наборе уже существует.");	
				}
			} else {
				log.error("баркод не задан");
			}
		}
		
		messages.add(" В исходных данных дублей: " + doubledBarcodes.size());

		return barcodes;
	}
	
	public static Set<TreatmentPostEntry> transformToWork(Set<? extends PostEntry> barcodes) {
		Set<TreatmentPostEntry> treateBarcodes = new TreeSet<TreatmentPostEntry>();
		
		for(PostEntry pe : barcodes) {
			treateBarcodes.add( new TreatmentPostEntry(pe));
		}
		
		return treateBarcodes;
	}
	
	/**
	 * Проверяем было ли вручена посылка клиенту, доставленная в почтовое отделени.
	 * 
	 * @param mailing
	 * @param depDeliveredDate
	 * @param depDeliveryAddress
	 * @return
	 */
	private static UndeliveredPostEntry verifyDeliveryDelay(PostEntry mailing, Date depDeliveredDate, String depDeliveryAddress) {
		Calendar startControlCalendar = Calendar.getInstance();
		startControlCalendar.setTime(depDeliveredDate);
		startControlCalendar.add(Calendar.DAY_OF_MONTH,
				Config.getPostDelay());

		if (log.isDebugEnabled()) {
			log.debug(" Контрольная дата вручения: "
					+ DateUtils.getLongDate(startControlCalendar.getTime()));
		}

		Calendar endControlCalendar = Calendar.getInstance();
		endControlCalendar.setTime(depDeliveredDate);
		endControlCalendar.add(Calendar.DAY_OF_MONTH,
				Config.getDeliveryDelay());

		if (log.isDebugEnabled()) {
			log.debug(" Контрольная дата хранения: "
					+ DateUtils.getLongDate(endControlCalendar.getTime()));
		}

		// задержка
		int delay = DateUtils.getDayDifference(depDeliveredDate,
				Config.getDate());

		// если задержка превышает контрольную дату вручения
		if (Config.getPostDelay() <= delay) {
			if (log.isDebugEnabled()) {
				log.debug(" От даты поступления посылки в почтовое отделение прошло уже "
						+ delay + " дней");
			}

			// но не позднее контрольной даты хранения
			if (delay <= Config.getDeliveryDelay()) {
				return new UndeliveredPostEntry(mailing, delay, depDeliveryAddress);
			} else {
					log.warn(" Срок хранения уже истек");
			}
		}
		return null;
	}
	
	private static PostEntry verifyOperationDelay(PostEntry mailing, OperationHistoryRecord lastOperRecord) {
		// get operation parameters
		OperationParameters operParams = lastOperRecord
				.getOperationParameters();
		Rtm02Parameter operType = operParams.getOperType();
		Rtm02Parameter operAttr = operParams.getOperAttr();

		// get date
		XMLGregorianCalendar calendar = operParams.getOperDate();
		Date currentDate = calendar.toGregorianCalendar().getTime();
		
		if (log.isDebugEnabled()) {
			log.debug(" " + mailing + ": [" + DateUtils.getDateTime(currentDate)
					+ "] Type(" + operType.getId() + ") " + operType.getName()
					+ "\t Attr(" + operAttr.getId() + ")" + operAttr.getName());
		}
		
		// задаем дату, после которой операция считается зависшей
		Calendar сontrolCalendar = Calendar.getInstance();
		сontrolCalendar.setTime(currentDate);
		сontrolCalendar.add(Calendar.DAY_OF_MONTH,
				Config.getOperationDelay());

		if (log.isDebugEnabled()) {
			log.debug(" Контрольная дата по операции: "
					+ DateUtils.getLongDate(сontrolCalendar.getTime()));
		}


		// задержка операции
		int operationDelay = DateUtils.getDayDifference(currentDate,
				Config.getDate());
		String operationAddress = null;
		
		// превышает максимальную задержку выполнения операции
		if (operationDelay >= Config.getOperationDelay()) {
			
			// но не позднее контрольной даты хранения
			if (operationDelay <= Config.getDeliveryDelay()) {
				operationAddress = getDestinationAddress(lastOperRecord);
				
				// посылка зависла по операции
				UndeliveredPostEntry undelPostEntry = new UndeliveredPostEntry(mailing, operationDelay, operationAddress);
				return new DelayedPostEntry(undelPostEntry, operType, operAttr);
				
			} else {
					log.warn(" Срок хранения уже истек");
			}
		} 
		
		return null;
	}

}
