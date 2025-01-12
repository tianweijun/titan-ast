use titan_ast_runtime_lib::ast_application::RuntimeAutomataAstApplication;

#[test]
fn load_c_persistent_data() {
    let mut app = RuntimeAutomataAstApplication::default();

    let automata_file_path = String::from("D:/github-pro/titan/titan-ast/test/c/automata.data");
    let set_context_result = app.set_context(&automata_file_path);
    if set_context_result.is_err() {
        println!("{}", set_context_result.err().unwrap());
        return;
    }
}
