package com.gcit.lms.entity;

import java.io.Serializable;
import java.util.List;

public class Branch implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -5152875690037128272L;
	private Integer branchId;
	private String branchName = null;
	private String branchAddress = null;
	private List<Borrower> borrower;
	private List<Book> book;
	private List<BookLoans> bookLoans;
	private List<BookCopies> bookCopies;

	/**
	 * @return the branchId
	 */
	public Integer getBranchId() {
		return branchId;
	}

	/**
	 * @param branchId
	 *            the branchId to set
	 */
	public void setBranchId(Integer branchId) {
		this.branchId = branchId;
	}

	/**
	 * @return the branchName
	 */
	public String getBranchName() {
		return branchName;
	}

	/**
	 * @param branchName
	 *            the branchName to set
	 */
	public void setBranchName(String branchName) {
		this.branchName = branchName;
	}

	/**
	 * @return the branchAddress
	 */
	public String getBranchAddress() {
		return branchAddress;
	}

	/**
	 * @param branchAddress
	 *            the branchAddress to set
	 */
	public void setBranchAddress(String branchAddress) {
		this.branchAddress = branchAddress;
	}

	/**
	 * @return the borrower
	 */
	public List<Borrower> getBorrower() {
		return borrower;
	}

	/**
	 * @param borrower
	 *            the borrower to set
	 */
	public void setBorrower(List<Borrower> borrower) {
		this.borrower = borrower;
	}

	/**
	 * @return the book
	 */
	public List<Book> getBook() {
		return book;
	}

	/**
	 * @param book
	 *            the book to set
	 */
	public void setBook(List<Book> book) {
		this.book = book;
	}

	/**
	 * @return the bookLoans
	 */
	public List<BookLoans> getBookLoans() {
		return bookLoans;
	}

	/**
	 * @param bookLoans
	 *            the bookLoans to set
	 */
	public void setBookLoans(List<BookLoans> bookLoans) {
		this.bookLoans = bookLoans;
	}

	/**
	 * @return the bookCopies
	 */
	public List<BookCopies> getBookCopies() {
		return bookCopies;
	}

	/**
	 * @param bookCopies
	 *            the bookCopies to set
	 */
	public void setBookCopies(List<BookCopies> bookCopies) {
		this.bookCopies = bookCopies;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((branchAddress == null) ? 0 : branchAddress.hashCode());
		result = prime * result + ((branchId == null) ? 0 : branchId.hashCode());
		result = prime * result + ((branchName == null) ? 0 : branchName.hashCode());
		return result;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Branch other = (Branch) obj;
		if (branchAddress == null) {
			if (other.branchAddress != null)
				return false;
		} else if (!branchAddress.equals(other.branchAddress))
			return false;
		if (branchId == null) {
			if (other.branchId != null)
				return false;
		} else if (!branchId.equals(other.branchId))
			return false;
		if (branchName == null) {
			if (other.branchName != null)
				return false;
		} else if (!branchName.equals(other.branchName))
			return false;
		return true;
	}
}
