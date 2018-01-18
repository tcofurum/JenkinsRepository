/*
 *
 *9:25:24 AM
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

import com.gcit.lms.dao.BookCopiesDAO;
import com.gcit.lms.dao.BookDAO;
import com.gcit.lms.dao.BookLoansDAO;
import com.gcit.lms.dao.BorrowerDAO;
import com.gcit.lms.dao.BranchDAO;
import com.gcit.lms.dao.ConnectDAO;
import com.gcit.lms.entity.Book;
import com.gcit.lms.entity.BookCopies;
import com.gcit.lms.entity.Branch;

/**
 * @author ThankGod4Life
 * @date Jan 1, 2018
 *
 */

@RestController
public class BranchService extends BaseService {

	@Autowired
	BranchDAO brDao;

	@Autowired
	BookDAO bDao;

	@Autowired
	BookCopiesDAO bcDao;

	@Autowired
	BookLoansDAO blDao;

	@Autowired
	BorrowerDAO borDao;

	@Autowired
	ConnectDAO cDao;

	// Jackson dependency converts JSON applications to Java objects and vice versa
	@RequestMapping(value = "/branches", method = RequestMethod.GET, produces = "application/json")
	public List<Branch> readAllBranchs() {
		try {
			List<Branch> branch = brDao.readAllBranchs();
			for (Branch branch2 : branch) {
				setObjectsByBranchId(branch2); // update attributes of branch objects
			}
			return branch;
		} catch (InstantiationException | IllegalAccessException | ClassNotFoundException | SQLException e) {
			return null;
		}
	}

	private void setObjectsByBranchId(Branch branch) {
		try {
			String sql = "SELECT * FROM tbl_book WHERE bookId IN (SELECT bookId FROM tbl_book_copies WHERE branchId = ?)";
			String sql2 = "SELECT * FROM tbl_borrower WHERE cardNo IN (SELECT cardNo FROM tbl_book_loans WHERE branchId = ?)";
			branch.setBook(bDao.readAllBooksByOtherID(sql, branch.getBranchId()));
			branch.setBorrower(borDao.readAllBorrowersByOtherID(sql2, branch.getBranchId()));
		} catch (InstantiationException | IllegalAccessException | ClassNotFoundException | SQLException e) {
		}
	}

	// Jackson dependency converts JSON applications to Java objects and vice versa
	@RequestMapping(value = "/branches/name", method = RequestMethod.GET, produces = "application/json")
	public List<Branch> readBranchsByName(@RequestParam("name") String name) {
		try {
			List<Branch> branch = brDao.readBranchsByName(name);
			for (Branch branch2 : branch) {
				setObjectsByBranchId(branch2); // update attributes of branch objects
			}
			return branch;
		} catch (InstantiationException | IllegalAccessException | ClassNotFoundException | SQLException e) {
			return null;
		}
	}

	// Jackson dependency converts JSON applications to Java objects and vice versa
	@RequestMapping(value = "/branches/{branchId}", method = RequestMethod.GET, produces = "application/json")
	public Branch readBranchByPK(@PathVariable Integer branchId) {
		try {
			Branch branch = brDao.readBranchByPK(branchId);
			setObjectsByBranchId(branch);
			return branch;
		} catch (InstantiationException | IllegalAccessException | ClassNotFoundException | SQLException e) {
			return null;
		}
	}

	// Jackson dependency converts JSON applications to Java objects and vice versa
	@RequestMapping(value = "/branches", method = RequestMethod.POST, consumes = "application/json", produces = "application/json")
	@Transactional
	public String addBranchWithID(@RequestBody String jsonString) {
		String message = "";
		try {
			JSONObject jObject = new JSONObject(jsonString);
			Branch branch = new Branch();
			branch.setBranchName(jObject.getString("name"));
			branch.setBranchAddress(jObject.getString("address"));
			Integer id = brDao.addBranchWithID(branch);
			branch.setBranchId(id);
			List<Integer> bookIds = convertJsonArryToIntList(jObject.getJSONArray("bookIds"));
			List<Book> bList = readObjectsByPk(bDao, "readBookByPK", bookIds);
			for (Book book : bList) {
				BookCopies bookCopies = new BookCopies();
				bookCopies.setBook(book);
				bookCopies.setBranch(branch);
				bcDao.addBookCopies(bookCopies);
			}
			message = "Operation Successful";
		} catch (InstantiationException | IllegalAccessException | ClassNotFoundException | SQLException | JSONException e) {
			message = "Operation failed";
		}
		return message;
	}

	// Jackson dependency converts JSON applications to Java objects and vice versa
	@RequestMapping(value = "/branches", method = RequestMethod.PUT, consumes = "application/json", produces = "application/json")
	@Transactional
	public String updateBranch(@RequestBody String jsonString) {
		String message = "";
		try {
			JSONObject jObject = new JSONObject(jsonString);
			Branch branch = brDao.readBranchByPK(jObject.getInt("branchId"));
			setObjectsByBranchId(branch); // complete setup process
			cDao.deleteBranchBooks(branch.getBook(), branch); // remove from database tables
			branch.setBranchName(jObject.getString("name"));
			branch.setBranchAddress(jObject.getString("address"));
			brDao.updateBranch(branch); // update the database table
			List<Integer> bookIds = convertJsonArryToIntList(jObject.getJSONArray("bookIds"));
			List<Book> bList = readObjectsByPk(bDao, "readBookByPK", bookIds);
			for (Book book : bList) {
				BookCopies bookCopies = new BookCopies();
				bookCopies.setBook(book);
				bookCopies.setBranch(branch);
				bcDao.addBookCopies(bookCopies);
			}
			message = "Operation Successful";
		} catch (InstantiationException | IllegalAccessException | ClassNotFoundException | SQLException | JSONException e) {
			message = "Operation failed";
		}
		return message;
	}

	// Jackson dependency converts JSON applications to Java objects and vice versa
	@RequestMapping(value = "/branches/{branchId}", method = RequestMethod.DELETE, consumes = "application/json", produces = "application/json")
	public String deleteBranch(@PathVariable Integer branchId) {
		String message = "";
		try {
			Branch branch = brDao.readBranchByPK(branchId);
			brDao.deleteBranch(branch);
			message = "Operation Successful";
		} catch (InstantiationException | IllegalAccessException | ClassNotFoundException | SQLException e) {
			message = "Operation failed";
		}
		return message;
	}

	// Jackson dependency converts JSON applications to Java objects and vice versa
	@RequestMapping(value = "/branches/page", method = RequestMethod.GET, produces = "application/json")
	public List<Branch> readBranchsLimitSearch(@RequestParam("offset") Integer offset,
			@RequestParam("limit") Integer limit) {
		try {
			List<Branch> branch = brDao.readAllBranchesWithLimit(offset, limit);
			for (Branch branch2 : branch) {
				setObjectsByBranchId(branch2); // update attributes of branch objects
			}
			return branch;
		} catch (InstantiationException | IllegalAccessException | ClassNotFoundException | SQLException e) {
			return null;
		}
	}

	// Jackson dependency converts JSON applications to Java objects and vice versa
	@RequestMapping(value = "/branches/count", method = RequestMethod.GET, produces = "application/json")
	public Integer getBranchsCount() {
		try {
			return brDao.getBranchesCount();
		} catch (InstantiationException | IllegalAccessException | ClassNotFoundException | SQLException e) {
			return null;
		}
	}
}
