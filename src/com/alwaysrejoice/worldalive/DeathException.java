package com.alwaysrejoice.worldalive;

/**
 * When something dies, this will stop any further processing
 */
public class DeathException extends RuntimeException {
 private static final long serialVersionUID = 1L;
 
 public DeathException(String message) {
   super(message);
 }
}
