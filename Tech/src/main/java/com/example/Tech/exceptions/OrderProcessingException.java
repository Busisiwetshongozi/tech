package com.example.Tech.exceptions;

public class OrderProcessingException extends Throwable  {    public OrderProcessingException(String message) {
  super(message);
}

  public OrderProcessingException(String message, Throwable cause) {
    super(message, cause);
  }

}