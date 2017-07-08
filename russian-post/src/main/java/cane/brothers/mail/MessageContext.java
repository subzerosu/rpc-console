package cane.brothers.mail;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MessageContext {

    private static final Logger log = LoggerFactory
            .getLogger(MessageContext.class);

    private static MessageContext instance = null;

    private MessageContext() {
        messages1.append("1. ");
        messages2.append("2. ");
        messages3.append("3. ");
        messages4.append("4. ");
        messages5.append("5. ");
    }

    public static MessageContext getContext() {
        if (instance == null) {
            instance = new MessageContext();
        }
        return instance;
    }

    private StringBuilder messages1 = new StringBuilder();
    private StringBuilder messages2 = new StringBuilder();
    private StringBuilder messages3 = new StringBuilder();
    private StringBuilder messages4 = new StringBuilder();
    private StringBuilder messages5 = new StringBuilder();

    private List<String> errorMessages = new ArrayList<>();

    public void addMessage1(String message) {
        if (log.isDebugEnabled()) {
            log.debug(message);
        }
        messages1.append(message).append("\n\r");
    }

    public void addMessage2(String message) {
        if (log.isDebugEnabled()) {
            log.debug(message);
        }
        messages2.append(message).append("\n\r");
    }

    public void addMessage3(String message) {
        if (log.isDebugEnabled()) {
            log.debug(message);
        }
        messages3.append(message).append("\n\r");
    }

    public void addMessage4(String message) {
        if (log.isDebugEnabled()) {
            log.debug(message);
        }
        messages4.append(message).append("\n\r");
    }

    public void addMessage5(String message) {
        if (log.isDebugEnabled()) {
            log.debug(message);
        }
        messages5.append(message).append("\n\r");
    }

    public void addErrorMessage(String message) {
        if (log.isDebugEnabled()) {
            log.debug(message);
        }
        errorMessages.add(message);
    }

    public boolean ifErrors() {
        return !errorMessages.isEmpty();
    }

    public StringBuilder getErrorMessage() {
        StringBuilder msg = new StringBuilder();

        for (String m : errorMessages) {
            msg.append(m).append("\r\n");
        }
        msg.append("\r\n");

        return msg;
    }

    public StringBuilder getMessage1() {
        return messages1;
    }

    public StringBuilder getMessage2() {
        return messages2;
    }

    public StringBuilder getMessage3() {
        return messages3;
    }

    public StringBuilder getMessage4() {
        return messages4;
    }

    public StringBuilder getMessage5() {
        return messages5;
    }

}
