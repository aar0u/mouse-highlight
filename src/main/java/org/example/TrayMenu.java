package org.example;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.event.ActionListener;
import java.io.IOException;

public class TrayMenu {
  private final ActionListener listener;

  public TrayMenu(ActionListener listener) {
    this.listener = listener;

    TrayIcon trayIcon;
    if (!SystemTray.isSupported()) {
      return;
    }
    SystemTray tray = SystemTray.getSystemTray();
    PopupMenu popup = new PopupMenu();

    Menu colorMenu = colorMenu();

    popup.add(colorMenu);
    popup.addSeparator();

    MenuItem defaultItem = new MenuItem(menuText("Exit"));
    defaultItem.addActionListener(e -> System.exit(0));
    popup.add(defaultItem);
    Image image;
    try {
      image =
          ImageIO.read(
              GlobalMouseListener.class.getClassLoader().getResource("images/icons8-mouse-64.png"));
    } catch (IOException e) {
      System.err.printf("Failed to load tray icon image: %s%n", e.getMessage());
      throw new RuntimeException("Failed to initialize system tray icon", e);
    }
    int trayIconWidth = new TrayIcon(image).getSize().width;
    trayIcon =
        new TrayIcon(
            image.getScaledInstance(trayIconWidth, -1, Image.SCALE_SMOOTH),
            "Mouse Highlight",
            popup);
    trayIcon.addActionListener(e -> System.out.println("Icon clicked"));
    try {
      tray.add(trayIcon);
    } catch (AWTException e) {
      System.err.printf("Failed to add system tray icon: %s%n", e.getMessage());
    }
  }

  private Menu colorMenu() {
    Menu colorMenu = new Menu(menuText("Color"));
    for (ShapedWindow.ColorTheme theme : ShapedWindow.ColorTheme.values()) {
      // Convert enum name to proper case (e.g., BLUE -> Blue)
      String colorName = theme.name().charAt(0) + theme.name().substring(1).toLowerCase();

      MenuItem colorItem = new MenuItem(colorName);
      colorItem.addActionListener(listener);
      colorMenu.add(colorItem);
    }
    return colorMenu;
  }

  private String menuText(String text) {
    String padding = System.getProperty("os.name").toLowerCase().contains("win") ? "    " : "";
    return padding + text;
  }
}
