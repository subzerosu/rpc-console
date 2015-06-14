package cane.brothers.mail;

import java.util.Set;

import javax.mail.Message.RecipientType;

import org.codemonkey.simplejavamail.Email;
import org.codemonkey.simplejavamail.MailException;
import org.codemonkey.simplejavamail.Mailer;
import org.codemonkey.simplejavamail.TransportStrategy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cane.brothers.russianpost.client.data.DelayedPostEntry;
import cane.brothers.russianpost.client.data.InvalidPostEntry;
import cane.brothers.russianpost.client.data.OldPostEntry;
import cane.brothers.russianpost.client.data.PostEntry;
import cane.brothers.russianpost.config.Config;
import cane.brothers.russianpost.utils.PostUtils;

public class EmailSender {

	private static final Logger log = LoggerFactory
			.getLogger(EmailSender.class);

	public static boolean sendEmail(Set<PostEntry> output) {
		// try {
		// Thread t = new Thread(new EmailRunnable());
		// t.start();
		// } catch (Exception e) {
		// log.error("проблемы при отправки сообщения..", e);
		// }
		if (log.isDebugEnabled()) {
			log.debug("Подготовка к отправке письма...");
		}

		int amount = PostUtils.getDelayed(output)
				+ PostUtils.getInvalid(output);

		int old = PostUtils.getOld(output);

		final Email email = new Email();
		email.setFromAddress(Config.getMailUser(), Config.getMailFrom());
		email.addRecipient("Subscriber", Config.getMailTo(), RecipientType.TO);
		email.setSubject(Config.getMailSubjecy() + ": " + amount);

		// add body text
		StringBuilder bodyText = new StringBuilder();
		if (amount > 0) {
			bodyText.append(
					"Возможны проблемы со следующими почтовыми отправлениями: ")
					.append("\r\n").append("\r\n");
			for (PostEntry postEntry : output) {
				if ((postEntry instanceof DelayedPostEntry)
						|| (postEntry instanceof InvalidPostEntry)) {
					bodyText.append(postEntry.toString()).append("\r\n");
				}
			}
		} else {
			bodyText.append("зависших почтовых отправлений нет");
		}

		bodyText.append("\r\n");

		if (old > 0) {
			bodyText.append("\r\n").append("Под удаление: ").append(old)
					.append(" посылок:").append("\r\n").append("\r\n");
			for (PostEntry postEntry : output) {
				if (postEntry instanceof OldPostEntry) {
					bodyText.append(postEntry.toString()).append("\r\n");
				}
			}
		} else {
			bodyText.append("удалять нечего");
		}

		email.setText(bodyText.toString());

		log.info("Отправляем письмо на {}", Config.getMailTo());

		try {
			new Mailer(Config.getMailHostName(), Config.getMailPort(),
					Config.getMailFrom(), Config.getMailPassword(),
					TransportStrategy.SMTP_SSL).sendMail(email);

			log.info("Сообщение было отправлено успешно");
			return true;
		} catch (MailException mex) {
			log.error("Проблемы с отправкой сообщения. ", mex);
		}

		return false;
	}

}
