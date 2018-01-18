package com.gcit.lms.service;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * Handles requests for the application home page.
 */

@RestController
public class HomeService {
	
	/**
	 * Simply selects the home view to render by returning its name.
	 */
	@RequestMapping(value = "/", method = RequestMethod.GET)
	public String home() {
		/*
		 * Singleton should be used for shared resource. Those with no getters or setters.
		 * Spring says let me handle every object instantiation or don't let me handle any,
		 * you get null objects when you try to let Spring handle just some. 
		 */
		return "welcome";
	}
	
}
