package com.goodworkalan.cups;

import com.goodworkalan.danger.ContextualDanger;

@SuppressWarnings("serial")
public class CupsError extends ContextualDanger {
    /**
     * Create a mix error with the given error code.
     * 
     * @param context
     *            The error context.
     * @param code
     *            The error code.
     */
    public CupsError(Class<?> context, String code, Object...arguments) {
        super(context, code, null, arguments);
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
        super(context, code, cause);
    }
}
