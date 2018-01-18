package com.gcit.lms.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;

import com.gcit.lms.entity.Author;
import com.gcit.lms.entity.Book;
import com.gcit.lms.entity.Genre;
import com.gcit.lms.entity.Publisher;
import com.mysql.cj.api.jdbc.Statement;

public class BookDAO extends BaseDAO<Book> implements ResultSetExtractor<List<Book>> {

	// adding book and publisher
	public Integer addBookWithID(final Book book)
			throws SQLException, InstantiationException, IllegalAccessException, ClassNotFoundException {
		KeyHolder holder = new GeneratedKeyHolder();
		final String sql = "INSERT INTO tbl_book (title, pubId) VALUES (?, ?)";
		jdbcTemplate.update(new PreparedStatementCreator() {

			@Override
			public PreparedStatement createPreparedStatement(Connection connection) throws SQLException {
				PreparedStatement ps = connection.prepareStatement(sql.toString(), Statement.RETURN_GENERATED_KEYS);
				ps.setString(1, book.getTitle());
				ps.setInt(2, book.getPublisher().getPublisherId());
				return ps;
			}
		}, holder);
		return holder.getKey().intValue();
	}

	public void addBookAuthors(Book book)
			throws SQLException, InstantiationException, IllegalAccessException, ClassNotFoundException {
		for (Author a : book.getAuthors()) {
			jdbcTemplate.update("INSERT INTO tbl_book_authors VALUES (?, ?)",
					new Object[] { book.getBookId(), a.getAuthorId() });
		}
	}

	public void addBookGenres(Book book)
			throws SQLException, InstantiationException, IllegalAccessException, ClassNotFoundException {
		for (Genre a : book.getGenres()) {
			jdbcTemplate.update("INSERT INTO tbl_book_genres VALUES (?, ?)",
					new Object[] { a.get_Genre_Id(), book.getBookId() });
		}
	}

	// can have author and genre and publisher change names and addresses but to
	// change id can't happen after addition except deletion
	public void updateBookTitle(Book book)
			throws SQLException, InstantiationException, IllegalAccessException, ClassNotFoundException {
		jdbcTemplate.update("UPDATE tbl_book SET title =? WHERE bookId = ?",
				new Object[] { book.getTitle(), book.getBookId() });
	}

	public void updateBookPublisher(Book book)
			throws SQLException, InstantiationException, IllegalAccessException, ClassNotFoundException {
		jdbcTemplate.update("UPDATE tbl_book SET pubId =? WHERE bookId = ?",
				new Object[] { book.getPublisher().getPublisherId(), book.getBookId() });
	}

	public void deleteBook(Book book)
			throws SQLException, InstantiationException, IllegalAccessException, ClassNotFoundException {
		jdbcTemplate.update("DELETE FROM tbl_book WHERE bookId = ?", new Object[] { book.getBookId() });
	}

	public void deleteBookAuthors(Book book)
			throws SQLException, InstantiationException, IllegalAccessException, ClassNotFoundException {
		for (Author a : book.getAuthors()) {
			jdbcTemplate.update("DELETE FROM tbl_book_authors WHERE bookId = ? and authorId = ?",
					new Object[] { book.getBookId(), a.getAuthorId() });
		}
	}

	public void deleteBookGenres(Book book)
			throws SQLException, InstantiationException, IllegalAccessException, ClassNotFoundException {
		for (Genre g : book.getGenres()) {
			jdbcTemplate.update("DELETE FROM tbl_book_genres WHERE genre_id = ? and bookId = ?",
					new Object[] { g.get_Genre_Id(), book.getBookId() });
		}
	}

	public List<Book> readAllBooks()
			throws InstantiationException, IllegalAccessException, ClassNotFoundException, SQLException {
		return jdbcTemplate.query("SELECT * FROM tbl_book", this);
	}

	public List<Book> readAllBooksByOtherID(String sql, Integer Id)
			throws InstantiationException, IllegalAccessException, ClassNotFoundException, SQLException {
		return jdbcTemplate.query(sql, new Object[] { Id }, this);
	}

	public Integer getBooksCount()
			throws InstantiationException, IllegalAccessException, ClassNotFoundException, SQLException {
		return jdbcTemplate.queryForObject("SELECT COUNT(*) AS COUNT FROM tbl_book", Integer.class);
	}

	public List<Book> readAllBooksWithLimit(Integer pageNo, Integer pageSize)
			throws InstantiationException, IllegalAccessException, ClassNotFoundException, SQLException {
		setPageNo(pageNo);
		setPageSize(pageSize);
		return jdbcTemplate.query(getLimit("SELECT * FROM tbl_book"), this);
	}

	public List<Book> readBooksByTitle(String title)
			throws InstantiationException, IllegalAccessException, ClassNotFoundException, SQLException {
		title = "%" + title + "%";
		return jdbcTemplate.query("SELECT * FROM tbl_book WHERE title LIKE ?", new Object[] { title }, this);
	}

	public Book readBookByPK(Integer bookId)
			throws InstantiationException, IllegalAccessException, ClassNotFoundException, SQLException {
		List<Book> books = jdbcTemplate.query("SELECT * FROM tbl_book WHERE bookId  = ?", new Object[] { bookId },
				this);
		if (books != null) {
			return books.get(0);
		} else {
			return null;
		}
	}

	@Override
	public List<Book> extractData(ResultSet rs) throws SQLException {
		List<Book> books = new ArrayList<>();
		Publisher publisher = new Publisher();
		while (rs.next()) {
			Book b = new Book();
			b.setBookId(rs.getInt("bookId"));
			b.setTitle(rs.getString("title"));
			publisher.setPublisherId(rs.getInt("pubId"));
			b.setPublisher(publisher);
			books.add(b);
		}
		return books;
	}

}
