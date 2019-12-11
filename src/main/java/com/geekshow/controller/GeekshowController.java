package com.geekshow.controller;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.geekshow.annotation.Autowired;
import com.geekshow.annotation.Controller;
import com.geekshow.annotation.RequestMapping;
import com.geekshow.annotation.RequestParam;
import com.geekshow.service.IGeekShowService;

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
