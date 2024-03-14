use titan_ast_runtime_lib::{self, ast_application::RuntimeAutomataAstApplication};

fn main() {
    build_c_ast();
}

fn build_c_ast() {
    let mut app = RuntimeAutomataAstApplication::default();

    let automata_file_path = String::from("D:/github-pro/titan/titan-ast/test/c/automata.data");
    let set_context_result = app.set_context(&automata_file_path);
    if set_context_result.is_err() {
        println!("{}", set_context_result.err().unwrap());
        return;
    }

    let source_code_file_path = String::from("D://github-pro/titan/titan-ast/test/c/helloworld.c");

    let build_ast_result = app.build_ast(&source_code_file_path);

    match build_ast_result {
        Ok(ast) => println!("{:#?}", ast),
        Err(err) => println!("{err}"),
    }
}
