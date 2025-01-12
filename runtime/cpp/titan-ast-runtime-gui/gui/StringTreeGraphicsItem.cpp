#include "StringTreeGraphicsItem.h"

#include <QPen>

std::mutex StringTreeGraphicsItem::initStaticVarsLock{};
QFont *StringTreeGraphicsItem::font = nullptr;
QFontMetrics *StringTreeGraphicsItem::fontMetrics = nullptr;
int StringTreeGraphicsItem::fontHeight = 0;

int StringTreeGraphicsItem::colLineHeight = 0;
int StringTreeGraphicsItem::rowTextGap = 0;

void StringTreeGraphicsItem::initStaticVars() {
  if (!StringTreeGraphicsItem::font) {
    initStaticVarsLock.lock();
    if (!StringTreeGraphicsItem::font) {
      StringTreeGraphicsItem::font = new QFont();
      StringTreeGraphicsItem::font->setPointSize(16);
      StringTreeGraphicsItem::fontMetrics =
          new QFontMetrics(*StringTreeGraphicsItem::font);
      StringTreeGraphicsItem::fontHeight = fontMetrics->height();
      StringTreeGraphicsItem::colLineHeight =
          StringTreeGraphicsItem::fontHeight * 2;
      StringTreeGraphicsItem::rowTextGap = fontHeight;
    }
    initStaticVarsLock.unlock();
  }
}

StringTreeGraphicsItem::StringTreeGraphicsItem(const StringTree *stringTree)
    : stringTree(stringTree), width(600), height(600) {
  StringTreeGraphicsItem::initStaticVars();
  getDrawTreeContext(stringTree);
}

// delete StringTreeGraphicsItem::fontMetrics;
// StringTreeGraphicsItem::fontMetrics = 0;
// delete StringTreeGraphicsItem::font;
// StringTreeGraphicsItem::font = 0;
StringTreeGraphicsItem::~StringTreeGraphicsItem() = default;

QRectF StringTreeGraphicsItem::boundingRect() const {
  return {0 - width / 2.0, 0 - height / 2.0, static_cast<qreal>(width), static_cast<qreal>(height)};
}

void StringTreeGraphicsItem::paint(QPainter *painter,
                                   const QStyleOptionGraphicsItem *option,
                                   QWidget *widget) {
  painter->setRenderHint(QPainter::Antialiasing); //抗锯齿
  painter->setFont(*StringTreeGraphicsItem::font);//设置字体

  //设置画笔
  QPen pen;
  pen.setWidth(1);                //线宽
  pen.setColor(Qt::black);        //划线颜色
  pen.setStyle(Qt::SolidLine);    //线的类型，实线、虚线等
  pen.setCapStyle(Qt::FlatCap);   //线端点样式
  pen.setJoinStyle(Qt::BevelJoin);//线的连接点样式
  painter->setPen(pen);

  if (!stringTree) {
    return;
  }
  BoxTreeContext *boxTreeContext = getDrawTreeContext(stringTree);
  drawBoxTree(painter, boxTreeContext, boxTreeContext->boxTree);
  delete boxTreeContext;
  boxTreeContext = nullptr;
}

void StringTreeGraphicsItem::drawBoxTree(QPainter *painter,
                                         BoxTreeContext *boxTreeContext,
                                         Box *box) {
  int leftTranslation = 0 - boxTreeContext->width / 2;
  int upTranslation = 0 - boxTreeContext->height / 2;
  if (!(0 == box->text || box->text->length() <= 0)) {
    painter->drawText(QPoint(box->horizontalAxis + leftTranslation,
                             box->verticalAxis + upTranslation),
                      QString::fromStdString(*(box->text)));
  }
  std::list<Box *> *children = box->children;
  if (!children->empty()) {
    int fromOfLineX = box->horizontalAxis + box->width / 2;
    int fromOfLineY = box->verticalAxis;

    for (auto child : *box->children) {
      int toOfLineX = child->horizontalAxis + child->width / 2;
      int toOfLineY = child->verticalAxis - fontHeight;
      painter->drawLine(
          QPoint(fromOfLineX + leftTranslation, fromOfLineY + upTranslation),
          QPoint(toOfLineX + leftTranslation, toOfLineY + upTranslation));
    }
  }
  if (!children->empty()) {
    for (auto child : *box->children) {
      drawBoxTree(painter, boxTreeContext, child);
    }
  }
}

BoxTreeContext *StringTreeGraphicsItem::getDrawTreeContext(
    const StringTree *strTree) {
  auto *boxTreeContext = new BoxTreeContext();
  boxTreeContext->build(strTree);
  refreshDrawingBoxTreeContext(boxTreeContext);
  return boxTreeContext;
}

void StringTreeGraphicsItem::refreshDrawingBoxTreeContext(
    BoxTreeContext *boxTreeContext) {
  this->width = boxTreeContext->width;
  this->height = boxTreeContext->height;
}

Box::Box(const std::string *text,  int indexOfRow)
    : text(text), indexOfRow(indexOfRow),
      horizontalAxis(0), verticalAxis(0) {
  this->children = new std::list<Box *>();
  this->height = StringTreeGraphicsItem::fontHeight;

  if (!text || text->length() <= 0) {
    this->width = StringTreeGraphicsItem::fontHeight;
  } else {
    QString qText = QString::fromStdString(*text);
    QRect rec = StringTreeGraphicsItem::fontMetrics->boundingRect(qText);
    //字符串所占的像素宽度,高度
    this->width = rec.width();
  }
}

Box::~Box() {
  // delete by StringTree
  // delete text;
  // text = nullptr;

  // delete children
  if (children) {
    for (std::list<Box *>::const_iterator childIt = children->begin();
         childIt != children->end();) {
      Box *child = *childIt;
      delete child;
      child = nullptr;
      childIt = children->erase(childIt);
    }
    delete children;
    children = nullptr;
  }
}

BoxTreeContext::BoxTreeContext() : width(0), height(0), boxTree(0) {
}

BoxTreeContext::~BoxTreeContext() {
  //删掉所有box
  delete boxTree;
  boxTree = nullptr;
}

void BoxTreeContext::build(const StringTree *stringTree) {
  initRows(stringTree->getHeight());
  boxTree = initBoxTree(stringTree, nullptr, 0);
  initLocationOfBox();
  alignCenter(boxTree);
  setWidthAndHeight();
}

void BoxTreeContext::initRows(int height) {
  rows.reserve(height);
  for (int heightOfTree = 1; heightOfTree <= height; heightOfTree++) {
    rows.push_back(HierarchicalRow(heightOfTree));
  }
}

Box *BoxTreeContext::initBoxTree(const StringTree *strTree, Box *parent,
                                 int indexOfRow) {
  Box *box = new Box(&(strTree->text),  indexOfRow);
  rows.at(indexOfRow).boxs.push_back(box);

  int indexOfChildrenRow = indexOfRow + 1;
  for (auto strTreeChild : *strTree->children) {
    Box *boxOfChild =
        initBoxTree(strTreeChild, box, indexOfChildrenRow);
    box->children->push_back(boxOfChild);
  }
  return box;
}

void BoxTreeContext::initLocationOfBox() {
  int padding = StringTreeGraphicsItem::fontHeight;
  int fontHeight = StringTreeGraphicsItem::fontHeight;
  int colLineHeight = StringTreeGraphicsItem::colLineHeight;
  int rowTextGap = StringTreeGraphicsItem::rowTextGap;
  for (int indexOfRow = 0; indexOfRow < rows.size(); indexOfRow++) {
    auto hierarchicalRow = &rows.at(indexOfRow);
    int endInRow = padding;
    int verticalAxis =
        padding + (hierarchicalRow->heightOfTree - 1) * (fontHeight + colLineHeight) + fontHeight;

    for (auto box : hierarchicalRow->boxs) {
      box->horizontalAxis = endInRow;
      box->verticalAxis = verticalAxis;

      endInRow += box->width + rowTextGap;
    }
    hierarchicalRow->endOfRow = endInRow;
    hierarchicalRow->height = verticalAxis;
  }
}

void BoxTreeContext::alignCenter(Box *box) {
  if (box->children->empty()) {
    return;
  }
  for (auto childBox : *box->children) {
    alignCenter(childBox);
  }
  int startOfChildRow = box->children->front()->horizontalAxis;
  Box *childrenLast = box->children->back();
  int endOfChildRow = childrenLast->horizontalAxis + childrenLast->width;
  int midOfChildRow = startOfChildRow + (endOfChildRow - startOfChildRow) / 2;
  int midOfThis = box->horizontalAxis + box->width / 2;
  if (midOfThis < midOfChildRow) {// 元素偏左，其之后的当前行box右移
    int thisMoveRight = midOfChildRow - midOfThis;
    moveRightBoxInRow(box, thisMoveRight);
  }
  if (midOfThis > midOfChildRow) {// 元素偏右,孩子右移
    int childMoveRight = midOfThis - midOfChildRow;
    std::set<int> hasMovedRows{};
    for (auto child : *box->children) {
      moveRightOnceForEachRow(&hasMovedRows, child, childMoveRight);
    }
  }
}

void BoxTreeContext::moveRightOnceForEachRow(
    std::set<int> *hasMovedRows, Box *box, int moveRight) {
  auto findHasMovedRowIt = hasMovedRows->find(box->indexOfRow);
  if (findHasMovedRowIt == hasMovedRows->end()) {
    moveRightBoxInRow(box, moveRight);
    hasMovedRows->insert(box->indexOfRow);
  }
  for (auto child : *box->children) {
    moveRightOnceForEachRow(hasMovedRows, child, moveRight);
  }
}

void BoxTreeContext::moveRightBoxInRow(Box *box, int moveRight) {
  bool shouldMoveRight = false;
  auto hierarchicalRow = &rows.at(box->indexOfRow);
  auto boxesInRow = &hierarchicalRow->boxs;
  auto boxesInRowIt = boxesInRow->begin();
  while (boxesInRowIt != boxesInRow->end()) {
    Box *boxInRow = *boxesInRowIt;
    boxesInRowIt++;
    if (boxInRow == box) {
      shouldMoveRight = true;
      boxInRow->horizontalAxis += moveRight;
      break;
    }
  }
  if (shouldMoveRight) {
    while (boxesInRowIt != boxesInRow->end()) {
      Box *boxInRow = *boxesInRowIt;
      boxesInRowIt++;
      boxInRow->horizontalAxis += moveRight;
    }
    hierarchicalRow->endOfRow += moveRight;
  }
}

void BoxTreeContext::setWidthAndHeight() {
  int padding = StringTreeGraphicsItem::fontHeight;
  for (const HierarchicalRow& hierarchicalRow : rows) {
    int currentWidth = hierarchicalRow.endOfRow + padding;
    if (currentWidth > width) {
      width = currentWidth;
    }
    int currentHeight = hierarchicalRow.height + padding;
    if (currentHeight > height) {
      height = currentHeight;
    }
  }
}

