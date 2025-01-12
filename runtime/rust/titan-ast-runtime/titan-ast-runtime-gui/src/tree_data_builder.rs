use std::collections::HashSet;

use crate::string_tree::StringTree;

static FONT_SIZE: i32 = 16;

pub(crate) struct LineTreeData {
    pub(crate) x: i32,
    pub(crate) y: i32,
    pub(crate) width: i32,
    pub(crate) height: i32,
    pub(crate) from_x: i32,
    pub(crate) from_y: i32,
    pub(crate) to_x: i32,
    pub(crate) to_y: i32,
}

pub(crate) struct TextTreeData {
    pub(crate) x: i32,
    pub(crate) y: i32,
    pub(crate) width: i32,
    pub(crate) height: i32,
    pub(crate) text: String,
}

pub(crate) struct TreeData {
    pub(crate) width: i32,
    pub(crate) height: i32,
    pub(crate) font_size: i32,
    pub(crate) lines: Vec<LineTreeData>,
    pub(crate) texts: Vec<TextTreeData>,
}

pub(crate) struct Box {
    pub(crate) index: usize,
    pub(crate) index_of_row: usize,
    pub(crate) horizontal_axis: i32,
    pub(crate) vertical_axis: i32,
    pub(crate) width: i32,
    pub(crate) height: i32,
    pub(crate) text: String,
    pub(crate) children: Vec<usize>,
}

pub(crate) struct HierarchicalRow {
    pub(crate) end_of_row: i32,
    pub(crate) height: i32,
    pub(crate) boxs: Vec<usize>,
}
pub(crate) struct BoxTreeContext {
    pub(crate) boxs: Vec<Box>,
    pub(crate) rows: Vec<HierarchicalRow>,
    pub(crate) font_size: i32,
    pub(crate) font_width: i32,
    pub(crate) font_height: i32,
    pub(crate) col_line_height: i32,
    pub(crate) row_text_gap: i32,
    pub(crate) padding: i32,
    pub(crate) width: i32,
    pub(crate) height: i32,
    pub(crate) box_tree: usize,
}

pub(crate) struct TreeDataBuilder {
    pub(crate) string_tree: StringTree,
}

impl TreeDataBuilder {
    pub(crate) fn build(&self, scale: f32) -> TreeData {
        let (count, height) = self.string_tree.get_count_height();
        let font_size = (FONT_SIZE as f32 * scale) as i32;
        let mut box_tree_context = BoxTreeContext {
            boxs: Vec::with_capacity(count),
            rows: Vec::with_capacity(height),
            font_size: font_size,
            font_width: font_size,
            font_height: font_size,
            col_line_height: font_size * 2,
            row_text_gap: font_size,
            padding: font_size,
            width: 0,
            height: 0,
            box_tree: 0,
        };
        box_tree_context.build(&self.string_tree, count, height);
        return box_tree_context.box_tree_context_to_tree_data();
    }
}

impl BoxTreeContext {
    fn build(&mut self, string_tree: &StringTree, count: usize, height: usize) {
        // set row
        self.set_rows(height);
        // create box
        self.create_box(string_tree, 0);
        // init location of box
        self.init_location_of_box();
        //align center
        self.align_center(self.box_tree);
        //set width and height
        self.set_width_height();
    }

    fn align_center(&mut self, index_of_box: usize) {
        let boz = self.boxs.get(index_of_box).unwrap();
        if boz.children.is_empty() {
            return;
        }
        let mid_of_this = boz.horizontal_axis + boz.width / 2;
        let box_children = boz.children.clone();
        for index_of_child_box in &box_children {
            self.align_center(*index_of_child_box);
        }
        //do align center
        let index_of_first_child_box = box_children.get(0).unwrap();
        let start_of_child_row = self
            .boxs
            .get(*index_of_first_child_box)
            .unwrap()
            .horizontal_axis;
        let index_of_last_child_box = box_children.get(box_children.len() - 1).unwrap();
        let last_child_box = self.boxs.get(*index_of_last_child_box).unwrap();
        let end_of_child_row = last_child_box.horizontal_axis + last_child_box.width;
        let mid_of_child_row = start_of_child_row + (end_of_child_row - start_of_child_row) / 2;

        if mid_of_this < mid_of_child_row {
            // 元素偏左，其之后的当前行box右移
            let this_move_right = mid_of_child_row - mid_of_this;
            self.move_right_box_in_row(index_of_box, this_move_right);
        }
        if mid_of_this > mid_of_child_row {
            // 元素偏右,孩子右移
            let child_move_right = mid_of_this - mid_of_child_row;
            let mut has_moved_rows: HashSet<usize> = Default::default();
            for index_of_child_box in &box_children {
                self.move_right_once_for_each_row(
                    &mut has_moved_rows,
                    *index_of_child_box,
                    child_move_right,
                );
            }
        }
    }

    fn move_right_once_for_each_row(
        &mut self,
        has_moved_rows: &mut HashSet<usize>,
        index_of_box: usize,
        move_right: i32,
    ) {
        let boz = self.boxs.get(index_of_box).unwrap();
        let box_children = boz.children.clone();
        let index_of_row: usize = boz.index_of_row;
        if !has_moved_rows.contains(&index_of_row) {
            self.move_right_box_in_row(index_of_box, move_right);
            has_moved_rows.insert(index_of_row);
        }
        for index_of_child in box_children {
            self.move_right_once_for_each_row(has_moved_rows, index_of_child, move_right);
        }
    }

    fn move_right_box_in_row(&mut self, index_of_box: usize, move_right: i32) {
        let index_of_row = self.boxs.get(index_of_box).unwrap().index_of_row;
        let hierarchical_row = self.rows.get_mut(index_of_row).unwrap();
        let mut start_offset_of_box_in_row: usize = 0;
        let mut offset = 0;
        for index_of_box_in_row in &hierarchical_row.boxs {
            if *index_of_box_in_row == index_of_box {
                start_offset_of_box_in_row = offset;
                break;
            }
            offset = offset + 1;
        }
        for offset_of_box_in_row in start_offset_of_box_in_row..hierarchical_row.boxs.len() {
            let index_of_box_in_row = hierarchical_row.boxs.get(offset_of_box_in_row).unwrap();
            let box_in_row = self.boxs.get_mut(*index_of_box_in_row).unwrap();
            box_in_row.horizontal_axis = box_in_row.horizontal_axis + move_right;
        }
        hierarchical_row.end_of_row = hierarchical_row.end_of_row + move_right;
    }

    fn init_location_of_box(&mut self) {
        for index_of_row in 0..self.rows.len() {
            let row = &self.rows.get(index_of_row).unwrap().boxs;
            let mut end_of_row = self.padding;
            let vertical_axis_of_row = self.padding
                + index_of_row as i32 * (self.font_height + self.col_line_height)
                + self.font_height;
            for index_of_box in row {
                let boz = self.boxs.get_mut(*index_of_box).unwrap();
                boz.horizontal_axis = end_of_row;
                boz.vertical_axis = vertical_axis_of_row;
                end_of_row = end_of_row + boz.width + self.row_text_gap;
            }
            let hierarchical_row = self.rows.get_mut(index_of_row).unwrap();
            hierarchical_row.end_of_row = end_of_row;
            hierarchical_row.height = vertical_axis_of_row;
        }
    }

    fn create_box(&mut self, string_tree: &StringTree, index_of_row: usize) -> usize {
        //create box
        let index_of_box = self.boxs.len();
        let boz = Box {
            index: index_of_box,
            index_of_row: index_of_row,
            horizontal_axis: 0,
            vertical_axis: 0,
            width: self.font_width * string_tree.text.len() as i32,
            height: self.font_height,
            text: string_tree.text.clone(),
            children: Vec::with_capacity(string_tree.children.len()),
        };
        self.boxs.push(boz);
        self.rows
            .get_mut(index_of_row)
            .unwrap()
            .boxs
            .push(index_of_box);

        // create next row boxs
        let index_of_next_row = index_of_row + 1;
        for child in &string_tree.children {
            let index_of_child_box = self.create_box(child, index_of_next_row);
            self.boxs
                .get_mut(index_of_box)
                .unwrap()
                .children
                .push(index_of_child_box);
        }
        return index_of_box;
    }

    fn set_width_height(&mut self) {
        for row in &self.rows {
            let current_width = row.end_of_row + self.padding;
            if current_width > self.width {
                self.width = current_width;
            }
            let current_height = row.height + self.padding;
            if current_height > self.height {
                self.height = current_height;
            }
        }
    }

    fn set_rows(&mut self, height: usize) {
        for index in 0..height {
            let hierarchical_row = HierarchicalRow {
                end_of_row: 0,
                height: 0,
                boxs: Vec::with_capacity(0),
            };
            self.rows.push(hierarchical_row);
        }
    }

    pub(crate) fn box_tree_context_to_tree_data(&self) -> TreeData {
        let mut texts: Vec<TextTreeData> = Vec::with_capacity(self.boxs.len());
        for boz in &self.boxs {
            texts.push(TextTreeData {
                x: boz.horizontal_axis,
                y: boz.vertical_axis - boz.height,
                width: boz.width,
                height: boz.height,
                text: boz.text.clone(),
            });
        }

        let mut lines: Vec<LineTreeData> = Vec::with_capacity(self.boxs.len());
        let font_size = self.font_size;
        for index_of_box in 0..self.boxs.len() {
            let parent_box = self.boxs.get(index_of_box).unwrap();
            let from_x =
                parent_box.horizontal_axis + font_size * (parent_box.text.len() / 3) as i32;
            let from_y = parent_box.vertical_axis - parent_box.height + font_size;
            for index_of_child_box in &parent_box.children {
                let child_box = self.boxs.get(*index_of_child_box).unwrap();
                let to_x =
                    child_box.horizontal_axis + font_size * (child_box.text.len() / 3) as i32;
                let to_y = child_box.vertical_axis - child_box.height;

                let mut left_x = from_x;
                let mut left_y = from_y;
                let mut right_x = to_x;
                let mut right_y = to_y;
                if left_x > right_x {
                    left_x = to_x;
                    left_y = to_y;
                    right_x = from_x;
                    right_y = from_y;
                }
                let mut min_y = left_y;
                let mut max_y = right_y;
                if min_y > max_y {
                    min_y = right_y;
                    max_y = left_y;
                }
                lines.push(LineTreeData {
                    x: left_x,
                    y: min_y,
                    width: right_x - left_x,
                    height: max_y - min_y,
                    from_x: left_x,
                    from_y: left_y,
                    to_x: right_x,
                    to_y: right_y,
                });
            }
        }

        return TreeData {
            width: self.width,
            height: self.height,
            font_size: self.font_size,
            lines: lines,
            texts: texts,
        };
    }
}

impl Default for TreeData {
    fn default() -> Self {
        return Self {
            width: 0,
            height: 0,
            font_size: FONT_SIZE,
            lines: vec![],
            texts: vec![],
        };
    }
}

impl Default for TreeDataBuilder {
    fn default() -> Self {
        return Self {
            string_tree: Default::default(),
        };
    }
}
