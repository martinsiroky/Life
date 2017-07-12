package cz.fg.ms.life;

import java.security.Permission;

import cz.fg.ms.life.exception.ExitException;

/**
 * Security manager to test JVM exit status of main method
 * 
 * @author Martin Siroky
 */
public class TestingSecurityManager extends SecurityManager {

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void checkPermission(Permission perm) {
		// allow anything.
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void checkPermission(Permission perm, Object context) {
		// allow anything.
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void checkExit(int status) {
		throw new ExitException(status);
	}
}
