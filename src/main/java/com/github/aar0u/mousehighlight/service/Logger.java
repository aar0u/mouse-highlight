package com.github.aar0u.mousehighlight.service;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.io.PrintWriter;
import java.io.StringWriter;

public class Logger {
  private final String loggerName;
  private final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
  private final LogWindow logWindow = LogWindow.getInstance();

  public Logger(String name) {
    loggerName = name;
  }

  public void info(String message) {
    String timestamp = dateFormat.format(new Date());
    String formattedMessage = timestamp + " [INFO] " + loggerName + " - " + message;
    System.out.println(formattedMessage);
    logWindow.appendLog(formattedMessage);
  }

  public void info(String format, Object... arguments) {
    info(formatMessage(format, arguments));
  }

  public void err(String message) {
    String timestamp = dateFormat.format(new Date());
    String formattedMessage = timestamp + " [ERROR] " + loggerName + " - " + message;
    System.err.println(formattedMessage);
    logWindow.appendLog(formattedMessage);
  }

  public void err(String format, Object... arguments) {
    if (arguments.length > 0 && arguments[arguments.length - 1] instanceof Throwable) {
      Throwable throwable = (Throwable) arguments[arguments.length - 1];
      Object[] messageArgs = Arrays.copyOf(arguments, arguments.length - 1);
      // Capture stack trace and add to log window
      StringWriter sw = new StringWriter();
      PrintWriter pw = new PrintWriter(sw);
      throwable.printStackTrace(pw);
      err(formatMessage(format, messageArgs) + "\n" + sw);
    } else {
      err(formatMessage(format, arguments));
    }
  }

  private String formatMessage(String messagePattern, Object... arguments) {
    if (messagePattern == null || arguments == null || arguments.length == 0) {
      return messagePattern;
    }

    StringBuilder result = new StringBuilder();
    int argIndex = 0;
    int placeholderIndex;
    int previousIndex = 0;

    while (argIndex < arguments.length) {
      // Find next placeholder
      placeholderIndex = messagePattern.indexOf("{}", previousIndex);
      if (placeholderIndex == -1) {
        // No more placeholders
        break;
      }

      // Add text before placeholder
      result.append(messagePattern, previousIndex, placeholderIndex);

      // Replace placeholder with argument value
      result.append(arguments[argIndex]);

      // Update indices
      previousIndex = placeholderIndex + 2;
      argIndex++;
    }

    // Add remaining text
    result.append(messagePattern.substring(previousIndex));

    return result.toString();
  }
}
