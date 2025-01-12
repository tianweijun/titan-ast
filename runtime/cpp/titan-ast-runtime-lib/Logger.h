//
// Created by tian wei jun on 2022/11/22 0022.
//

#ifndef AST__RUNTIME__LOGGER_H_
#define AST__RUNTIME__LOGGER_H_

#define debug(format, ...) \
  Logger::logger.log(Logger::DEBUG, __FILE__, __LINE__, format, ##__VA_ARGS__)

#define info(format, ...) \
  Logger::logger.log(Logger::INFO, __FILE__, __LINE__, format, ##__VA_ARGS__)

#define warn(format, ...) \
  Logger::logger.log(Logger::WARN, __FILE__, __LINE__, format, ##__VA_ARGS__)

#define error(format, ...) \
  Logger::logger.log(Logger::ERROR, __FILE__, __LINE__, format, ##__VA_ARGS__)

#define fatal(format, ...) \
  Logger::logger.log(Logger::FATAL, __FILE__, __LINE__, format, ##__VA_ARGS__)

class Logger {
 public:
  enum Level { DEBUG = 0,
               INFO,
               WARN,
               ERROR,
               FATAL,
               LEVEL_COUNT };
  Logger(const Logger &logger) = delete;
  Logger(const Logger &&logger) = delete;

  static Logger logger;

  void log(Level level, const char *file, int line, const char *format,
           ...) const;

  void level(int level);

 private:
  Logger();

  ~Logger();

 private:
  int m_level;
  static const char *s_level[LEVEL_COUNT];
};

#endif// AST__RUNTIME__LOGGER_H_