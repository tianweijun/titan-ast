use crate::{
    ast::{Ast, Token},
    error::AstAppError,
};

pub struct AstGeneratorResult {
    pub tokens_result: Result<Vec<Token>, AstAppError>,
    pub ast_result: Result<Ast, AstAppError>,
}

pub struct LineNumberRange {
    pub start: usize,
    pub end: usize,
}

pub struct LineNumberRangeDto {
    pub line_number: usize,
    pub start: usize,
    pub end: usize,
}

pub struct LineNumberDetail {
    pub line_number_ranges: Vec<LineNumberRange>,
}

pub struct RichAstGeneratorResult {
    pub tokens_result: Result<Vec<Token>, AstAppError>,
    pub ast_result: Result<Ast, AstAppError>,
}

impl AstGeneratorResult {
    pub fn is_ok(&self) -> bool {
        return self.tokens_result.is_ok() && self.ast_result.is_ok();
    }

    pub fn get_ok_ast(&self) -> &Ast {
        return self.ast_result.as_ref().unwrap();
    }

    pub fn get_error_msg(&self) -> String {
        match &self.tokens_result {
            Ok(_) => {}
            Err(err) => {
                return err.to_string();
            }
        };
        match &self.ast_result {
            Ok(_) => {}
            Err(err) => {
                return err.to_string();
            }
        };
        return "".to_string();
    }
}

impl RichAstGeneratorResult {
    pub fn is_ok(&self) -> bool {
        return self.tokens_result.is_ok() && self.ast_result.is_ok();
    }

    pub fn get_ok_ast(&self) -> &Ast {
        return self.ast_result.as_ref().unwrap();
    }

    pub fn get_error_msg(&self) -> String {
        match &self.tokens_result {
            Ok(_) => {}
            Err(err) => {
                return err.to_string();
            }
        };
        match &self.ast_result {
            Ok(_) => {}
            Err(err) => {
                return err.to_string();
            }
        };
        return "".to_string();
    }
}

impl LineNumberDetail {
    pub fn get_line_number_range_dto(&self, byte_position: usize) -> Option<LineNumberRangeDto> {
        let mut left: usize = 0;
        let mut right: usize = self.line_number_ranges.len() - 1;
        while left <= right {
            // 计算中间元素的索引
            let mid = left + (right - left) / 2;
            let mid_line_number_range = self.line_number_ranges.get(mid).unwrap();
            if byte_position >= mid_line_number_range.start
                && byte_position < mid_line_number_range.end
            {
                // 找到目标值，返回行号lineNumber=index+1
                return Some(LineNumberRangeDto {
                    line_number: mid + 1,
                    start: mid_line_number_range.start,
                    end: mid_line_number_range.end,
                });
            } else if byte_position < mid_line_number_range.start {
                right = mid - 1; // 目标值在左半部分
            } else {
                left = mid + 1; // 目标值在右半部分
            }
        }
        return None;
    }
}

impl Default for LineNumberDetail {
    fn default() -> Self {
        Self {
            line_number_ranges: Default::default(),
        }
    }
}
