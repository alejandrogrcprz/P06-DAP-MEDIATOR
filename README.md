# 💬 Chat Mediator - Java Swing Edition

![Java](https://img.shields.io/badge/Java-17%2B-orange?style=for-the-badge&logo=java)
![Pattern](https://img.shields.io/badge/Pattern-Mediator-blue?style=for-the-badge)
![GUI](https://img.shields.io/badge/UI-Swing-green?style=for-the-badge)
![Status](https://img.shields.io/badge/Status-Completed-brightgreen?style=for-the-badge)

> Una aplicación de mensajería en tiempo real elegante y robusta, diseñada para demostrar la implementación del **Patrón de Diseño Mediador** en Java.

---

## 🚀 Características

Este proyecto va más allá de una simple consola, ofreciendo una experiencia de usuario moderna:

* **👥 Arquitectura Desacoplada:** Uso estricto del patrón Mediador. Los usuarios no se conocen entre sí; toda comunicación pasa por el `ChatRoom`.
* **🎨 UI Estilo WhatsApp:** Interfaz gráfica moderna basada en `CardLayout` para una navegación fluida entre lista de contactos y chats.
* **🔒 Privacidad vs Grupo:** Soporte completo para difusión global (Broadcast) y mensajería directa privada.
* **🔔 Sistema de Notificaciones:** Contador de mensajes no leídos en tiempo real y alertas visuales en la navegación.
* **✨ Estética "Aesthetic":**
    * Burbujas de chat renderizadas gráficamente (`Graphics2D`).
    * Colores dinámicos para los nombres de usuario (hash-based coloring).
    * Tema visual en tonos morados y lilas.
* **🖱️ Interactividad:** Menús contextuales al hacer clic en los nombres de usuario para abrir chats privados rápidamente.

---

## 📐 Diagrama de Clases (UML)

El núcleo del proyecto se basa en la inversión de dependencias y la centralización del control.

![Diagrama UML del Patrón Mediador](https://github.com/alejandrogrcprz/P06-DAP-MEDIATOR/blob/main/p06.png)

---

## 📂 Estructura del Proyecto

El código sigue los principios **SOLID** y está organizado por paquetes funcionales:

```text
src/com/chat/
├── main/
│   └── Main.java           # Punto de entrada (Lanzador de la simulación)
├── mediator/
│   ├── ChatMediator.java   # Contrato del Mediador
│   └── ChatRoom.java       # Lógica de enrutamiento (Cerebro)
└── user/
    ├── User.java           # Abstracción del participante
    └── GUIUser.java        # Implementación Swing (Vista + Control)
