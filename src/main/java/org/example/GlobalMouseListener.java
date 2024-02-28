package org.example;

import static java.awt.GraphicsDevice.WindowTranslucency.PERPIXEL_TRANSPARENT;
import static java.awt.GraphicsDevice.WindowTranslucency.TRANSLUCENT;

import com.github.kwhat.jnativehook.GlobalScreen;
import com.github.kwhat.jnativehook.NativeHookException;
import com.github.kwhat.jnativehook.mouse.NativeMouseEvent;
import com.github.kwhat.jnativehook.mouse.NativeMouseInputListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Ellipse2D;
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.swing.*;

public class GlobalMouseListener implements NativeMouseInputListener {
  static final Ellipse2D.Double SHAPE = new Ellipse2D.Double(0, 0, 50, 50);
  static final Ellipse2D.Double CLICKED = new Ellipse2D.Double(0, 0, 40, 40);
  static ShapedWindow shapedWindow;

  @Override
  public void nativeMouseClicked(NativeMouseEvent e) {
    System.out.println("Mouse Clicked: " + e.getClickCount());
  }

  @Override
  public void nativeMousePressed(NativeMouseEvent e) {
    System.out.println("Mouse Pressed: " + e.getButton());
    if (shapedWindow == null) return;
    shapedWindow.setShape(CLICKED);
  }

  @Override
  public void nativeMouseReleased(NativeMouseEvent e) {
    System.out.println("Mouse Released: " + e.getButton());
    if (shapedWindow == null) return;
    shapedWindow.setShape(SHAPE);
  }

  @Override
  public void nativeMouseDragged(NativeMouseEvent e) {
    shapedWindow.setPos(e.getX(), e.getY());
  }

  @Override
  public void nativeMouseMoved(NativeMouseEvent e) {
    // System.out.println("Mouse Moved: " + e.getX() + ", " + e.getY());
    if (shapedWindow == null) return;
    shapedWindow.setPos(e.getX(), e.getY());
  }

  public static void main(String[] args) {
    try {
      GlobalScreen.registerNativeHook();
    } catch (NativeHookException ex) {
      System.err.println("There was a problem registering the native hook.");
      System.err.println(ex.getMessage());

      System.exit(1);
    }

    // Construct the example object.
    GlobalMouseListener example = new GlobalMouseListener();

    // Add the appropriate listeners.
    GlobalScreen.addNativeMouseListener(example);
    GlobalScreen.addNativeMouseMotionListener(example);

    tray();
    display();
  }

  public static void display() {
    // Determine what the GraphicsDevice can support.
    GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
    GraphicsDevice gd = ge.getDefaultScreenDevice();
    final boolean isTranslucencySupported = gd.isWindowTranslucencySupported(TRANSLUCENT);

    // If shaped windows aren't supported, exit.
    if (!gd.isWindowTranslucencySupported(PERPIXEL_TRANSPARENT)) {
      System.err.println("Shaped windows are not supported");
      System.exit(0);
    }

    // If translucent windows aren't supported,
    // create an opaque window.
    if (!isTranslucencySupported) {
      System.out.println("Translucency is not supported, creating an opaque window");
    }

    // Create the GUI on the event-dispatching thread
    SwingUtilities.invokeLater(
        new Runnable() {
          @Override
          public void run() {
            shapedWindow = new ShapedWindow();

            // Set the window to 70% translucency, if supported.
            if (isTranslucencySupported) {
              shapedWindow.setOpacity(0.7f);
            }

            // Display the window.
            shapedWindow.setVisible(true);
          }
        });
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
