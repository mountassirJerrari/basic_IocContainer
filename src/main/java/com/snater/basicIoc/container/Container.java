package com.snater.basicIoc.container;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import javax.management.RuntimeErrorException;

import com.snater.basicIoc.annotations.Component;
import com.snater.basicIoc.annotations.Default;
import com.snater.basicIoc.autowiring.WiringEngine;
import com.snater.basicIoc.scanning.ClassPathScanner;

public class Container {

	private Map<Class<?>, List<Class<?>>> diMap;
	private Map<Class<?>, Object> applicationScope;
	private static Container instance;
	private ClassPathScanner classScanner;
	private WiringEngine wiringEngine;

	private Container() {

		diMap = new HashMap<>();
		applicationScope = new HashMap<>();
		classScanner = new ClassPathScanner();
		wiringEngine = new WiringEngine();
	}

	public static Container getInstance(Class<?> mainClass) throws InstantiationException, IllegalAccessException,
			IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException {

		if (instance == null) {
			instance = new Container();

			instance.init(mainClass);
		}
		return instance;
	}

	private void init(Class<?> mainClass) throws InstantiationException, IllegalAccessException,
			IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException {

		List<Class<?>> types = classScanner.getClasses(mainClass.getPackageName());
		for (Class<?> implementationClass : types) {

			Class<?>[] interfaces = implementationClass.getInterfaces();
			if (interfaces.length == 0) {
				List<Class<?>> impList = new ArrayList<>();
				impList.add(implementationClass);
				diMap.put(implementationClass, impList);
			} else {
				List<Class<?>> impList = new ArrayList<>();

				for (Class<?> iface : interfaces) {
					impList.add(iface);
				}
				diMap.put(implementationClass, impList);
			}
		}
		for (Class<?> classz : types) {
			if (classz.isAnnotationPresent(Component.class)) {
				Object classInstance = classz.getDeclaredConstructor().newInstance();
				;
				applicationScope.put(classz, classInstance);

				wiringEngine.autowire(this, classz, classInstance);
			}
		}

	}

	public <T> T getService(Class<T> classz) {
		try {
			return instance.getBeanInstance(classz);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	private Class<?> getImplimentationClass(Class<?> interfaceClass, final String fieldName, final String qualifier) {
		Class<?> defaultimplementation = null;
		Set<Entry<Class<?>, List<Class<?>>>> implementationClasses = diMap.entrySet().stream().filter(entry -> {

			List<Class<?>> ifaceList = entry.getValue();

			return ifaceList.contains(interfaceClass);
		}).collect(Collectors.toSet());
		String errorMessage = "";
		if (implementationClasses == null || implementationClasses.size() == 0) {
			errorMessage = "no implementation found for interface " + interfaceClass.getName();
		} else if (implementationClasses.size() == 1) {
			Optional<Entry<Class<?>, List<Class<?>>>> optional = implementationClasses.stream().findFirst();
			if (optional.isPresent()) {
				return optional.get().getKey();
			}
		} else if (implementationClasses.size() > 1) {
			// handling the default implimentation
			int count = 0;

			for (Entry<Class<?>, List<Class<?>>> entry : implementationClasses) {
				if (entry.getKey().isAnnotationPresent(Default.class)) {

					Class<?>[] faces = entry.getKey().getAnnotation(Default.class).value();
					
					for (Class<?> face : faces) {
						if (interfaceClass.getName().equals(face.getName())) {
							count++;

							defaultimplementation = entry.getKey();
						}
					}

				}
			}
			if (count > 1) {
				throw new RuntimeErrorException(new Error("there is " + count
						+ " default classes ,Expected single default implementation or make use of @CustomQualifier to resolve conflict "));
			}
			final String findBy = (qualifier == null || qualifier.trim().length() == 0) ? fieldName : qualifier;
			Optional<Entry<Class<?>, List<Class<?>>>> optional = implementationClasses.stream()
					.filter(entry -> entry.getKey().getSimpleName().equalsIgnoreCase(findBy)).findAny();
			if (optional.isPresent()) {
				return optional.get().getKey();
			} else {

				if (defaultimplementation != null) {
					return defaultimplementation;
				}
				errorMessage = "There are " + implementationClasses.size() + " implimentations of interface "
						+ interfaceClass.getName()
						+ " Expected single implementation or make use of @CustomQualifier to resolve conflict";
			}
		}
		throw new RuntimeErrorException(new Error(errorMessage));
	}

	/**
	 * Create and Get the Object instance of the implementation class for input
	 * interface service
	 * 
	 * @throws SecurityException
	 * @throws NoSuchMethodException
	 * @throws InvocationTargetException
	 * @throws IllegalArgumentException
	 */
	@SuppressWarnings("unchecked")
	public <T> T getBeanInstance(Class<T> interfaceClass) throws InstantiationException, IllegalAccessException,
			IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException {
		return (T) getBeanInstance(interfaceClass, null, null);
	}

	public <T> Object getBeanInstance(Class<T> interfaceClass, String fieldName, String qualifier)
			throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException,
			NoSuchMethodException, SecurityException {
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
