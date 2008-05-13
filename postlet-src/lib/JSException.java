/* -*- Mode: Java; tab-width: 8; c-basic-offset: 4 -*-

	This program is free software; you can redistribute it and/or
	modify it under the terms of the GNU General Public License
	as published by the Free Software Foundation; either version 2
	of the License, or (at your option) any later version.

	This program is distributed in the hope that it will be useful,
	but WITHOUT ANY WARRANTY; without even the implied warranty of
	MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
	GNU General Public License for more details.

	You should have received a copy of the GNU General Public License
	along with this program; if not, write to the Free Software
	Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA. */

package netscape.javascript;

/**
 * JSException is an exception which is thrown when JavaScript code
 * returns an error.
 */

public
class JSException extends RuntimeException {
    public static final int EXCEPTION_TYPE_EMPTY = -1;
    public static final int EXCEPTION_TYPE_VOID = 0;
    public static final int EXCEPTION_TYPE_OBJECT = 1;
    public static final int EXCEPTION_TYPE_FUNCTION = 2;
    public static final int EXCEPTION_TYPE_STRING = 3;
    public static final int EXCEPTION_TYPE_NUMBER = 4;
    public static final int EXCEPTION_TYPE_BOOLEAN = 5;
    public static final int EXCEPTION_TYPE_ERROR = 6;

    String filename;
    int lineno;
    String source;
    int tokenIndex;
    private int wrappedExceptionType;
    private Object wrappedException;

    /**
     * Constructs a JSException without a detail message.
     * A detail message is a String that describes this particular exception.
     *
     * @deprecated Not for public use in future versions.
     */
    public JSException() {
	super();
        filename = "unknown";
        lineno = 0;
        source = "";
        tokenIndex = 0;
	wrappedExceptionType = EXCEPTION_TYPE_EMPTY;
    }

    /**
     * Constructs a JSException with a detail message.
     * A detail message is a String that describes this particular exception.
     * @param s the detail message
     *
     * @deprecated Not for public use in future versions.
     */
    public JSException(String s) {
	super(s);
        filename = "unknown";
        lineno = 0;
        source = "";
        tokenIndex = 0;
	wrappedExceptionType = EXCEPTION_TYPE_EMPTY;
    }

    /**
     * Constructs a JSException with a wrapped JavaScript exception object.
     * This constructor needs to be public so that Java users can throw 
     * exceptions to JS cleanly.
     */
    private JSException(int wrappedExceptionType, Object wrappedException) {
	super();
	this.wrappedExceptionType = wrappedExceptionType;
	this.wrappedException = wrappedException;
    }
    
    /**
     * Constructs a JSException with a detail message and all the
     * other info that usually comes with a JavaScript error.
     * @param s the detail message
     *
     * @deprecated Not for public use in future versions.
     */
    public JSException(String s, String filename, int lineno,
                       String source, int tokenIndex) {
	super(s);
        this.filename = filename;
        this.lineno = lineno;
        this.source = source;
        this.tokenIndex = tokenIndex;
	wrappedExceptionType = EXCEPTION_TYPE_EMPTY;
    }

    /**
     * Instance method getWrappedExceptionType returns the int mapping of the
     * type of the wrappedException Object.
     */
    public int getWrappedExceptionType() {
	return wrappedExceptionType;
    }

    /**
     * Instance method getWrappedException.
     */
    public Object getWrappedException() {
	return wrappedException;
    }

}

