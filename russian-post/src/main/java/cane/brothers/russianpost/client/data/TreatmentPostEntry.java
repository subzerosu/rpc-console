package cane.brothers.russianpost.client.data;

public class TreatmentPostEntry extends PostEntry {


	private boolean isTreated;
	
	/**
	 * Constructor
	 * 
	 * @param post post entry
	 */
	public TreatmentPostEntry(PostEntry post) {
		this(post, false);
	}
	
	/**
	 * Constructor
	 * 
	 * @param post post entry
	 * @param treated is treaded flag
	 */
	public TreatmentPostEntry(PostEntry post, boolean treated) {
		super(post);
		isTreated = treated;
	}

	public boolean isTreated() {
		return isTreated;
	}

	public void setTreated(boolean isTreated) {
		this.isTreated = isTreated;
	}
	
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("ПО ").append(getBarcode()).append("; номер заказа: ")
				.append(getArticle()).append("; обработано: ").append(isTreated);
		return builder.toString();
	}

}
