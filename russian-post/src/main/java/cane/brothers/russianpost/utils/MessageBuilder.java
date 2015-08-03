package cane.brothers.russianpost.utils;

import java.util.Set;

import cane.brothers.mail.MessageContext;
import cane.brothers.russianpost.client.data.OldPostEntry;
import cane.brothers.russianpost.client.data.PostEntry;
import cane.brothers.russianpost.client.data.TreatmentPostEntry;

public class MessageBuilder {

	private Set<? extends PostEntry> input;
	private Set<PostEntry> output;
	private MessageContext context;

	private int amountAttended;

	private StringBuilder message = new StringBuilder();

	/**
	 * Constructor
	 * 
	 * @param context
	 * @param input
	 * @param output
	 */
	public MessageBuilder(MessageContext context, Set<? extends PostEntry> input, Set<PostEntry> output) {
		this.context = context;
		this.input = input;
		this.output = output;
		// int amount = PostUtils.getDelayed(output) +
		// PostUtils.getInvalid(output);
		this.amountAttended = PostUtils.getAttended(output);
	}

	public MessageBuilder build() {

		if (context.ifErrors()) {
			message.append(context.getErrorMessage());

		} else {
			// 3. зависшие посылки
			if (amountAttended > 0) {
				context.addMessage3("Возможны проблемы со следующими почтовыми отправлениями: ");
				for (PostEntry postEntry : output) {
					if (postEntry.isNeedAttetion()) {
						context.addMessage3(postEntry.toString());
					}
				}
			} else {
				context.addMessage3("Зависших почтовых отправлений нет");
			}

			// 4. исходные баркоды
			if (input != null && input.size() > 0) {
				context.addMessage4("Обработанные баркоды: ");
				for (PostEntry postEntry : input) {
					if (postEntry instanceof TreatmentPostEntry) {
						TreatmentPostEntry tpe = (TreatmentPostEntry) postEntry;
						if (tpe.isTreated()) {
							context.addMessage4(postEntry.getBarcode());
						}
					}
				}
			} else {
				context.addMessage4("Баркоды не считаны");
			}

			// 5. старые баркоды
			int old = PostUtils.getOld(output);

			if (old > 0) {
				context.addMessage5("Под удаление " + old + " посылок:");
				for (PostEntry postEntry : output) {
					if (postEntry instanceof OldPostEntry) {
						context.addMessage5(postEntry.toString());
					}
				}
			} else {
				context.addMessage5("удалять нечего");
			}

			//
			message.append(context.getMessage1()).append("\n\r").append("\n\r");
			message.append(context.getMessage2()).append("\n\r").append("\n\r");
			message.append(context.getMessage3()).append("\n\r").append("\n\r");
			message.append(context.getMessage4()).append("\n\r").append("\n\r");
			message.append(context.getMessage5()).append("\n\r").append("\n\r");

		}

		return this;
	}

	public String getAmountAttended() {
		if (context.ifErrors()) {
			return "?";
		} else {
			return String.valueOf(amountAttended);
		}
	}

	public String getMessage() {
		return message.toString();
	}

}
