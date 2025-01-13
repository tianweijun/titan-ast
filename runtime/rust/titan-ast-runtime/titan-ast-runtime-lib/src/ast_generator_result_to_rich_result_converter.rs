use crate::{
    ast::{Ast, Token},
    error::AstAppError,
    result::{AstGeneratorResult, LineNumberDetail, LineNumberRange, RichAstGeneratorResult},
};

#[derive(Clone)]
pub(crate) struct AstGeneratorResultToRichResultConverter {
    pub(crate) newline: u8,
}

impl AstGeneratorResultToRichResultConverter {
    pub fn convert(&self, ast_generator_result: &mut AstGeneratorResult) -> RichAstGeneratorResult {
        let rich_ast_result = self.convert_to_rich_ast_result(&ast_generator_result);
        let tokens_result = self.convert_to_rich_tokens_result(&mut ast_generator_result.tokens_result);
        return RichAstGeneratorResult {
            tokens_result: tokens_result,
            ast_result: rich_ast_result,
        };
    }

    fn convert_to_rich_ast_result(
        &self,
        ast_generator_result: &AstGeneratorResult,
    ) -> Result<Ast, AstAppError> {
        match &ast_generator_result.ast_result {
            Ok(ast) => return Ok(ast.clone()),
            Err(err) => {
                if let AstAppError::AstParseError {
                    start,
                    end,
                    error_text,
                } = err
                {
                    return Err(self.ast_parse_error_to_rich_ast_parse_error(
                        *start,
                        *end,
                        error_text,
                        ast_generator_result.tokens_result.as_ref().unwrap(),
                    ));
                } else {
                    return Err(err.clone());
                }
            }
        }
    }

    fn ast_parse_error_to_rich_ast_parse_error(
        &self,
        start: usize,
        end: usize,
        error_text: &Vec<u8>,
        tokens: &Vec<Token>,
    ) -> AstAppError {
        let line_number_detail = self.build_line_number_detail(tokens);
        let start_line_number_range = line_number_detail.get_line_number_range_dto(start).unwrap();
        let end_line_number_range = line_number_detail
            .get_line_number_range_dto(end - 1)
            .unwrap();
        return AstAppError::RichAstParseError {
            start: start,
            end: end,
            start_line_number: start_line_number_range.line_number,
            start_offset_in_line: start - start_line_number_range.start + 1, // 用户角度下标从1开始
            end_line_number: end_line_number_range.line_number,
            end_offset_in_line: end - end_line_number_range.start + 1, // 用户角度下标从1开始
            error_text: error_text.clone(),
        };
    }
    fn token_parse_error_to_rich_token_parse_error(
        &self,
        finished_tokens: &mut Vec<Token>,
        start: usize,
        end: usize,
        error_text: &Vec<u8>,
    ) -> AstAppError {
        let errorToken = Token {
            start,
            text: error_text.clone(),
            terminal: Default::default(),
            type_: crate::ast::TokenType::Text,
        };
        finished_tokens.push(errorToken);
        let line_number_detail = self.build_line_number_detail(finished_tokens);
        let start_line_number_range = line_number_detail.get_line_number_range_dto(start).unwrap();
        let end_line_number_range = line_number_detail
            .get_line_number_range_dto(end - 1)
            .unwrap();

        let err =  AstAppError::RichTokenParseError {
            finished_tokens: finished_tokens.clone(),
            start: start,
            end: end,
            start_line_number: start_line_number_range.line_number,
            start_offset_in_line: start - start_line_number_range.start + 1, // 用户角度下标从1开始
            end_line_number: end_line_number_range.line_number,
            end_offset_in_line: end - end_line_number_range.start + 1, // 用户角度下标从1开始
            error_text: error_text.clone(),
        };
        finished_tokens.pop();
        return err;
    }

    fn build_line_number_detail(&self, tokens: &Vec<Token>) -> LineNumberDetail {
        let mut line_number_detail: LineNumberDetail = Default::default();
        let mut next_start: usize = 0;
        let mut index_of_bytes: i32 = -1;
        for token in tokens {
            for ch in &token.text {
                index_of_bytes = index_of_bytes + 1;
                if *ch == self.newline {
                    let next_end = index_of_bytes as usize + 1;
                    line_number_detail.line_number_ranges.push(LineNumberRange {
                        start: next_start,
                        end: next_end,
                    });
                    // 更新下一行
                    next_start = next_end;
                }
            }
        }
        // 最后一行可以没有换行符，设置这个特殊行
        if index_of_bytes >= next_start as i32 {
            let next_end = index_of_bytes as usize + 1;
            line_number_detail.line_number_ranges.push(LineNumberRange {
                start: next_start,
                end: next_end,
            });
            // 更新下一行
            next_start = next_end;
        }
        return line_number_detail;
    }

    fn convert_to_rich_tokens_result(
        &self,
        tokens_result: &mut Result<Vec<Token>, AstAppError>,
    ) -> Result<Vec<Token>, AstAppError> {
        match tokens_result {
            Ok(tokens) => {
                return Ok(tokens.clone());
            }
            Err(err) => {
                if let AstAppError::TokenParseError {
                    finished_tokens,
                    start,
                    end,
                    error_text,
                } = err
                {
                    return Err(self.token_parse_error_to_rich_token_parse_error(
                        finished_tokens,
                        *start,
                        *end,
                        error_text,
                    ));
                } else {
                    return Err(err.clone());
                }
            }
        };
    }
}

impl Default for AstGeneratorResultToRichResultConverter {
    fn default() -> Self {
        Self {
            newline: '\n' as u32 as u8,
        }
    }
}
