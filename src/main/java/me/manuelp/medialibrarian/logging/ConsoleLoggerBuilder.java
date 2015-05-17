package me.manuelp.medialibrarian.logging;

import fj.function.Effect2;
import jline.console.ConsoleReader;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class ConsoleLoggerBuilder implements LoggerBuilder {
  private ConsoleReader console;
  private LogLevel minimum;

  public ConsoleLoggerBuilder(ConsoleReader console, LogLevel minimum) {
    this.console = console;
    this.minimum = minimum;
  }

  @Override
  public Effect2<String, LogLevel> logger(String subject) {
    return (s, l) -> {
      if (l.compareTo(minimum) >= 0)
        printToConsole(subject, LocalDateTime.now(), l, s);
    };
  }

  @Override
  public Effect2<String, LogLevel> logger(Class subject) {
    return logger(subject.getSimpleName());
  }

  @Override
  public Effect2<String, LogLevel> logger(Object subject) {
    return logger(subject.getClass().getSimpleName());
  }

  private void printToConsole(String subject, LocalDateTime date,
      LogLevel logLevel, String message) {
    try {
      String s = String.format("%s %s [%s] ~ %s",
        date.format(DateTimeFormatter.ISO_DATE_TIME), logLevel.name(), subject,
        message);
      console.println(s);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

}
