use std::{fs::File, io::Read};

use crate::{byte_buffer::Byte, error::AstAppError};

pub(crate) const EOF: i32 = -1;
const STANDARD_BUFFER_CAPACITY: usize = 512;

#[derive(Debug)]
pub(crate) struct ByteBufferedInputStream {
    pub(crate) next_read_index: usize,
    next_pos: usize,
    limit: usize,
    limit_of_invalid_data: usize,
    mark: i32,
    mark_next_read_index: usize,

    is_read_all_from_file: bool,

    buffer: Vec<Byte>,
    byte_input_stream: Option<File>,
}

impl ByteBufferedInputStream {
    pub(crate) fn read(&mut self) -> i32 {
        // 从缓冲中正常读取
        if self.next_pos < self.limit {
            let tmp_readed_byte = self.buffer[self.next_pos] as i32;
            let readed_byte = tmp_readed_byte & 0xFF as i32;

            self.next_pos += 1;
            self.next_read_index += 1;

            return readed_byte;
        }

        // nextPos >= limit
        if !self.is_read_all_from_file {
            // 文件还没有读完就先填充，在尝试读取
            self.fill_buffer();
            return self.read();
        }

        return EOF;
    }

    fn fill_buffer(&mut self) {
        if self.limit < self.buffer.len() {
            self.fill_remainder();
        } else {
            if self.limit_of_invalid_data > 0 {
                self.compact();
                self.fill_remainder();
            } else {
                self.fill_by_expansion();
            }
        }
    }

    fn compact(&mut self) {
        let move_count = self.limit - self.limit_of_invalid_data;
        for i in 0..move_count {
            self.buffer[i] = self.buffer[i + self.limit_of_invalid_data];
        }
        self.next_pos -= self.limit_of_invalid_data;
        self.mark -= self.limit_of_invalid_data as i32;
        self.limit = move_count;
        self.limit_of_invalid_data = 0;
    }

    fn fill_remainder(&mut self) {
        let count_of_readed = self.do_buffer_read(self.limit, self.buffer.len() - self.limit);
        self.limit += count_of_readed;
    }

    fn fill_by_expansion(&mut self) {
        let nsz = self.buffer.len() + self.buffer.len();
        let mut new_buffer = vec![0; nsz];
        for index_of_buffer in 0..self.limit {
            new_buffer[index_of_buffer] = self.buffer[index_of_buffer];
        }
        self.buffer = new_buffer;
        // extend read
        let count_of_readed = self.do_buffer_read(self.limit, nsz - self.limit);

        self.limit = self.limit + count_of_readed;
    }

    pub(crate) fn reset(&mut self) {
        if self.mark < 0 {
            return;
        }
        self.next_pos = (self.mark + 1) as usize;
        self.next_read_index = self.mark_next_read_index;
        if self.next_pos >= self.limit {
            // 数据全失效了
            self.next_pos = 0;
            self.limit = 0;
            self.limit_of_invalid_data = 0;
        } else {
            // 还有可用数据
            self.limit_of_invalid_data = self.next_pos;
        }
        self.mark = EOF;
    }

    pub(crate) fn mark(&mut self) {
        self.mark = (self.next_pos - 1) as i32;
        self.mark_next_read_index = self.next_read_index;
    }

    fn do_buffer_read(&mut self, offset: usize, len: usize) -> usize {
        let byte_input_stream: &mut File = self.byte_input_stream.as_mut().unwrap();
        let end = offset + len;
        let count = byte_input_stream
            .read(&mut self.buffer[offset..end])
            .unwrap();
        if count == 0 {
            self.is_read_all_from_file = true;
        }
        return count;
    }

    pub(crate) fn init(&mut self, source_code_file_path: &String) -> Result<(), AstAppError> {
        self.clear();

        if self.buffer.len() < STANDARD_BUFFER_CAPACITY
            || self.buffer.len() != self.buffer.capacity()
        {
            self.buffer = vec![0; STANDARD_BUFFER_CAPACITY];
        }
        let source_code_file = File::open(source_code_file_path)?;
        self.byte_input_stream = Some(source_code_file);
        Ok(())
    }

    pub(crate) fn clear(&mut self) {
        self.next_read_index = 0;
        self.next_pos = 0;
        self.limit = 0;
        self.mark = EOF;
        self.limit_of_invalid_data = 0;
        self.is_read_all_from_file = false;

        self.byte_input_stream = None;

        self.buffer = Vec::with_capacity(0);
    }
}

impl Clone for ByteBufferedInputStream {
    fn clone(&self) -> Self {
        Self {
            next_read_index: self.next_read_index,
            next_pos: self.next_pos,
            limit: self.limit,
            mark: self.mark,
            mark_next_read_index: self.mark_next_read_index,
            limit_of_invalid_data: self.limit_of_invalid_data,
            is_read_all_from_file: self.is_read_all_from_file,
            buffer: self.buffer.clone(),
            byte_input_stream: None,
        }
    }
}

impl Default for ByteBufferedInputStream {
    fn default() -> Self {
        Self {
            next_read_index: 0,
            next_pos: 0,
            limit: 0,
            mark: EOF,
            mark_next_read_index: 0,
            limit_of_invalid_data: 0,
            is_read_all_from_file: false,
            buffer: vec![0; STANDARD_BUFFER_CAPACITY],
            byte_input_stream: None,
        }
    }
}
