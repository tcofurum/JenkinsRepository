/*
 *
 *6:17:20 PM
 *Dec 31, 2017
 */
package com.gcit.lms.service;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;

import com.gcit.lms.dao.AuthorDAO;
import com.gcit.lms.dao.BookDAO;

/**
 * @author ThankGod4Life
 * @date Dec 31, 2017
 *
 */
public abstract class BaseService {

	@Autowired
	BookDAO bDao;

	@Autowired
	AuthorDAO aDao;

	@SuppressWarnings("unchecked")
	public <T> List<T> readObjectsByPk(Object daoEntity, String daoMethod, List<Integer> Ids) throws SQLException {
		try {
			List<T> tList = new ArrayList<T>();
			Method getDaoMethod = daoEntity.getClass().getMethod(daoMethod, Integer.class);
			for (Integer integer : Ids) {
				tList.add((T) getDaoMethod.invoke(daoEntity, integer));
			}
			return tList;
		} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException
				| SecurityException e) {
			e.printStackTrace();
		}
		return null;
	}

	public List<Integer> convertJsonArryToIntList(JSONArray jsonIds) throws JSONException {
		List<Integer> integerIds = new ArrayList<>();
		for (int i = 0; i < jsonIds.length(); i++) {
			JSONObject json = jsonIds.getJSONObject(i);
			integerIds.add(Integer.parseInt(json.toString()));
		}
		return integerIds;
	}
}
