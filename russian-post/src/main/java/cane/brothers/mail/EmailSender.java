package cane.brothers.mail;

import javax.mail.Message.RecipientType;

import org.codemonkey.simplejavamail.Email;
import org.codemonkey.simplejavamail.MailException;
import org.codemonkey.simplejavamail.Mailer;
import org.codemonkey.simplejavamail.TransportStrategy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cane.brothers.russianpost.config.Config;
import cane.brothers.russianpost.utils.MessageBuilder;

public class EmailSender {

	private static final Logger log = LoggerFactory
			.getLogger(EmailSender.class);

	public static boolean sendEmail(MessageBuilder messages) {
		// try {
		// Thread t = new Thread(new EmailRunnable());
		// t.start();
		// } catch (Exception e) {
		// log.error("проблемы при отправки сообщения..", e);
		// }
		if (log.isDebugEnabled()) {
			log.debug("Подготовка к отправке письма...");
		}

		final Email email = new Email();
		email.setFromAddress(Config.getMailUser(), Config.getMailFrom());
		email.addRecipient("Subscriber", Config.getMailTo(), RecipientType.TO);
		email.setSubject(Config.getMailSubjecy() + ": " + messages.getAmountAttended());

		// add body text
		email.setText(messages.getMessage());

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
