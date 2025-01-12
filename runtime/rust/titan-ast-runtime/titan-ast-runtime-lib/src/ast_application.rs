use crate::{
    ast::{Ast, AstGrammar},
    ast_automata::AstBuilder,
    ast_automata_builder,
    ast_generator_result_to_rich_result_converter::AstGeneratorResultToRichResultConverter,
    automata_data::AutomataData,
    error::AstAppError,
    persistent_object::PersistentObject,
    result::{AstGeneratorResult, RichAstGeneratorResult},
    super_backtracking_bottom_up_ast_automata::SuperBacktrackingBottomUpAstAutomata,
    super_dfa_token_automata::SuperDfaTokenAutomata,
    token_automata::TokenBuilder,
    token_automata_builder,
};

#[derive(Clone)]
pub struct RuntimeAutomataRichAstApplication {
    runtime_automata_ast_app: RuntimeAutomataAstApplication,
    rich_result_converter: AstGeneratorResultToRichResultConverter,
}

impl RuntimeAutomataRichAstApplication {
    pub fn set_newline(&mut self, newline: u8) {
        self.rich_result_converter.newline = newline;
    }

    pub fn set_context(&mut self, automata_file_path: &String) -> Result<(), AstAppError> {
        return self
            .runtime_automata_ast_app
            .set_context(automata_file_path);
    }

    pub fn build_rich_ast(&mut self, source_code_file_path: &String) -> RichAstGeneratorResult {
        let ast_generator_result = self
            .runtime_automata_ast_app
            .build_ast(source_code_file_path);
        return self.rich_result_converter.convert(ast_generator_result);
    }

    pub fn get_grammars(&self) -> Vec<AstGrammar> {
        return self.runtime_automata_ast_app.get_grammars();
    }
}

impl Default for RuntimeAutomataRichAstApplication {
    fn default() -> Self {
        Self {
            runtime_automata_ast_app: Default::default(),
            rich_result_converter: Default::default(),
        }
    }
}

#[derive(Clone)]
pub struct RuntimeAutomataAstApplication {
    //matadata
    pub(crate) grammars: Vec<AstGrammar>,
    //dfa
    pub(crate) token_automata: SuperDfaTokenAutomata,
    pub(crate) ast_automata: SuperBacktrackingBottomUpAstAutomata,
}

impl RuntimeAutomataAstApplication {
    pub fn build_ast(&mut self, source_code_file_path: &String) -> AstGeneratorResult {
        let tokens_result = self.token_automata.build_token(source_code_file_path);
        let mut ast_result = Err(AstAppError::TokensError {});
        if let Ok(tokens) = &tokens_result {
            ast_result = self.ast_automata.build_ast(tokens);
        }
        return AstGeneratorResult {
            tokens_result: tokens_result,
            ast_result: ast_result,
        };
    }

    pub fn set_context(&mut self, automata_file_path: &String) -> Result<(), AstAppError> {
        let mut persistent_object: PersistentObject = Default::default();

        persistent_object.persistent_data.init(automata_file_path)?;
        persistent_object.init();

        let automata_data: AutomataData = persistent_object.into();

        self.token_automata = token_automata_builder::build(&automata_data);
        self.ast_automata = ast_automata_builder::build(&automata_data);

        self.set_meta_data(automata_data);

        Ok(())
    }

    pub fn get_grammars(&self) -> Vec<AstGrammar> {
        self.grammars.clone()
    }

    fn set_meta_data(&mut self, automata_data: AutomataData) {
        let mut grammars: Vec<AstGrammar> = Vec::with_capacity(automata_data.grammars.len());
        for original_grammar in automata_data.grammars.iter() {
            grammars.push(original_grammar.get_ast_grammar());
        }
        self.grammars = grammars;
    }
}

impl Default for RuntimeAutomataAstApplication {
    fn default() -> Self {
        Self {
            token_automata: Default::default(),
            ast_automata: Default::default(),
            grammars: Default::default(),
        }
    }
}
