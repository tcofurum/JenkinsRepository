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
import com.mysql.cj.api.jdbc.Statement;

public class AuthorDAO extends BaseDAO<Author> implements ResultSetExtractor<List<Author>> {

	public Integer addAuthorWithID(final Author author)
			throws SQLException, InstantiationException, IllegalAccessException, ClassNotFoundException {
		KeyHolder holder = new GeneratedKeyHolder();
		final String sql = "INSERT INTO tbl_author (authorName) VALUES (?)";
		jdbcTemplate.update(new PreparedStatementCreator() {

			@Override
			public PreparedStatement createPreparedStatement(Connection connection) throws SQLException {
				PreparedStatement ps = connection.prepareStatement(sql.toString(), Statement.RETURN_GENERATED_KEYS);
				ps.setString(1, author.getAuthorName());
				return ps;
			}
		}, holder);
		return holder.getKey().intValue();
	}

	public Integer getAuthorsCount()
			throws InstantiationException, IllegalAccessException, ClassNotFoundException, SQLException {
		System.out.println(jdbcTemplate);
		return jdbcTemplate.queryForObject("SELECT COUNT(*) AS COUNT FROM tbl_author", Integer.class);
	}

	public void updateAuthor(Author author)
			throws SQLException, InstantiationException, IllegalAccessException, ClassNotFoundException {
		jdbcTemplate.update("UPDATE tbl_author SET authorName =? WHERE authorId = ?",
				new Object[] { author.getAuthorName(), author.getAuthorId() });
	}

	public void deleteAuthor(Author author)
			throws SQLException, InstantiationException, IllegalAccessException, ClassNotFoundException {
		jdbcTemplate.update("DELETE FROM tbl_author WHERE authorId = ?", new Object[] { author.getAuthorId() });
	}

	public List<Author> readAllAuthors()
			throws InstantiationException, IllegalAccessException, ClassNotFoundException, SQLException {
		return jdbcTemplate.query("SELECT * FROM tbl_author", this); // this refer to result-set extractor class of
																		// extractData()
	}

	public List<Author> readAllAuthorsByBookID(Integer bookId)
			throws InstantiationException, IllegalAccessException, ClassNotFoundException, SQLException {
		return jdbcTemplate.query(
				"SELECT * FROM tbl_author WHERE authorId IN (SELECT authorId FROM tbl_book_authors WHERE bookId = ?)",
				new Object[] { bookId }, this);
	}

	public List<Author> readAllAuthorsWithLimit(Integer pageNo, Integer pageSize)
			throws InstantiationException, IllegalAccessException, ClassNotFoundException, SQLException {
		setPageNo(pageNo);
		setPageSize(pageSize);
		return jdbcTemplate.query(getLimit("SELECT * FROM tbl_author"), this);
	}

	public List<Author> readAuthorsByName(String authorName)
			throws InstantiationException, IllegalAccessException, ClassNotFoundException, SQLException {
		authorName = "%" + authorName + "%";
		return jdbcTemplate.query("SELECT * FROM tbl_author WHERE authorName LIKE ?", new Object[] { authorName },
				this);
	}

	public Author readAuthorsByPK(Integer authorId)
			throws InstantiationException, IllegalAccessException, ClassNotFoundException, SQLException {
		List<Author> authors = jdbcTemplate.query("SELECT * FROM tbl_author WHERE authorId  = ?",
				new Object[] { authorId }, this);
		if (authors != null) {
			return authors.get(0);
		} else {
			return null;
		}
	}

	@Override
	public List<Author> extractData(ResultSet rs) throws SQLException {
		List<Author> authors = new ArrayList<>();
		while (rs.next()) {
			Author a = new Author();
			a.setAuthorId(rs.getInt("authorId"));
			a.setAuthorName(rs.getString("authorName"));
			
			authors.add(a);
		}
		return authors;
	}

}
