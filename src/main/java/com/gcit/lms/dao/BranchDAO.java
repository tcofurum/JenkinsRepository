/*
 *
 *2:31:45 PM
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

import com.gcit.lms.entity.Branch;
import com.mysql.cj.api.jdbc.Statement;

/**
 * @author ThankGod4Life
 * @date Dec 21, 2017
 *
 */
public class BranchDAO extends BaseDAO<Branch> implements ResultSetExtractor<List<Branch>>{

	public Integer addBranchWithID(final Branch branch)
			throws SQLException, InstantiationException, IllegalAccessException, ClassNotFoundException {
		KeyHolder holder = new GeneratedKeyHolder();
		final String sql = "INSERT INTO tbl_library_branch (branchName, branchAddress) VALUES (?, ?)";
		jdbcTemplate.update(new PreparedStatementCreator() {

			@Override
			public PreparedStatement createPreparedStatement(Connection connection) throws SQLException {
				PreparedStatement ps = connection.prepareStatement(sql.toString(), Statement.RETURN_GENERATED_KEYS);
				ps.setString(1, branch.getBranchName());
				ps.setString(2, branch.getBranchAddress());
				return ps;
			}
		}, holder);
		return holder.getKey().intValue();
	}

	public void updateBranch(Branch branch)
			throws SQLException, InstantiationException, IllegalAccessException, ClassNotFoundException {
		jdbcTemplate.update("UPDATE tbl_library_branch SET branchName =?, branchAddress =? WHERE branchId = ?",
				new Object[] { branch.getBranchName(), branch.getBranchAddress(), branch.getBranchId() });
	}

	public void deleteBranch(Branch branch)
			throws SQLException, InstantiationException, IllegalAccessException, ClassNotFoundException {
		jdbcTemplate.update("DELETE FROM tbl_library_branch WHERE branchId = ?", new Object[] { branch.getBranchId() });
	}

	public List<Branch> readAllBranchs()
			throws InstantiationException, IllegalAccessException, ClassNotFoundException, SQLException {
		return jdbcTemplate.query("SELECT * FROM tbl_library_branch", this);
	}
	
	public List<Branch> readAllBranchByOtherID(String sql, Integer Id)
			throws InstantiationException, IllegalAccessException, ClassNotFoundException, SQLException {
		return jdbcTemplate.query(sql, new Object[] { Id }, this);
	}

	public List<Branch> readBranchsByName(String branchName)
			throws InstantiationException, IllegalAccessException, ClassNotFoundException, SQLException {
		branchName = "%" + branchName + "%";
		return jdbcTemplate.query("SELECT * FROM tbl_library_branch WHERE branchName LIKE ?", new Object[] { branchName }, this);
	}
	
	public List<Branch> readAllBranchesWithLimit(Integer pageNo, Integer pageSize)
			throws InstantiationException, IllegalAccessException, ClassNotFoundException, SQLException {
		setPageNo(pageNo);
		setPageSize(pageSize);
		return jdbcTemplate.query(getLimit("SELECT * FROM tbl_library_branch"), this);
	}
	
	public Integer getBranchesCount()
			throws InstantiationException, IllegalAccessException, ClassNotFoundException, SQLException {
		return jdbcTemplate.queryForObject("SELECT COUNT(*) AS COUNT FROM tbl_library_branch", Integer.class);
	}

	public Branch readBranchByPK(Integer branchId)
			throws InstantiationException, IllegalAccessException, ClassNotFoundException, SQLException {
		List<Branch> branchs = jdbcTemplate.query("SELECT * FROM tbl_library_branch WHERE branchId  = ?", new Object[] { branchId }, this);
		if (branchs != null) {
			return branchs.get(0);
		} else {
			return null;
		}
	}

	@Override
	public List<Branch> extractData(ResultSet rs)
			throws SQLException{
		List<Branch> branchs = new ArrayList<>();
		while (rs.next()) {
			Branch a = new Branch();
			a.setBranchId(rs.getInt("branchId"));
			a.setBranchName(rs.getString("branchName"));
			a.setBranchAddress(rs.getString("branchAddress"));
			branchs.add(a);
		}
		return branchs;
	}

}
