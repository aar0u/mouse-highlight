package com.github.aar0u.mousehighlight.service;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class LogWindow extends JFrame {
  private final JTextArea logArea;
  private final JTextField maxLinesField;
  private int maxLines = 1000; // Default max lines

  private LogWindow() {
    setTitle("Logging");
    setSize(600, 400);
    setLocationRelativeTo(null);

    logArea = new JTextArea();
    logArea.setEditable(false);
    logArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12)); // Use monospace font

    JScrollPane scrollPane = new JScrollPane(logArea);
    scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);

    // Add max lines control panel
    JPanel controlPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
    JLabel maxLinesLabel = new JLabel("Max Lines:");
    maxLinesField = new JTextField(String.valueOf(maxLines), 5);
    maxLinesField.addActionListener(e -> updateMaxLines()); // when pressing Enter
    maxLinesField.addFocusListener(
        new java.awt.event.FocusAdapter() {
          @Override
          public void focusLost(java.awt.event.FocusEvent e) {
            updateMaxLines();
          }
        });
    controlPanel.add(maxLinesLabel);
    controlPanel.add(maxLinesField);

    getContentPane().add(controlPanel, BorderLayout.NORTH);
    getContentPane().add(scrollPane, BorderLayout.CENTER);

    // Hide window instead of exiting program when closing
    setDefaultCloseOperation(WindowConstants.HIDE_ON_CLOSE);

    addWindowListener(
        new WindowAdapter() {
          @Override
          public void windowClosing(WindowEvent e) {
            setVisible(false);
          }
        });
  }

  private static class SingletonHelper {
    private static final LogWindow INSTANCE = new LogWindow();
  }

  public static LogWindow getInstance() {
    return SingletonHelper.INSTANCE;
  }

  private void updateMaxLines() {
    try {
      int newMaxLines = Integer.parseInt(maxLinesField.getText().trim());
      if (newMaxLines > 0) {
        maxLines = newMaxLines;
        trimLogLines();
      }
    } catch (NumberFormatException ex) {
      maxLinesField.setText(String.valueOf(maxLines));
    }
  }

  private void trimLogLines() {
    SwingUtilities.invokeLater(
        () -> {
          String[] lines = logArea.getText().split("\n");
          if (lines.length > maxLines) {
            StringBuilder newText = new StringBuilder();
            for (int i = lines.length - maxLines; i < lines.length; i++) {
              newText.append(lines[i]).append("\n");
            }
            logArea.setText(newText.toString());
          }
        });
  }

  public void appendLog(String logMessage) {
    SwingUtilities.invokeLater(
        () -> {
          logArea.append(logMessage + "\n");
          trimLogLines();
          // Auto-scroll to latest log
          logArea.setCaretPosition(logArea.getDocument().getLength());
        });
  }

  public void clear() {
    SwingUtilities.invokeLater(() -> logArea.setText(""));
  }

  public void showWindow() {
    setVisible(true);
    toFront(); // Bring window to front
    requestFocus();
  }
}
