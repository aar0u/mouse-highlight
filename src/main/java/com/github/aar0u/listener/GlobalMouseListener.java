package com.github.aar0u.listener;

import com.github.aar0u.ui.ShapedWindow;
import com.github.aar0u.ui.TrayMenu;
import com.github.kwhat.jnativehook.GlobalScreen;
import com.github.kwhat.jnativehook.NativeHookException;
import com.github.kwhat.jnativehook.mouse.NativeMouseEvent;
import com.github.kwhat.jnativehook.mouse.NativeMouseInputListener;
import com.github.aar0u.service.Logger;

import javax.swing.*;

public class GlobalMouseListener implements NativeMouseInputListener {
  private final Logger logger = new Logger(this.getClass().getSimpleName());
  private ShapedWindow shapedWindow;

  @Override
  public void nativeMouseClicked(NativeMouseEvent e) {
    // for clicked count - logger.info("Mouse Clicked: {}", e.getClickCount())
  }

  @Override
  public void nativeMousePressed(NativeMouseEvent e) {
    logger.info("Mouse Pressed: {}", e.getButton());
    draw(true);
  }

  @Override
  public void nativeMouseReleased(NativeMouseEvent e) {
    logger.info("Mouse Released: {}", e.getButton());
    draw(false);
  }

  @Override
  public void nativeMouseDragged(NativeMouseEvent e) {
    move(e);
  }

  @Override
  public void nativeMouseMoved(NativeMouseEvent e) {
    move(e);
  }

  private void draw(boolean pressed) {
    if (shapedWindow == null) return;
    shapedWindow.draw(pressed);
  }

  private void move(NativeMouseEvent e) {
    if (shapedWindow == null) return;
    shapedWindow.setPos(e.getX(), e.getY());
  }

  public void start() {
    logger.info("Starting Mouse Highlight application");
    System.setProperty("jnativehook.lib.path", System.getProperty("java.io.tmpdir"));

    try {
      GlobalScreen.registerNativeHook();
    } catch (NativeHookException ex) {
      logger.err("There was a problem registering the native hook: {}", ex.getMessage());
      System.exit(1);
    }

    // Add the appropriate listeners.
    GlobalScreen.addNativeMouseListener(this);
    GlobalScreen.addNativeMouseMotionListener(this);

    // Create the GUI on the event-dispatching thread
    SwingUtilities.invokeLater(
        () -> {
          shapedWindow = new ShapedWindow();
          shapedWindow.setVisible(true);
        });
    new TrayMenu(
        event -> {
          logger.info("Tray action executed: {}", event);
          ShapedWindow.ColorTheme theme =
              ShapedWindow.ColorTheme.valueOf(event.getActionCommand().toUpperCase());
          shapedWindow.setColorTheme(theme);
        });
  }
}
