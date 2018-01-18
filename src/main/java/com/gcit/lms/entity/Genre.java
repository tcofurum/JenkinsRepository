package com.gcit.lms.entity;

import java.io.Serializable;
import java.util.List;

public class Genre implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2949058495391850755L;
	private Integer genre_id;
	private String genre_name = null;
	private List<Book> book;

	/**
	 * @return the genre_id
	 */
	public Integer get_Genre_Id() {
		return genre_id;
	}

	/**
	 * @param genre_id
	 *            the genre_id to set
	 */
	public void set_Genre_Id(Integer genre_id) {
		this.genre_id = genre_id;
	}

	/**
	 * @return the genre_name
	 */
	public String get_Genre_Name() {
		return genre_name;
	}

	/**
	 * @param genre_name
	 *            the genre_name to set
	 */
	public void set_Genre_Name(String genre_name) {
		this.genre_name = genre_name;
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

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((genre_id == null) ? 0 : genre_id.hashCode());
		result = prime * result + ((genre_name == null) ? 0 : genre_name.hashCode());
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
		Genre other = (Genre) obj;
		if (genre_id == null) {
			if (other.genre_id != null)
				return false;
		} else if (!genre_id.equals(other.genre_id))
			return false;
		if (genre_name == null) {
			if (other.genre_name != null)
				return false;
		} else if (!genre_name.equals(other.genre_name))
			return false;
		return true;
	}

}
