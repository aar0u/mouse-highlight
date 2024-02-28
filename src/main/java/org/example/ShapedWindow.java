package org.example;

import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import javax.swing.*;

public class ShapedWindow extends JFrame {
  public ShapedWindow() {
    setLayout(new GridBagLayout());
    getContentPane().setBackground(Color.yellow);

    // It is best practice to set the window's shape in
    // the componentResized method.  Then, if the window
    // changes size, the shape will be correctly recalculated.
    addComponentListener(
        new ComponentAdapter() {
          // Give the window an elliptical shape.
          // If the window is resized, the shape is recalculated here.
          @Override
          public void componentResized(ComponentEvent e) {
            setShape(GlobalMouseListener.SHAPE);
          }
        });

    setUndecorated(true);
    setType(Type.UTILITY); // no task bar
    setSize(300, 300);
    setLocation(MouseInfo.getPointerInfo().getLocation());
    setAlwaysOnTop(true);
    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

    //    add(new JButton("I am a Button"));
  }

  public void setPos(int x, int y) {
    setLocation(x, y);
  }
}
