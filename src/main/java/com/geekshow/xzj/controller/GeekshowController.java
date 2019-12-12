package com.geekshow.xzj.controller;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.geekshow.xzj.annotation.Autowired;
import com.geekshow.xzj.annotation.Controller;
import com.geekshow.xzj.annotation.RequestMapping;
import com.geekshow.xzj.annotation.RequestParam;
import com.geekshow.xzj.service.IGeekShowService;

@Controller
@RequestMapping("/geekshow")
public class GeekshowController {
	
	@Autowired("geeshowServiceImpl")
	private IGeekShowService geeshowService;
	
	@RequestMapping("/query")
	public void query(HttpServletRequest request,HttpServletResponse respone,
			@RequestParam("name") String name, @RequestParam("age") String age){
		
		try {
			
			PrintWriter printWriter = respone.getWriter();
			String result = geeshowService.query(name, age);
			printWriter.write(result);
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	
}
