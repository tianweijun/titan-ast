//
// Created by tian wei jun on 2022/11/24 0024.
//

#include "PersistentData.h"
#include "AstRuntimeException.h"
#include "SyntaxDfaState.h"

PersistentData::PersistentData(const std::string *automataFilePath)
    : intByteBuffer(ByteBuffer(4, true)), stringPool(nullptr),
      sizeOfStringPool(0), grammars(nullptr), sizeOfGramamrs(0),
      productionRules(nullptr), sizeOfProductionRules(0) {
  init(automataFilePath);
}

void PersistentData::init(const std::string *automataFilePath) {
  inputStream.open(*automataFilePath, std::ios::in | std::ios::binary);

  if (!inputStream.is_open()) {
    AstRuntimeExceptionResolver::throwException(AstRuntimeException(
        AstRuntimeExceptionCode::IO_ERROR,
        "open automata File error,path:'" + *automataFilePath + "'"));
  }
}

PersistentData::~PersistentData() {
  inputStream.close();
  // other delete by AutomataData
}

void PersistentData::getProductionRulesByInputStream() {
  int countOfProductionRules = readInt();
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
    productionRule->grammar = grammars[readInt()];
    int indexOfAliasInStringPool = readInt();
    if (indexOfAliasInStringPool >= 0) {
      productionRule->alias = stringPool[indexOfAliasInStringPool];
    }
    productionRule->reducingDfa = getSyntaxDfaByInputStream();
  }
}

SyntaxDfa *PersistentData::getSyntaxDfaByInputStream() {
  int sizeOfSyntaxDfaStates = readInt();
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
    syntaxDfaState->type = readInt();
    int sizeOfEdges = readInt();
    syntaxDfaState->edges =
        std::unordered_map<const Grammar *, SyntaxDfaState *,
                           PtrGrammarContentHash, PtrGrammarContentEq>(
            sizeOfEdges);
    for (int indexOfEdge = 0; indexOfEdge < sizeOfEdges; indexOfEdge++) {
      Grammar *ch = grammars[readInt()];
      SyntaxDfaState *chToState = syntaxDfaStates[readInt()];
      std::pair<const Grammar *, SyntaxDfaState *> keyValue(ch, chToState);
      syntaxDfaState->edges.insert(keyValue);
    }
    int sizeOfProductions = readInt();
    for (int indexOfProduction = 0; indexOfProduction < sizeOfProductions;
         indexOfProduction++) {
      syntaxDfaState->closingProductionRules.push_back(
          productionRules[readInt()]);
    }
    syntaxDfaState->closingProductionRules.shrink_to_fit();
  }
  auto *syntaxDfa = new SyntaxDfa(syntaxDfaStates[0],
                                  (const SyntaxDfaState **)syntaxDfaStates,
                                  sizeOfSyntaxDfaStates);
  return syntaxDfa;
}

Grammar *PersistentData::getGrammarByInputStream() {
  int indexOfGrammar = readInt();
  return grammars[indexOfGrammar];
}

TokenDfa *PersistentData::getTokenDfaByInputStream() {
  int sizeOfTokenDfaStates = readInt();
  auto **tokenDfaStates = new TokenDfaState *[sizeOfTokenDfaStates];
  for (int indexOfTokenDfaState = 0;
       indexOfTokenDfaState < sizeOfTokenDfaStates; indexOfTokenDfaState++) {
    tokenDfaStates[indexOfTokenDfaState] = new TokenDfaState();
  }
  // countOfTokenDfaStates-(type-weight-terminal-countOfEdges-[ch,dest]{countOfEdges})
  for (int indexOfTokenDfaState = 0;
       indexOfTokenDfaState < sizeOfTokenDfaStates; indexOfTokenDfaState++) {
    TokenDfaState *tokenDfaState = tokenDfaStates[indexOfTokenDfaState];
    tokenDfaState->type = readInt();
    tokenDfaState->weight = readInt();
    int intOfTerminal = readInt();
    if (intOfTerminal >= 0) {
      tokenDfaState->terminal = grammars[intOfTerminal];
    }
    int sizeOfEdges = readInt();
    tokenDfaState->edges =
        std::unordered_map<byte, TokenDfaState *>(sizeOfEdges);
    for (int indexOfEdge = 0; indexOfEdge < sizeOfEdges; indexOfEdge++) {
      int ch = readInt();
      TokenDfaState *chToState = tokenDfaStates[readInt()];
      std::pair<byte, TokenDfaState *> keyValue(ch, chToState);
      tokenDfaState->edges.insert(keyValue);
    }
  }

  auto *tokenDfa =
      new TokenDfa(tokenDfaStates[0], (const TokenDfaState **)tokenDfaStates,
                   sizeOfTokenDfaStates);
  return tokenDfa;
}

KeyWordAutomata *PersistentData::getKeyWordAutomataByInputStream() {
  KeyWordAutomata *keyWordAutomata = new KeyWordAutomata();

  keyWordAutomata->emptyOrNot = readInt();

  if (keyWordAutomata->emptyOrNot == KeyWordAutomata::EMPTY) {
    return keyWordAutomata;
  }

  keyWordAutomata->rootKeyWord = grammars[readInt()];

  int keyWordsSize = readInt();

  keyWordAutomata->textTerminalMap =
      std::unordered_map<std::string *, Grammar *, TextTerminalMapHash,
                         TextTerminalMapEq>(keyWordsSize);
  for (int indexOfKeyWords = 0; indexOfKeyWords < keyWordsSize;
       indexOfKeyWords++) {
    int intOfText = readInt();
    std::string *text = stringPool[intOfText];

    int intOfTerminal = readInt();
    Grammar *terminal = grammars[intOfTerminal];

    std::pair<std::string *, Grammar *> pair(text, terminal);
    keyWordAutomata->textTerminalMap.insert(pair);
  }

  return keyWordAutomata;
}

Grammar **PersistentData::getGrammarsByInputStream() {
  int _sizeOfGramamrs = readInt();
  auto **heapGrammars = new Grammar *[_sizeOfGramamrs];

  for (int indexOfGrammar = 0; indexOfGrammar < _sizeOfGramamrs;
       indexOfGrammar++) {
    auto type = GrammarType(readInt());
    auto *grammar = newGrammarByType(type, indexOfGrammar);
    grammar->name = *(stringPool[readInt()]);
    grammar->action = GrammarAction(readInt());
    if (type == GrammarType::TERMINAL) {
      auto *terminalGrammar = (TerminalGrammar *)grammar;
      terminalGrammar->lookaheadMatchingMode = LookaheadMatchingMode(readInt());
    }
    heapGrammars[indexOfGrammar] = grammar;
  }
  this->sizeOfGramamrs = _sizeOfGramamrs;
  this->grammars = heapGrammars;
  return heapGrammars;
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

std::string **PersistentData::getStringPoolByInputStream() {
  int sizeOfStrings = readInt();
  auto **strings = new std::string *[sizeOfStrings];
  for (int indexOfString = 0; indexOfString < sizeOfStrings; indexOfString++) {
    int countOfStringBytes = readInt();
    std::string *str = readByteString(countOfStringBytes);
    strings[indexOfString] = str;
  }
  this->stringPool = strings;
  this->sizeOfStringPool = sizeOfStrings;
  return strings;
}

std::string *PersistentData::readByteString(int countOfStringBytes) {
  auto *str = new std::string(countOfStringBytes, 0);
  char *buf = const_cast<char *>(str->data());
  doRead((byte *)(buf), 0, countOfStringBytes);
  return str;
}

int PersistentData::readInt() {
  doRead(intByteBuffer.buffer, 0, intByteBuffer.capacity);
  intByteBuffer.setLimit(intByteBuffer.capacity);
  return intByteBuffer.getInt();
}

void PersistentData::doRead(byte bytes[], int offset, int length) {
  char *base = reinterpret_cast<char *>(bytes + offset);
  int countOfRead = inputStream.read(base, length).gcount();
  if (countOfRead != length) { // 文件数据损坏
    AstRuntimeExceptionResolver::throwException(AstRuntimeException(
        AstRuntimeExceptionCode::IO_ERROR, "data of automata File is error"));
  }
}

void PersistentData::compact() { inputStream.close(); }

AstAutomataType PersistentData::getAstAutomataTypeByInputStream() {
  auto type = AstAutomataType(readInt());
  return type;
}

std::unordered_map<const Grammar *,
                   std::unordered_set<const Grammar *, PtrGrammarContentHash,PtrGrammarContentEq> *,
                   PtrGrammarContentHash,PtrGrammarContentEq> *
PersistentData::getNonterminalFollowMapByInputStream() {
  int size = readInt();
  auto *nonterminalFollowMap =
      new std::unordered_map<const Grammar *,
                             std::unordered_set<const Grammar *, PtrGrammarContentHash,PtrGrammarContentEq> *,
                             PtrGrammarContentHash,PtrGrammarContentEq>(size);
  for (int indexOfNonterminal = 0; indexOfNonterminal < size;
       indexOfNonterminal++) {
    Grammar *nonterminal = getGrammarByInputStream();

    int sizeOfFollow = readInt();
    auto follow = new std::unordered_set<const Grammar *, PtrGrammarContentHash,PtrGrammarContentEq>(sizeOfFollow);
    for (int indexOfFollow = 0; indexOfFollow < sizeOfFollow; indexOfFollow++) {
      follow->insert(getGrammarByInputStream());
    }

    std::pair<const Grammar *,
              std::unordered_set<const Grammar *, PtrGrammarContentHash,PtrGrammarContentEq> *>
        pair(nonterminal, follow);
    nonterminalFollowMap->insert(pair);
  }
  return nonterminalFollowMap;
}
