#[derive(Clone)]
pub(crate) enum FaStateType {
    NONE = 0,
    NORMAL = 1,
    OpeningTag = 2,
    ClosingTag = 4,
}

impl FaStateType {
    pub(crate) fn is_closing_tag(state_type: i32) -> bool {
        return (state_type & FaStateType::ClosingTag as i32) != 0;
    }
}
