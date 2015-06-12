package cane.brothers.russianpost.client.data;

/**
 * Простой POJO класс описывающий почтовое отправление.
 *
 */
public class PostEntry implements Comparable<PostEntry> {

	/**
	 * @param barcode
	 *            номер почтового отправления
	 * @param article
	 *            номер заказа
	 * @param date
	 *            Дата добавления
	 */
	public PostEntry(String barcode, String article, String date) {
		super();
		this.barcode = barcode;
		this.article = article;
		this.date = date;
	}

	protected String barcode;
	protected String article;
	protected String date;

	public String getBarcode() {
		return barcode;
	}

	public void setBarcode(String barcode) {
		this.barcode = barcode;
	}

	public String getArticle() {
		return article;
	}

	public void setArticle(String article) {
		this.article = article;
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	@Override
	public int compareTo(PostEntry o) {
		if (o != null && o.getBarcode() != null) {
			return this.barcode.toUpperCase().compareTo(
					o.getBarcode().toUpperCase());
		} else {
			return 1;
		}
	}

	@Override
	public String toString() {
		return "PostEntry[" + barcode + "]";
	}
}
