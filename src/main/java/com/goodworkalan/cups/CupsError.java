package com.goodworkalan.cups;

import java.util.Locale;
import java.util.ResourceBundle;

/**
 * An exception with internationalized messages for Cups and commands
 * created in the cups namespace.
 *
 * @author Alan Gutierrez
 */
@SuppressWarnings("serial")
public class CupsError extends RuntimeException {
    /** The message context class. */
    public final Class<?> contextClass;
    
    /** The message error code. */
    public final String code;

	/**
     * Create a mix error with the given error code.
     * 
     * @param context
     *            The error context.
     * @param code
     *            The error code.
     */
    public CupsError(Class<?> context, String code, Object...arguments) {
        this(context, code, null, arguments);
    }

    /**
     * Create a mix error with the given error code.
     * 
     * @param contextClass
     *            The error context.
     * @param code
     *            The error code.
     * @param cause
     *            The cause.
     */
    public CupsError(Class<?> contextClass, String code, Throwable cause, Object...arguments) {
        super(formatMessage(contextClass, code, arguments), cause);
        this.contextClass = contextClass;
        this.code = code;
    }

    /**
     * Format the exception message using the message arguments to format the
     * message found with the message key in the message bundle found in the
     * package of the given context class.
     * 
     * @param contextClass
     *            The context class.
     * @param code
     *            The error code.
     * @param arguments
     *            The format message arguments.
     * @return The formatted message.
     */
    private final static String formatMessage(Class<?> contextClass, String code, Object...arguments) {
        String baseName = contextClass.getPackage().getName() + ".exceptions";
        String messageKey = contextClass.getSimpleName() + "/" + code;
        try {
            ResourceBundle bundle = ResourceBundle.getBundle(baseName, Locale.getDefault(), Thread.currentThread().getContextClassLoader());
            return String.format((String) bundle.getObject(messageKey), arguments);
        } catch (Exception e) {
            return String.format("Cannot load message key [%s] from bundle [%s] becuase [%s].", messageKey, baseName, e.getMessage());
        }
    }
}
