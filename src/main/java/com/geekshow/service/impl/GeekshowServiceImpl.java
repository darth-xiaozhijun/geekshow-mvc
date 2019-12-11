package com.geekshow.service.impl;

import com.geekshow.annotation.Service;
import com.geekshow.service.IGeekShowService;

@Service("geeshowServiceImpl") //map.put("geeshowServiceImpl",new GeekShowServiceImpl)
public class GeekshowServiceImpl implements IGeekShowService {

	public String query(String name, String age) {
		
		return "name = " + name + " age = " + age;
	}

}
