#ifndef STRINGTREEGRAPHICSITEM_H
#define STRINGTREEGRAPHICSITEM_H
#include <QGraphicsItem>
#include <QPainter>
#include <list>
#include <map>
#include <mutex>
#include <set>

#include "StringTree.h"

class Box;
class BoxTreeContext;

class StringTreeGraphicsItem : public QGraphicsItem {
 public:
  explicit StringTreeGraphicsItem(const StringTree *stringTree);
  StringTreeGraphicsItem(const StringTreeGraphicsItem &stringTreeGraphicsItem) = delete;
  StringTreeGraphicsItem(const StringTreeGraphicsItem &&stringTreeGraphicsItem) = delete;
  ~StringTreeGraphicsItem() override;

 public:
  QRectF boundingRect() const override;
  void paint(QPainter *painter, const QStyleOptionGraphicsItem *option,
             QWidget *widget) override;

 private:
  int width;
  int height;
  const StringTree *stringTree;

 public:
  static QFont *font;
  static QFontMetrics *fontMetrics;
  static int fontHeight;
  static int colLineHeight;
  static int rowTextGap;

 private:
  static std::mutex initStaticVarsLock;
  static void initStaticVars();

 private:
  void drawBoxTree(QPainter *painter, BoxTreeContext *boxTreeContext, Box *box);
  BoxTreeContext *getDrawTreeContext(const StringTree *strTree);
  void refreshDrawingBoxTreeContext(BoxTreeContext *boxTreeContext);
};

class HierarchicalRow {
 public:
  HierarchicalRow() : heightOfTree(0), endOfRow(0), height(0) {}
  explicit HierarchicalRow(int heightOfTree)
      : heightOfTree(heightOfTree), endOfRow(0), height(0) {}
  HierarchicalRow(const HierarchicalRow &hierarchicalRow) {
    set(hierarchicalRow);
  };
  HierarchicalRow(const HierarchicalRow &&hierarchicalRow) noexcept {
    set(hierarchicalRow);
  };
  HierarchicalRow &operator=(const HierarchicalRow &hierarchicalRow) {
    set(hierarchicalRow);
  }
  void set(const HierarchicalRow &hierarchicalRow) {
    heightOfTree = hierarchicalRow.heightOfTree;
    endOfRow = hierarchicalRow.endOfRow;
    height = hierarchicalRow.height;
  }
  ~HierarchicalRow() = default;
  int heightOfTree{};
  int endOfRow{};
  int height{};

  bool operator<(const HierarchicalRow &o) const {
    return this->heightOfTree < o.heightOfTree;
  }
};

class Box {
 public:
  Box() = default;
  Box(const std::string *text, Box *parent, HierarchicalRow hierarchicalRow);
  Box(const Box &box) {
    set(box);
  }
  Box(const Box &&box) noexcept {
    set(box);
  }
  ~Box();

  void set(const Box &box) {
    hierarchicalRow = box.hierarchicalRow;
    horizontalAxis = box.horizontalAxis;
    verticalAxis = box.verticalAxis;
    width = box.width;
    height = box.height;
    text = box.text;
    parent = box.parent;
    children = box.children;
  }
  HierarchicalRow hierarchicalRow{};
  int horizontalAxis{};
  int verticalAxis{};
  int width{};
  int height{};
  const std::string *text;
  Box *parent{};
  std::list<Box *> *children{};
};

class BoxTreeContext {
 public:
  BoxTreeContext();
  BoxTreeContext(const BoxTreeContext &boxTreeContext) = delete;
  BoxTreeContext(const BoxTreeContext &&boxTreeContext) = delete;
  ~BoxTreeContext();

  int width;
  int height;
  std::map<HierarchicalRow, std::list<Box *> *> *hierarchicalRowMap;
  Box *boxTree;

  void build(const StringTree *stringTree);

 private:
  Box *initBoxTree(const StringTree *tree, Box *parent,
                   HierarchicalRow hierarchicalRow);
  void addBoxToHierarchicalRowMap(HierarchicalRow hierarchicalRow, Box *box) const;
  void initLocationOfBox();
  void alignCenter(Box *box);
  void moveRightBoxInRow(Box *box, int moveRight);
  void moveRightOnceForEachRow(std::set<HierarchicalRow> &hasMovedRows,
                               Box *box, int moveRight);
  void setWidthAndHeight();
};

#endif// STRINGTREEGRAPHICSITEM_H
