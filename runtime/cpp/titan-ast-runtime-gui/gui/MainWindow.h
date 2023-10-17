#ifndef MAINWINDOW_H
#define MAINWINDOW_H

#include <QApplication>
#include <QFontMetrics>
#include <QMainWindow>

#include "StringTree.h"
#include "StringTreeGraphicsItem.h"

namespace Ui {
class MainWindow;
}

class MainWindow : public QMainWindow {
  Q_OBJECT

 public:
  explicit MainWindow(const StringTree *stringTree, QWidget *parent = 0);
  MainWindow(const MainWindow &mainWindow) = delete;
  MainWindow(const MainWindow &&mainWindow) = delete;
  ~MainWindow() override;

  QApplication *app{};

 protected:
  void closeEvent(QCloseEvent *event) override;

 private slots:
  void on_exportImgBtn_clicked();
  void on_treeViewScaleSliderValueChanged(int value);

 private:
  Ui::MainWindow *ui;

  QGraphicsScene *sence;
  StringTreeGraphicsItem *stringTreeGraphicsItem;
};

#endif// MAINWINDOW_H
