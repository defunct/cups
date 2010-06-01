package com.goodworkalan.cups.pom;

import java.util.ResourceBundle;
import java.util.concurrent.ConcurrentHashMap;

import com.goodworkalan.danger.CodedDanger;

public class PomException extends CodedDanger {
    /** A cache of resource bundles. */
    private final static ConcurrentHashMap<String, ResourceBundle> bundles = new ConcurrentHashMap<String, ResourceBundle>();

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
        super(bundles, code, null, arguments);
    }
}