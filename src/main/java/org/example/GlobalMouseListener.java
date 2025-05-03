package org.example;

import com.github.kwhat.jnativehook.GlobalScreen;
import com.github.kwhat.jnativehook.NativeHookException;
import com.github.kwhat.jnativehook.mouse.NativeMouseEvent;
import com.github.kwhat.jnativehook.mouse.NativeMouseInputListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.swing.*;

public class GlobalMouseListener implements NativeMouseInputListener {
  static ShapedWindow shapedWindow;

  @Override
  public void nativeMouseClicked(NativeMouseEvent e) {
    System.out.println("Mouse Clicked: " + e.getClickCount());
  }

  @Override
  public void nativeMousePressed(NativeMouseEvent e) {
    System.out.println("Mouse Pressed: " + e.getButton());
    if (shapedWindow == null) return;
    shapedWindow.draw(true);
  }

  @Override
  public void nativeMouseReleased(NativeMouseEvent e) {
    System.out.println("Mouse Released: " + e.getButton());
    if (shapedWindow == null) return;
    shapedWindow.draw(false);
  }

  @Override
  public void nativeMouseDragged(NativeMouseEvent e) {
    shapedWindow.setPos(e.getX(), e.getY());
  }

  @Override
  public void nativeMouseMoved(NativeMouseEvent e) {
    if (shapedWindow == null) return;
    shapedWindow.setPos(e.getX(), e.getY());
  }

  public static void main(String[] args) {
    // System.setProperty("jnativehook.lib.path", System.getProperty("java.io.tmpdir"));

    try {
      GlobalScreen.registerNativeHook();
    } catch (NativeHookException ex) {
      System.err.println("There was a problem registering the native hook.");
      System.err.println(ex.getMessage());

      System.exit(1);
    }

    GlobalMouseListener example = new GlobalMouseListener();
    // Add the appropriate listeners.
    GlobalScreen.addNativeMouseListener(example);
    GlobalScreen.addNativeMouseMotionListener(example);

    // Create the GUI on the event-dispatching thread
    SwingUtilities.invokeLater(
        new Runnable() {
          @Override
          public void run() {
            shapedWindow = new ShapedWindow();
            shapedWindow.setVisible(true);
          }
        });
    tray();
  }

  public static void tray() {
    TrayIcon trayIcon = null;
    if (!SystemTray.isSupported()) {
      return;
    }
    SystemTray tray = SystemTray.getSystemTray();
    PopupMenu popup = new PopupMenu();
    MenuItem defaultItem = new MenuItem("    Exit");
    defaultItem.addActionListener(
        new ActionListener() {
          public void actionPerformed(ActionEvent e) {
            System.exit(0);
          }
        });
    popup.add(defaultItem);
    Image image = null;
    try {
      image =
          ImageIO.read(
              GlobalMouseListener.class.getClassLoader().getResource("images/icons8-mouse-64.png"));
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
    int trayIconWidth = new TrayIcon(image).getSize().width;
    trayIcon =
        new TrayIcon(
            image.getScaledInstance(trayIconWidth, -1, Image.SCALE_SMOOTH),
            "Mouse Highlight",
            popup);
    trayIcon.addActionListener(
        new ActionListener() {
          public void actionPerformed(ActionEvent e) {
            System.out.println("Icon clicked");
          }
        });
    try {
      tray.add(trayIcon);
    } catch (AWTException e) {
      System.err.println(e);
    }

    // ...
    // some time later
    // the application state has changed - update the image
    //    if (trayIcon != null) {
    //      trayIcon.setImage(updatedImage);
    //    }
    // ...
  }
}
