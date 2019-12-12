package com.geekshow.xzj.service.impl;

import com.geekshow.xzj.annotation.Service;
import com.geekshow.xzj.service.IGeekShowService;

@Service("geeshowServiceImpl") //map.put("geeshowServiceImpl",new GeekShowServiceImpl)
public class GeekshowServiceImpl implements IGeekShowService {

	public String query(String name, String age) {
		
		return "name = " + name + " age = " + age;
	}

}
