package com.goodworkalan.cups.maven;

import com.goodworkalan.danger.CodedDanger;

// TODO Document.
public class PomException extends CodedDanger {
    /** Serial version id. */
    private static final long serialVersionUID = 1L;

    /** POM file not found. */
    public final static int POM_FILE_NOT_FOUND = 502;

    /**
     * Create a cups exception with the given error code and the given cause.
     * 
     * @param code
     *            The error code.
     * @param arguments
     *            The error message format arguments.
     */
    public PomException(int code, Object...arguments) {
        super(code, null, arguments);
    }
}