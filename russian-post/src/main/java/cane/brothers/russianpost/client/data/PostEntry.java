package cane.brothers.russianpost.client.data;

/**
 * Простой класс описывающий почтовое отправление. <br />
 * Все ПО сортируются по номеру заказа.
 *
 * @author cane
 */
public class PostEntry implements Comparable<PostEntry> {

	protected String barcode;
	protected String article;
	protected String date;
	protected boolean needAttetion = false;
	
	/**
	 * default constructor
	 */
	public PostEntry() {
		super();
	}
	
	/**
	 * Copy constructor
	 * 
	 * @param postEtrty not null post entry
	 */
	public PostEntry(PostEntry postEtrty) {
		this(postEtrty.getBarcode(), postEtrty.getArticle(), postEtrty.getDate(), postEtrty.isNeedAttetion());
	}
	
	
	/**
	 * @param postEtrty
	 * @param isNeedAttetion
	 */
	public PostEntry(PostEntry postEtrty, boolean isNeedAttetion) {
		this(postEtrty.getBarcode(), postEtrty.getArticle(), postEtrty.getDate(), isNeedAttetion);
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
		this(barcode, article, date, false);
	}
	
	/**
	 * @param barcode
	 * @param article
	 * @param date
	 * @param needAttetion
	 */
	public PostEntry(String barcode, String article, String date, boolean needAttetion) {
		super();
		this.barcode = barcode;
		this.article = article;
		this.date = date;
		this.needAttetion = needAttetion;
	}


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

	public boolean isNeedAttetion() {
		return needAttetion;
	}

	public void setNeedAttetion(boolean needAttetion) {
		this.needAttetion = needAttetion;
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
		StringBuilder builder = new StringBuilder();
		builder.append("ПО ").append(getBarcode()).append("; номер заказа: ")
				.append(getArticle());
		return builder.toString();
	}
}
