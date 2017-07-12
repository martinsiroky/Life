package cz.fg.ms.life.exception;

/**
 * Exception for testing JVM exist status in TestingSecurityManager
 * 
 * @see cz.fg.ms.life.TestingSecurityManager 
 * 
 * @author Martin Siroky
 */
public class ExitException extends SecurityException {

	private static final long serialVersionUID = 8516189990856455635L;

	private final int status;

	public ExitException(int status) {
		this.status = status;
	}

	public int getStatus() {
		return status;
	}
}
