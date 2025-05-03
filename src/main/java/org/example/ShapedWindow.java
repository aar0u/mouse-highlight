package org.example;

import java.awt.*;
import java.awt.geom.Ellipse2D;
import javax.swing.*;

public class ShapedWindow extends JWindow {
  private static final boolean PAINT_IN_PANEL = false; // paint in panel will cause circle's grey boarder remains when repaint smaller Circle (MACOS)

  private final Ellipse2D.Double circle = new Ellipse2D.Double(0, 0, 50, 50);
  private final Ellipse2D.Double smallerCircle = new Ellipse2D.Double(0, 0, 40, 40);

  private Ellipse2D.Double currentShape = circle;

  public ShapedWindow() {
    setLayout(null);

    setOpacity(0.7f);
    setBackground(new Color(0, 0, 0, 0)); // 透明背景
    setSize(80, 80);
    setPos(MouseInfo.getPointerInfo().getLocation().x, MouseInfo.getPointerInfo().getLocation().y);
    setAlwaysOnTop(true);

    if (PAINT_IN_PANEL) {
      JPanel panel =
          new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
              // 清除背景
              setOpaque(false);
              super.paintComponent(g);
              paintShape(g, currentShape);
            }
          };
      // setContentPane(panel);
      setGlassPane(panel);
      getGlassPane().setVisible(true);
    }
  }

  public void setPos(int x, int y) {
    setLocation(x + 5, y + 5);
  }

  public void draw(boolean pressed) {
    currentShape = pressed ? this.smallerCircle : circle;

    if (PAINT_IN_PANEL) {
      getGlassPane().repaint();
    } else {
      Graphics g = getGraphics();
      g.clearRect(0, 0, getWidth(), getHeight());
      paintShape(g, currentShape);
    }
  }

  private void paintShape(Graphics g, Ellipse2D fill) {
    if (g != null) {
      // 使用Graphics2D进行抗锯齿绘制
      Graphics2D g2d = (Graphics2D) g.create();
      g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
      g2d.setColor(Color.yellow);
      g2d.fill(fill);
      g2d.dispose();
    }
  }

  // 处理重绘
  @Override
  public void paint(Graphics g) {
    super.paint(g);
    // 确保内容被重绘
    if (!PAINT_IN_PANEL) {
      draw(currentShape == smallerCircle);
    }
  }
}
