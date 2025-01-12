#ifndef STRINGTREE_H
#define STRINGTREE_H

#include <list>
#include <string>

class StringTree {
 public:
  StringTree();
  StringTree(const StringTree &stringTree) = delete;
  StringTree(const StringTree &&stringTree) = delete;
  ~StringTree();
  int getHeight() const;
  std::list<StringTree *> *children;
  std::string text;

 private:
  int getMaxHeight(const StringTree* stringTree, int height, int currentHeight) const;
};

#endif// STRINGTREE_H
