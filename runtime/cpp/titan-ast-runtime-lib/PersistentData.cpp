//
// Created by tian wei jun on 2022/11/24 0024.
//

#include "PersistentData.h"
#include "SyntaxDfaState.h"

PersistentData::PersistentData()
    : intByteBuffer(ByteBuffer(4, true)), stringPool(nullptr),
      sizeOfStringPool(0), grammars(nullptr), sizeOfGramamrs(0),
      productionRules(nullptr), sizeOfProductionRules(0) {}

BuildAutomataResult PersistentData::init(const std::string *automataFilePath) {
  inputStream.open(*automataFilePath, std::ios::in | std::ios::binary);

  if (!inputStream.is_open()) {
    return {false, "open automata File error,path:'" + *automataFilePath + "'"};
  }
  return {true, ""};
}

PersistentData::~PersistentData() {
  inputStream.close();
  // other delete by AutomataData
}

GetProductionRulesByInputStreamResult
PersistentData::getProductionRulesByInputStream() {
  ReadIntResult readIntResult = readInt();
  if (!readIntResult.isOk) {
    return GetProductionRulesByInputStreamResult{false};
  }
  int countOfProductionRules = readIntResult.data;
  auto **heapProductionRules = new ProductionRule *[countOfProductionRules];
  for (int indexOfProductionRule = 0;
       indexOfProductionRule < countOfProductionRules;
       indexOfProductionRule++) {
    heapProductionRules[indexOfProductionRule] = new ProductionRule();
  }

  this->sizeOfProductionRules = countOfProductionRules;
  this->productionRules = heapProductionRules;

  for (int indexOfProductionRule = 0;
       indexOfProductionRule < countOfProductionRules;
       indexOfProductionRule++) {
    ProductionRule *productionRule = heapProductionRules[indexOfProductionRule];
    readIntResult = readInt();
    if (!readIntResult.isOk) {
      return GetProductionRulesByInputStreamResult{false};
    }
    productionRule->grammar = grammars[readIntResult.data];
    readIntResult = readInt();
    if (!readIntResult.isOk) {
      return GetProductionRulesByInputStreamResult{false};
    }
    int indexOfAliasInStringPool = readIntResult.data;
    if (indexOfAliasInStringPool >= 0) {
      productionRule->alias = stringPool[indexOfAliasInStringPool];
    }
    GetSyntaxDfaByInputStreamResult getSyntaxDfaByInputStreamResult =
        getSyntaxDfaByInputStream();
    if (!getSyntaxDfaByInputStreamResult.isOk) {
      return GetProductionRulesByInputStreamResult{false};
    }
    productionRule->reducingDfa = getSyntaxDfaByInputStreamResult.data;
  }
  return GetProductionRulesByInputStreamResult{true, heapProductionRules,
                                               countOfProductionRules};
}

GetSyntaxDfaByInputStreamResult PersistentData::getSyntaxDfaByInputStream() {
  ReadIntResult readIntResult = readInt();
  if (!readIntResult.isOk) {
    return GetSyntaxDfaByInputStreamResult{false, nullptr};
  }
  int sizeOfSyntaxDfaStates = readIntResult.data;
  auto **syntaxDfaStates = new SyntaxDfaState *[sizeOfSyntaxDfaStates];
  for (int indexOfSyntaxDfaState = 0;
       indexOfSyntaxDfaState < sizeOfSyntaxDfaStates; indexOfSyntaxDfaState++) {
    syntaxDfaStates[indexOfSyntaxDfaState] =
        new SyntaxDfaState(indexOfSyntaxDfaState);
  }
  // countOfSyntaxDfaStates-(type-countOfEdges-[ch,dest]{countOfEdges}-countOfProductions-productions)
  for (int indexOfSyntaxDfaState = 0;
       indexOfSyntaxDfaState < sizeOfSyntaxDfaStates; indexOfSyntaxDfaState++) {
    SyntaxDfaState *syntaxDfaState = syntaxDfaStates[indexOfSyntaxDfaState];
    readIntResult = readInt();
    if (!readIntResult.isOk) {
      return GetSyntaxDfaByInputStreamResult{false, nullptr};
    }
    syntaxDfaState->type = readIntResult.data;
    readIntResult = readInt();
    if (!readIntResult.isOk) {
      return GetSyntaxDfaByInputStreamResult{false, nullptr};
    }
    int sizeOfEdges = readIntResult.data;
    syntaxDfaState->edges =
        std::unordered_map<const Grammar *, SyntaxDfaState *,
                           PtrGrammarContentHash, PtrGrammarContentEq>(
            sizeOfEdges);
    for (int indexOfEdge = 0; indexOfEdge < sizeOfEdges; indexOfEdge++) {
      readIntResult = readInt();
      if (!readIntResult.isOk) {
        return GetSyntaxDfaByInputStreamResult{false, nullptr};
      }
      Grammar *ch = grammars[readIntResult.data];
      readIntResult = readInt();
      if (!readIntResult.isOk) {
        return GetSyntaxDfaByInputStreamResult{false, nullptr};
      }
      SyntaxDfaState *chToState = syntaxDfaStates[readIntResult.data];
      std::pair<const Grammar *, SyntaxDfaState *> keyValue(ch, chToState);
      syntaxDfaState->edges.insert(keyValue);
    }
    readIntResult = readInt();
    if (!readIntResult.isOk) {
      return GetSyntaxDfaByInputStreamResult{false, nullptr};
    }
    int sizeOfProductions = readIntResult.data;
    for (int indexOfProduction = 0; indexOfProduction < sizeOfProductions;
         indexOfProduction++) {
      readIntResult = readInt();
      if (!readIntResult.isOk) {
        return GetSyntaxDfaByInputStreamResult{false, nullptr};
      }
      syntaxDfaState->closingProductionRules.push_back(
          productionRules[readIntResult.data]);
    }
    syntaxDfaState->closingProductionRules.shrink_to_fit();
  }
  return GetSyntaxDfaByInputStreamResult{
      true, new SyntaxDfa(syntaxDfaStates[0], (const SyntaxDfaState **) syntaxDfaStates, sizeOfSyntaxDfaStates)};
}

GetGrammarByInputStreamResult PersistentData::getGrammarByInputStream() {
  ReadIntResult readIntResult = readInt();
  if (!readIntResult.isOk) {
    return GetGrammarByInputStreamResult{false, nullptr};
  }
  int indexOfGrammar = readIntResult.data;
  return GetGrammarByInputStreamResult{true, grammars[indexOfGrammar]};
}

GetTokenDfaByInputStreamResult PersistentData::getTokenDfaByInputStream() {
  ReadIntResult readIntResult = readInt();
  if (!readIntResult.isOk) {
    return GetTokenDfaByInputStreamResult{false, nullptr};
  }
  int sizeOfTokenDfaStates = readIntResult.data;
  auto **tokenDfaStates = new TokenDfaState *[sizeOfTokenDfaStates];
  for (int indexOfTokenDfaState = 0;
       indexOfTokenDfaState < sizeOfTokenDfaStates; indexOfTokenDfaState++) {
    tokenDfaStates[indexOfTokenDfaState] = new TokenDfaState();
  }
  // countOfTokenDfaStates-(type-weight-terminal-countOfEdges-[ch,dest]{countOfEdges})
  for (int indexOfTokenDfaState = 0;
       indexOfTokenDfaState < sizeOfTokenDfaStates; indexOfTokenDfaState++) {
    TokenDfaState *tokenDfaState = tokenDfaStates[indexOfTokenDfaState];
    readIntResult = readInt();
    if (!readIntResult.isOk) {
      return GetTokenDfaByInputStreamResult{false, nullptr};
    }
    tokenDfaState->type = readIntResult.data;
    readIntResult = readInt();
    if (!readIntResult.isOk) {
      return GetTokenDfaByInputStreamResult{false, nullptr};
    }
    tokenDfaState->weight = readIntResult.data;
    readIntResult = readInt();
    if (!readIntResult.isOk) {
      return GetTokenDfaByInputStreamResult{false, nullptr};
    }
    int intOfTerminal = readIntResult.data;
    if (intOfTerminal >= 0) {
      tokenDfaState->terminal = grammars[intOfTerminal];
    }
    readIntResult = readInt();
    if (!readIntResult.isOk) {
      return GetTokenDfaByInputStreamResult{false, nullptr};
    }
    int sizeOfEdges = readIntResult.data;
    tokenDfaState->edges =
        std::unordered_map<byte, TokenDfaState *>(sizeOfEdges);
    for (int indexOfEdge = 0; indexOfEdge < sizeOfEdges; indexOfEdge++) {
      readIntResult = readInt();
      if (!readIntResult.isOk) {
        return GetTokenDfaByInputStreamResult{false, nullptr};
      }
      int ch = readIntResult.data;
      readIntResult = readInt();
      if (!readIntResult.isOk) {
        return GetTokenDfaByInputStreamResult{false, nullptr};
      }
      TokenDfaState *chToState = tokenDfaStates[readIntResult.data];
      std::pair<byte, TokenDfaState *> keyValue(ch, chToState);
      tokenDfaState->edges.insert(keyValue);
    }
  }

  return GetTokenDfaByInputStreamResult{
      true,
      new TokenDfa(tokenDfaStates[0], (const TokenDfaState **) tokenDfaStates,
                   sizeOfTokenDfaStates)};
}

GetKeyWordAutomataByInputStreamResult
PersistentData::getKeyWordAutomataByInputStream() {
  KeyWordAutomata *keyWordAutomata = new KeyWordAutomata();

  ReadIntResult readIntResult = readInt();
  if (!readIntResult.isOk) {
    return GetKeyWordAutomataByInputStreamResult{false, nullptr};
  }
  keyWordAutomata->emptyOrNot = readIntResult.data;

  if (keyWordAutomata->emptyOrNot == KeyWordAutomata::EMPTY) {
    return GetKeyWordAutomataByInputStreamResult{true, keyWordAutomata};
  }

  readIntResult = readInt();
  if (!readIntResult.isOk) {
    return GetKeyWordAutomataByInputStreamResult{false, nullptr};
  }
  keyWordAutomata->rootKeyWord = grammars[readIntResult.data];

  readIntResult = readInt();
  if (!readIntResult.isOk) {
    return GetKeyWordAutomataByInputStreamResult{false, nullptr};
  }
  int keyWordsSize = readIntResult.data;

  keyWordAutomata->textTerminalMap =
      std::unordered_map<std::string *, Grammar *, TextTerminalMapHash,
                         TextTerminalMapEq>(keyWordsSize);
  for (int indexOfKeyWords = 0; indexOfKeyWords < keyWordsSize;
       indexOfKeyWords++) {
    readIntResult = readInt();
    if (!readIntResult.isOk) {
      return GetKeyWordAutomataByInputStreamResult{false, nullptr};
    }
    int intOfText = readIntResult.data;
    std::string *text = stringPool[intOfText];

    readIntResult = readInt();
    if (!readIntResult.isOk) {
      return GetKeyWordAutomataByInputStreamResult{false, nullptr};
    }
    int intOfTerminal = readIntResult.data;
    Grammar *terminal = grammars[intOfTerminal];

    std::pair<std::string *, Grammar *> pair(text, terminal);
    keyWordAutomata->textTerminalMap.insert(pair);
  }

  return GetKeyWordAutomataByInputStreamResult{true, keyWordAutomata};
}

GetGrammarsByInputStreamResult PersistentData::getGrammarsByInputStream() {
  ReadIntResult readIntResult = readInt();
  if (!readIntResult.isOk) {
    return GetGrammarsByInputStreamResult{false, nullptr};
  }
  int _sizeOfGramamrs = readIntResult.data;
  auto **heapGrammars = new Grammar *[_sizeOfGramamrs];

  for (int indexOfGrammar = 0; indexOfGrammar < _sizeOfGramamrs;
       indexOfGrammar++) {
    readIntResult = readInt();
    if (!readIntResult.isOk) {
      return GetGrammarsByInputStreamResult{false, nullptr};
    }
    auto type = GrammarType(readIntResult.data);
    auto *grammar = newGrammarByType(type, indexOfGrammar);
    readIntResult = readInt();
    if (!readIntResult.isOk) {
      return GetGrammarsByInputStreamResult{false, nullptr};
    }
    grammar->name = *(stringPool[readIntResult.data]);
    readIntResult = readInt();
    if (!readIntResult.isOk) {
      return GetGrammarsByInputStreamResult{false, nullptr};
    }
    grammar->action = GrammarAction(readIntResult.data);
    if (type == GrammarType::TERMINAL) {
      auto *terminalGrammar = (TerminalGrammar *) grammar;
      readIntResult = readInt();
      if (!readIntResult.isOk) {
        return GetGrammarsByInputStreamResult{false, nullptr};
      }
      terminalGrammar->lookaheadMatchingMode =
          LookaheadMatchingMode(readIntResult.data);
    }
    heapGrammars[indexOfGrammar] = grammar;
  }
  this->sizeOfGramamrs = _sizeOfGramamrs;
  this->grammars = heapGrammars;
  return GetGrammarsByInputStreamResult{true, heapGrammars, _sizeOfGramamrs};
}

Grammar *PersistentData::newGrammarByType(GrammarType type,
                                          int indexOfGrammar) {
  Grammar *grammar = nullptr;
  switch (type) {
    case GrammarType::TERMINAL:
      grammar = new TerminalGrammar(indexOfGrammar);
      break;
    case GrammarType::NONTERMINAL:
      grammar = new NonterminalGrammar(indexOfGrammar);
      break;
    case GrammarType::TERMINAL_FRAGMENT:
    default:
      break;
  }
  return grammar;
}

GetStringPoolByInputStreamResult PersistentData::getStringPoolByInputStream() {
  ReadIntResult readIntResult = readInt();
  if (!readIntResult.isOk) {
    return GetStringPoolByInputStreamResult{false, nullptr};
  }
  int sizeOfStrings = readIntResult.data;
  auto **strings = new std::string *[sizeOfStrings];
  for (int indexOfString = 0; indexOfString < sizeOfStrings; indexOfString++) {
    readIntResult = readInt();
    if (!readIntResult.isOk) {
      return GetStringPoolByInputStreamResult{false, nullptr};
    }
    int countOfStringBytes = readIntResult.data;
    ReadStringResult readStringResult = readByteString(countOfStringBytes);
    if (!readStringResult.isOk) {
      return GetStringPoolByInputStreamResult{false, nullptr};
    }
    std::string *str = readStringResult.data;
    strings[indexOfString] = str;
  }
  this->stringPool = strings;
  this->sizeOfStringPool = sizeOfStrings;
  return GetStringPoolByInputStreamResult{true, strings, sizeOfStrings};
}

ReadStringResult PersistentData::readByteString(int countOfStringBytes) {
  auto *str = new std::string(countOfStringBytes, 0);
  char *buf = const_cast<char *>(str->data());
  bool readResult = doRead((byte *) (buf), 0, countOfStringBytes);
  return ReadStringResult{readResult, str};
}

ReadIntResult PersistentData::readInt() {
  bool readResult = doRead(intByteBuffer.buffer, 0, intByteBuffer.capacity);
  intByteBuffer.setPosition(intByteBuffer.capacity);
  return {readResult, intByteBuffer.getInt()};
}

bool PersistentData::doRead(byte bytes[], int offset, int length) {
  char *base = reinterpret_cast<char *>(bytes + offset);
  int countOfRead = inputStream.read(base, length).gcount();
  // 文件数据可能损坏
  return countOfRead == length;
}

BuildAutomataResult PersistentData::sourceDataError() {
  return {false, "data of automata File is error"};
}

void PersistentData::compact() { inputStream.close(); }

GetAstAutomataTypeResult PersistentData::getAstAutomataTypeByInputStream() {
  ReadIntResult readIntResult = readInt();
  if (!readIntResult.isOk) {
    return GetAstAutomataTypeResult{false};
  }
  auto type = AstAutomataType(readIntResult.data);
  return GetAstAutomataTypeResult{true, type};
}

GetNonterminalFollowMapByInputStreamResult
PersistentData::getNonterminalFollowMapByInputStream() {
  ReadIntResult readIntResult = readInt();
  if (!readIntResult.isOk) {
    return GetNonterminalFollowMapByInputStreamResult{false};
  }
  int size = readIntResult.data;
  auto *nonterminalFollowMap = new std::unordered_map<
      const Grammar *,
      std::unordered_set<const Grammar *, PtrGrammarContentHash,
                         PtrGrammarContentEq> *,
      PtrGrammarContentHash, PtrGrammarContentEq>(size);
  for (int indexOfNonterminal = 0; indexOfNonterminal < size;
       indexOfNonterminal++) {
    GetGrammarByInputStreamResult getGrammarByInputStreamResult =
        getGrammarByInputStream();
    if (!getGrammarByInputStreamResult.isOk) {
      return GetNonterminalFollowMapByInputStreamResult{false};
    }
    Grammar *nonterminal = getGrammarByInputStreamResult.data;

    readIntResult = readInt();
    if (!readIntResult.isOk) {
      return GetNonterminalFollowMapByInputStreamResult{false};
    }
    int sizeOfFollow = readIntResult.data;
    auto follow = new std::unordered_set<const Grammar *, PtrGrammarContentHash,
                                         PtrGrammarContentEq>(sizeOfFollow);
    for (int indexOfFollow = 0; indexOfFollow < sizeOfFollow; indexOfFollow++) {
      getGrammarByInputStreamResult = getGrammarByInputStream();
      if (!getGrammarByInputStreamResult.isOk) {
        return GetNonterminalFollowMapByInputStreamResult{false};
      }
      follow->insert(getGrammarByInputStreamResult.data);
    }

    std::pair<const Grammar *,
              std::unordered_set<const Grammar *, PtrGrammarContentHash,
                                 PtrGrammarContentEq> *>
        pair(nonterminal, follow);
    nonterminalFollowMap->insert(pair);
  }
  return GetNonterminalFollowMapByInputStreamResult{true, nonterminalFollowMap};
}
