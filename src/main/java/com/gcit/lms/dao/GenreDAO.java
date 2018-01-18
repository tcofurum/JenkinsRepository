/*
 *
 *2:32:13 PM
 *Dec 21, 2017
 */
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

import com.gcit.lms.entity.Genre;
import com.mysql.cj.api.jdbc.Statement;

/**
 * @genre ThankGod4Life
 * @date Dec 21, 2017
 *
 */
public class GenreDAO extends BaseDAO<Genre> implements ResultSetExtractor<List<Genre>> {

	public Integer addGenreWithID(final Genre genre)
			throws SQLException, InstantiationException, IllegalAccessException, ClassNotFoundException {
		KeyHolder holder = new GeneratedKeyHolder();
		final String sql = "INSERT INTO tbl_genre (genre_name) VALUES (?)";
		jdbcTemplate.update(new PreparedStatementCreator() {

			@Override
			public PreparedStatement createPreparedStatement(Connection connection) throws SQLException {
				PreparedStatement ps = connection.prepareStatement(sql.toString(), Statement.RETURN_GENERATED_KEYS);
				ps.setString(1, genre.get_Genre_Name());
				return ps;
			}
		}, holder);
		return holder.getKey().intValue();
	}

	public void updateGenre(Genre genre)
			throws SQLException, InstantiationException, IllegalAccessException, ClassNotFoundException {
		jdbcTemplate.update("UPDATE tbl_genre SET genre_name =? WHERE genre_id = ?",
				new Object[] { genre.get_Genre_Name(), genre.get_Genre_Id() });
	}

	public void deleteGenre(Genre genre)
			throws SQLException, InstantiationException, IllegalAccessException, ClassNotFoundException {
		jdbcTemplate.update("DELETE FROM tbl_genre WHERE genre_id = ?", new Object[] { genre.get_Genre_Id() });
	}

	public List<Genre> readAllGenres()
			throws InstantiationException, IllegalAccessException, ClassNotFoundException, SQLException {
		return jdbcTemplate.query("SELECT * FROM tbl_genre", this);
	}
	
	public List<Genre> readAllGenresByBookID(Integer bookId)
			throws InstantiationException, IllegalAccessException, ClassNotFoundException, SQLException {
		return jdbcTemplate.query(
				"SELECT * FROM tbl_genre WHERE genre_id IN (SELECT genre_id FROM tbl_book_genres WHERE bookId = ?)",
				new Object[] { bookId }, this);
	}
	
	public List<Genre> readAllGenresWithLimit(Integer pageNo, Integer pageSize)
			throws InstantiationException, IllegalAccessException, ClassNotFoundException, SQLException {
		setPageNo(pageNo);
		setPageSize(pageSize);
		return jdbcTemplate.query(getLimit("SELECT * FROM tbl_genre"), this);
	}
	
	public Integer getGenresCount()
			throws InstantiationException, IllegalAccessException, ClassNotFoundException, SQLException {
		return jdbcTemplate.queryForObject("SELECT COUNT(*) AS COUNT FROM tbl_genre", Integer.class);
	}

	public List<Genre> readGenresByName(String genre_name)
			throws InstantiationException, IllegalAccessException, ClassNotFoundException, SQLException {
		genre_name = "%" + genre_name + "%";
		return jdbcTemplate.query("SELECT * FROM tbl_genre WHERE genre_name LIKE ?", new Object[] { genre_name }, this);
	}

	public Genre readGenreByPK(Integer genre_id)
			throws InstantiationException, IllegalAccessException, ClassNotFoundException, SQLException {
		List<Genre> genres = jdbcTemplate.query("SELECT * FROM tbl_genre WHERE genre_id  = ?", new Object[] { genre_id }, this);
		if (genres != null) {
			return genres.get(0);
		} else {
			return null;
		}
	}

	@Override
	public List<Genre> extractData(ResultSet rs) throws SQLException{
		List<Genre> genres = new ArrayList<>();
		while (rs.next()) {
			Genre a = new Genre();
			a.set_Genre_Id(rs.getInt("genre_id"));
			a.set_Genre_Name(rs.getString("genre_name"));
			genres.add(a);
		}
		return genres;
	}

}
