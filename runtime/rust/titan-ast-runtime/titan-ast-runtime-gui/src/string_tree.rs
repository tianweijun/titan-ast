pub struct StringTree {
    pub text: String,
    pub children: Vec<StringTree>,
}

impl StringTree {
    pub fn get_count_height(&self) -> (usize, usize) {
        let mut count: usize = 0;
        let mut height: usize = 1;
        StringTree::set_count_height(self, &mut count, &mut height, 1);
        return (count, height);
    }

    pub fn set_count_height(
        tree_node: &StringTree,
        count: &mut usize,
        height: &mut usize,
        current_height: usize,
    ) {
        if current_height > *height {
            *height = current_height;
        }
        *count = *count + 1;

        let child_current_height = current_height + 1;
        for child in &tree_node.children {
            StringTree::set_count_height(child, count, height, child_current_height);
        }
    }
}

impl Default for StringTree {
    fn default() -> Self {
        return Self {
            text: Default::default(),
            children: Default::default(),
        };
    }
}
