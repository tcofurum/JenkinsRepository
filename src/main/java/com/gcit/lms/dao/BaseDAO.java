package com.gcit.lms.dao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;

public abstract class BaseDAO<T> {

//	@Autowired
//	private LMSConfig lmsconfig;
	
	@Autowired
	public JdbcTemplate jdbcTemplate;
	
	private Integer pageNo = 0;
	private Integer pageSize = 5;
	
	/**
	 * @return the pageNo
	 */
	public Integer getPageNo() {
		return pageNo;
	}

	/**
	 * @param pageNo the pageNo to set
	 */
	public void setPageNo(Integer pageNo) {
		this.pageNo = pageNo;
	}

	/**
	 * @return the pageSize
	 */
	public Integer getPageSize() {
		return pageSize;
	}

	/**
	 * @param pageSize the pageSize to set
	 */
	public void setPageSize(Integer pageSize) {
		this.pageSize = pageSize;
	}
	// limit of sql object needed
	public String getLimit(String sql) {
		if(getPageNo() > 0){
			sql+= " LIMIT "+(getPageNo()-1)*getPageSize()+" ,"+getPageSize();
		}
		return sql;
	}

}
