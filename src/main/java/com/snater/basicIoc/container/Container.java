package com.snater.basicIoc.container;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import javax.management.RuntimeErrorException;

import com.snater.basicIoc.annotations.Component;
import com.snater.basicIoc.autowiring.WiringEngine;
import com.snater.basicIoc.scanning.ClassPathScanner;

public class Container {

	private Map<Class<?>, Class<?>> diMap;
	private Map<Class<?>, Object> applicationScope;
	private static Container instance;
	private ClassPathScanner classScanner;
	private WiringEngine wiringEngine ;

	private Container() {

		diMap = new HashMap<>();
		applicationScope = new HashMap<>();
		classScanner = new ClassPathScanner();
		wiringEngine = new WiringEngine();
	}

	public static Container getInstance(Class<?> mainClass) throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException {

		if (instance == null) {
			instance = new Container();

			instance.init(mainClass);
		}
		return instance;
	}

	private void init(Class<?> mainClass) throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException {

		List<Class<?>> types = classScanner.getClasses(mainClass.getPackageName());
		for (Class<?> implementationClass : types) {
			Class<?>[] interfaces = implementationClass.getInterfaces();
			if (interfaces.length == 0) {
				diMap.put(implementationClass, implementationClass);
			} else {
				for (Class<?> iface : interfaces) {
					diMap.put(implementationClass, iface);
				}
			}
		}
		for (Class<?> classz : types) {
			if (classz.isAnnotationPresent(Component.class)) {
				Object classInstance = classz.getDeclaredConstructor().newInstance();;
				applicationScope.put(classz, classInstance);
				
				wiringEngine.autowire(this, classz, classInstance);
			}
		}

	}
	
	public  <T> T getService(Class<T> classz) {
        try {
            return instance.getBeanInstance(classz);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
	private Class<?> getImplimentationClass(Class<?> interfaceClass, final String fieldName, final String qualifier) {
        Set<Entry<Class<?>, Class<?>>> implementationClasses = diMap.entrySet().stream()
                .filter(entry -> entry.getValue() == interfaceClass).collect(Collectors.toSet());
        String errorMessage = "";
        if (implementationClasses == null || implementationClasses.size() == 0) {
            errorMessage = "no implementation found for interface " + interfaceClass.getName();
        } else if (implementationClasses.size() == 1) {
            Optional<Entry<Class<?>, Class<?>>> optional = implementationClasses.stream().findFirst();
            if (optional.isPresent()) {
                return optional.get().getKey();
            }
        } else if (implementationClasses.size() > 1) {
            final String findBy = (qualifier == null || qualifier.trim().length() == 0) ? fieldName : qualifier;
            Optional<Entry<Class<?>, Class<?>>> optional = implementationClasses.stream()
                    .filter(entry -> entry.getKey().getSimpleName().equalsIgnoreCase(findBy)).findAny();
            if (optional.isPresent()) {
                return optional.get().getKey();
            } else {
                errorMessage = "There are " + implementationClasses.size() + " of interface " + interfaceClass.getName()
                        + " Expected single implementation or make use of @CustomQualifier to resolve conflict";
            }
        }
        throw new RuntimeErrorException(new Error(errorMessage));
    }

    /**
     * Create and Get the Object instance of the implementation class for input
     * interface service
     * @throws SecurityException 
     * @throws NoSuchMethodException 
     * @throws InvocationTargetException 
     * @throws IllegalArgumentException 
     */
    @SuppressWarnings("unchecked")
    public <T> T getBeanInstance(Class<T> interfaceClass) throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException {
        return (T) getBeanInstance(interfaceClass, null, null);
    }
    
	public <T> Object getBeanInstance(Class<T> interfaceClass, String fieldName, String qualifier)
            throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException {
        Class<?> implementationClass = getImplimentationClass(interfaceClass, fieldName, qualifier);

        if (applicationScope.containsKey(implementationClass)) {
            return applicationScope.get(implementationClass);
        }

        synchronized (applicationScope) {
            Object service = implementationClass.getDeclaredConstructor().newInstance();
            applicationScope.put(implementationClass, service);
            return service;
        }
    }

}
