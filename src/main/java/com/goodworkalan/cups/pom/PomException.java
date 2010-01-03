package com.goodworkalan.cups.pom;

import java.util.ResourceBundle;
import java.util.concurrent.ConcurrentHashMap;

import com.goodworkalan.danger.CodedDanger;

public class PomException extends CodedDanger {
    /** A cache of resource bundles. */
    private final static ConcurrentHashMap<String, ResourceBundle> bundles = new ConcurrentHashMap<String, ResourceBundle>();

    /** Serial version id. */
    private static final long serialVersionUID = 1L;

    /** Unable to create an XML parser to read a POM. */
    public final static int ARTIFACT_NOT_FOUND = 400;

    /** Unable to create an XML parser to read a POM. */
    public final static int CANNOT_CREATE_XML_PARSER = 501;

    /** POM file not found. */
    public final static int POM_FILE_NOT_FOUND = 502;

    /** An I/O exception was thrown while reading a POM file. */
    public final static int POM_IO_EXCEPTION = 503;

    /** Unable to parse the XML in a POM file. */
    public final static int POM_SAX_EXCEPTION = 504;

    /** POM file not found in repository. */
    public final static int POM_URI_NOT_FOUND = 505;

    /** Unable to create task. */
    public final static int CANNOT_CREATE_TASK = 601;

    /** Unable to find a string constructor for argument of type. */
    public final static int CANNOT_CREATE_FROM_STRING = 602;

    /** Invocation of string constructor on an argument failed. */
    public final static int STRING_CONSTRUCTOR_ERROR = 603;

    /** Invocation of string constructor rejected the string data. */
    public final static int STRING_CONVERSION_ERROR = 604;

    /**
     * Create a cups exception with the given error code and the given cause.
     * 
     * @param code
     *            The error code.
     * @param cause
     *            The cause.
     */
    public PomException(int code) {
        super(bundles, code, null);
    }

    /**
     * Create a cups exception with the given error code and the given cause.
     * 
     * @param code
     *            The error code.
     * @param cause
     *            The cause.
     */
    public PomException(int code, Throwable cause) {
        super(bundles, code, cause);
    }
}