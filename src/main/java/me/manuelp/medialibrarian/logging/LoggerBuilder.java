package me.manuelp.medialibrarian.logging;

import fj.function.Effect2;

public interface LoggerBuilder {
  Effect2<String, LogLevel> logger(String subject);
  Effect2<String, LogLevel> logger(Class subject);
  Effect2<String, LogLevel> logger(Object subject);
}
