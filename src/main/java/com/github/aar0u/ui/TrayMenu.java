package com.github.aar0u.ui;

import com.github.aar0u.service.Logger;
import com.github.aar0u.listener.GlobalMouseListener;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.event.ActionListener;
import java.io.IOException;

public class TrayMenu {
  private final Logger logger = new Logger(this.getClass().getSimpleName());

  public TrayMenu(ActionListener listener) {
    TrayIcon trayIcon;
    if (!SystemTray.isSupported()) {
      return;
    }
    SystemTray tray = SystemTray.getSystemTray();
    PopupMenu popup = new PopupMenu();

    for (ShapedWindow.ColorTheme theme : ShapedWindow.ColorTheme.values()) {
      // Convert enum name to proper case (e.g., BLUE -> Blue)
      String colorName = theme.name().charAt(0) + theme.name().substring(1).toLowerCase();

      MenuItem colorItem = new MenuItem(colorName);
      colorItem.addActionListener(listener);
      popup.add(colorItem);
    }
    popup.addSeparator();

    MenuItem logItem = newMenu("Logging");
    logItem.addActionListener(e -> LogWindow.getInstance().showWindow());
    popup.add(logItem);
    popup.addSeparator();

    MenuItem defaultItem = newMenu("Exit");
    defaultItem.addActionListener(e -> System.exit(0));
    popup.add(defaultItem);
    Image image;
    try {
      image =
          ImageIO.read(
              GlobalMouseListener.class.getClassLoader().getResource("images/icons8-mouse-64.png"));
    } catch (IOException e) {
      logger.err("Failed to load tray icon image: {}", e.getMessage());
      throw new RuntimeException("Failed to initialize system tray icon", e);
    }
    int trayIconWidth = new TrayIcon(image).getSize().width;
    trayIcon =
        new TrayIcon(
            image.getScaledInstance(trayIconWidth, -1, Image.SCALE_SMOOTH),
            "Mouse Highlight",
            popup);
    trayIcon.addActionListener(e -> logger.info("Icon clicked"));
    try {
      tray.add(trayIcon);
    } catch (AWTException e) {
      logger.err("Failed to add system tray icon: {}", e.getMessage());
    }
  }

  private MenuItem newMenu(String text) {
    String padding = System.getProperty("os.name").toLowerCase().contains("win") ? "    " : "";
    return new MenuItem(padding + text);
  }
}
