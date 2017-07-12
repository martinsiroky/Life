package cz.fg.ms.life.exception;

/**
 * Reading configuration failed exception
 * 
 * @author Martin Siroky
 */
public class ConfigurationException extends Exception {

	private static final long serialVersionUID = 7553384330679709548L;

	public ConfigurationException() {
		super();
	}

	public ConfigurationException(String message, Throwable cause, boolean enableSuppression,
			boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public ConfigurationException(String message, Throwable cause) {
		super(message, cause);
	}

	public ConfigurationException(String message) {
		super(message);
	}

	public ConfigurationException(Throwable cause) {
		super(cause);
	}

}
