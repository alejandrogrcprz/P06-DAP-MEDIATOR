package com.chat.mediator;

import com.chat.user.User;
import java.util.ArrayList;
import java.util.List;

public class ChatRoom implements ChatMediator {
  private List<User> users;

  public ChatRoom(){
    this.users = new ArrayList<>();
  }

  @Override
  public void addUser(User user){
    this.users.add(user);
  }

  @Override
  public void sendMessage(String msg, User user) {
    for(User u : this.users){
      // No enviar el mensaje a quien lo escribió
      if(u != user){
        u.receive(msg, user.getName(), false);
      }
    }
  }

  @Override
  public void sendPrivateMessage(String msg, User sender, String receiverName) {
    boolean found = false;
    for(User u : users){
      // Comparamos nombres ignorando mayúsculas/minúsculas
      if(u.getName().equalsIgnoreCase(receiverName)){
        u.receive(msg, sender.getName(), true);
        found = true;
      }
    }
    if(!found){
      sender.receive("Error: El usuario '" + receiverName + "' no existe o no está conectado.", "SISTEMA", true);
    }
  }
}