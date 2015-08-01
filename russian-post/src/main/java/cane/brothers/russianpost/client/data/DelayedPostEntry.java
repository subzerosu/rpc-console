package cane.brothers.russianpost.client.data;

/**
 * Простой класс описывающий почтовое отправление которое зависло на одной из
 * стадий, не дойдя до стадии вручения.
 * 
 * @author cane
 */
public class DelayedPostEntry extends PostEntry {

	public DelayedPostEntry() {
	}

	public DelayedPostEntry(PostEntry postEtrty) {
		super(postEtrty);
	}

	public DelayedPostEntry(String barcode, String article, String date) {
		super(barcode, article, date);
	}

}
