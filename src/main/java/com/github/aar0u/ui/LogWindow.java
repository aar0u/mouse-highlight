package com.github.aar0u.ui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class LogWindow extends JFrame {
  private static LogWindow instance;
  private final JTextArea logArea;

  private LogWindow() {
    setTitle("Logging");
    setSize(600, 400);
    setLocationRelativeTo(null); // 居中显示

    logArea = new JTextArea();
    logArea.setEditable(false); // 设置为只读
    logArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12)); // 使用等宽字体

    JScrollPane scrollPane = new JScrollPane(logArea);
    scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);

    getContentPane().add(scrollPane, BorderLayout.CENTER);

    // 窗口关闭时只隐藏窗口，不退出程序
    setDefaultCloseOperation(WindowConstants.HIDE_ON_CLOSE);

    addWindowListener(
        new WindowAdapter() {
          @Override
          public void windowClosing(WindowEvent e) {
            setVisible(false);
          }
        });
  }

  public static synchronized LogWindow getInstance() {
    if (instance == null) {
      instance = new LogWindow();
    }
    return instance;
  }

  public void appendLog(String logMessage) {
    SwingUtilities.invokeLater(
        () -> {
          logArea.append(logMessage + "\n");
          // 自动滚动到最新日志
          logArea.setCaretPosition(logArea.getDocument().getLength());
        });
  }

  public void clear() {
    SwingUtilities.invokeLater(() -> logArea.setText(""));
  }

  public void showWindow() {
    setVisible(true);
    toFront(); // 将窗口置于前台
    requestFocus();
  }
}
