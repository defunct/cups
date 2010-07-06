package com.goodworkalan.cups;

import com.goodworkalan.go.go.Commandable;
import com.goodworkalan.go.go.Environment;

/**
 * The cups command is used for qualification of sub-commands only. It provides
 * no functionality. It simply indicates that the sub-commands are contained by
 * the Cups application namespace.
 * 
 * @author Alan Gutierrez
 */
public class CupsCommand implements Commandable {
    /**
     * Does nothing.
     * 
     * @param env
     *            The environment.
     */
    public void execute(Environment env) {
    }
}
