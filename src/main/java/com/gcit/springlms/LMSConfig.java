/*
 *
 *11:04:33 AM
 *Dec 30, 2017
 */
package com.gcit.springlms;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.gcit.lms.dao.AuthorDAO;
import com.gcit.lms.dao.BookCopiesDAO;
import com.gcit.lms.dao.BookDAO;
import com.gcit.lms.dao.BookLoansDAO;
import com.gcit.lms.dao.BorrowerDAO;
import com.gcit.lms.dao.BranchDAO;
import com.gcit.lms.dao.ConnectDAO;
import com.gcit.lms.dao.GenreDAO;
import com.gcit.lms.dao.PublisherDAO;
import com.gcit.lms.service.AuthorService;
import com.gcit.lms.service.BookCopiesService;
import com.gcit.lms.service.BookLoansService;
import com.gcit.lms.service.BookService;
import com.gcit.lms.service.BorrowerService;
import com.gcit.lms.service.BranchService;
import com.gcit.lms.service.GenreService;
import com.gcit.lms.service.PublisherService;

/**
 * @author ThankGod4Life
 * @date Dec 30, 2017
 *
 */
@Configuration
public class LMSConfig {
//	public final String driver = "com.mysql.cj.jdbc.Driver";
//	public final String url = "jdbc:mysql://localhost/library?useSSL=true";
//	public final String username = "root";
//	public final String password = "SoccerFootballer18";
//	
//	@Bean
//	public BasicDataSource dataSource() {
//		BasicDataSource ds = new BasicDataSource();
//		ds.setDriverClassName(driver);
//		ds.setUrl(url);
//		ds.setUsername(username);
//		ds.setPassword(password);
//		
//		return ds;
//	}
//	
//	@Bean
//	public JdbcTemplate jdbcTemplate() {
//		return new JdbcTemplate(dataSource());
//	}
	
	@Bean
	public AuthorDAO adao() {
		return new AuthorDAO();
	}
	
	@Bean
	public BookDAO bdao() {
		return new BookDAO();
	}
	
	@Bean
	public GenreDAO gdao() {
		return new GenreDAO();
	}
	
	@Bean
	public PublisherDAO pdao() {
		return new PublisherDAO();
	}
	
	@Bean
	public BookLoansDAO bldao() {
		return new BookLoansDAO();
	}
	
	@Bean
	public BranchDAO brdao() {
		return new BranchDAO();
	}
	
	@Bean
	public BookCopiesDAO bcdao() {
		return new BookCopiesDAO();
	}
	
	@Bean
	public BorrowerDAO bordao() {
		return new BorrowerDAO();
	}
	
	@Bean
	public ConnectDAO cdao() {
		return new ConnectDAO();
	}
	
	@Bean
	public AuthorService aService() {
		return new AuthorService();
	}
	
	@Bean
	public BookService bService() {
		return new BookService();
	}
	
	@Bean
	public GenreService gService() {
		return new GenreService();
	}
	
	@Bean
	public PublisherService pService() {
		return new PublisherService();
	}
	
	@Bean
	public BranchService brService() {
		return new BranchService();
	}
	
	@Bean
	public BookCopiesService bcService() {
		return new BookCopiesService();
	}
	
	@Bean
	public BookLoansService blService() {
		return new BookLoansService();
	}
	
	@Bean
	public BorrowerService borService() {
		return new BorrowerService();
	}
	
//	@Bean
//	public PlatformTransactionManager txManager() {
//		return new DataSourceTransactionManager(dataSource());
//	}
	
}
