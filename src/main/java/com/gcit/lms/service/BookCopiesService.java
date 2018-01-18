/*
 *
 *10:32:55 AM
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

import com.gcit.lms.dao.BookCopiesDAO;
import com.gcit.lms.dao.BookDAO;
import com.gcit.lms.dao.BranchDAO;
import com.gcit.lms.entity.Book;
import com.gcit.lms.entity.BookCopies;
import com.gcit.lms.entity.Branch;

/**
 * @author ThankGod4Life
 * @date Jan 1, 2018
 *
 */

@RestController
public class BookCopiesService {

	@Autowired
	BookDAO bDao;

	@Autowired
	BookCopiesDAO bcDao;

	@Autowired
	BranchDAO brDao;

	// pageBookCopies is the entry point for all things GET related to bookCopies.
	// Jackson
	// dependency converts JSON applications to Java objects and vice versa
	@RequestMapping(value = "/bookcopies/page", method = RequestMethod.GET, produces = "application/json")
	public List<BookCopies> readBookCopiesLimitSearch(@RequestParam("offset") Integer offset,
			@RequestParam("limit") Integer limit) {
		try {
			List<BookCopies> bookCopies = bcDao.readAllBookCopiesWithLimit(offset, limit);
			for (BookCopies bookCopies2 : bookCopies) {
				setObjectsByBookCopiesId(bookCopies2); // update attributes of book objects
			}
			return bookCopies;
		} catch (InstantiationException | IllegalAccessException | ClassNotFoundException | SQLException e) {
			return null;
		}
	}

	private void setObjectsByBookCopiesId(BookCopies bookCopies) {
		try {
			bookCopies.setBook(bDao.readBookByPK(bookCopies.getBook().getBookId())); // for singular book copy there's
			// singular branchId and bookId
			bookCopies.setBranch(brDao.readBranchByPK(bookCopies.getBranch().getBranchId()));
		} catch (InstantiationException | IllegalAccessException | ClassNotFoundException | SQLException e) {
		}
	}

	// Jackson dependency converts JSON applications to Java objects and vice versa
	@RequestMapping(value = "/bookcopies/count", method = RequestMethod.GET, produces = "application/json")
	public Integer getBookCopiesCount() {
		try {
			return bcDao.getBookCopiesCount();
		} catch (InstantiationException | IllegalAccessException | ClassNotFoundException | SQLException e) {
			return null;
		}
	}

	// Jackson dependency converts JSON applications to Java objects and vice versa
	@RequestMapping(value = "/bookcopies/+page", method = RequestMethod.GET, produces = "application/json")
	public List<BookCopies> readBookCopiesLimitSearchOverZero(@RequestParam("offset") Integer offset,
			@RequestParam("limit") Integer limit) {
		try {
			List<BookCopies> bookCopies = bcDao.readAllBookCopiesWithLimitOverZero(offset, limit);
			for (BookCopies bookCopies2 : bookCopies) {
				setObjectsByBookCopiesId(bookCopies2); // update attributes of bookCopies objects
			}
			return bookCopies;
		} catch (InstantiationException | IllegalAccessException | ClassNotFoundException | SQLException e) {
			return null;
		}
	}

	// Jackson dependency converts JSON applications to Java objects and vice versa
	@RequestMapping(value = "/bookcopies/+count", method = RequestMethod.GET, produces = "application/json")
	public Integer getBookCopiesCountOverZero() {
		try {
			return bcDao.getBookCopiesCountOverZero();
		} catch (InstantiationException | IllegalAccessException | ClassNotFoundException | SQLException e) {
			return null;
		}
	}

	@Transactional
	public String addBookCopies(Integer bookId, Integer branchId, Integer noOfCopies) {
		String message = "";
		try {
			BookCopies bookCopies = new BookCopies();
			Book book = new Book();
			Branch branch = new Branch();
			book.setBookId(bookId);
			branch.setBranchId(branchId);
			bookCopies.setBook(book);
			bookCopies.setBranch(branch);
			bookCopies.setNoOfCopies(noOfCopies);
			bcDao.addBookCopies(bookCopies);
			message = "Operation successful!";
		} catch (InstantiationException | IllegalAccessException | ClassNotFoundException | SQLException e) {
			message = "Operation failed!";
		}
		return message;
	}

	@Transactional
	public String updateBookCopiesByIds(Integer bookId, Integer branchId, Integer noOfCopies) {
		String message = "";
		try {
			BookCopies bookCopies = bcDao.readBookCopiesByIds(bookId, branchId);
			bookCopies.setNoOfCopies(bookCopies.getNoOfCopies() + noOfCopies);
			bcDao.updateBookCopies(bookCopies);
			message = "Operation Successful!";
		} catch (InstantiationException | IllegalAccessException | ClassNotFoundException | SQLException e) {
			message = "Operation failed!";
		}
		return message;
	}

	// Jackson dependency converts JSON applications to Java objects and vice versa
	@RequestMapping(value = "/books/{bookId}/branches/{branchId}", method = RequestMethod.GET, produces = "application/json")
	public BookCopies readBookCopiesByIds(@PathVariable Integer bookId, @PathVariable Integer branchId) {
		try {
			BookCopies bookCopies = bcDao.readBookCopiesByIds(bookId, branchId);
			setObjectsByBookCopiesId(bookCopies); // update attributes of bookCopies objects
			return bookCopies;
		} catch (InstantiationException | IllegalAccessException | ClassNotFoundException | SQLException e) {
			return null;
		}
	}

	// Jackson dependency converts JSON applications to Java objects and vice versa
	@RequestMapping(value = "/bookcopies", method = RequestMethod.GET, produces = "application/json")
	public List<BookCopies> readAllBookCopies() {
		try {
			List<BookCopies> bookCopies = bcDao.readAllBookCopies();
			for (BookCopies bookCopies2 : bookCopies) {
				setObjectsByBookCopiesId(bookCopies2); // update attributes of book objects
			}
			return bookCopies;
		} catch (InstantiationException | IllegalAccessException | ClassNotFoundException | SQLException e) {
			return null;
		}
	}
	
	// Jackson dependency converts JSON applications to Java objects and vice versa
	@RequestMapping(value = "/books/{bookId}/branches/{branchId}", method = RequestMethod.DELETE, consumes = "application/json", produces = "application/json")
	public String deleteBookCopies(@PathVariable Integer bookId, @PathVariable Integer branchId) {
		String message = "";
		try {
			BookCopies bookCopies = bcDao.readBookCopiesByIds(bookId, branchId);
			bcDao.deleteBookCopies(bookCopies);
			message = "Operation successful!";
		} catch (InstantiationException | IllegalAccessException | ClassNotFoundException | SQLException e) {
			message = "Operation failed!";
		}
		return message;
	}
}
