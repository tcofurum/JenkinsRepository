/*
 *
 *3:25:35 AM
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
import com.gcit.lms.dao.ConnectDAO;
import com.gcit.lms.dao.PublisherDAO;
import com.gcit.lms.entity.Book;
import com.gcit.lms.entity.Publisher;

/**
 * @author ThankGod4Life
 * @date Jan 1, 2018
 *
 */

@RestController
public class PublisherService extends BaseService {

	@Autowired
	PublisherDAO pDao;

	@Autowired
	BookDAO bDao;

	@Autowired
	ConnectDAO cDao;

	// Jackson dependency converts JSON applications to Java objects and vice versa
	@RequestMapping(value = "/publishers", method = RequestMethod.GET, produces = "application/json")
	public List<Publisher> readAllPublishers() {
		try {
			List<Publisher> publisher = pDao.readAllPublishers();
			for (Publisher publisher2 : publisher) {
				publisher2.setBook(setBooksByPublisherId(publisher2.getPublisherId()));
			}
			return publisher;
		} catch (InstantiationException | IllegalAccessException | ClassNotFoundException | SQLException e) {
			return null;
		}
	}

	private List<Book> setBooksByPublisherId(Integer publisherId) {
		try {
			String sql = "SELECT * FROM tbl_book WHERE pubId IN (SELECT publisherId FROM tbl_publisher WHERE publisherId = ?)";
			return bDao.readAllBooksByOtherID(sql, publisherId);
		} catch (InstantiationException | IllegalAccessException | ClassNotFoundException | SQLException e) {
			return null;
		}
	}

	// Jackson dependency converts JSON applications to Java objects and vice versa
	@RequestMapping(value = "/publishers/name", method = RequestMethod.GET, produces = "application/json")
	public List<Publisher> readPublishersByName(@RequestParam("name") String publisherName) {
		try {
			// no transaction taking place, just reading
			List<Publisher> publisher = pDao.readPublishersByName(publisherName);
			for (Publisher publisher2 : publisher) {
				publisher2.setBook(setBooksByPublisherId(publisher2.getPublisherId()));
			}
			return publisher;
		} catch (InstantiationException | IllegalAccessException | ClassNotFoundException | SQLException e) {
			return null;
		}
	}

	// Jackson dependency converts JSON applications to Java objects and vice versa
	@RequestMapping(value = "/publishers/page", method = RequestMethod.GET, produces = "application/json")
	public List<Publisher> readPublishersLimitSearch(@RequestParam("offset") Integer offset,
			@RequestParam("limit") Integer limit) {
		try {
			List<Publisher> publisher = pDao.readAllPublishersWithLimit(offset, limit);
			for (Publisher publisher2 : publisher) {
				publisher2.setBook(setBooksByPublisherId(publisher2.getPublisherId()));
			}
			return publisher;
		} catch (InstantiationException | IllegalAccessException | ClassNotFoundException | SQLException e) {
			return null;
		}
	}

	// Jackson dependency converts JSON applications to Java objects and vice versa
	@RequestMapping(value = "/publishers/count", method = RequestMethod.GET, produces = "application/json")
	public Integer getPublishersCount() {
		try {
			return pDao.getPublishersCount();
		} catch (InstantiationException | IllegalAccessException | ClassNotFoundException | SQLException e) {
			return null;
		}
	}

	// Jackson dependency converts JSON applications to Java objects and vice versa
	@RequestMapping(value = "/publishers/{publisherId}", method = RequestMethod.GET, produces = "application/json")
	public Publisher readPublisherByPK(@PathVariable Integer publisherId) {
		try {
			Publisher publisher = pDao.readPublisherByPK(publisherId);
			publisher.setBook(setBooksByPublisherId(publisher.getPublisherId()));
			return publisher;
		} catch (InstantiationException | IllegalAccessException | ClassNotFoundException | SQLException e) {
			return null;
		}
	}

	// Jackson dependency converts JSON applications to Java objects and vice versa
	@RequestMapping(value = "/publishers", method = RequestMethod.POST, consumes = "application/json", produces = "application/json")
	@Transactional
	public Integer addPublisherWithID(@RequestBody String jsonString) {
		try {
			JSONObject jObject = new JSONObject(jsonString);
			Publisher publisher = new Publisher();
			publisher.setPublisherName(jObject.getString("name"));
			publisher.setPublisherAddress(jObject.getString("address"));
			publisher.setPublisherPhone(jObject.getString("phone"));
			Integer id = pDao.addPublisherWithID(publisher);
			publisher.setPublisherId(id);
			List<Integer> bookIds = convertJsonArryToIntList(jObject.getJSONArray("bookIds"));
			List<Book> bList = readObjectsByPk(bDao, "readBookByPK", bookIds);
			for (Book book : bList) {
				book.setPublisher(publisher);
				bDao.updateBookPublisher(book); // update table with new publisher Id and each book Id
			}
			return id;
		} catch (InstantiationException | IllegalAccessException | ClassNotFoundException | SQLException
				| JSONException e) {
			return null;
		}
	}

	// Jackson dependency converts JSON applications to Java objects and vice versa
	@RequestMapping(value = "/publishers", method = RequestMethod.PUT, consumes = "application/json", produces = "application/json")
	@Transactional
	public String updatePublisher(@RequestBody String jsonString) {
		String message = "";
		try {
			JSONObject jObject = new JSONObject(jsonString);
			Publisher publisher = pDao.readPublisherByPK(jObject.getInt("id"));
			List<Book> bList = setBooksByPublisherId(jObject.getInt("id")); // get currently related books
			cDao.deletePublisherBooks(bList, publisher); // remove link of pubId on table
			publisher.setPublisherName(jObject.getString("name"));
			publisher.setPublisherAddress(jObject.getString("address"));
			publisher.setPublisherPhone(jObject.getString("phone"));
			pDao.updatePublisher(publisher);
			List<Integer> bookIds = convertJsonArryToIntList(jObject.getJSONArray("bookIds"));
			List<Book> bList2 = readObjectsByPk(bDao, "readBookByPK", bookIds);
			for (Book book : bList2) {
				book.setPublisher(publisher);
				bDao.updateBookPublisher(book); // update table with new book Ids
			}
			message = "Operation Successful";
		} catch (InstantiationException | IllegalAccessException | ClassNotFoundException | SQLException
				| JSONException e) {
			message = "Operation failed";
		}
		return message;
	}

	// Jackson dependency converts JSON applications to Java objects and vice versa
	@RequestMapping(value = "/publishers/{publisherId}", method = RequestMethod.DELETE, consumes = "application/json", produces = "application/json")
	public String deletePublisher(@PathVariable Integer publisherId) {
		String message = "";
		try {
			Publisher publisher = pDao.readPublisherByPK(publisherId);
			pDao.deletePublisher(publisher);
			message = "Operation successful";
		} catch (InstantiationException | IllegalAccessException | ClassNotFoundException | SQLException e) {
			message = "Operation failed";
		}
		return message;
	}
}