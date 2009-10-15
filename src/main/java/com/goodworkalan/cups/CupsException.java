package com.goodworkalan.cups;

import com.goodworkalan.cassandra.CassandraException;
import com.goodworkalan.cassandra.Report;

public class CupsException extends CassandraException {
    /** Serial version id. */
    private static final long serialVersionUID = 1L;

    /**
     * Create an exception with the given error code.
     * 
     * @param code
     *            The error code.
     */
    public CupsException(int code) {
        super(code, new Report());
    }

    /**
     * Create a mix exception with the given error code and cause.
     * 
     * @param code
     *            The error code.
     * @param cause
     *            The wrapped exception.
     */
    public CupsException(int code, Throwable cause) {
        super(code, new Report(), cause);
    }

    /**
     * Create an exception with the given error code and the given initial
     * report structure.
     * 
     * @param code
     *            The error code.
     * @param report
     *            An initial report structure.
     */
    public CupsException(int code, Report report) {
        super(code, report);
    }

    /**
     * Create an exception with the given error code and the given initial
     * report structure that wraps the given cause exception.
     * 
     * @param code
     *            The error code.
     * @param report
     *            An initial report structure.
     * @param cause
     *            The cause.
     */
    public CupsException(int code, Report report, Throwable cause) {
        super(code, report, cause);
    }

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
}