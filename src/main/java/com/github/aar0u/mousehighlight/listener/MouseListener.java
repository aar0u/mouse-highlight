package com.github.aar0u.mousehighlight.listener;

import com.github.aar0u.mousehighlight.ui.ShapedWindow;
import com.github.aar0u.mousehighlight.ui.TrayMenu;
import com.github.kwhat.jnativehook.GlobalScreen;
import com.github.kwhat.jnativehook.NativeHookException;
import com.github.kwhat.jnativehook.mouse.NativeMouseEvent;
import com.github.kwhat.jnativehook.mouse.NativeMouseInputListener;
import com.github.aar0u.mousehighlight.service.Logger;

import javax.swing.*;
import java.awt.*;

public class MouseListener implements NativeMouseInputListener {
  private final Logger logger = new Logger(this.getClass().getSimpleName());
  private ShapedWindow shapedWindow;
  private Timer refreshTimer;

  @Override
  public void nativeMouseClicked(NativeMouseEvent e) {
    // Log click count if needed - logger.info("Mouse Clicked: {}", e.getClickCount())
  }

  @Override
  public void nativeMousePressed(NativeMouseEvent e) {
    String buttonName;
    switch (e.getButton()) {
      case NativeMouseEvent.BUTTON1:
        buttonName = "LEFT";
        break;
      case NativeMouseEvent.BUTTON2:
        buttonName = "RIGHT";
        break;
      case NativeMouseEvent.BUTTON3:
        buttonName = "MIDDLE";
        break;
      default:
        buttonName = "UNKNOWN";
        break;
    }
    logger.info("Mouse Pressed: {} button", buttonName);
    draw(true);
  }

  @Override
  public void nativeMouseReleased(NativeMouseEvent e) {
    // Log mouse button release if needed - logger.info("Mouse Released: {}", e.getButton())
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

  // Only refresh position via timer, not in mouse event handlers
  private void move(NativeMouseEvent e) {
    // if (shapedWindow == null) return;
    // shapedWindow.setPos(e.getX(), e.getY());
  }

  public void start() {
    logger.info("Starting application");
    System.setProperty("jnativehook.lib.path", System.getProperty("java.io.tmpdir"));

    try {
      GlobalScreen.registerNativeHook();
    } catch (NativeHookException ex) {
      logger.err("There was a problem registering the native hook: {}", ex.getMessage());
      System.exit(1);
    }

    // Register mouse event listeners
    GlobalScreen.addNativeMouseListener(this);
    GlobalScreen.addNativeMouseMotionListener(this);

    // Initialize and display the window on the Event Dispatch Thread
    SwingUtilities.invokeLater(
        () -> {
          shapedWindow = new ShapedWindow();
          shapedWindow.setVisible(true);
            // Periodically refresh window position and bring to front (only position and z-order, no draw)
          refreshTimer = new Timer(16, evt -> {
            if (shapedWindow != null) {
              Point p = MouseInfo.getPointerInfo().getLocation();
              shapedWindow.setPos(p.x, p.y);
              shapedWindow.toFront();
            }
          });
          refreshTimer.start();
        });
    new TrayMenu(
        color -> {
          logger.info("Color theme: {}", color);
          shapedWindow.setColorTheme(color);
        });
  }
}
