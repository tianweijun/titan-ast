use std::fmt;

pub(crate) type Byte = u8;

#[derive(Clone)]
pub(crate) struct ByteBuffer {
    pub(crate) is_big_endian: bool,
    pub(crate) buffer: Vec<Byte>,
    pub(crate) position: usize,
}

impl ByteBuffer {
    pub(crate) fn append(&mut self, b: Byte) {
        if self.position < self.buffer.len() {
            self.buffer[self.position] = b;
        } else {
            self.buffer.push(b);
        }
        self.position += 1;
    }

    pub(crate) fn len(&self) -> usize {
        return self.position;
    }

    pub(crate) fn clear(&mut self) {
        self.position = 0;
    }

    pub(crate) fn set_position(&mut self, position: usize) {
        self.position = position;
    }

    pub(crate) fn get_i32(&self) -> i32 {
        match self.is_big_endian {
            true => self.get_i32_b(),
            false => self.get_i32_l(),
        }
    }

    pub(crate) fn get_i32_b(&self) -> i32 {
        let mut base: i32 = 0;
        let mut value: i32 = 0;
        for index_of_buffer in (0..self.position).rev().step_by(1) {
            let tmp = self.buffer[index_of_buffer] as i32;
            value = value | tmp << base;
            base += 8;
        }
        return value;
    }

    pub(crate) fn get_i32_l(&self) -> i32 {
        let mut base: i32 = 0;
        let mut value: i32 = 0;
        for index_of_buffer in 0..self.position {
            let tmp = self.buffer[index_of_buffer] as i32;
            value = value | tmp << base;
            base += 8;
        }
        return value;
    }
}

impl fmt::Display for ByteBuffer {
    fn fmt(&self, f: &mut fmt::Formatter<'_>) -> fmt::Result {
        let string_info_cow = String::from_utf8_lossy(&self.buffer[0..self.position]);
        let string_info: String = string_info_cow.to_string();
        write!(f, "{}", string_info)
    }
}

impl Default for ByteBuffer {
    fn default() -> Self {
        return ByteBuffer {
            is_big_endian: true,
            buffer: Vec::with_capacity(0),
            position: 0,
        };
    }
}
