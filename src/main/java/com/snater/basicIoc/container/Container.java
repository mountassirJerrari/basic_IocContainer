package com.snater.basicIoc.container;

import javax.management.RuntimeErrorException;
import java.util.*;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import com.snater.basicIoc.annotations.Component;
import com.snater.basicIoc.annotations.Default;
import com.snater.basicIoc.annotations.Prototype;
import com.snater.basicIoc.autowiring.WiringEngine;
import com.snater.basicIoc.scanning.ClassPathScanner;

public class Container {
    private Map<Class<?>, List<Class<?>>> diMap = new HashMap<>();
    private Map<Class<?>, Object> applicationScope = new HashMap<>();
    private static Container instance;
    private final ClassPathScanner classScanner;
    private final WiringEngine wiringEngine;

    private Container() {
        classScanner = new ClassPathScanner();
        wiringEngine = new WiringEngine();
    }

    public static Container getInstance(Class<?> mainClass) throws Exception {
        if (instance == null) {
            instance = new Container();
            instance.init(mainClass);
        }
        return instance;
    }

    private void init(Class<?> mainClass) throws Exception {
        List<Class<?>> types = classScanner.getClasses(mainClass.getPackageName());

        for (Class<?> implementationClass : types) {
            Class<?>[] interfaces = implementationClass.getInterfaces();

            if (interfaces.length == 0) {
                diMap.put(implementationClass, Collections.singletonList(implementationClass));
            } else {
                diMap.put(implementationClass, Arrays.asList(interfaces));
            }
        }

        for (Class<?> classz : types) {
            if (classz.isAnnotationPresent(Component.class)) {
                Object classInstance = wiringEngine.getConstructorInjectedInstance(instance, classz);
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

    public Class<?> getImplementationClass(Class<?> interfaceClass, String fieldName, String qualifier) {
        Set<Entry<Class<?>, List<Class<?>>>> implementationClasses = diMap.entrySet().stream()
                .filter(entry -> entry.getValue().contains(interfaceClass))
                .collect(Collectors.toSet());

        String errorMessage = "";

        if (implementationClasses == null || implementationClasses.size() == 0) {
            errorMessage = "No implementation found for interface " + interfaceClass.getName();
        } else if (implementationClasses.size() == 1) {
            Optional<Entry<Class<?>, List<Class<?>>>>
            optional = implementationClasses.stream().findFirst();

            if (optional.isPresent()) {
                return optional.get().getKey();
            }
        } else if (implementationClasses.size() > 1) {
            int count = 0;
            Class<?> defaultImplementation = null;

            for (Entry<Class<?>, List<Class<?>>> entry : implementationClasses) {
                if (entry.getKey().isAnnotationPresent(Default.class)) {
                    Class<?>[] faces = entry.getKey().getAnnotation(Default.class).value();

                    for (Class<?> face : faces) {
                        if (interfaceClass.getName().equals(face.getName())) {
                            count++;
                            defaultImplementation = entry.getKey();
                        }
                    }
                }
            }

            if (count > 1) {
                throw new RuntimeErrorException(new Error("There are " + count +
                        " default classes. Expected a single default implementation or use @CustomQualifier to resolve conflicts."));
            }

            final String findBy = (qualifier == null || qualifier.trim().length() == 0) ? fieldName : qualifier;
            Optional<Entry<Class<?>, List<Class<?>>>>
            optional = implementationClasses.stream()
                    .filter(entry -> entry.getKey().getSimpleName().equalsIgnoreCase(findBy))
                    .findAny();

            if (optional.isPresent()) {
                return optional.get().getKey();
            } else {
                if (defaultImplementation != null) {
                    return defaultImplementation;
                }
                errorMessage = "There are " + implementationClasses.size() + " implementations of interface " +
                        interfaceClass.getName() + ". Expected a single implementation or use @CustomQualifier to resolve conflicts.";
            }
        }

        throw new RuntimeErrorException(new Error(errorMessage));
    }

    @SuppressWarnings("unchecked")
    public <T> T getBeanInstance(Class<T> interfaceClass) throws Exception {
        return (T) getBeanInstance(interfaceClass, null, null);
    }

    public Object getBeanInstance(Class<?> interfaceClass, String fieldName, String qualifier)
            throws Exception {
        Class<?> implementationClass = getImplementationClass(interfaceClass, fieldName, qualifier);

        if (implementationClass.isAnnotationPresent(Prototype.class)) {
            return wiringEngine.getConstructorInjectedInstance(instance, implementationClass);
        }

        if (applicationScope.containsKey(implementationClass)) {
            return applicationScope.get(implementationClass);
        }

        synchronized (applicationScope) {
            Object service = wiringEngine.getConstructorInjectedInstance(instance, implementationClass);
            applicationScope.put(implementationClass, service);
            return service;
        }
    }
}
