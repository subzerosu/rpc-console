package cane.brothers.russianpost.client.data;

/**
 * Простой класс описывающий почтовое отправление. <br />
 * Все ПО сортируются по номеру заказа.
 *
 */
public class PostEntry implements Comparable<PostEntry> {

	
	/**
	 * default constructor
	 */
	public PostEntry() {
		super();
	}
	
	/**
	 * Copy constructor
	 * 
	 * @param postEtrty not null post enrty
	 */
	public PostEntry(PostEntry postEtrty) {
		this(postEtrty.getBarcode(), postEtrty.getArticle(), postEtrty.getArticle());
	}
	
	/**
	 * Constructor
	 * 
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
		if (o != null && o.getArticle() != null) {
			return this.article.toUpperCase().compareTo(
					o.getArticle().toUpperCase());
		} else {
			return 1;
		}
	}

	@Override
	public String toString() {
		return "PostEntry[" + barcode + "]";
	}
}
