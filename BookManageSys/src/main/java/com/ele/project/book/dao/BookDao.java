package com.ele.project.book.dao;

import java.util.List;
import java.util.Map;

public interface BookDao {
	public List<Map<String,Object>> getBookList(Map bookMap);
	public int getBookListCount(Map bookMap);
	public List<Map<String,Object>> searchBook(Map bookMap);
	public int searchBookCount(Map bookMap);
	public Map<String,Object> getBookDetailByid(Map bookMap);
	public int insertBook(Map params);
	public int updateBook(Map params);
	public int deleteBook(Map params);
}
