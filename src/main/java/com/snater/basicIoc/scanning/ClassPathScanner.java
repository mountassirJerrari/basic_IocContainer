package com.snater.basicIoc.scanning;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import com.snater.basicIoc.annotations.Component;


public class ClassPathScanner {
	
	public List<Class<?>> getClasses(String rootPackage) {
        List<Class<?>> classes = new ArrayList<>();
        String packagePath = rootPackage.replace(".", "/");
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        scanPackage(classLoader, packagePath, rootPackage, classes);
        return classes;
    }

	private  void scanPackage(ClassLoader classLoader, String packagePath, String packageName, List<Class<?>> classes) {
        try {
            InputStream stream = classLoader.getResourceAsStream(packagePath);
            if (stream != null) {
                BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
                String line;
                while ((line = reader.readLine()) != null) {
                    if (line.endsWith(".class")) {
                    	String className = packageName + "." + line.replace(".class", "");
                    	Class<?> clazz = Class.forName(className) ;
                    	if (clazz.getAnnotation(Component.class)!=null) {
                    		classes.add(clazz);
						}
                    } else if (line.indexOf('.') == -1) {
                        scanPackage(classLoader, packagePath + "/" + line, packageName + "." + line, classes);
                    }
                }
            }
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
}
