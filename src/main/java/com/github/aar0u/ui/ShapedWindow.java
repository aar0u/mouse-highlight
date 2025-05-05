package com.github.aar0u.ui;

import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.util.EnumMap;
import java.util.Map;
import javax.swing.*;

public class ShapedWindow extends JWindow {
  private final Ellipse2D.Double circle = new Ellipse2D.Double(0, 0, 50, 50);
  private final Ellipse2D.Double pressedCircle = new Ellipse2D.Double(5, 5, 40, 40);

  private Ellipse2D.Double currentShape = circle;

  public enum ColorTheme {
    BLUE,
    YELLOW,
    GREEN,
    PURPLE,
    PINK,
    ORANGE
  }

  private static final Map<ColorTheme, Color[]> COLOR_MAP = new EnumMap<>(ColorTheme.class);
  private ColorTheme currentTheme = ColorTheme.YELLOW;

  static {
    // highlight color, pressed color
    COLOR_MAP.put(ColorTheme.BLUE, new Color[] {new Color(100, 181, 246), new Color(30, 136, 229)});
    COLOR_MAP.put(
        ColorTheme.YELLOW, new Color[] {new Color(255, 223, 100), new Color(255, 193, 7)});
    COLOR_MAP.put(ColorTheme.GREEN, new Color[] {new Color(129, 199, 132), new Color(56, 142, 60)});
    COLOR_MAP.put(
        ColorTheme.PURPLE, new Color[] {new Color(179, 157, 219), new Color(123, 31, 162)});
    COLOR_MAP.put(ColorTheme.PINK, new Color[] {new Color(244, 143, 177), new Color(194, 24, 91)});
    COLOR_MAP.put(ColorTheme.ORANGE, new Color[] {new Color(255, 183, 77), new Color(239, 108, 0)});
  }

  public ShapedWindow() {
    setLayout(null);

    setOpacity(0.7f);
    setBackground(new Color(0, 0, 0, 0)); // Transparent background
    setSize(80, 80);
    setPos(MouseInfo.getPointerInfo().getLocation().x, MouseInfo.getPointerInfo().getLocation().y);
    setAlwaysOnTop(true);

    JPanel panel =
        new JPanel() {
          @Override
          protected void paintComponent(Graphics g) {
            // Clear the background
            setOpaque(false);
            super.paintComponent(g);
            paintShape(g);
          }
        };
    // Or set as by setContentPane(panel)
    setGlassPane(panel);
    getGlassPane().setVisible(true);
  }

  public void setPos(int x, int y) {
    setLocation(x + 5, y + 5);
  }

  public void draw(boolean pressed) {
    currentShape = pressed ? this.pressedCircle : circle;
    getGlassPane().repaint();
  }

  public void setColorTheme(ColorTheme theme) {
    this.currentTheme = theme;
    getGlassPane().repaint();
  }

  private void paintShape(Graphics g) {
    Color[] colors = COLOR_MAP.get(currentTheme);
    if (g != null) {
      // 使用Graphics2D进行抗锯齿绘制
      Graphics2D g2d = (Graphics2D) g.create();
      g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

      if (currentShape == pressedCircle) {
        g2d.setColor(colors[0]); // 外圈
        g2d.fill(circle);
        g2d.setColor(colors[1]); // 内圈
        g2d.fill(pressedCircle);
      } else {
        g2d.setColor(colors[0]);
        g2d.fill(circle);
      }
      g2d.dispose();
    }
  }
}
