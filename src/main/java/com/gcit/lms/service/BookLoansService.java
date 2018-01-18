/*
 *
 *11:02:22 AM
 *Jan 1, 2018
 */
package com.gcit.lms.service;

import java.sql.SQLException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.gcit.lms.dao.BookDAO;
import com.gcit.lms.dao.BookLoansDAO;
import com.gcit.lms.dao.BorrowerDAO;
import com.gcit.lms.dao.BranchDAO;
import com.gcit.lms.entity.Book;
import com.gcit.lms.entity.BookLoans;
import com.gcit.lms.entity.Borrower;
import com.gcit.lms.entity.Branch;

/**
 * @author ThankGod4Life
 * @date Jan 1, 2018
 *
 */

@RestController
public class BookLoansService {

	@Autowired
	BookDAO bDao;

	@Autowired
	BookLoansDAO blDao;

	@Autowired
	BranchDAO brDao;

	@Autowired
	BorrowerDAO borDao;

	// Jackson dependency converts JSON applications to Java objects and vice versa
	@RequestMapping(value = "/bookloans/page", method = RequestMethod.GET, produces = "application/json")
	public List<BookLoans> readBookLoansLimitSearch(@RequestParam("offset") Integer offset,
			@RequestParam("limit") Integer limit) {
		try {
			List<BookLoans> bookLoans = blDao.readAllBookLoansWithLimit(offset, limit);
			for (BookLoans bookLoans2 : bookLoans) {
				setObjectsByBookLoansId(bookLoans2); // update attributes of book objects
			}
			return bookLoans;
		} catch (InstantiationException | IllegalAccessException | ClassNotFoundException | SQLException e) {
			return null;
		}
	}

	private String setObjectsByBookLoansId(BookLoans bookLoans) {
		String message = "";
		try {
			bookLoans.setBook(bDao.readBookByPK(bookLoans.getBook().getBookId())); // for singular book loan there's
			// singular branchId and bookId
			bookLoans.setBranch(brDao.readBranchByPK(bookLoans.getBranch().getBranchId()));
			bookLoans.setBorrower(borDao.readBorrowerByPK(bookLoans.getBorrower().getCardNo()));
			message = "Operation successful";
		} catch (InstantiationException | IllegalAccessException | ClassNotFoundException | SQLException e) {
			message = "Operation failed";
		}
		return message;
	}

	// Jackson dependency converts JSON applications to Java objects and vice versa
	@RequestMapping(value = "/bookloans/count", method = RequestMethod.GET, produces = "application/json")
	public Integer getBookLoansCount() {
		try {
			return blDao.getBookLoansCount();
		} catch (InstantiationException | IllegalAccessException | ClassNotFoundException | SQLException e) {
			return null;
		}
	}

	// Jackson dependency converts JSON applications to Java objects and vice versa
	@RequestMapping(value = "/bookloans", method = RequestMethod.GET, produces = "application/json")
	public List<BookLoans> readAllBookLoans() {
		try {
			List<BookLoans> bookLoans = blDao.readAllBookLoans();
			for (BookLoans bookLoans2 : bookLoans) {
				setObjectsByBookLoansId(bookLoans2); // update attributes of book objects
			}
			return bookLoans;
		} catch (InstantiationException | IllegalAccessException | ClassNotFoundException | SQLException e) {
			return null;
		}
	}

	public String updateBookLoansByDueDate(Integer bookId, Integer branchId, Integer cardNo, String dueDate) {
		String message = "";
		try {
			BookLoans bookLoans = blDao.readBookLoansByIds(bookId, branchId, cardNo);
			bookLoans.setDueDate(dueDate);
			blDao.updateBookLoansByDueDate(bookLoans);
			message = "Operation successful";
		} catch (InstantiationException | IllegalAccessException | ClassNotFoundException | SQLException e) {
			message = "Operation failed";
		}
		return message;
	}

	@Transactional
	public String addBookLoans(Book book, Branch branch, Borrower borrower, String dateOut, String dueDate) {
		String message = "";
		try {
			BookLoans bookLoans = new BookLoans();
			bookLoans.setBook(book);
			bookLoans.setBranch(branch);
			bookLoans.setBorrower(borrower);
			bookLoans.setDateOut(dateOut);
			bookLoans.setDueDate(dueDate);
			blDao.addBookLoans(bookLoans);
			message = "Operation Successful";
		} catch (InstantiationException | IllegalAccessException | ClassNotFoundException | SQLException e) {
			message = "Operation Failed";
		}
		return message;
	}

	@Transactional
	public String updateBookLoansByDateIn(Integer bookId, Integer branchId, Integer cardNo, String dateIn) {
		String message = "";
		try {
			BookLoans bookLoans = blDao.readBookLoansByIds(bookId, branchId, cardNo);
			bookLoans.setDateIn(dateIn);
			blDao.updateBookLoansByDateIn(bookLoans);
			message = "Operation Successful";
		} catch (InstantiationException | IllegalAccessException | ClassNotFoundException | SQLException e) {
			message = "Operation Failed";
		}
		return message;
	}

	// Jackson dependency converts JSON applications to Java objects and vice versa
	@RequestMapping(value = "/books/{bookId}/branches/{branchId}/borrowers/{cardNo}", method = RequestMethod.GET, produces = "application/json")
	public BookLoans readBookLoansByIds(@PathVariable Integer bookId, @PathVariable Integer branchId,
			@PathVariable Integer cardNo) {
		try {
			BookLoans bookLoans = blDao.readBookLoansByIds(bookId, branchId, cardNo);
			setObjectsByBookLoansId(bookLoans); // update attributes of book loan objects
			return bookLoans;
		} catch (InstantiationException | IllegalAccessException | ClassNotFoundException | SQLException e) {
			return null;
		}
	}

	// Jackson dependency converts JSON applications to Java objects and vice versa
	@RequestMapping(value = "/books/{bookId}/branches/{branchId}/borrowers/{cardNo}", method = RequestMethod.DELETE, consumes = "application/json", produces = "application/json")
	public String deleteBookLoans(@PathVariable Integer bookId, @PathVariable Integer branchId,
			@PathVariable Integer cardNo) {
		String message = "";
		try {
			BookLoans bookLoans = blDao.readBookLoansByIds(bookId, branchId, cardNo);
			blDao.deleteBookLoans(bookLoans);
			message = "Operation successful!";
		} catch (InstantiationException | IllegalAccessException | ClassNotFoundException | SQLException e) {
			message = "Operation failed!";
		}
		return message;
	}
}
