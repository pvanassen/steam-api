import ch.qos.logback.classic.encoder.PatternLayoutEncoder
import ch.qos.logback.core.ConsoleAppender

import static ch.qos.logback.classic.Level.ERROR
import static ch.qos.logback.classic.Level.INFO

appender("stdout", ConsoleAppender) {
  target = "System.out"
  encoder(PatternLayoutEncoder) {
    pattern = "%d{yyyy-MM-dd HH:mm:ss} %-5p %c{1}:%L - %m%n"
  }
}
logger("org.apache.http.wire", ERROR)
logger("org.apache.http", ERROR)
root(INFO, ["stdout"])