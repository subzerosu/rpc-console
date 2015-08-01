package cane.brothers.russianpost.client.data;

/**
 * Простой класс описывающий почтовое отправление поступившее в почтовое
 * отделение клиента, "зависшее" там и не врученное клиенту.
 * 
 * @author cane
 */
public class UndeliveredPostEntry extends PostEntry {

	/**
	 * Constructor 
	 * 
	 * @param entry почтовое отправление
	 * @param delay задержка 
	 * @param address адрес почтового отделения
	 */
	public UndeliveredPostEntry(PostEntry entry, int delay,
			String address) {
		super(entry);
		this.delay = delay;
		this.postOfficeAddress = address;
	}

	private int delay;
	private String postOfficeAddress;

	public int getDelay() {
		return delay;
	}

	public void setDelay(int delay) {
		this.delay = delay;
	}

	public String getPostOfficeAddress() {
		return postOfficeAddress;
	}

	public void setPostOfficeAddress(String address) {
		this.postOfficeAddress = address;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("ПО ").append(getBarcode()).append("; номер заказа: ")
				.append(getArticle()).append("; задержка ").append(delay)
				.append(" дней по адресу: ").append(postOfficeAddress)
				.append("");
		return builder.toString();
	}
}
