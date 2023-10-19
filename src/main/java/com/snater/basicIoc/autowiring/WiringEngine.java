package com.snater.basicIoc.autowiring;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

import com.snater.basicIoc.annotations.Autowired;
import com.snater.basicIoc.annotations.Qualifier;
import com.snater.basicIoc.container.Container;

public class WiringEngine {

	public WiringEngine() {

	}
	
	public  void autowire(Container container, Class<?> classz, Object classInstance)
            throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException {
            List<Field> fields = getAutoWiredFields(classz) ;
           
        for (Field field : fields) {
            String qualifier = field.isAnnotationPresent(Qualifier.class)
                    ? field.getAnnotation(Qualifier.class).value()
                    : null;
            Object fieldInstance = container.getBeanInstance(field.getType(), field.getName(), qualifier);
            boolean isAccessible = field.canAccess(classInstance);
            // Make the private field accessible
            field.setAccessible(true);
            field.set(classInstance, fieldInstance);
            field.setAccessible(isAccessible);
            autowire(container, fieldInstance.getClass(), fieldInstance);
        }
    }
	
	public static List<Field> getAutoWiredFields( Class<?> clazz ) {
		List<Field> fields = new ArrayList<>() ;
		
		for (Field field : clazz.getDeclaredFields() ) {
		    if (field.isAnnotationPresent(Autowired.class)) {
				fields.add(field);
			}
		}
		return fields;
		
	}
}
