package com.example.wundergarten.exceptions;

public class IllegalPageException extends RuntimeException {

	  private static final long serialVersionUID = 1L;
	  
	  /**
	   * Default constructor 
	   * @param message message explaining what went wrong
	   * @param e original exception
	   */
	  public IllegalPageException(String message, Exception e) {
	    super(message, e);
	  }

	  /**
	   * No-exception constructor. Used when there is no original exception
	   *  
	   * @param message message explaining what went wrong
	   */
	  public IllegalPageException(String message) {
	    super(message, null);
	  }

}
