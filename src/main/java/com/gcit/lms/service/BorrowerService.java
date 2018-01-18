/*
 *
 *11:59:06 AM
 *Jan 1, 2018
 */
package com.gcit.lms.service;

import java.sql.SQLException;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.gcit.lms.dao.BookDAO;
import com.gcit.lms.dao.BorrowerDAO;
import com.gcit.lms.dao.BranchDAO;
import com.gcit.lms.dao.ConnectDAO;
import com.gcit.lms.entity.Book;
import com.gcit.lms.entity.BookCopies;
import com.gcit.lms.entity.Borrower;
import com.gcit.lms.entity.Branch;

/**
 * @author ThankGod4Life
 * @date Jan 1, 2018
 *
 */

@RestController
public class BorrowerService extends BaseService {

	@Autowired
	BorrowerDAO borDao;

	@Autowired
	BookDAO bDao;

	@Autowired
	BranchDAO brDao;

	@Autowired
	BookCopiesService bcService;

	@Autowired
	BookLoansService blService;

	@Autowired
	ConnectDAO cDao;

	// Jackson dependency converts JSON applications to Java objects and vice versa
	@RequestMapping(value = "/borrowers", method = RequestMethod.GET, produces = "application/json")
	public List<Borrower> readAllBorrowers() {
		try {
			List<Borrower> borrower = borDao.readAllBorrowers();
			for (Borrower borrower2 : borrower) {
				setObjectsByCardNo(borrower2); // update attributes of borrower objects
			}
			return borrower;
		} catch (InstantiationException | IllegalAccessException | ClassNotFoundException | SQLException e) {
			return null;
		}
	}

	private void setObjectsByCardNo(Borrower borrower) {
		try {
			String sql = "SELECT * FROM tbl_book WHERE bookId IN (SELECT bookId FROM tbl_book_loans WHERE cardNo = ?)";
			String sql2 = "SELECT * FROM tbl_library_branch WHERE branchId IN (SELECT branchId FROM tbl_book_loans WHERE cardNo = ?)";
			borrower.setBook(bDao.readAllBooksByOtherID(sql, borrower.getCardNo()));
			borrower.setBranch(brDao.readAllBranchByOtherID(sql2, borrower.getCardNo()));
		} catch (InstantiationException | IllegalAccessException | ClassNotFoundException | SQLException e) {
		}
	}

	// Jackson dependency converts JSON applications to Java objects and vice versa
	@RequestMapping(value = "/borrowers/name", method = RequestMethod.GET, produces = "application/json")
	public List<Borrower> readBorrowersByName(@RequestParam("name") String name) {
		try {
			List<Borrower> borrower = borDao.readBorrowersByName(name);
			for (Borrower borrower2 : borrower) {
				setObjectsByCardNo(borrower2); // update attributes of borrower objects
			}
			return borrower;
		} catch (InstantiationException | IllegalAccessException | ClassNotFoundException | SQLException e) {
			return null;
		}
	}

	// Jackson dependency converts JSON applications to Java objects and vice versa
	@RequestMapping(value = "/borrowers/{cardNo}", method = RequestMethod.GET, produces = "application/json")
	public Borrower readBorrowerByPK(@PathVariable Integer cardNo) {
		try {
			Borrower borrower = borDao.readBorrowerByPK(cardNo);
			setObjectsByCardNo(borrower);
			return borrower;
		} catch (InstantiationException | IllegalAccessException | ClassNotFoundException | SQLException e) {
			return null;
		}
	}

	// Jackson dependency converts JSON applications to Java objects and vice versa
	@RequestMapping(value = "/borrowers", method = RequestMethod.POST, consumes = "application/json", produces = "application/json")
	@Transactional
	public String addBorrowerWithID(@RequestBody String jsonString) {
		String message = "";
		try {
			JSONObject jObject = new JSONObject(jsonString);
			Borrower borrower = new Borrower();
			borrower.setName(jObject.getString("name"));
			borrower.setAddress(jObject.getString("address"));
			borrower.setPhone(jObject.getString("phone"));
			Integer id = borDao.addBorrowerWithID(borrower);
			borrower.setCardNo(id);
			List<Integer> bookIds = convertJsonArryToIntList(jObject.getJSONArray("bookIds"));
			List<Integer> branchIds = convertJsonArryToIntList(jObject.getJSONArray("branchIds"));
			List<Book> bList = readObjectsByPk(bDao, "readBookByPK", bookIds);
			List<Branch> brList = readObjectsByPk(brDao, "readBranchByPK", branchIds);
			for (Book book : bList) {
				for (Branch branch : brList) {
					blService.addBookLoans(book, branch, borrower, jObject.getString("dateOut"),
							jObject.getString("dueDate"));
				}
			}
			List<BookCopies> currentCopies = bcService.readAllBookCopies();

			for (Book book : bList) {
				for (Branch branch : brList) {
					boolean newCopy = true;
					for (BookCopies bookCopies : currentCopies) {
						if ((bookCopies.getBook().getBookId() == book.getBookId())
								&& (bookCopies.getBranch().getBranchId() == branch.getBranchId())) {
							bcService.updateBookCopiesByIds(book.getBookId(), branch.getBranchId(), -1);
							newCopy = false;
						}
					}
					if (newCopy) {
						bcService.addBookCopies(book.getBookId(), branch.getBranchId(), 3);
					}
				}
			}
			message = "Operation Successful";
		} catch (InstantiationException | IllegalAccessException | ClassNotFoundException | SQLException | JSONException e) {
			message = "Operation Failed";
		}
		return message;
	}

	// Jackson dependency converts JSON applications to Java objects and vice versa
	@RequestMapping(value = "/borrowers", method = RequestMethod.PUT, consumes = "application/json", produces = "application/json")
	@Transactional
	public String updateBorrower(@RequestBody String jsonString) {
		String message = "";
		try {
			JSONObject jObject = new JSONObject(jsonString);
			Borrower borrower = borDao.readBorrowerByPK(jObject.getInt("cardNo"));
			setObjectsByCardNo(borrower); // complete setup process
			cDao.deleteBorrowerBookLoans(borrower.getBook(), borrower.getBranch(), borrower); // just need to remove
																								// book
																								// loans table data
			borrower.setName(jObject.getString("name"));
			borrower.setAddress(jObject.getString("address"));
			borrower.setPhone(jObject.getString("phone"));
			borDao.updateBorrower(borrower); // update the database table
			List<Integer> bookIds = convertJsonArryToIntList(jObject.getJSONArray("bookIds"));
			List<Integer> branchIds = convertJsonArryToIntList(jObject.getJSONArray("branchIds"));
			List<Book> bList = readObjectsByPk(bDao, "readBookByPK", bookIds);
			List<Branch> brList = readObjectsByPk(brDao, "readBranchByPK", branchIds);
			for (Book book : bList) {
				for (Branch branch : brList) {
					blService.addBookLoans(book, branch, borrower, jObject.getString("dateOut"),
							jObject.getString("dueDate"));
				}
			}
			List<BookCopies> currentCopies = bcService.readAllBookCopies();

			for (Book book : bList) {
				for (Branch branch : brList) {
					boolean newCopy = true;
					for (BookCopies bookCopies : currentCopies) {
						if ((bookCopies.getBook().getBookId() == book.getBookId())
								&& (bookCopies.getBranch().getBranchId() == branch.getBranchId())) {
							bcService.updateBookCopiesByIds(book.getBookId(), branch.getBranchId(), -1);
							newCopy = false;
						}
					}
					if (newCopy) {
						bcService.addBookCopies(book.getBookId(), branch.getBranchId(), 3);
					}
				}
			}
			message = "Operation Successful";
		} catch (InstantiationException | IllegalAccessException | ClassNotFoundException | SQLException | JSONException e) {
			message = "Operation failed";
		}
		return message;
	}

	// Jackson dependency converts JSON applications to Java objects and vice versa
	@RequestMapping(value = "/borrowers/{cardNo}", method = RequestMethod.DELETE, consumes = "application/json", produces = "application/json")
	public String deleteBorrower(@PathVariable Integer cardNo) {
		String message = "";
		try {
			Borrower borrower = borDao.readBorrowerByPK(cardNo);
			borDao.deleteBorrower(borrower);
			message = "Operation Successful";
		} catch (InstantiationException | IllegalAccessException | ClassNotFoundException | SQLException e) {
			message = "Operation failed";
		}
		return message;
	}

	// Jackson dependency converts JSON applications to Java objects and vice versa
	@RequestMapping(value = "/borrowers/page", method = RequestMethod.GET, produces = "application/json")
	public List<Borrower> readBorrowersLimitSearch(@RequestParam("offset") Integer offset,
			@RequestParam("limit") Integer limit) {
		try {
			List<Borrower> borrower = borDao.readAllBorrowersWithLimit(offset, limit);
			for (Borrower borrower2 : borrower) {
				setObjectsByCardNo(borrower2); // update attributes of borrower objects
			}
			return borrower;
		} catch (InstantiationException | IllegalAccessException | ClassNotFoundException | SQLException e) {
			return null;
		}
	}

	// Jackson dependency converts JSON applications to Java objects and vice versa
	@RequestMapping(value = "/borrowers/count", method = RequestMethod.GET, produces = "application/json")
	public Integer getBorrowersCount() {
		try {
			return borDao.getBorrowersCount();
		} catch (InstantiationException | IllegalAccessException | ClassNotFoundException | SQLException e) {
			return null;
		}
	}
}
