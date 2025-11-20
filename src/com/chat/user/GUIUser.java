package com.chat.user;

import com.chat.mediator.ChatMediator;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.plaf.basic.BasicButtonUI;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

public class GUIUser extends User {
  private JFrame frame;

  // GESTIÓN DE NAVEGACIÓN
  private JPanel rootPanel;
  private CardLayout rootLayout;

  // PANTALLA 1: LISTA
  private JList<String> contactList;
  private DefaultListModel<String> listModel;

  // PANTALLA 2: CHAT
  private JPanel chatInterfacePanel;
  private JLabel headerLabel;
  private JButton backButton; // Hacemos el botón variable de clase para cambiarle el texto
  private JPanel chatCardPanel;
  private CardLayout chatCardLayout;

  // MAPAS
  private Map<String, JPanel> chatPanelsMap;
  private Map<String, Integer> unreadMap;
  private Map<String, String> listDisplayToRealName;

  private String currentChat = "";

  // COLORES
  private final Color COLOR_HEADER = new Color(106, 27, 154);
  private final Color COLOR_BG = new Color(245, 240, 250);
  private final Color COLOR_MINE = new Color(74, 20, 140);
  private final Color COLOR_THEIRS = new Color(156, 39, 176);

  public GUIUser(ChatMediator med, String name) {
    super(med, name);
    chatPanelsMap = new HashMap<>();
    unreadMap = new HashMap<>();
    listDisplayToRealName = new HashMap<>();
    initializeGUI();
  }

  private void initializeGUI() {
    frame = new JFrame("Chat App - " + this.name);
    frame.setSize(500, 750);
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

    rootLayout = new CardLayout();
    rootPanel = new JPanel(rootLayout);

    // ------------------------------------------------
    // VISTA 1: LISTA DE CONTACTOS
    // ------------------------------------------------
    JPanel contactListView = new JPanel(new BorderLayout());

    JLabel mainTitle = new JLabel("Mis Chats", SwingConstants.CENTER);
    mainTitle.setFont(new Font("Segoe UI", Font.BOLD, 20));
    mainTitle.setForeground(Color.WHITE);
    mainTitle.setOpaque(true);
    mainTitle.setBackground(COLOR_HEADER);
    mainTitle.setPreferredSize(new Dimension(0, 60));
    contactListView.add(mainTitle, BorderLayout.NORTH);

    listModel = new DefaultListModel<>();
    addContactToList("GRUPO");
    String[] allUsers = {"Ana", "Beto", "Carla", "Dani"};
    for (String u : allUsers) {
      if (!u.equals(this.name)) addContactToList(u);
    }

    contactList = new JList<>(listModel);
    contactList.setFixedCellHeight(70);
    contactList.setFont(new Font("Segoe UI", Font.PLAIN, 16));
    contactList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    contactList.setBackground(Color.WHITE);

    contactList.setCellRenderer(new DefaultListCellRenderer() {
      @Override
      public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
        JLabel label = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
        label.setBorder(new EmptyBorder(0, 20, 0, 10));
        String text = (String) value;

        // LOGICA VISUAL DE NOTIFICACIÓN EN LISTA
        if (text.contains("(")) {
          label.setFont(label.getFont().deriveFont(Font.BOLD));
          label.setForeground(COLOR_HEADER);
          // Renderiza HTML para poner el número en rojo/negrita
          label.setText("<html>" + text.replace("(", "<font color='rgb(180,0,0)'><b>(").replace(")", ")</b></font>") + "</html>");
        } else {
          label.setForeground(Color.DARK_GRAY);
        }

        if(isSelected){
          label.setBackground(new Color(225, 190, 231));
          label.setForeground(COLOR_HEADER);
        }
        return label;
      }
    });
    contactListView.add(new JScrollPane(contactList), BorderLayout.CENTER);

    // ------------------------------------------------
    // VISTA 2: CHAT
    // ------------------------------------------------
    chatInterfacePanel = new JPanel(new BorderLayout());

    // --- HEADER ---
    JPanel headerPanel = new JPanel(new BorderLayout());
    headerPanel.setBackground(COLOR_HEADER);
    headerPanel.setPreferredSize(new Dimension(0, 60));

    // BOTÓN VOLVER (Variable de clase ahora)
    backButton = new JButton("◀ VOLVER");
    backButton.setUI(new BasicButtonUI());
    backButton.setForeground(Color.WHITE);
    backButton.setBackground(COLOR_HEADER);
    backButton.setFont(new Font("SansSerif", Font.BOLD, 12));
    backButton.setBorder(new EmptyBorder(0, 15, 0, 15));
    backButton.setCursor(new Cursor(Cursor.HAND_CURSOR));

    backButton.addActionListener(e -> {
      currentChat = "";
      contactList.clearSelection();
      rootLayout.show(rootPanel, "LIST_VIEW");
      // Al volver a la lista, reseteamos el texto del botón porque ya veremos la lista
      updateBackButtonTotal();
    });

    headerLabel = new JLabel("CHAT", SwingConstants.CENTER);
    headerLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
    headerLabel.setForeground(Color.WHITE);

    headerPanel.add(backButton, BorderLayout.WEST);
    headerPanel.add(headerLabel, BorderLayout.CENTER);
    JPanel dummy = new JPanel();
    dummy.setPreferredSize(new Dimension(90,0));
    dummy.setOpaque(false);
    headerPanel.add(dummy, BorderLayout.EAST);

    chatInterfacePanel.add(headerPanel, BorderLayout.NORTH);

    // --- HISTORIAL ---
    chatCardLayout = new CardLayout();
    chatCardPanel = new JPanel(chatCardLayout);
    chatCardPanel.setBackground(COLOR_BG);

    for (String realName : listDisplayToRealName.values()) {
      JPanel messagesPanel = new JPanel();
      messagesPanel.setLayout(new BoxLayout(messagesPanel, BoxLayout.Y_AXIS));
      messagesPanel.setBackground(COLOR_BG);
      messagesPanel.setBorder(new EmptyBorder(10, 10, 10, 10));

      JPanel wrapper = new JPanel(new BorderLayout());
      wrapper.add(messagesPanel, BorderLayout.NORTH);
      wrapper.setBackground(COLOR_BG);

      JScrollPane scroll = new JScrollPane(wrapper);
      scroll.getVerticalScrollBar().setUnitIncrement(16);
      scroll.setBorder(null);

      chatPanelsMap.put(realName, messagesPanel);
      chatCardPanel.add(scroll, realName);
    }
    chatInterfacePanel.add(chatCardPanel, BorderLayout.CENTER);

    // --- INPUT ---
    JPanel inputPanel = new JPanel(new BorderLayout(10, 10));
    inputPanel.setBorder(new EmptyBorder(10, 15, 10, 15));
    inputPanel.setBackground(Color.WHITE);

    JTextField inputField = new JTextField();
    inputField.setFont(new Font("Segoe UI", Font.PLAIN, 16));

    JButton sendButton = new JButton("▶");
    sendButton.setUI(new BasicButtonUI());
    sendButton.setFont(new Font("SansSerif", Font.BOLD, 20));
    sendButton.setBackground(COLOR_HEADER);
    sendButton.setForeground(Color.WHITE);
    sendButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
    sendButton.setBorder(new EmptyBorder(5, 15, 5, 15));

    inputPanel.add(inputField, BorderLayout.CENTER);
    inputPanel.add(sendButton, BorderLayout.EAST);
    chatInterfacePanel.add(inputPanel, BorderLayout.SOUTH);

    rootPanel.add(contactListView, "LIST_VIEW");
    rootPanel.add(chatInterfacePanel, "CHAT_VIEW");

    frame.add(rootPanel);

    // ------------------------------------------------
    // LÓGICA
    // ------------------------------------------------

    contactList.addMouseListener(new MouseAdapter() {
      @Override
      public void mouseClicked(MouseEvent e) {
        String selectedDisplay = contactList.getSelectedValue();
        if (selectedDisplay == null) return;

        String realName = getRealNameFromDisplay(selectedDisplay);
        openChat(realName);
      }
    });

    Runnable sendAction = () -> {
      String text = inputField.getText();
      if (text.trim().isEmpty() || currentChat.isEmpty()) return;

      addMessageBubble(currentChat, text, "Yo", true);

      if ("GRUPO".equals(currentChat)) {
        send(text);
      } else {
        sendPrivate(text, currentChat);
      }
      inputField.setText("");
      inputField.requestFocus();
    };

    sendButton.addActionListener(e -> sendAction.run());
    inputField.addActionListener(e -> sendAction.run());

    frame.setLocationRelativeTo(null);
    frame.setVisible(true);
  }

  private void openChat(String realName) {
    currentChat = realName;
    headerLabel.setText(realName.toUpperCase());
    clearNotifications(realName); // Limpiar al entrar
    chatCardLayout.show(chatCardPanel, realName);
    rootLayout.show(rootPanel, "CHAT_VIEW");
    updateBackButtonTotal(); // Actualizar botón por si quedan otros chats
  }

  // AUXILIARES
  private void addContactToList(String name) {
    listModel.addElement(name);
    unreadMap.put(name, 0);
    listDisplayToRealName.put(name, name);
  }

  private String getRealNameFromDisplay(String display) {
    if (display.contains(" (")) {
      return display.substring(0, display.lastIndexOf(" ("));
    }
    return display;
  }

  private void clearNotifications(String realName) {
    if (unreadMap.get(realName) > 0) {
      unreadMap.put(realName, 0);
      updateListLabel(realName, 0);
    }
    updateBackButtonTotal();
  }

  private void updateListLabel(String realName, int count) {
    // Buscamos EXACTAMENTE el elemento que corresponde a este usuario
    for (int i = 0; i < listModel.getSize(); i++) {
      String val = listModel.get(i);
      String itemRealName = getRealNameFromDisplay(val);

      if (itemRealName.equals(realName)) {
        String newLabel = (count > 0) ? realName + " (" + count + ")" : realName;
        listModel.set(i, newLabel);
        // No necesitamos actualizar el mapa inverso si usamos getRealNameFromDisplay bien
        break;
      }
    }
  }

  // --- ACTUALIZAR EL BOTÓN DE VOLVER CON TOTAL DE MENSAJES ---
  private void updateBackButtonTotal() {
    int totalUnread = 0;
    for (int count : unreadMap.values()) {
      totalUnread += count;
    }

    if (totalUnread > 0) {
      backButton.setText("◀ VOLVER (" + totalUnread + ")");
      backButton.setBackground(new Color(200, 0, 0)); // Rojo para avisar
    } else {
      backButton.setText("◀ VOLVER");
      backButton.setBackground(COLOR_HEADER); // Color normal
    }
  }

  private Color getUserColor(String username) {
    int hash = username.hashCode();
    float hue = (hash % 360) / 360f;
    return Color.getHSBColor(hue, 0.8f, 0.6f);
  }

  // BURBUJAS
  private void addMessageBubble(String chatKey, String msg, String senderName, boolean isMine) {
    JPanel targetPanel = chatPanelsMap.get(chatKey);
    if (targetPanel == null) return;

    JPanel rowPanel = new JPanel(new BorderLayout());
    rowPanel.setOpaque(false);
    rowPanel.setBorder(new EmptyBorder(5, 0, 5, 0));

    BubblePanel bubble = new BubblePanel(msg, senderName, isMine, chatKey.equals("GRUPO"));

    JPanel buffer = new JPanel();
    buffer.setOpaque(false);

    if (isMine) {
      rowPanel.add(buffer, BorderLayout.CENTER);
      rowPanel.add(bubble, BorderLayout.EAST);
    } else {
      rowPanel.add(bubble, BorderLayout.WEST);
      rowPanel.add(buffer, BorderLayout.CENTER);
    }

    targetPanel.add(rowPanel);
    targetPanel.revalidate();
    targetPanel.repaint();

    SwingUtilities.invokeLater(() -> {
      JScrollPane scroll = (JScrollPane) SwingUtilities.getAncestorOfClass(JScrollPane.class, targetPanel);
      if (scroll != null) {
        JScrollBar vertical = scroll.getVerticalScrollBar();
        vertical.setValue(vertical.getMaximum());
      }
    });
  }

  class BubblePanel extends JPanel {
    private boolean isMine;

    public BubblePanel(String msg, String sender, boolean isMine, boolean isGroupChat) {
      this.isMine = isMine;
      setLayout(new BorderLayout());
      setBorder(new EmptyBorder(8, 12, 8, 12));
      setOpaque(false);

      if (!isMine && isGroupChat) {
        JLabel nameLabel = new JLabel(sender);
        nameLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        nameLabel.setForeground(getUserColor(sender));
        nameLabel.setBorder(new EmptyBorder(0, 0, 2, 0));

        nameLabel.setCursor(new Cursor(Cursor.HAND_CURSOR));
        nameLabel.setToolTipText("Click para mensaje privado");
        nameLabel.addMouseListener(new MouseAdapter() {
          @Override
          public void mouseClicked(MouseEvent e) {
            JPopupMenu popup = new JPopupMenu();
            JMenuItem item = new JMenuItem("Abrir chat privado con " + sender);
            item.addActionListener(ev -> openChat(sender));
            popup.add(item);
            popup.show(nameLabel, e.getX(), e.getY());
          }
        });

        add(nameLabel, BorderLayout.NORTH);
      }

      JTextArea textLabel = new JTextArea(msg);
      textLabel.setWrapStyleWord(true);
      textLabel.setLineWrap(true);
      textLabel.setEditable(false);
      textLabel.setOpaque(false);
      textLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
      textLabel.setForeground(Color.WHITE);

      textLabel.setSize(new Dimension(260, Short.MAX_VALUE));
      int rows = (int) Math.ceil(msg.length() / 32.0);
      textLabel.setRows(Math.max(1, rows));

      add(textLabel, BorderLayout.CENTER);

      JLabel timeLabel = new JLabel(LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm")));
      timeLabel.setFont(new Font("Segoe UI", Font.PLAIN, 10));
      timeLabel.setForeground(new Color(230, 230, 230));
      timeLabel.setHorizontalAlignment(SwingConstants.RIGHT);
      timeLabel.setBorder(new EmptyBorder(4, 0, 0, 0));
      add(timeLabel, BorderLayout.SOUTH);
    }

    @Override
    protected void paintComponent(Graphics g) {
      Graphics2D g2 = (Graphics2D) g.create();
      g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

      g2.setColor(isMine ? COLOR_MINE : COLOR_THEIRS);
      g2.fillRoundRect(0, 0, getWidth(), getHeight(), 16, 16);
      g2.dispose();
    }
  }

  @Override
  public void send(String msg) {
    mediator.sendMessage(msg, this);
  }

  @Override
  public void sendPrivate(String msg, String receiverName) {
    mediator.sendPrivateMessage(msg, this, receiverName);
  }

  @Override
  public void receive(String msg, String senderName, boolean isPrivate) {
    String chatKey = isPrivate ? senderName : "GRUPO";
    addMessageBubble(chatKey, msg, senderName, false);

    // LÓGICA CRÍTICA DE NOTIFICACIÓN
    if (!currentChat.equals(chatKey)) {
      int count = unreadMap.getOrDefault(chatKey, 0);
      count++;
      unreadMap.put(chatKey, count);
      updateListLabel(chatKey, count);
      updateBackButtonTotal(); // <-- ESTO AVISA EN EL BOTÓN DE VOLVER
      Toolkit.getDefaultToolkit().beep();
    }
  }
}