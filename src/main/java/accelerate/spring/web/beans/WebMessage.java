package accelerate.spring.web.beans;

import java.io.Serializable;

import accelerate.commons.utils.StringUtils;

/**
 * Basic class to pass message with attributed to UI layer
 * 
 * @version 1.0 Initial Version
 * @author Rohit Narayanan
 * @since October 20, 2018
 */
public class WebMessage implements Serializable {
	/**
	 * serialVersionUID
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * {@link MessageType}
	 */
	private MessageType messageType;

	/**
	 * Message Code
	 */
	private String messageCode;

	/**
	 * Message Text
	 */
	private String messageText;

	/**
	 * @param aMessageType
	 * @param aMessageCode
	 * @param aMessageText
	 */
	public WebMessage(MessageType aMessageType, String aMessageCode, String aMessageText) {
		this.messageType = aMessageType;
		this.messageCode = aMessageCode;
		this.messageText = aMessageText;
	}

	/**
	 * @param aMessageType
	 * @param aMessageCode
	 */
	public WebMessage(MessageType aMessageType, String aMessageCode) {
		this.messageType = aMessageType;
		this.messageCode = aMessageCode;
	}

	/**
	 * Overloaded Setter method for "messageType" property accepting argument orf
	 * type {@link String}
	 *
	 * @param aMessageType
	 */
	public void setMessageType(String aMessageType) {
		this.messageType = MessageType.getMessageType(aMessageType);
	}

	/**
	 * Getter method for "messageType" property
	 *
	 * @return messageType
	 */
	public MessageType getMessageType() {
		return this.messageType;
	}

	/**
	 * Setter method for "messageType" property
	 *
	 * @param aMessageType
	 */
	public void setMessageType(MessageType aMessageType) {
		this.messageType = aMessageType;
	}

	/**
	 * Getter method for "messageCode" property
	 *
	 * @return messageCode
	 */
	public String getMessageCode() {
		return this.messageCode;
	}

	/**
	 * Setter method for "messageCode" property
	 *
	 * @param aMessageCode
	 */
	public void setMessageCode(String aMessageCode) {
		this.messageCode = aMessageCode;
	}

	/**
	 * Getter method for "messageText" property
	 *
	 * @return messageText
	 */
	public String getMessageText() {
		return this.messageText;
	}

	/**
	 * Setter method for "messageText" property
	 *
	 * @param aMessageText
	 */
	public void setMessageText(String aMessageText) {
		this.messageText = aMessageText;
	}

	/**
	 * {@link Enum} to define the type of message
	 *
	 * @author Rohit Narayanan
	 * @version 1.0 Initial Version
	 * @since Feb 1, 2014
	 */
	public static enum MessageType {
		/**
		 *
		 */
		INFO,

		/**
		 *
		 */
		SUCCESS,

		/**
		 *
		 */
		ERROR;

		/**
		 * This method returns the {@link MessageType} instance mapped to the given
		 * string
		 *
		 * @param aMessageType
		 * @return {@link MessageType}
		 */
		public static MessageType getMessageType(String aMessageType) {
			for (MessageType messageType : values()) {
				if (StringUtils.safeEquals(messageType.name(), aMessageType)) {
					return messageType;
				}
			}

			return null;
		}
	}
}
