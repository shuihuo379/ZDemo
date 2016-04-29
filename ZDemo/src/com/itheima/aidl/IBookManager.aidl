package com.itheima.aidl;

import java.util.List;
import com.itheima.aidl.Book;

interface IBookManager {
	List<Book> getBookList();
	void addBook(in Book book);
}