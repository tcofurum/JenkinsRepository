/*
 *
 *10:08:02 PM
 *Dec 21, 2017
 */
package com.gcit.lms.service;

import java.sql.SQLException;
import java.util.ArrayList;
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
import com.gcit.lms.dao.ConnectDAO;
import com.gcit.lms.entity.Author;
import com.gcit.lms.entity.Book;

/**
 * @author ThankGod4Life
 * @date Dec 21, 2017
 *
 */

@RestController
public class AuthorService extends BaseService {
	@Autowired
	AuthorDAO aDao;

	@Autowired
	BookDAO bDao;

	@Autowired
	ConnectDAO cDao;

	// try {
	//
	// } catch (InstantiationException | IllegalAccessException |
	// ClassNotFoundException | SQLException e) {
	// return null;
	// }

	// pageAuthor is the entry point for all things GET related to author. Jackson
	// dependency converts JSON applications to Java objects and vice versa
	@RequestMapping(value = "/authors", method = RequestMethod.GET, produces = "application/json")
	public List<Author> readAllAuthors() {
		try {
			List<Author> author = aDao.readAllAuthors();
			for (Author author2 : author) {
				author2.setBook(setBooksByAuthorId(author2.getAuthorId()));
			}
			return author;
		} catch (InstantiationException | IllegalAccessException | ClassNotFoundException | SQLException e) {
			return null;
		}
	}

	private List<Book> setBooksByAuthorId(Integer authorId) {
		try {
			String sql = "SELECT * FROM tbl_book WHERE bookId IN (SELECT bookId FROM tbl_book_authors WHERE authorId = ?)";
			return bDao.readAllBooksByOtherID(sql, authorId);
		} catch (InstantiationException | IllegalAccessException | ClassNotFoundException | SQLException e) {
			return null;
		}
	}

	// Jackson dependency converts JSON applications to Java objects and vice versa
	@RequestMapping(value = "/authors/name", method = RequestMethod.GET, produces = "application/json")
	public List<Author> readAuthorsByName(@RequestParam("authorName") String authorName) {
		try {
			// no transaction taking place, just reading
			List<Author> author = aDao.readAuthorsByName(authorName);
			for (Author author2 : author) {
				author2.setBook(setBooksByAuthorId(author2.getAuthorId()));
			}
			return author;
		} catch (InstantiationException | IllegalAccessException | ClassNotFoundException | SQLException e) {
			return null;
		}
	}

	// Jackson dependency converts JSON applications to Java objects and vice versa
	@RequestMapping(value = "/authors/page", method = RequestMethod.GET, produces = "application/json")
	public List<Author> readAuthorsLimitSearch(@RequestParam("offset") Integer offset,
			@RequestParam("limit") Integer limit) {
		try {
			List<Author> author = aDao.readAllAuthorsWithLimit(offset, limit);
			for (Author author2 : author) {
				author2.setBook(setBooksByAuthorId(author2.getAuthorId()));
			}
			return author;
		} catch (InstantiationException | IllegalAccessException | ClassNotFoundException | SQLException e) {
			return null;
		}
	}

	// Jackson dependency converts JSON applications to Java objects and vice versa.
	@RequestMapping(value = "/authors/count", method = RequestMethod.GET, produces = "application/json")
	public Integer getAuthorsCount() {
		// HTTP header: X-Total-Count is best practice. Don't know how to do that so
		// /authors/count instead
		try {
			return aDao.getAuthorsCount();
		} catch (InstantiationException | IllegalAccessException | ClassNotFoundException | SQLException e) {
			return null;
		}
	}

	// Jackson dependency converts JSON applications to Java objects and vice versa
	@RequestMapping(value = "/authors/{authorId}", method = RequestMethod.GET, produces = "application/json")
	public Author readAuthorByPK(@PathVariable Integer authorId) {
		try {
			Author author = aDao.readAuthorsByPK(authorId);
			author.setBook(setBooksByAuthorId(author.getAuthorId()));
			return author;
		} catch (InstantiationException | IllegalAccessException | ClassNotFoundException | SQLException e) {
			return null;
		}
	}

	// Jackson dependency converts JSON applications to Java objects and vice versa
	@RequestMapping(value = "/authors", method = RequestMethod.POST, consumes = "application/json", produces = "application/json")
	@Transactional
	public Integer addAuthorWithID(@RequestBody String jsonString) { // can't have multiple request bodies as there's
																		// only one body
		try {
			JSONObject jObject = new JSONObject(jsonString);
			Author author = new Author();
			author.setAuthorName(jObject.getString("name"));
			Integer id = aDao.addAuthorWithID(author);
			author.setAuthorId(id);
			List<Integer> bookIds = convertJsonArryToIntList(jObject.getJSONArray("bookIds"));
			System.out.println(bookIds);
			List<Book> bList = readObjectsByPk(bDao, "readBookByPK", bookIds);
			List<Author> aList = new ArrayList<>();
			aList.add(author);
			for (Book book : bList) {
				book.setAuthors(aList);
				bDao.addBookAuthors(book); // update table with new author Id and each book Id
			}
			return id;
		} catch (InstantiationException | IllegalAccessException | ClassNotFoundException | SQLException
				| JSONException e) {
			return null;
		}
	}

	// Jackson dependency converts JSON applications to Java objects and vice versa
	@RequestMapping(value = "/authors", method = RequestMethod.PUT, consumes = "application/json", produces = "application/json")
	@Transactional
	public String updateAuthor(@RequestBody String jsonString) {
		String message = "";
		try {
			JSONObject jObject = new JSONObject(jsonString);
			Author author = aDao.readAuthorsByPK(jObject.getInt("id"));
			List<Book> bList = setBooksByAuthorId(jObject.getInt("id")); // get currently related books
			List<Author> aList = new ArrayList<>();
			aList.add(author);
			cDao.deleteBookAuthors(bList, aList); // update book_author table for related books
			author.setAuthorName(jObject.getString("name"));
			aDao.updateAuthor(author);
			List<Integer> bookIds = convertJsonArryToIntList(jObject.getJSONArray("bookIds"));
			List<Book> bList2 = readObjectsByPk(bDao, "readBookByPK", bookIds);
			for (Book book : bList2) {
				book.setAuthors(aList);
				bDao.addBookAuthors(book); // update table with new book Ids
			}
			message = "Task completed successfully";
		} catch (InstantiationException | IllegalAccessException | ClassNotFoundException | SQLException
				| JSONException e) {
			message = "Task failed";
		}
		return message;
	}

	// Jackson dependency converts JSON applications to Java objects and vice versa
	@RequestMapping(value = "/authors/{authorId}", method = RequestMethod.DELETE, consumes = "application/json", produces = "application/json")
	public String deleteAuthor(@PathVariable Integer authorId) {
		String message = "";
		try {
			Author author = aDao.readAuthorsByPK(authorId);
			aDao.deleteAuthor(author);
			message = "Task completed successfully";
		} catch (InstantiationException | IllegalAccessException | ClassNotFoundException | SQLException e) {
			message = "Task failed";
		}
		return message;
	}

	// can do request mapping value = "DELETE /authors" for deleting all records
	// from table
	// can do request mapping value = "PUT /authors" for updating all records in
	// table

}
