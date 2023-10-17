#include "GuiApi.h"

#include <QApplication>

#include "MainWindow.h"

int guiapi::showViewUntilClose(const StringTree *strTree) {
  if (nullptr == strTree) {
    return 0;
  }
  int argc = 0;
  char *argv[] = {nullptr};
  QApplication app(argc, argv);
  MainWindow w(strTree, nullptr);
  w.app = &app;
  w.show();

  int exitCode = QApplication::exec();

  return exitCode;
}
