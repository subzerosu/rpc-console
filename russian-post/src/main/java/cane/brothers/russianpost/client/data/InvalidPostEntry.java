package cane.brothers.russianpost.client.data;

/**
 * Простой класс описывающий почтовое отправление которое имеет какие-либо проблемы.
 * 
 * <br />
 * Возможные причины проблем:
 * <ul>
 * <li>дублирующая запись в исходных баркодах</li>
 * <li>история запросов пуста или ее не удалось получить</li>
 * </ul>
 * 
 * @author cane
 */
public class InvalidPostEntry extends PostEntry {

	private InvalidReasons reason;

	/**
	 * Constructor
	 * 
	 * @param post
	 * @param reason
	 */
	public InvalidPostEntry(PostEntry post, InvalidReasons reason) {
		super(post);
		this.reason = reason;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("ПО ").append(getBarcode()).append("; номер заказа: ")
				.append(getArticle()).append("; проблемы: ").append(reason);
		return builder.toString();
	}

}
