/*
 *
 *2:36:49 PM
 *Dec 21, 2017
 */
package com.gcit.lms.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.jdbc.core.ResultSetExtractor;

import com.gcit.lms.entity.Book;
import com.gcit.lms.entity.BookCopies;
import com.gcit.lms.entity.Branch;

/**
 * @author ThankGod4Life
 * @date Dec 21, 2017
 *
 */
public class BookCopiesDAO extends BaseDAO<BookCopies> implements ResultSetExtractor<List<BookCopies>> {

	public void addBookCopies(BookCopies bookCopies)
			throws SQLException, InstantiationException, IllegalAccessException, ClassNotFoundException {
		jdbcTemplate.update("INSERT INTO tbl_book_copies (bookId, branchId, noOfCopies) VALUES (?, ?, ?)",
				new Object[] { bookCopies.getBook().getBookId(), bookCopies.getBranch().getBranchId(),
						bookCopies.getNoOfCopies() });
	}

	public void updateBookCopies(BookCopies bookCopies)
			throws SQLException, InstantiationException, IllegalAccessException, ClassNotFoundException {
		jdbcTemplate.update("UPDATE tbl_book_copies SET noOfCopies =? WHERE bookId = ? and branchId = ?", new Object[] {
				bookCopies.getNoOfCopies(), bookCopies.getBook().getBookId(), bookCopies.getBranch().getBranchId() });
	}

	public void deleteBookCopies(BookCopies bookCopies)
			throws SQLException, InstantiationException, IllegalAccessException, ClassNotFoundException {
		jdbcTemplate.update("DELETE FROM tbl_book_copies WHERE bookId = ? and branchId = ?",
				new Object[] { bookCopies.getBook().getBookId(), bookCopies.getBranch().getBranchId() });
	}

	public List<BookCopies> readAllBookCopies()
			throws InstantiationException, IllegalAccessException, ClassNotFoundException, SQLException {
		return jdbcTemplate.query("SELECT * FROM tbl_book_copies", this);
	}

	public Integer getBookCopiesCount()
			throws InstantiationException, IllegalAccessException, ClassNotFoundException, SQLException {
		return jdbcTemplate.queryForObject("SELECT COUNT(*) AS COUNT FROM tbl_book_copies", Integer.class);
	}

	public Integer getBookCopiesCountOverZero()
			throws InstantiationException, IllegalAccessException, ClassNotFoundException, SQLException {
		return jdbcTemplate.queryForObject("SELECT COUNT(*) AS COUNT FROM tbl_book_copies WHERE noOfCopies > 0",
				Integer.class);
	}

	public List<BookCopies> readAllBookCopiesWithLimit(Integer pageNo, Integer pageSize)
			throws InstantiationException, IllegalAccessException, ClassNotFoundException, SQLException {
		setPageNo(pageNo);
		setPageSize(pageSize);
		return jdbcTemplate.query(getLimit("SELECT * FROM tbl_book_copies"), this);
	}

	public List<BookCopies> readAllBookCopiesWithLimitOverZero(Integer pageNo, Integer pageSize)
			throws InstantiationException, IllegalAccessException, ClassNotFoundException, SQLException {
		setPageNo(pageNo);
		setPageSize(pageSize);
		return jdbcTemplate.query(getLimit("SELECT * FROM tbl_book_copies WHERE noOfCopies > 0"), this);
	}

	public List<BookCopies> readAllBookCopiesOverZero()
			throws InstantiationException, IllegalAccessException, ClassNotFoundException, SQLException {
		return jdbcTemplate.query("SELECT * FROM tbl_book_copies WHERE noOfCopies > 0", this);
	}

	public BookCopies readBookCopiesByIds(Integer bookId, Integer branchId)
			throws InstantiationException, IllegalAccessException, ClassNotFoundException, SQLException {
		List<BookCopies> bookCopies = jdbcTemplate.query(
				"SELECT * FROM tbl_book_copies WHERE bookId =? and branchId  = ?", new Object[] { bookId, branchId },
				this);
		if (bookCopies != null) {
			return bookCopies.get(0);
		} else {
			return null;
		}
	}

	public List<BookCopies> readBookCopiesByBookId(Integer bookId)
			throws InstantiationException, IllegalAccessException, ClassNotFoundException, SQLException {
		List<BookCopies> bookCopies = jdbcTemplate.query("SELECT * FROM tbl_book_copies WHERE bookId  = ?",
				new Object[] { bookId }, this);
		if (bookCopies != null) {
			return bookCopies;
		} else {
			return null;
		}
	}

	public List<BookCopies> readBookCopiesByBranchId(Integer branchId)
			throws InstantiationException, IllegalAccessException, ClassNotFoundException, SQLException {
		List<BookCopies> bookCopies = jdbcTemplate.query("SELECT * FROM tbl_book_copies WHERE branchId  = ?",
				new Object[] { branchId }, this);
		if (bookCopies != null) {
			return bookCopies;
		} else {
			return null;
		}
	}

	@Override
	public List<BookCopies> extractData(ResultSet rs) throws SQLException {
		List<BookCopies> bookCopies = new ArrayList<>();
		while (rs.next()) {
			Book book = new Book();
			Branch branch = new Branch();
			BookCopies a = new BookCopies();
			a.setNoOfCopies(rs.getInt("noOfCopies"));
			book.setBookId(rs.getInt("bookId"));
			branch.setBranchId(rs.getInt("branchId"));
			a.setBook(book);
			a.setBranch(branch);
			
			bookCopies.add(a);
		}
		return bookCopies;
	}

}
