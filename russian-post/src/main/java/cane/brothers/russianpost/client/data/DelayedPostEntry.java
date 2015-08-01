package cane.brothers.russianpost.client.data;

import org.russianpost.operationhistory.data.Rtm02Parameter;

/**
 * Простой класс описывающий почтовое отправление которое зависло на одной из
 * стадий, не дойдя до стадии вручения. Является также недоставленным.
 * 
 * @see UndeliveredPostEntry
 * 
 * @author cane
 */
public class DelayedPostEntry extends UndeliveredPostEntry {

	Rtm02Parameter operationType;

	Rtm02Parameter operationAttr;

	/**
	 * Constructor
	 * 
	 * @param postEtrty
	 *            Undelivered post entry
	 * @param operationType
	 * @param operationAttr
	 */
	public DelayedPostEntry(UndeliveredPostEntry postEtrty, Rtm02Parameter operationType,
			Rtm02Parameter operationAttr) {
		super(postEtrty);
		this.operationType = operationType;
		this.operationAttr = operationAttr;
	}

	/**
	 * @param postEtrty
	 *            Delayed post entry
	 */
	public DelayedPostEntry(DelayedPostEntry postEtrty) {
		super(postEtrty);
		this.operationType = postEtrty.getOperationType();
		this.operationAttr = postEtrty.getOperationAttr();
	}

	public Rtm02Parameter getOperationType() {
		return operationType;
	}

	public void setOperationType(Rtm02Parameter operationType) {
		this.operationType = operationType;
	}

	public Rtm02Parameter getOperationAttr() {
		return operationAttr;
	}

	public void setOperationAttr(Rtm02Parameter operationAttr) {
		this.operationAttr = operationAttr;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("ПО с задержкой операции ").append(getBarcode()).append(delay)
				.append("дней; номер заказа: ").append(getArticle()).append("; операция типа(")
				.append(operationType.getId()).append(") ").append(operationType.getName()).append(" атрибут(")
				.append(operationAttr.getId()).append(") ").append(operationAttr.getName()).append("; по адресу: ")
				.append(postOfficeAddress).append("");
		return builder.toString();
	}

}
