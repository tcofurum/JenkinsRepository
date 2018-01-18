/*
 *
+ *2:39:15 PM
 *Dec 21, 2017
 */
package com.gcit.lms.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.jdbc.core.ResultSetExtractor;

import com.gcit.lms.entity.Book;
import com.gcit.lms.entity.BookLoans;
import com.gcit.lms.entity.Borrower;
import com.gcit.lms.entity.Branch;

/**
 * @author ThankGod4Life
 * @date Dec 21, 2017
 *
 */
public class BookLoansDAO extends BaseDAO<BookLoans> implements ResultSetExtractor<List<BookLoans>> {

	public void addBookLoans(BookLoans bookLoans)
			throws SQLException, InstantiationException, IllegalAccessException, ClassNotFoundException {
		jdbcTemplate.update("INSERT INTO tbl_book_loans (bookId, branchId, cardNo, dateOut, dueDate) VALUES (?, ?, ?, ?, ?)",
				new Object[] { bookLoans.getBook().getBookId(), bookLoans.getBranch().getBranchId(),
						bookLoans.getBorrower().getCardNo(), bookLoans.getDateOut(), bookLoans.getDueDate() });
	}

	public void updateBookLoansByDueDate(BookLoans bookLoans)
			throws SQLException, InstantiationException, IllegalAccessException, ClassNotFoundException {
		jdbcTemplate.update("UPDATE tbl_book_loans SET dueDate =? WHERE bookId = ? and branchId = ? and cardNo = ?",
				new Object[] { bookLoans.getDueDate(), bookLoans.getBook().getBookId(),
						bookLoans.getBranch().getBranchId(), bookLoans.getBorrower().getCardNo() });
	}

	public void updateBookLoansByDateIn(BookLoans bookLoans)
			throws SQLException, InstantiationException, IllegalAccessException, ClassNotFoundException {
		jdbcTemplate.update("UPDATE tbl_book_loans SET dateIn =? WHERE bookId = ? and branchId = ? and cardNo = ?",
				new Object[] { bookLoans.getDateIn(), bookLoans.getBook().getBookId(),
						bookLoans.getBranch().getBranchId(), bookLoans.getBorrower().getCardNo() });
	}

	public void deleteBookLoans(BookLoans bookLoans)
			throws SQLException, InstantiationException, IllegalAccessException, ClassNotFoundException {
		jdbcTemplate.update("DELETE FROM tbl_book_loans WHERE bookId = ? and branchId = ? and cardNo = ?",
				new Object[] { bookLoans.getBook().getBookId(), bookLoans.getBranch().getBranchId(),
						bookLoans.getBorrower().getCardNo() });
	}

	public List<BookLoans> readAllBookLoans()
			throws InstantiationException, IllegalAccessException, ClassNotFoundException, SQLException {
		return jdbcTemplate.query("SELECT * FROM tbl_book_loans", this);
	}
	
	public List<BookLoans> readAllBookLoansWithLimit(Integer pageNo, Integer pageSize)
			throws InstantiationException, IllegalAccessException, ClassNotFoundException, SQLException {
		setPageNo(pageNo);
		setPageSize(pageSize);
		return jdbcTemplate.query(getLimit("SELECT * FROM tbl_book_loans"), this);
	}
	
	public Integer getBookLoansCount()
			throws InstantiationException, IllegalAccessException, ClassNotFoundException, SQLException {
		return jdbcTemplate.queryForObject("SELECT COUNT(*) AS COUNT FROM tbl_book_loans", Integer.class);
	}

	public BookLoans readBookLoansByIds(Integer bookId, Integer branchId, Integer cardNo)
			throws InstantiationException, IllegalAccessException, ClassNotFoundException, SQLException {
		List<BookLoans> bookLoans = jdbcTemplate.query(
				"SELECT * FROM tbl_book_loans WHERE bookId  = ? and branchId = ? and cardNo = ?",
				new Object[] { bookId, branchId, cardNo }, this);
		if (bookLoans != null) {
			return bookLoans.get(0);
		} else {
			return null;
		}
	}

	public List<BookLoans> readBookLoansByBookId(Integer bookId)
			throws InstantiationException, IllegalAccessException, ClassNotFoundException, SQLException {
		List<BookLoans> bookLoans = jdbcTemplate.query("SELECT * FROM tbl_book_loans WHERE bookId  = ?", new Object[] { bookId }, this);
		if (bookLoans != null) {
			return bookLoans;
		} else {
			return null;
		}
	}

	public List<BookLoans> readBookLoansByBranchId(Integer branchId)
			throws InstantiationException, IllegalAccessException, ClassNotFoundException, SQLException {
		List<BookLoans> bookLoans = jdbcTemplate.query("SELECT * FROM tbl_book_loans WHERE branchId  = ?", new Object[] { branchId }, this);
		if (bookLoans != null) {
			return bookLoans;
		} else {
			return null;
		}
	}

	public List<BookLoans> readBookLoansByCardNo(Integer cardNo)
			throws InstantiationException, IllegalAccessException, ClassNotFoundException, SQLException {
		List<BookLoans> bookLoans = jdbcTemplate.query("SELECT * FROM tbl_book_loans WHERE cardNo  = ?", new Object[] { cardNo }, this);
		if (bookLoans != null) {
			return bookLoans;
		} else {
			return null;
		}
	}

	@Override
	public List<BookLoans> extractData(ResultSet rs)
			throws SQLException{
		List<BookLoans> bookLoans = new ArrayList<>();
		while (rs.next()) {
			Book book = new Book();
			Branch branch = new Branch();
			Borrower borrower = new Borrower();
			BookLoans a = new BookLoans();
			a.setDateOut(rs.getString("dateOut"));
			a.setDueDate(rs.getString("dueDate"));
			a.setDateIn(rs.getString("dateIn"));
			book.setBookId(rs.getInt("bookId"));
			branch.setBranchId(rs.getInt("branchId"));
			borrower.setCardNo(rs.getInt("cardNo"));
			a.setBook(book);
			a.setBranch(branch);
			a.setBorrower(borrower);
			bookLoans.add(a);
		}
		return bookLoans;
	}

}
