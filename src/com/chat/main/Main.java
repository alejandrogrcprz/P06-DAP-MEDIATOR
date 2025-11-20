package com.chat.main;

import com.chat.mediator.ChatMediator;
import com.chat.mediator.ChatRoom;
import com.chat.user.GUIUser;
import com.chat.user.User;
import javax.swing.*;

public class Main {
  public static void main(String[] args) {
    // 1. ESTO ES CLAVE: Activa el estilo visual del sistema operativo
    try {
      UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
    } catch (Exception e) {
      e.printStackTrace();
    }

    SwingUtilities.invokeLater(() -> {
      ChatMediator chatRoom = new ChatRoom();

      User user1 = new GUIUser(chatRoom, "Ana");
      User user2 = new GUIUser(chatRoom, "Beto");
      User user3 = new GUIUser(chatRoom, "Carla");
      User user4 = new GUIUser(chatRoom, "Dani");

      chatRoom.addUser(user1);
      chatRoom.addUser(user2);
      chatRoom.addUser(user3);
      chatRoom.addUser(user4);

      System.out.println("Chat Profesional iniciado.");
    });
  }
}