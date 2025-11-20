package com.chat.mediator;

import com.chat.user.User;

public interface ChatMediator {
  void sendMessage(String msg, User user); // Enviar a grupo
  void sendPrivateMessage(String msg, User sender, String receiverName); // Enviar privado
  void addUser(User user);
}


