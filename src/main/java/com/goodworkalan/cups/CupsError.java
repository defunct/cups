package com.goodworkalan.cups;

import java.util.ResourceBundle;
import java.util.concurrent.ConcurrentHashMap;

import com.goodworkalan.danger.ContextDanger;

@SuppressWarnings("serial")
public class CupsError extends ContextDanger {
    /** A cache of resource bundles. */
    private final static ConcurrentHashMap<String, ResourceBundle> bundles = new ConcurrentHashMap<String, ResourceBundle>();

    /**
     * Create a mix error with the given error code.
     * 
     * @param context
     *            The error context.
     * @param code
     *            The error code.
     */
    public CupsError(Class<?> context, String code) {
        super(bundles, context, code, null);
    }

    /**
     * Create a mix error with the given error code.
     * 
     * @param context
     *            The error context.
     * @param code
     *            The error code.
     * @param cause
     *            The cause.
     */
    public CupsError(Class<?> context, String code, Throwable cause, Object...arguments) {
        super(bundles, context, code, cause);
    }
}
