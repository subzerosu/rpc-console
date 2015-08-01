package cane.brothers.russianpost.client.data;

/**
 * Класс-перечисление определяющий типы проблем с почтовыми отправлениями.
 * 
 * @author cane
 */
public enum InvalidReasons {

	EXPIRED("У посылки истек срок хранения"), 
	DUPLICATE("Дублирующая запись"), 
	EMPTY("История запросов пуста");

	private String reason;

	/**
	 * Constructor
	 * 
	 * @param reason причина проблемы с ПО
	 */
	InvalidReasons(String reason) {
		this.reason = reason;
	}

	public String getReason() {
		return reason;
	}

	@Override
	public String toString() {
		return this.getReason();
	}

}
