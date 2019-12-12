package com.geekshow.xzj.servlet;

import java.io.File;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.omg.CORBA.ServerRequest;

import com.geekshow.xzj.annotation.Autowired;
import com.geekshow.xzj.annotation.Controller;
import com.geekshow.xzj.annotation.RequestMapping;
import com.geekshow.xzj.annotation.RequestParam;
import com.geekshow.xzj.annotation.Service;
import com.geekshow.xzj.controller.GeekshowController;

public class DispatchServlet extends HttpServlet {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	List<String> classNames = new ArrayList<String>();

	Map<String, Object> beans = new HashMap<String, Object>();
	
	Map<String, Object> handlerMap = new HashMap<String, Object>();

	@Override
	public void init(ServletConfig servletConfig) throws ServletException {

		// 把所有的bean扫描---扫描所有的class文件
		scanPackage("com.geekshow");

		doInstance();

		doIoc();

		buildUrlMapping();
	}

	/**
	 * 路径映射方法
	 */
	private void buildUrlMapping() {

		if (beans.entrySet().size() <= 0) {
			System.out.println("没有一个实例化类");
			return;
		}

		for (Map.Entry<String, Object> entry : beans.entrySet()) {

			Object instance = entry.getValue();
			Class<? extends Object> clazz = instance.getClass();
			
			if (clazz.isAnnotationPresent(Controller.class)) {
				
				RequestMapping requestMapping = clazz.getAnnotation(RequestMapping.class);
				String classPath = requestMapping.value();
				
				Method[] methods = clazz.getMethods();
				for (Method method : methods) {
					if (method.isAnnotationPresent(RequestMapping.class)) {
						
						RequestMapping methodMapping = method.getAnnotation(RequestMapping.class);
						String methodPath = methodMapping.value();
						handlerMap.put(classPath + methodPath, method);
					
					}else {
						continue;
					}
				}
			}else {
				continue;
			}
		}

	}

	/**
	 * 依赖注入 把service注入到controller
	 */
	private void doIoc() {

		if (beans.entrySet().size() <= 0) {
			System.out.println("没有一个实例化类");
			return;
		}

		for (Map.Entry<String, Object> entry : beans.entrySet()) {

			Object instance = entry.getValue();
			Class<? extends Object> clazz = instance.getClass();

			if (clazz.isAnnotationPresent(Controller.class)) {

				Field[] fields = clazz.getDeclaredFields();
				for (Field field : fields) {
					if (field.isAnnotationPresent(Autowired.class)) {
						Autowired autowired = field.getAnnotation(Autowired.class);
						String key = autowired.value();
						field.setAccessible(true);
						try {
							field.set(instance, beans.get(key));
						} catch (IllegalArgumentException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						} catch (IllegalAccessException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					} else {
						continue;
					}
				}
			} else {
				continue;
			}
		}
	}

	/**
	 * 根据list集合实例化对象
	 */
	private void doInstance() {

		if (classNames.size() <= 0) {
			System.out.println("包扫描失败、、、、");
			return;
		}

		// 实例化对象
		for (String className : classNames) {

			String cn = className.replace(".class", "");
			try {

				Class<?> clazz = Class.forName(cn);
				if (clazz.isAnnotationPresent(Controller.class)) {

					Object instance = clazz.newInstance();// 创建控制类

					RequestMapping requestMapping = clazz.getAnnotation(RequestMapping.class);
					String rmValue = requestMapping.value();
					beans.put(rmValue, instance);

				} else if (clazz.isAnnotationPresent(Service.class)) {

					Service service = clazz.getAnnotation(Service.class);
					String value = service.value();

					Object instance = clazz.newInstance();
					beans.put(value, instance);

				} else {
					continue;
				}

			} catch (Exception e) {

				e.printStackTrace();
			}
		}

	}

	/**
	 * 扫描所有的类路径到集合中
	 * 
	 * @param path
	 */
	private void scanPackage(String path) {

		String newPath = path.replace(".", "/");
		URL url = this.getClass().getClassLoader().getResource("/" + newPath);
		String filePath = url.getFile();

		File files = new File(filePath);

		String[] filePaths = files.list();// geekshow
		for (String fileStr : filePaths) {

			File file = new File(filePath + fileStr);// com.geekshow.*
			if (file.isDirectory()) {
				scanPackage(path + "." + fileStr);
			} else {

				// 把所有类路径加入到list中
				classNames.add(path + "." + file.getName());
			}
		}
	}

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		String uri = req.getRequestURI();
		String contextPath = req.getContextPath();
		String path = uri.replace(contextPath, "");
		
		Method method = (Method) handlerMap.get(path);
		
		GeekshowController instance = (GeekshowController) beans.get("/" + path.split("/")[1]);
	
		Object[] args = handler(req, resp, method);
		try {
			method.invoke(instance, args);
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

		String uri = req.getRequestURI();
		String contextPath = req.getContextPath();
		String path = uri.replace(contextPath, "");
		
		Method method = (Method) handlerMap.get(path);
		
		GeekshowController instance = (GeekshowController) beans.get("/" + path.split("/")[1]);
	
		Object[] args = handler(req, resp, method);
		try {
			method.invoke(instance, args);
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private static Object[] handler(HttpServletRequest request,HttpServletResponse response,Method method){
		
		//拿到当前要执行的方法有哪些参数
		Class<?>[] paramClazzs = method.getParameterTypes();
		
		//根据参数的个数，new一个参数的数组，将方法里的所有参数赋值到args中
		Object[] args = new Object[paramClazzs.length];
		
		int args_i = 0;
		int index = 0;
		for (Class<?> paramClazz : paramClazzs) {
			
			if (ServletRequest.class.isAssignableFrom(paramClazz)) {
				args[args_i++] = request;
			}
			if (ServletResponse.class.isAssignableFrom(paramClazz)) {
				args[args_i++] = response;
			}
			
			//从0-3判断有没有RequestParam注解，很明显paramClazz为0和1是，不是，
			//为2和3时为@RequestParam，需要解析
			Annotation[] paramAns = method.getParameterAnnotations()[index];
			if (paramAns.length > 0) {
				for (Annotation paramAn : paramAns) {
					if (RequestParam.class.isAssignableFrom(paramAn.getClass())) {
						
						RequestParam rp = (RequestParam) paramAn;
						//找到注释里的name和age
						args[args_i++] = request.getParameter(rp.value());
					}
				}
			}
			index++;
		}
		return args;
	}
	
	public static void main(String[] args) {
		
		File file = new File("C:\\Program Files\\Apache Software Foundation\\Tomcat 7.0\\wtpwebapps\\geekshow-mvc\\WEB-INF\\classes\\com\\geekshow\\xzj");
		String[] strings = file.list();
		for (int i = 0; i < strings.length; i++) {
			System.out.println(strings[i]);
		}
	}

}
