package org.example;

import com.github.kwhat.jnativehook.GlobalScreen;
import com.github.kwhat.jnativehook.NativeHookException;
import com.github.kwhat.jnativehook.mouse.NativeMouseEvent;
import com.github.kwhat.jnativehook.mouse.NativeMouseInputListener;
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
    System.setProperty("jnativehook.lib.path", System.getProperty("java.io.tmpdir"));

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
        () -> {
          shapedWindow = new ShapedWindow();
          shapedWindow.setVisible(true);
        });
    new TrayMenu(
        event -> {
          System.out.println("Tray action executed" + event);
          ShapedWindow.ColorTheme theme =
              ShapedWindow.ColorTheme.valueOf(event.getActionCommand().toUpperCase());
          shapedWindow.setColorTheme(theme);
        });
  }
}
