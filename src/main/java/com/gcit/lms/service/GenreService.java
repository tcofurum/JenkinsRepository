/*
 *
 *3:03:48 AM
 *Jan 1, 2018
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

import com.gcit.lms.dao.BookDAO;
import com.gcit.lms.dao.ConnectDAO;
import com.gcit.lms.dao.GenreDAO;
import com.gcit.lms.entity.Book;
import com.gcit.lms.entity.Genre;

/**
 * @author ThankGod4Life
 * @date Jan 1, 2018
 *
 */

@RestController
public class GenreService extends BaseService {

	@Autowired
	GenreDAO gDao;

	@Autowired
	BookDAO bDao;

	@Autowired
	ConnectDAO cDao;

	// Jackson dependency converts JSON applications to Java objects and vice versa
	@RequestMapping(value = "/genres", method = RequestMethod.GET, produces = "application/json")
	public List<Genre> readAllGenres() {
		try {
			List<Genre> genre = gDao.readAllGenres();
			for (Genre genre2 : genre) {
				genre2.setBook(setBooksByGenreId(genre2.get_Genre_Id()));
			}
			return genre;
		} catch (InstantiationException | IllegalAccessException | ClassNotFoundException | SQLException e) {
			return null;
		}
	}

	private List<Book> setBooksByGenreId(Integer genreId) {
		try {
			String sql = "SELECT * FROM tbl_book WHERE bookId IN (SELECT bookId FROM tbl_book_genres WHERE genre_id = ?)";
			return bDao.readAllBooksByOtherID(sql, genreId);
		} catch (InstantiationException | IllegalAccessException | ClassNotFoundException | SQLException e) {
			return null;
		}
	}

	// Jackson dependency converts JSON applications to Java objects and vice versa
	@RequestMapping(value = "/genres/name", method = RequestMethod.GET, produces = "application/json")
	public List<Genre> readGenresByName(@RequestParam("name") String genreName) {
		try {
			// no transaction taking place, just reading
			List<Genre> genre = gDao.readGenresByName(genreName);
			for (Genre genre2 : genre) {
				genre2.setBook(setBooksByGenreId(genre2.get_Genre_Id()));
			}
			return genre;
		} catch (InstantiationException | IllegalAccessException | ClassNotFoundException | SQLException e) {
			return null;
		}
	}

	// Jackson dependency converts JSON applications to Java objects and vice versa
	@RequestMapping(value = "/genres/page", method = RequestMethod.GET, produces = "application/json")
	public List<Genre> readGenresLimitSearch(@RequestParam("offset") Integer offset,
			@RequestParam("limit") Integer limit) {
		try {
			List<Genre> genre = gDao.readAllGenresWithLimit(offset, limit);
			for (Genre genre2 : genre) {
				genre2.setBook(setBooksByGenreId(genre2.get_Genre_Id()));
			}
			return genre;
		} catch (InstantiationException | IllegalAccessException | ClassNotFoundException | SQLException e) {
			return null;
		}
	}

	// Jackson dependency converts JSON applications to Java objects and vice versa
	@RequestMapping(value = "/genres/count", method = RequestMethod.GET, produces = "application/json")
	public Integer getGenresCount() {
		try {
			return gDao.getGenresCount();
		} catch (InstantiationException | IllegalAccessException | ClassNotFoundException | SQLException e) {
			return null;
		}
	}

	// Jackson dependency converts JSON applications to Java objects and vice versa
	@RequestMapping(value = "/genres/{genreId}", method = RequestMethod.GET, produces = "application/json")
	public Genre readGenreByPK(@PathVariable Integer genreId) {
		try {
			Genre genre = gDao.readGenreByPK(genreId);
			genre.setBook(setBooksByGenreId(genre.get_Genre_Id()));
			return genre;
		} catch (InstantiationException | IllegalAccessException | ClassNotFoundException | SQLException e) {
			return null;
		}
	}

	// Jackson dependency converts JSON applications to Java objects and vice versa
	@RequestMapping(value = "/genres", method = RequestMethod.POST, consumes = "application/json", produces = "application/json")
	@Transactional
	public Integer addGenreWithID(@RequestBody String jsonString) {
		try {
			JSONObject jObject = new JSONObject(jsonString);
			Genre genre = new Genre();
			genre.set_Genre_Name(jObject.getString("name"));
			Integer id = gDao.addGenreWithID(genre);
			genre.set_Genre_Id(id);
			List<Integer> bookIds = convertJsonArryToIntList(jObject.getJSONArray("bookIds"));
			List<Book> bList = readObjectsByPk(bDao, "readBookByPK", bookIds);
			List<Genre> aList = new ArrayList<>();
			aList.add(genre);
			for (Book book : bList) {
				book.setGenres(aList);
				bDao.addBookGenres(book); // update table with new genre Id and each book Id
			}
			return id;
		} catch (InstantiationException | IllegalAccessException | ClassNotFoundException | SQLException
				| JSONException e) {
			return null;
		}
	}

	// Jackson dependency converts JSON applications to Java objects and vice versa
	@RequestMapping(value = "/genres", method = RequestMethod.PUT, consumes = "application/json", produces = "application/json")
	@Transactional
	public String updateGenre(@RequestBody String jsonString) {
		String message = "";
		try {
			JSONObject jObject = new JSONObject(jsonString);
			Genre genre = gDao.readGenreByPK(jObject.getInt("id"));
			List<Book> bList = setBooksByGenreId(jObject.getInt("id")); // get currently related books
			List<Genre> aList = new ArrayList<>();
			aList.add(genre);
			cDao.deleteBookGenres(bList, aList); // update book_genre table for related books
			genre.set_Genre_Name(jObject.getString("name"));
			gDao.updateGenre(genre);
			List<Integer> bookIds = convertJsonArryToIntList(jObject.getJSONArray("bookIds"));
			List<Book> bList2 = readObjectsByPk(bDao, "readBookByPK", bookIds);
			for (Book book : bList2) {
				book.setGenres(aList);
				bDao.addBookGenres(book); // update table with new book Ids
			}
			message = "Operation Successful";
		} catch (InstantiationException | IllegalAccessException | ClassNotFoundException | SQLException | JSONException e) {
			message = "Operation failed";
		}
		return message;
	}

	// Jackson dependency converts JSON applications to Java objects and vice versa
	@RequestMapping(value = "/genres/{genre_id}", method = RequestMethod.DELETE, consumes = "application/json", produces = "application/json")
	public String deleteGenre(@PathVariable Integer genre_id) {
		String message = "";
		try {
			Genre genre = gDao.readGenreByPK(genre_id);
			gDao.deleteGenre(genre);
			message = "Operation Successful";
		} catch (InstantiationException | IllegalAccessException | ClassNotFoundException | SQLException e) {
			message = "Operation failed";
		}
		return message;
	}
}