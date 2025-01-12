slint::include_modules!();

use std::{
    rc::Rc,
    sync::{Arc, Mutex},
};

use slint::{ModelRc, SharedString, VecModel};
use titan_ast_runtime_lib::ast::Ast;

use crate::{string_tree::StringTree, tree_data_builder::TreeDataBuilder};

pub struct AstGuiOutputer {}

impl AstGuiOutputer {
    pub fn show(string_tree: StringTree) -> Result<(), slint::PlatformError> {
        // new view
        //std::env::set_var("SLINT_FULLSCREEN", "1");
        let app = AstWindow::new().unwrap();
        // send data to view
        let syn_tree_data_lock = Arc::new(Mutex::new(0));
        let tree_data_builder: Arc<TreeDataBuilder> = Arc::new(TreeDataBuilder {
            string_tree: string_tree,
        });
        let syn_tree_data_lock_handler = Arc::clone(&syn_tree_data_lock);
        let tree_data_builder_handler = Arc::clone(&tree_data_builder);
        let app_handler = app.as_weak();

        app.global::<GlobalTreeData>()
            .on_get_and_render_tree_data(move |scale| {
                // syn tree_data
                let try_lock_result = syn_tree_data_lock_handler.try_lock();
                if !try_lock_result.is_ok() {
                    return;
                }
                //send tree_data
                let tree_data = tree_data_builder_handler.build(scale);
                let tmp_app = app_handler.upgrade().unwrap();
                //width-height
                tmp_app
                    .global::<GlobalTreeData>()
                    .set_width(tree_data.width);
                tmp_app
                    .global::<GlobalTreeData>()
                    .set_height(tree_data.height);
                //texts
                tmp_app
                    .global::<GlobalTreeData>()
                    .set_text_font_size(tree_data.font_size);
                let texts = &tree_data.texts;
                let mut text_x_array: Vec<i32> = Vec::with_capacity(texts.len());
                let mut text_y_array: Vec<i32> = Vec::with_capacity(texts.len());
                let mut text_width_array: Vec<i32> = Vec::with_capacity(texts.len());
                let mut text_height_array: Vec<i32> = Vec::with_capacity(texts.len());
                let mut text_text_array: Vec<SharedString> = Vec::with_capacity(texts.len());
                for text in texts {
                    text_x_array.push(text.x);
                    text_y_array.push(text.y);
                    text_width_array.push(text.width);
                    text_height_array.push(text.height);
                    text_text_array.push(text.text.clone().into());
                }
                /*
                let text_x_array_model: Rc<VecModel<i32>> = Rc::new(VecModel::from(text_x_array));
                let text_x_array_model_rc = ModelRc::from(text_x_array_model.clone());
                tmp_app
                    .global::<GlobalTreeData>()
                    .set_text_x_array(text_x_array_model_rc);*/
                tmp_app
                    .global::<GlobalTreeData>()
                    .set_text_x_array(ModelRc::from(Rc::new(VecModel::<i32>::from(text_x_array))));
                tmp_app
                    .global::<GlobalTreeData>()
                    .set_text_y_array(ModelRc::from(Rc::new(VecModel::<i32>::from(text_y_array))));
                tmp_app
                    .global::<GlobalTreeData>()
                    .set_text_width_array(ModelRc::from(Rc::new(VecModel::<i32>::from(
                        text_width_array,
                    ))));
                tmp_app
                    .global::<GlobalTreeData>()
                    .set_text_height_array(ModelRc::from(Rc::new(VecModel::<i32>::from(
                        text_height_array,
                    ))));
                tmp_app
                    .global::<GlobalTreeData>()
                    .set_text_text_array(ModelRc::from(Rc::new(VecModel::<SharedString>::from(
                        text_text_array,
                    ))));
                //lines
                let lines = &tree_data.lines;
                let mut line_x_array: Vec<i32> = Vec::with_capacity(lines.len());
                let mut line_y_array: Vec<i32> = Vec::with_capacity(lines.len());
                let mut line_width_array: Vec<i32> = Vec::with_capacity(lines.len());
                let mut line_height_array: Vec<i32> = Vec::with_capacity(lines.len());
                let mut line_from_x_array: Vec<i32> = Vec::with_capacity(lines.len());
                let mut line_from_y_array: Vec<i32> = Vec::with_capacity(lines.len());
                let mut line_to_x_array: Vec<i32> = Vec::with_capacity(lines.len());
                let mut line_to_y_array: Vec<i32> = Vec::with_capacity(lines.len());
                for line in lines {
                    line_x_array.push(line.x);
                    line_y_array.push(line.y);
                    line_width_array.push(line.width);
                    line_height_array.push(line.height);
                    line_from_x_array.push(line.from_x);
                    line_from_y_array.push(line.from_y);
                    line_to_x_array.push(line.to_x);
                    line_to_y_array.push(line.to_y);
                }
                tmp_app
                    .global::<GlobalTreeData>()
                    .set_line_x_array(ModelRc::from(Rc::new(VecModel::<i32>::from(line_x_array))));
                tmp_app
                    .global::<GlobalTreeData>()
                    .set_line_y_array(ModelRc::from(Rc::new(VecModel::<i32>::from(line_y_array))));
                tmp_app
                    .global::<GlobalTreeData>()
                    .set_line_width_array(ModelRc::from(Rc::new(VecModel::<i32>::from(
                        line_width_array,
                    ))));
                tmp_app
                    .global::<GlobalTreeData>()
                    .set_line_height_array(ModelRc::from(Rc::new(VecModel::<i32>::from(
                        line_height_array,
                    ))));
                tmp_app
                    .global::<GlobalTreeData>()
                    .set_line_from_x_array(ModelRc::from(Rc::new(VecModel::<i32>::from(
                        line_from_x_array,
                    ))));
                tmp_app
                    .global::<GlobalTreeData>()
                    .set_line_from_y_array(ModelRc::from(Rc::new(VecModel::<i32>::from(
                        line_from_y_array,
                    ))));
                tmp_app
                    .global::<GlobalTreeData>()
                    .set_line_to_x_array(ModelRc::from(Rc::new(VecModel::<i32>::from(
                        line_to_x_array,
                    ))));
                tmp_app
                    .global::<GlobalTreeData>()
                    .set_line_to_y_array(ModelRc::from(Rc::new(VecModel::<i32>::from(
                        line_to_y_array,
                    ))));
                //render tree_data by slint
                tmp_app.global::<GlobalTreeData>().invoke_render_tree_data();
            });
        // init
        app.global::<GlobalTreeData>()
            .invoke_update_tree_data(1.0f32);
        // run loop until exited
        return app.run();
    }

    pub fn show_ast_by_utf8_string_tree(ast: &Ast) -> Result<(), slint::PlatformError> {
        let string_tree: StringTree = AstGuiOutputer::build_utf8_string_tree_by_ast(ast);
        return AstGuiOutputer::show(string_tree);
    }

    pub fn build_utf8_string_tree_by_ast(ast: &Ast) -> StringTree {
        let mut string_tree = StringTree {
            text: ast.to_utf8_string(),
            children: Vec::with_capacity(ast.get_children().len()),
        };

        for child_ast in ast.get_children() {
            string_tree
                .children
                .push(AstGuiOutputer::build_utf8_string_tree_by_ast(child_ast));
        }
        return string_tree;
    }
}

impl Default for AstGuiOutputer {
    fn default() -> Self {
        return Self {};
    }
}
