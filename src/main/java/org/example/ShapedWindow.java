package org.example;

import java.awt.*;
import java.awt.geom.Ellipse2D;
import javax.swing.*;

public class ShapedWindow extends JWindow {
  private Ellipse2D.Double SHAPE = new Ellipse2D.Double(0, 0, 50, 50);
  private Ellipse2D.Double CLICKED = new Ellipse2D.Double(0, 0, 40, 40);

  private Ellipse2D.Double currentShape = SHAPE;

  public ShapedWindow() {
    setLayout(new GridBagLayout());

    // 创建自定义内容面板用于绘制
    JPanel panel =
        new JPanel() {
          @Override
          protected void paintComponent(Graphics g) {
            // 清除背景
            setOpaque(false);
            super.paintComponent(g);

            // 使用Graphics2D进行抗锯齿绘制
            Graphics2D g2d = (Graphics2D) g.create();
            g2d.setRenderingHint(
                RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2d.setColor(Color.yellow);
            g2d.fill(currentShape);
            g2d.dispose();
          }
        };
    setContentPane(panel);

    setBackground(new Color(0, 0, 0, 0)); // 透明背景
    setSize(300, 300);
    setPos(MouseInfo.getPointerInfo().getLocation().x, MouseInfo.getPointerInfo().getLocation().y);
    setAlwaysOnTop(true);
  }

  public void setPos(int x, int y) {
    setLocation(x + 5, y + 5);
  }

  public void draw(boolean pressed) {
    currentShape = pressed ? CLICKED : SHAPE;
    repaint();
  }
}
