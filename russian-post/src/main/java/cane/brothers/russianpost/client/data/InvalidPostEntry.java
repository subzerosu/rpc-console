package cane.brothers.russianpost.client.data;

public class InvalidPostEntry extends PostEntry {

	private String reason;

	public InvalidPostEntry(PostEntry post, String reason) {
		super(post.getBarcode(), post.getArticle(), post.getDate());
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
