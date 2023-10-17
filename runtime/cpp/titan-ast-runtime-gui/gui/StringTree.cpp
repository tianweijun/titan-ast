#include "StringTree.h"

StringTree::StringTree() : parent(nullptr) {
  children = new std::list<StringTree *>();
}

StringTree::StringTree(StringTree *parent) : StringTree() {
  this->parent = parent;
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
