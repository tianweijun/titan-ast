use std::collections::BTreeSet;

use crate::{
    ast::{Ast, AstToken, NonterminalAst, TerminalAst, Token},
    ast_automata::{AstBuilder, SubBacktrackingBottomUpAstAutomata},
    automata_tmp_ast::{AutomataTmpAst, NonterminalAutomataTmpAst, TerminalAutomataTmpAst},
    backtracking_bottom_up_branch::BacktrackingBottomUpBranch,
    error::AstAppError,
    fa::FaStateType,
    reducing_symbol::ReducingSymbol,
    syntax_dfa::SyntaxDfa,
    token_reducing_symbol_input_stream::TokenReducingSymbolInputStream,
};

#[derive(Clone)]
pub(crate) struct SuperBacktrackingBottomUpAstAutomata {
    pub(crate) ast_dfa: SyntaxDfa,
    pub(crate) start_grammar: usize,
    pub(crate) token_reducing_symbol_input_stream: TokenReducingSymbolInputStream,
    pub(crate) bottom_up_branchs: BTreeSet<BacktrackingBottomUpBranch>,
    pub(crate) tried_bottom_up_branchs: BTreeSet<BacktrackingBottomUpBranch>,
    pub(crate) result: Option<AutomataTmpAst>,

    pub(crate) sub_ast_automata: SubBacktrackingBottomUpAstAutomata,
}

impl AstBuilder for SuperBacktrackingBottomUpAstAutomata {
    fn build_ast(&mut self, tokens: &Vec<Token>) -> Result<Ast, crate::error::AstAppError> {
        self.init(tokens);
        while self.result.is_none() && !self.bottom_up_branchs.is_empty() {
            self.consume_bottom_up_branch();
        }
        if self.result.is_none() {
            return Err(self.get_ast_parse_error());
        }
        let tmp_ast = self.result.take().unwrap();
        let ret = self.automata_tmp_ast_to_ast(&tmp_ast);
        self.clear();
        Ok(ret)
    }
}

impl SuperBacktrackingBottomUpAstAutomata {
    fn consume_bottom_up_branch(&mut self) {
        let bottom_up_branch = self.bottom_up_branchs.pop_first().unwrap();
        if self.tried_bottom_up_branchs.contains(&bottom_up_branch) {
            return;
        }

        let tried_bottom_up_branch = bottom_up_branch.clone();
        self.tried_bottom_up_branchs.insert(tried_bottom_up_branch);

        if self.is_accepted_bottom_up_branch(&bottom_up_branch) {
            self.result = Some(
                bottom_up_branch
                    .reducing_symbols
                    .last()
                    .unwrap()
                    .ast_of_current_dfa_state
                    .clone(),
            );

            return;
        }

        self.reduce_bottom_up_branch(&bottom_up_branch);
        self.shift_bottom_up_branch(&bottom_up_branch);
    }

    fn reduce_bottom_up_branch(&mut self, bottom_up_branch: &BacktrackingBottomUpBranch) {
        match self.sub_ast_automata.get_type() {
            crate::ast_automata::AstAutomataType::BacktrackingBottomUpAstAutomata => {
                self.super_reduce_bottom_up_branch(bottom_up_branch);
            }
            crate::ast_automata::AstAutomataType::FollowFilterBacktrackingBottomUpAstAutomata => {
                self.reduce_bottom_up_branch_by_follow_filter(bottom_up_branch);
            }
        }
    }

    fn reduce_bottom_up_branch_by_follow_filter(
        &mut self,
        bottom_up_branch: &BacktrackingBottomUpBranch,
    ) {
        let follow_filter_backtracking_bottom_up_ast_automata = self
            .sub_ast_automata
            .get_follow_filter_backtracking_bottom_up_ast_automata_ref()
            .unwrap();
        let top_reducing_symbol = bottom_up_branch.reducing_symbols.last().unwrap();
        self.token_reducing_symbol_input_stream.next_read_index =
            (top_reducing_symbol.end_index_of_token + 1) as usize;

        let mut terminal_of_next_token: usize = 0;
        if self.token_reducing_symbol_input_stream.has_read_all() {
            terminal_of_next_token = follow_filter_backtracking_bottom_up_ast_automata.eof_grammar;
        } else {
            let index_of_token = self.token_reducing_symbol_input_stream.read();
            terminal_of_next_token = self
                .token_reducing_symbol_input_stream
                .get_token_terminal_index(index_of_token);
        }

        let index_of_current_dfa_state = top_reducing_symbol.current_dfa_state;
        let current_dfa_state = self.ast_dfa.get_state(index_of_current_dfa_state);

        let mut do_reduce_production_rules: Vec<usize> =
            Vec::with_capacity(current_dfa_state.closing_production_rules.len());

        for index_of_closing_production_rule in current_dfa_state.closing_production_rules.iter() {
            let nonterminal = self
                .ast_dfa
                .get_production_rule_grammar(*index_of_closing_production_rule);
            let follow = follow_filter_backtracking_bottom_up_ast_automata
                .nonterminal_follow_map
                .get(&nonterminal)
                .unwrap();
            if follow.contains(&terminal_of_next_token) {
                do_reduce_production_rules.push(*index_of_closing_production_rule);
            }
        }

        for index_of_closing_production_rule in do_reduce_production_rules {
            self.do_reduce(bottom_up_branch, index_of_closing_production_rule);
        }
    }

    pub(crate) fn super_reduce_bottom_up_branch(
        &mut self,
        bottom_up_branch: &BacktrackingBottomUpBranch,
    ) {
        let top_reducing_symbol = bottom_up_branch.reducing_symbols.last().unwrap();
        let index_of_current_dfa_state = top_reducing_symbol.current_dfa_state;
        let current_dfa_state = self.ast_dfa.get_state(index_of_current_dfa_state);
        for index_of_closing_production_rule in current_dfa_state.closing_production_rules.iter() {
            self.do_reduce(bottom_up_branch, *index_of_closing_production_rule);
        }
    }

    fn do_reduce(
        &mut self,
        bottom_up_branch: &BacktrackingBottomUpBranch,
        index_of_closing_production_rule: usize,
    ) {
        let closing_production_rule = self
            .ast_dfa
            .get_production_rule(index_of_closing_production_rule);
        let end_index_of_token = bottom_up_branch
            .reducing_symbols
            .last()
            .unwrap()
            .end_index_of_token;
        // 空归约
        if FaStateType::is_closing_tag(
            closing_production_rule
                .reducing_dfa
                .get_state(closing_production_rule.reducing_dfa.start)
                .type_,
        ) {
            let top_reducing_symbol_dfa_state = self.ast_dfa.get_state_ref(
                bottom_up_branch
                    .reducing_symbols
                    .last()
                    .unwrap()
                    .current_dfa_state,
            );
            let index_of_next_state_option = top_reducing_symbol_dfa_state
                .edges
                .get(&closing_production_rule.grammar);
            // 连通的
            if let Some(index_of_next_state) = index_of_next_state_option {
                // 归约的符号
                let nonterminal_reducing_symbol = ReducingSymbol {
                    ast_of_current_dfa_state: AutomataTmpAst::NonterminalAutomataTmpAst(
                        NonterminalAutomataTmpAst {
                            grammar: closing_production_rule.grammar,
                            alias: index_of_closing_production_rule,
                            children: Vec::with_capacity(0),
                        },
                    ),
                    current_dfa_state: index_of_next_state.clone(),
                    end_index_of_token,
                };
                // 归约的符号进栈
                let mut new_bottom_up_branch = bottom_up_branch.clone();
                new_bottom_up_branch
                    .reducing_symbols
                    .push(nonterminal_reducing_symbol);
                self.add_new_backtracking_bottom_up_branch(new_bottom_up_branch);
            }
        }
        // 非空归约
        let mut reducing_production_rule_dfa_state = closing_production_rule
            .reducing_dfa
            .get_state(closing_production_rule.reducing_dfa.start);
        let mut count_of_comsumed_reducing_symbol = 0;
        let reducing_symbols = &bottom_up_branch.reducing_symbols;
        for index_of_reducing_symbols in (0..reducing_symbols.len()).rev().step_by(1) {
            // 读取一个归约符号
            let input_reducing_symbol = &reducing_symbols[index_of_reducing_symbols];
            count_of_comsumed_reducing_symbol += 1;
            if index_of_reducing_symbols == 0 {
                // 栈顶都没有，直接结束
                break;
            }
            let next_reducing_production_rule_dfa_state_option = reducing_production_rule_dfa_state
                .edges
                .get(&(input_reducing_symbol.ast_of_current_dfa_state.get_grammar()));
            if next_reducing_production_rule_dfa_state_option.is_none() {
                // 无法按照产生式向前归约，结束
                break;
            }
            let next_reducing_production_rule_dfa_state = closing_production_rule
                .reducing_dfa
                .get_state(*(next_reducing_production_rule_dfa_state_option.unwrap()));

            if FaStateType::is_closing_tag(next_reducing_production_rule_dfa_state.type_) {
                let index_top_reducing_symbol_dfa_state =
                    &(reducing_symbols[index_of_reducing_symbols - 1].current_dfa_state);
                let top_reducing_symbol_dfa_state = self
                    .ast_dfa
                    .get_state_ref(index_top_reducing_symbol_dfa_state.clone());
                let next_dfa_state_option = top_reducing_symbol_dfa_state
                    .edges
                    .get(&(closing_production_rule.grammar));
                if let Some(index_next_dfa_state) = next_dfa_state_option {
                    // 连通的
                    let mut new_bottom_up_branch = bottom_up_branch.clone();
                    // 被归约的符号出栈，同时建立语法树孩子节点
                    let mut reducing_ast =
                        AutomataTmpAst::NonterminalAutomataTmpAst(NonterminalAutomataTmpAst {
                            grammar: closing_production_rule.grammar,
                            alias: index_of_closing_production_rule,
                            children: Vec::with_capacity(count_of_comsumed_reducing_symbol),
                        });
                    for _ in 0..count_of_comsumed_reducing_symbol {
                        let child_reducing_symbol =
                            new_bottom_up_branch.reducing_symbols.pop().unwrap();
                        reducing_ast
                            .get_children_mut()
                            .push(child_reducing_symbol.ast_of_current_dfa_state);
                    }
                    reducing_ast.get_children_mut().reverse();
                    // 归约的符号
                    let nonterminal_reducing_symbol = ReducingSymbol {
                        ast_of_current_dfa_state: reducing_ast,
                        current_dfa_state: *index_next_dfa_state,
                        end_index_of_token,
                    };
                    // 归约的符号进栈
                    new_bottom_up_branch
                        .reducing_symbols
                        .push(nonterminal_reducing_symbol);
                    self.add_new_backtracking_bottom_up_branch(new_bottom_up_branch);
                }
            }
            reducing_production_rule_dfa_state = next_reducing_production_rule_dfa_state;
        }
    }

    fn shift_bottom_up_branch(&mut self, bottom_up_branch: &BacktrackingBottomUpBranch) {
        let top_reducing_symbol = bottom_up_branch.reducing_symbols.last().unwrap();
        // 将输入流定位到分支读取的位置
        self.token_reducing_symbol_input_stream.next_read_index =
            (top_reducing_symbol.end_index_of_token + 1) as usize;
        // 移进一个token
        if self.token_reducing_symbol_input_stream.has_next() {
            let index_of_token = self.token_reducing_symbol_input_stream.read();
            let index_terminal = self
                .token_reducing_symbol_input_stream
                .get_token_terminal_index(index_of_token);
            let next_dfa_state_option = self
                .ast_dfa
                .get_state_ref(top_reducing_symbol.current_dfa_state)
                .edges
                .get(&index_terminal);
            if let Some(index_next_dfa_state) = next_dfa_state_option {
                // 连通的
                // 归约的符号
                let terminal_reducing_symbol = ReducingSymbol {
                    ast_of_current_dfa_state: AutomataTmpAst::TerminalAutomataTmpAst(
                        TerminalAutomataTmpAst {
                            grammar: index_terminal,
                            token: index_of_token,
                            children: Vec::with_capacity(0),
                        },
                    ),
                    current_dfa_state: *index_next_dfa_state,
                    end_index_of_token: (self.token_reducing_symbol_input_stream.next_read_index
                        - 1) as i32,
                };
                // 归约的符号进栈
                let mut terminal_bottom_up_branch = bottom_up_branch.clone();
                terminal_bottom_up_branch
                    .reducing_symbols
                    .push(terminal_reducing_symbol);
                self.add_new_backtracking_bottom_up_branch(terminal_bottom_up_branch);
            }
        }
    }

    fn is_accepted_bottom_up_branch(
        &mut self,
        bottom_up_branch: &BacktrackingBottomUpBranch,
    ) -> bool {
        let top_reducing_symbol = bottom_up_branch.reducing_symbols.last().unwrap();
        self.token_reducing_symbol_input_stream.next_read_index =
            (top_reducing_symbol.end_index_of_token + 1) as usize;
        return self.token_reducing_symbol_input_stream.has_read_all()
            && bottom_up_branch.reducing_symbols.len() == 2
            && self.start_grammar == top_reducing_symbol.ast_of_current_dfa_state.get_grammar();
    }

    fn automata_tmp_ast_to_ast(&self, tmp_ast: &AutomataTmpAst) -> Ast {
        let mut ast: Ast;

        match tmp_ast {
            AutomataTmpAst::TerminalAutomataTmpAst(terminal_automata_tmp_ast) => {
                //token
                let token = self
                    .token_reducing_symbol_input_stream
                    .get_token_ref(terminal_automata_tmp_ast.token);
                let token = AstToken {
                    start: token.start,
                    text: token.text.clone(),
                };
                ast = Ast::TerminalAst(TerminalAst {
                    grammar: self.ast_dfa.grammars[terminal_automata_tmp_ast.grammar]
                        .get_ast_grammar(),
                    token: token,
                    children: Default::default(),
                });
            }
            AutomataTmpAst::NonterminalAutomataTmpAst(nonterminal_automata_tmp_ast) => {
                //alias
                let production_rule =
                    &(self.ast_dfa.production_rules[nonterminal_automata_tmp_ast.alias]);
                let alias = production_rule.alias.clone();
                ast = Ast::NonterminalAst(NonterminalAst {
                    grammar: self.ast_dfa.grammars[nonterminal_automata_tmp_ast.grammar]
                        .get_ast_grammar(),
                    alias: alias,
                    children: Vec::with_capacity(nonterminal_automata_tmp_ast.children.len()),
                });
            }
        }

        //children
        let ast_children = ast.get_children_mut();
        for tmp_child in tmp_ast.get_children().iter() {
            ast_children.push(self.automata_tmp_ast_to_ast(tmp_child));
        }

        return ast;
    }

    fn init(&mut self, tokens: &Vec<Token>) {
        self.clear();
        self.token_reducing_symbol_input_stream.init(tokens);

        let connected_sign_of_start_grammar_reducing_symbol: ReducingSymbol =
            self.get_connected_sign_of_start_grammar_reducing_symbol();
        let mut beginning_bottom_up_branch: BacktrackingBottomUpBranch = Default::default();
        beginning_bottom_up_branch
            .reducing_symbols
            .push(connected_sign_of_start_grammar_reducing_symbol);

        self.add_new_backtracking_bottom_up_branch(beginning_bottom_up_branch);
    }

    fn add_new_backtracking_bottom_up_branch(
        &mut self,
        new_backtracking_bottom_up_branch: BacktrackingBottomUpBranch,
    ) {
        if self
            .tried_bottom_up_branchs
            .contains(&new_backtracking_bottom_up_branch)
        {
            return;
        }
        if self
            .bottom_up_branchs
            .insert(new_backtracking_bottom_up_branch)
        {
            if self.tried_bottom_up_branchs.is_empty() {
                return;
            }

            let min_end_index_of_task = self
                .bottom_up_branchs
                .first()
                .unwrap()
                .reducing_symbols
                .last()
                .unwrap()
                .end_index_of_token;
            let mut min_end_index_of_tried_branch = self
                .tried_bottom_up_branchs
                .first()
                .unwrap()
                .reducing_symbols
                .last()
                .unwrap()
                .end_index_of_token;
            while min_end_index_of_tried_branch < min_end_index_of_task {
                self.tried_bottom_up_branchs.pop_first();
                if self.tried_bottom_up_branchs.is_empty() {
                    break;
                }
                min_end_index_of_tried_branch = self
                    .tried_bottom_up_branchs
                    .first()
                    .unwrap()
                    .reducing_symbols
                    .last()
                    .unwrap()
                    .end_index_of_token;
            }
        }
    }

    fn get_connected_sign_of_start_grammar_reducing_symbol(&mut self) -> ReducingSymbol {
        return ReducingSymbol {
            ast_of_current_dfa_state: AutomataTmpAst::NonterminalAutomataTmpAst(
                NonterminalAutomataTmpAst {
                    grammar: self.start_grammar, // 应该是augmentedNonterminal,简化为startGrammar
                    alias: 0,
                    children: Vec::new(),
                },
            ),
            current_dfa_state: self.ast_dfa.start,
            end_index_of_token: -1,
        };
    }

    fn clear(&mut self) {
        self.result = None;
        self.token_reducing_symbol_input_stream.clear();
        self.bottom_up_branchs.clear();
        self.tried_bottom_up_branchs.clear();
    }

    fn get_ast_parse_error(&mut self) -> AstAppError {
        let token_reducing_symbols = &self
            .token_reducing_symbol_input_stream
            .token_reducing_symbols;
        if token_reducing_symbols.is_empty() {
            return AstAppError::AstParseError {
                start: 0,
                end: 0,
                error_text: "".to_string(),
            };
        }

        let index_of_last_token = (token_reducing_symbols.len() - 1) as i32;

        let mut start_index_of_token: i32 = index_of_last_token as i32;
        let mut end_index_of_token: i32 = 0;
        for branch in self.tried_bottom_up_branchs.iter() {
            let mut last_index_of_branch =
                branch.reducing_symbols.last().unwrap().end_index_of_token;
            if last_index_of_branch < 0 {
                last_index_of_branch = 0;
            }
            if start_index_of_token > last_index_of_branch {
                // 错误开始处尽量小
                start_index_of_token = last_index_of_branch;
            }
            if end_index_of_token < last_index_of_branch {
                // 错误结束处尽量大
                end_index_of_token = last_index_of_branch;
            }
        }
        if end_index_of_token + 1 <= index_of_last_token {
            // 如果还有下一个token，将他加入过来，错误信息必须涵盖可能的位置。
            end_index_of_token += 1;
        }

        let start_index_byte = token_reducing_symbols[start_index_of_token as usize].start;

        let end_token = &token_reducing_symbols[end_index_of_token as usize];
        let end_index_byte = end_token.start + end_token.text.len();

        let mut token_info: Vec<u8> = Vec::with_capacity(end_index_byte - start_index_byte + 1);
        for index_of_token in start_index_of_token as usize..=end_index_of_token as usize {
            let token = &token_reducing_symbols[index_of_token];
            token_info.append(&mut token.text.clone());
            token_info.push(' ' as u8);
        }
        token_info.pop();

        return AstAppError::AstParseError {
            start: start_index_byte,
            end: end_index_byte,
            error_text: String::from_utf8_lossy(&token_info).to_string(),
        };
    }
}

impl Default for SuperBacktrackingBottomUpAstAutomata {
    fn default() -> Self {
        Self {
            sub_ast_automata: Default::default(),
            ast_dfa: Default::default(),
            start_grammar: 0,
            token_reducing_symbol_input_stream: Default::default(),
            result: Default::default(),
            bottom_up_branchs: Default::default(),
            tried_bottom_up_branchs: Default::default(),
        }
    }
}
