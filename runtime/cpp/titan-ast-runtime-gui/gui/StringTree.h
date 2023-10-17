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
  explicit StringTree(StringTree *parent);
  StringTree *parent;
  std::list<StringTree *> *children;
  std::string text;
};

#endif// STRINGTREE_H
