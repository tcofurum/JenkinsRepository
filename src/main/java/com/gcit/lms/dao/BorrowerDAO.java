/*
 *
 *2:31:13 PM
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

import com.gcit.lms.entity.Borrower;
import com.mysql.cj.api.jdbc.Statement;

/**
 * @borrower ThankGod4Life
 * @date Dec 21, 2017
 *
 */
public class BorrowerDAO extends BaseDAO<Borrower> implements ResultSetExtractor<List<Borrower>>{

	public Integer addBorrowerWithID(final Borrower borrower)
			throws SQLException, InstantiationException, IllegalAccessException, ClassNotFoundException {
		KeyHolder holder = new GeneratedKeyHolder();
		final String sql = "INSERT INTO tbl_borrower (name, address, phone) VALUES (?, ?, ?)";
		jdbcTemplate.update(new PreparedStatementCreator() {

			@Override
			public PreparedStatement createPreparedStatement(Connection connection) throws SQLException {
				PreparedStatement ps = connection.prepareStatement(sql.toString(), Statement.RETURN_GENERATED_KEYS);
				ps.setString(1, borrower.getName());
				ps.setString(2, borrower.getAddress());
				ps.setString(3, borrower.getPhone());
				return ps;
			}
		}, holder);
		return holder.getKey().intValue();
	}

	public void updateBorrower(Borrower borrower)
			throws SQLException, InstantiationException, IllegalAccessException, ClassNotFoundException {
		jdbcTemplate.update("UPDATE tbl_borrower SET name =?, address =?, phone =? WHERE cardNo = ?",
				new Object[] { borrower.getName(), borrower.getAddress(), borrower.getPhone(), borrower.getCardNo() });
	}

	public void deleteBorrower(Borrower borrower)
			throws SQLException, InstantiationException, IllegalAccessException, ClassNotFoundException {
		jdbcTemplate.update("DELETE FROM tbl_borrower WHERE cardNo = ?", new Object[] { borrower.getCardNo() });
	}

	public List<Borrower> readAllBorrowers()
			throws InstantiationException, IllegalAccessException, ClassNotFoundException, SQLException {
		return jdbcTemplate.query("SELECT * FROM tbl_borrower", this);
	}
	
	public List<Borrower> readAllBorrowersByOtherID(String sql, Integer Id)
			throws InstantiationException, IllegalAccessException, ClassNotFoundException, SQLException {
		return jdbcTemplate.query(sql, new Object[] { Id }, this);
	}
	
	public List<Borrower> readAllBorrowersWithLimit(Integer pageNo, Integer pageSize)
			throws InstantiationException, IllegalAccessException, ClassNotFoundException, SQLException {
		setPageNo(pageNo);
		setPageSize(pageSize);
		return jdbcTemplate.query(getLimit("SELECT * FROM tbl_borrower"), this);
	}
	
	public Integer getBorrowersCount()
			throws InstantiationException, IllegalAccessException, ClassNotFoundException, SQLException {
		return jdbcTemplate.queryForObject("SELECT COUNT(*) AS COUNT FROM tbl_borrower", Integer.class);
	}

	public List<Borrower> readBorrowersByName(String name)
			throws InstantiationException, IllegalAccessException, ClassNotFoundException, SQLException {
		name = "%" + name + "%";
		return jdbcTemplate.query("SELECT * FROM tbl_borrower WHERE name LIKE ?", new Object[] { name }, this);
	}

	public Borrower readBorrowerByPK(Integer cardNo)
			throws InstantiationException, IllegalAccessException, ClassNotFoundException, SQLException {
		List<Borrower> borrowers = jdbcTemplate.query("SELECT * FROM tbl_borrower WHERE cardNo  = ?", new Object[] { cardNo }, this);
		if (borrowers != null) {
			return borrowers.get(0);
		} else {
			return null;
		}
	}

	@Override
	public List<Borrower> extractData(ResultSet rs)
			throws SQLException{
		List<Borrower> borrowers = new ArrayList<>();
		while (rs.next()) {
			Borrower a = new Borrower();
			a.setCardNo(rs.getInt("cardNo"));
			a.setName(rs.getString("name"));
			a.setAddress(rs.getString("address"));
			a.setPhone(rs.getString("phone"));
//			a.setBook(bDao.readFirstLevel(
//					"SELECT * FROM tbl_book WHERE bookId IN (SELECT bookId FROM tbl_book_loans WHERE cardNo = ?)",
//					new Object[] { a.getCardNo() }));
//			a.setBranch(brDao.readFirstLevel(
//					"SELECT * FROM tbl_library_branch WHERE branchId IN (SELECT branchId FROM tbl_book_loans WHERE cardNo = ?)",
//					new Object[] { a.getCardNo() }));
			borrowers.add(a);
		}
		return borrowers;
	}

}
