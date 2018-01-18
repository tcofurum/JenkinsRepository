/*
 *
 *3:05:34 PM
 *Dec 31, 2017
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

import com.gcit.lms.dao.AuthorDAO;
import com.gcit.lms.dao.BookDAO;
import com.gcit.lms.dao.GenreDAO;
import com.gcit.lms.dao.PublisherDAO;

import com.gcit.lms.entity.Author;
import com.gcit.lms.entity.Book;
import com.gcit.lms.entity.Genre;

/**
 * @author ThankGod4Life
 * @date Dec 31, 2017
 *
 */

@RestController
public class BookService extends BaseService {
	@Autowired
	BookDAO bDao;

	@Autowired
	AuthorDAO aDao;

	@Autowired
	GenreDAO gDao;

	@Autowired
	PublisherDAO pDao;

	// Jackson dependency converts JSON applications to Java objects and vice versa
	@RequestMapping(value = "/books", method = RequestMethod.GET, produces = "application/json")
	public List<Book> readAllBooks() {
		try {
			List<Book> book = bDao.readAllBooks();
			for (Book book2 : book) {
				setObjectsByBookId(book2); // update attributes of book objects
			}
			return book;
		} catch (InstantiationException | IllegalAccessException | ClassNotFoundException | SQLException e) {
			return null;
		}
	}

	private void setObjectsByBookId(Book book) {
		try {
			book.setAuthors(aDao.readAllAuthorsByBookID(book.getBookId()));
			book.setGenres(gDao.readAllGenresByBookID(book.getBookId()));
		} catch (InstantiationException | IllegalAccessException | ClassNotFoundException | SQLException e) {
		}
	}

	// Jackson dependency converts JSON applications to Java objects and vice versa
	@RequestMapping(value = "/books/name", method = RequestMethod.GET, produces = "application/json")
	public List<Book> readBooksByTitle(@RequestParam("title") String bookName) {
		try {
			List<Book> book = bDao.readBooksByTitle(bookName);
			for (Book book2 : book) {
				setObjectsByBookId(book2); // update attributes of book objects
			}
			return book;
		} catch (InstantiationException | IllegalAccessException | ClassNotFoundException | SQLException e) {
			return null;
		}
	}

	// Jackson dependency converts JSON applications to Java objects and vice versa
	@RequestMapping(value = "/books/{bookId}", method = RequestMethod.GET, produces = "application/json")
	public Book readBookByPK(@PathVariable Integer bookId) {
		try {
			Book book = bDao.readBookByPK(bookId);
			setObjectsByBookId(book);
			return book;
		} catch (InstantiationException | IllegalAccessException | ClassNotFoundException | SQLException e) {
			return null;
		}
	}

	// Jackson dependency converts JSON applications to Java objects and vice versa
	@RequestMapping(value = "/books", method = RequestMethod.POST, consumes = "application/json", produces = "application/json")
	@Transactional
	public String addBookWithID(@RequestBody String jsonString) {
		String message = "";
		try {
			JSONObject jObject = new JSONObject(jsonString);
			// must have title, publisherId, list of authorIds, and list of genreIds
			Book book = new Book();
			book.setTitle(jObject.getString("title"));
			book.setPublisher(pDao.readPublisherByPK(jObject.getInt("publisherId")));
			List<Integer> authorIds = convertJsonArryToIntList(jObject.getJSONArray("authorIds"));
			List<Integer> genreIds = convertJsonArryToIntList(jObject.getJSONArray("genreIds"));
			List<Author> aList = readObjectsByPk(aDao, "readAuthorsByPK", authorIds);
			List<Genre> gList = readObjectsByPk(gDao, "readGenreByPK", genreIds);
			book.setAuthors(aList);
			book.setGenres(gList);
			Integer id = bDao.addBookWithID(book);
			book.setBookId(id);
			bDao.addBookAuthors(book);
			bDao.addBookGenres(book);
			message = "Operation successful";
		} catch (InstantiationException | IllegalAccessException | ClassNotFoundException | SQLException | JSONException e) {
			message = "Operation failed";
		}
		return message;
	}

	// Jackson dependency converts JSON applications to Java objects and vice versa
	@RequestMapping(value = "/books", method = RequestMethod.PUT, consumes = "application/json", produces = "application/json")
	@Transactional
	public String updateBook(@RequestBody String jsonString) {
		String message = "";
		try {
			JSONObject jObject = new JSONObject(jsonString);
			Book book = bDao.readBookByPK(jObject.getInt("bookId"));
			setObjectsByBookId(book); // complete setup process
			bDao.deleteBookAuthors(book); // remove from database tables
			bDao.deleteBookGenres(book);
			book.setTitle(jObject.getString("title"));
			bDao.updateBookTitle(book); // update the database table for title
			book.setPublisher(pDao.readPublisherByPK(jObject.getInt("publisherId")));
			bDao.updateBookPublisher(book); // update the database table for pub id
			List<Integer> authorIds = convertJsonArryToIntList(jObject.getJSONArray("authorIds"));
			List<Integer> genreIds = convertJsonArryToIntList(jObject.getJSONArray("genreIds"));
			List<Author> aList = readObjectsByPk(aDao, "readAuthorsByPK", authorIds);
			List<Genre> gList = readObjectsByPk(gDao, "readGenreByPK", genreIds);
			book.setAuthors(aList); // now set the new list of authors and genres
			book.setGenres(gList);
			bDao.addBookAuthors(book); // update the tables of the database
			bDao.addBookGenres(book);
			message = "Operation Successful";
		} catch (InstantiationException | IllegalAccessException | ClassNotFoundException | SQLException | JSONException e) {
			message = "Operation failed";
		}
		return message;
	}

	// Jackson dependency converts JSON applications to Java objects and vice versa
	@RequestMapping(value = "/books/{bookId}", method = RequestMethod.DELETE, consumes = "application/json", produces = "application/json")
	public String deleteBook(@PathVariable Integer bookId) {
		String message = "";
		try {
			Book book = bDao.readBookByPK(bookId);
			bDao.deleteBook(book);
			message = "Operation Successful";
		} catch (InstantiationException | IllegalAccessException | ClassNotFoundException | SQLException e) {
			message = "Operation failed";
		}
		return message;
	}

	// Jackson dependency converts JSON applications to Java objects and vice versa
	@RequestMapping(value = "/books/page", method = RequestMethod.GET, produces = "application/json")
	public List<Book> readBooksLimitSearch(@RequestParam("offset") Integer offset,
			@RequestParam("limit") Integer limit) {
		try {
			List<Book> book = bDao.readAllBooksWithLimit(offset, limit);
			for (Book book2 : book) {
				setObjectsByBookId(book2); // update attributes of book objects
			}
			return book;
		} catch (InstantiationException | IllegalAccessException | ClassNotFoundException | SQLException e) {
			return null;
		}
	}

	// Jackson dependency converts JSON applications to Java objects and vice versa
	@RequestMapping(value = "/books/count", method = RequestMethod.GET, produces = "application/json")
	public Integer getBooksCount() {
		try {
			return bDao.getBooksCount();
		} catch (InstantiationException | IllegalAccessException | ClassNotFoundException | SQLException e) {
			return null;
		}
	}

}
