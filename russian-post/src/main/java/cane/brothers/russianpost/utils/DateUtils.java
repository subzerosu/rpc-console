package cane.brothers.russianpost.utils;

import java.text.DateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class DateUtils {

	private static Locale locale = new Locale("ru", "RU");
	private static DateFormat longDateFormat = DateFormat.getDateInstance(
			DateFormat.LONG, locale);

	private static DateFormat shortDateFormat = DateFormat.getDateInstance(
			DateFormat.SHORT, locale);

	private static DateFormat dateTimeFormat = DateFormat.getDateTimeInstance(
			DateFormat.MEDIUM, DateFormat.MEDIUM, locale);

	public static String getLongDate(Date date) {
		return longDateFormat.format(date);
	}

	public static String getDateTime(Date date) {
		return dateTimeFormat.format(date);
	}

	public static String getDate(Date date) {
		return shortDateFormat.format(date);
	}

	/**
	 * Получить разницу в днях между двумя датами
	 * 
	 * @param date1
	 *            Date 1
	 * @param date2
	 *            Date 2
	 * @return возвращает разницу в днях между двух дат без учета часов
	 */
	public static int getDayDifference(Date date1, Date date2) {

		Calendar c1 = Calendar.getInstance();
		c1.setTime(date1);

		Calendar c2 = Calendar.getInstance();
		c2.setTime(date2);

		return getDayDifference(c1, c2);
	}

	/**
	 * Получить разницу в днях между двумя датами в календаре
	 * 
	 * @param c1
	 *            Calendar 1
	 * @param c2
	 *            Calendar 2
	 * @return возвращает разницу в днях между двух дат без учета часов
	 */
	public static int getDayDifference(Calendar c1, Calendar c2) {
		// DateFormat dateFormat = DateFormat.getDateInstance(DateFormat.LONG,
		// Locale.getDefault());

		// System.out.println(dateFormat.format(c1.getTime()));
		// System.out.println(dateFormat.format(c2.getTime()));

		int y1 = c1.get(Calendar.YEAR);
		int y2 = c2.get(Calendar.YEAR);

		int y = 0;
		boolean firstLess = true;
		if (y2 >= y1) {
			y = y2 - y1;
		} else {
			y = y1 - y2;
			firstLess = false;
		}
		// System.out.println(y);

		int d1 = c1.get(Calendar.DAY_OF_YEAR);
		// System.out.println(d1);

		int d2 = c2.get(Calendar.DAY_OF_YEAR);
		// System.out.println(d2);

		int d = 0;

		// первый год меньше или равен
		if (firstLess) {

			// сравниваем только по дням
			if (y == 0) {
				if (d1 >= d2) {
					d = d1 - d2;
				} else {
					d = d2 - d1;
				}
			}
			// учитываем года
			else {
				d = 365 * y - d1 + d2;
			}
		}
		// первый год больше
		else {
			d = 365 * y - d2 + d1;
		}

		return d;
	}
}
