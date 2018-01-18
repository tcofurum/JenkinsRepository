/*
 *
 *1:47:04 AM
 *Jan 1, 2018
 */
package com.gcit.lms.dao;

import java.sql.SQLException;
import java.util.List;

import com.gcit.lms.entity.Author;
import com.gcit.lms.entity.Book;
import com.gcit.lms.entity.Borrower;
import com.gcit.lms.entity.Branch;
import com.gcit.lms.entity.Genre;
import com.gcit.lms.entity.Publisher;

/**
 * @author ThankGod4Life
 * @date Jan 1, 2018
 *
 */
public class ConnectDAO extends BaseDAO<Author> {

	public void deleteBookAuthors(List<Book> books, List<Author> authors)
			throws SQLException, InstantiationException, IllegalAccessException, ClassNotFoundException {
		for (Book b : books) {
			for (Author a : authors) {
				jdbcTemplate.update("DELETE FROM tbl_book_authors WHERE bookId = ? and authorId = ?",
						new Object[] { b.getBookId(), a.getAuthorId() });
			}
		}
	}

	public void deleteBookGenres(List<Book> books, List<Genre> genres)
			throws SQLException, InstantiationException, IllegalAccessException, ClassNotFoundException {
		for (Book b : books) {
			for (Genre g : genres) {
				jdbcTemplate.update("DELETE FROM tbl_book_genres WHERE genre_id = ? and bookId = ?",
						new Object[] { g.get_Genre_Id(), b.getBookId() });
			}
		}
	}

	public void deletePublisherBooks(List<Book> books, Publisher publisher)
			throws SQLException, InstantiationException, IllegalAccessException, ClassNotFoundException {
		for (Book b : books) {
			jdbcTemplate.update("UPDATE tbl_book SET pubId = null WHERE bookId = ?", new Object[] { b.getBookId() });
		}
	}

	public void deleteBranchBooks(List<Book> books, Branch branch)
			throws SQLException, InstantiationException, IllegalAccessException, ClassNotFoundException {
		for (Book b : books) {
			jdbcTemplate.update("DELETE FROM tbl_book_copies WHERE bookId = ? and branchId = ?",
					new Object[] { b.getBookId(), branch.getBranchId() });
		}
	}

	public void deleteBorrowerBookLoans(List<Book> books, List<Branch> branches, Borrower borrower)
			throws SQLException, InstantiationException, IllegalAccessException, ClassNotFoundException {
		for (Book book : books) {
			for (Branch branch : branches) {
				jdbcTemplate.update("DELETE FROM tbl_book_loans WHERE bookId = ? and branchId = ? and cardNo = ?",
						new Object[] { book.getBookId(), branch.getBranchId(), borrower.getCardNo() });
			}
		}
	}
}
