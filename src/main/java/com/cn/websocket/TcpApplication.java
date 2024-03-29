package com.cn.websocket;

import com.cn.websocket.annotation.SingleExecute;
import com.cn.websocket.entity.CommandMapping;
import com.cn.websocket.entity.SpringContext;
import com.cn.websocket.exception.ServerException;
import com.cn.websocket.util.ClassUtil;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.lang.reflect.Method;
import java.util.*;

public class TcpApplication {

	private static Map<String, CommandMapping> commandMappingMap = new HashMap<>();
	
	public static void run(Class<?> primarySource) {
		SpringBootApplication applicationAnnotation = primarySource.getAnnotation(SpringBootApplication.class);
		if(applicationAnnotation == null) {
			throw new RuntimeException("找不到SpringBootApplication注解");
		}
		Collection<Class<?>> classCollection = queryAllPackageClass(primarySource, applicationAnnotation);
		commandMappingMap.putAll(queryMapping(classCollection));
	}
	
	public static CommandMapping getCommandMapping(String uri) {
		CommandMapping commandMapping = commandMappingMap.get(uri);
		if(commandMapping == null) {
			throw new ServerException(404, "指令不存在");
		}
		return commandMapping;
	}

	private static Map<String, CommandMapping> queryMapping(Collection<Class<?>> classCollection) {
		Map<String, CommandMapping> commandMappingMap = new HashMap<>(); 
		for(Class<?> clazz : classCollection) {
			RestController controller = clazz.getDeclaredAnnotation(RestController.class);
			RequestMapping controllerMapping = clazz.getDeclaredAnnotation(RequestMapping.class);
			if(controller != null && controllerMapping != null) {
				String[] rootPaths = controllerMapping.value();
				Method[] methods = clazz.getMethods();
				for(Method method : methods) {
					RequestMapping methodMapping = method.getDeclaredAnnotation(RequestMapping.class);
					SingleExecute singleExecute = method.getDeclaredAnnotation(SingleExecute.class);
					if(methodMapping != null) {
						String[] subPaths = methodMapping.value();
						for(String rootPath : rootPaths) {
							rootPath = rootPath.trim();
							for(String subPath : subPaths) {
								subPath = subPath.trim();
								String uri = null;
								if(subPath.equals("/")) {
									uri = rootPath;
								} else {
									uri = rootPath + subPath;
								}
								uri = uri.replace("//", "/");
								commandMappingMap.put(uri, new CommandMapping(uri, SpringContext.getBean(clazz),
										method, singleExecute != null ? true : false));
							}
						}
					}
				}
			}
		}
		return commandMappingMap;
	}

	/**
	 * 便利所有class文件
	 * @param primarySource
	 * @param applicationAnnotation
	 */
	private static Collection<Class<?>> queryAllPackageClass(Class<?> primarySource, SpringBootApplication applicationAnnotation) {
		Set<Class<?>> clazzSet = new LinkedHashSet<>(); 
		if(applicationAnnotation.scanBasePackages() != null) {
			for(String packageName : applicationAnnotation.scanBasePackages()) {
				clazzSet.addAll(ClassUtil.getClasses(packageName));
			}
		} else {
			clazzSet.addAll(ClassUtil.getClasses(primarySource.getPackage().getName()));
		}
		return clazzSet;
	}
	
}
