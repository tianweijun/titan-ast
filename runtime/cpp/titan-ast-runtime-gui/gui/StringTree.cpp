#include "StringTree.h"

StringTree::StringTree() {
  children = new std::list<StringTree *>();
}

StringTree::~StringTree() {
  // delete children
  if (children) {
    for (std::list<StringTree *>::const_iterator strTreeChildrenIt =
             children->begin();
         strTreeChildrenIt != children->end();) {
      StringTree *strTreeChild = *strTreeChildrenIt;
      delete strTreeChild;
      strTreeChild = nullptr;
      strTreeChildrenIt = children->erase(strTreeChildrenIt);
    }
    delete children;
    children = nullptr;
  }
}
int StringTree::getHeight() const{
  return getMaxHeight(this,1,1);
}
int StringTree::getMaxHeight(const StringTree* stringTree, int height, int currentHeight) const{
  if (currentHeight > height) {
    height = currentHeight;
  }
  int childHeight = currentHeight + 1;
  for (auto child : *stringTree->children) {
    int maxHeightOfChild = getMaxHeight(child, height, childHeight);
    if (maxHeightOfChild > height) {
      height = maxHeightOfChild;
    }
  }
  return height;
}
