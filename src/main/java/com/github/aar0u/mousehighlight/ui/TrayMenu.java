package com.github.aar0u.mousehighlight.ui;

import com.github.aar0u.mousehighlight.listener.MouseListener;
import com.github.aar0u.mousehighlight.service.ConfigManager;
import com.github.aar0u.mousehighlight.service.LogWindow;
import com.github.aar0u.mousehighlight.service.Logger;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import javax.imageio.ImageIO;

public class TrayMenu {
  private final Logger logger = new Logger(this.getClass().getSimpleName());
  private final ConfigManager configManager = new ConfigManager();

  public TrayMenu(Consumer<ShapedWindow.ColorTheme> colorConsumer) {
    if (!SystemTray.isSupported()) {
      return;
    }
    SystemTray tray = SystemTray.getSystemTray();
    PopupMenu popup = new PopupMenu();

    List<MenuItem> colorThemeItems = colorMenu(colorConsumer);
    colorThemeItems.forEach(popup::add);
    popup.addSeparator();

    MenuItem logItem = newMenu("Logging");
    logItem.addActionListener(e -> LogWindow.getInstance().showWindow());
    popup.add(logItem);
    popup.addSeparator();

    MenuItem defaultItem = newMenu("Exit");
    defaultItem.addActionListener(e -> System.exit(0));
    popup.add(defaultItem);

    try {
      tray.add(createTrayIcon(popup));
    } catch (AWTException e) {
      logger.err("Failed to add system tray icon: {}", e.getMessage());
    }
  }

  private List<MenuItem> colorMenu(Consumer<ShapedWindow.ColorTheme> colorConsumer) {
    List<MenuItem> menuItems = new ArrayList<>();

    for (ShapedWindow.ColorTheme theme : ShapedWindow.ColorTheme.values()) {
      String colorName = theme.name().charAt(0) + theme.name().substring(1).toLowerCase();
      MenuItem colorItem = newMenu(colorName);
      colorItem.addActionListener(
          e -> {
            configManager.setColorTheme(theme);
            colorConsumer.accept(theme);
            MenuContainer parent = colorItem.getParent();
            if (parent instanceof PopupMenu) {
              updateMenuCheckmarks((PopupMenu) parent, colorItem);
            }
          });
      menuItems.add(colorItem);

      ShapedWindow.ColorTheme savedTheme = configManager.getColorTheme();
      if (theme == savedTheme) {
        colorConsumer.accept(theme);
        colorItem.setLabel(colorItem.getLabel() + " ✓");
      }
    }
    return menuItems;
  }

  private void updateMenuCheckmarks(PopupMenu popup, MenuItem selectedItem) {
    for (int i = 0; i < popup.getItemCount(); i++) {
      MenuItem item = popup.getItem(i);
      if (item != null) {
        String label = item.getLabel().replace(" ✓", "");
        item.setLabel(label + (item == selectedItem ? " ✓" : ""));
      }
    }
  }

  private MenuItem newMenu(String text) {
    String padding = System.getProperty("os.name").toLowerCase().contains("win") ? "    " : "";
    return new MenuItem(padding + text);
  }

  private TrayIcon createTrayIcon(PopupMenu popup) {
    Dimension trayIconSize = SystemTray.getSystemTray().getTrayIconSize();
    Image scaledInstance = getIcon().getScaledInstance(trayIconSize.width, -1, Image.SCALE_SMOOTH);
    TrayIcon trayIcon = new TrayIcon(scaledInstance, "Mouse Highlight", popup);
    trayIcon.addActionListener(e -> logger.info("Icon clicked"));
    return trayIcon;
  }

  private Image getIcon() {
    Image image;
    try {
      // Attempt to load the icon from the resources directory
      URL iconUrl = MouseListener.class.getClassLoader().getResource("images/icons8-mouse-64.png");
      if (iconUrl == null) {
        logger.err("Could not find tray icon resource");
        // Generate a default icon as fallback
        image = createFallbackIcon();
      } else {
        image = ImageIO.read(iconUrl);
      }
    } catch (IOException e) {
      logger.err("Failed to load tray icon image: {}", e.getMessage());
      // Use default icon instead of throwing exception
      image = createFallbackIcon();
    }
    return image;
  }

  private Image createFallbackIcon() {
    // Generate a simple blue square as the default icon
    int size = 64;
    BufferedImage fallbackIcon = new BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB);
    Graphics2D g2d = fallbackIcon.createGraphics();
    g2d.setColor(new Color(30, 144, 255)); // A nice blue color
    g2d.fillRect(0, 0, size, size);
    g2d.dispose();
    return fallbackIcon;
  }
}
