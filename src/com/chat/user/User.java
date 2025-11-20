package com.chat.user;

import com.chat.mediator.ChatMediator;

public abstract class User {
  protected ChatMediator mediator;
  protected String name;

  public User(ChatMediator med, String name){
    this.mediator = med;
    this.name = name;
  }

  public String getName() {
    return name;
  }

  // Métodos que la interfaz gráfica deberá implementar
  public abstract void send(String msg);
  public abstract void sendPrivate(String msg, String receiverName);
  public abstract void receive(String msg, String senderName, boolean isPrivate);
}