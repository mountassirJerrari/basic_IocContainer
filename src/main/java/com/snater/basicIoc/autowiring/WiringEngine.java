package com.snater.basicIoc.autowiring;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.List;
import com.snater.basicIoc.annotations.Autowired;
import com.snater.basicIoc.annotations.Qualifier;
import com.snater.basicIoc.container.Container;

public class WiringEngine {

    public WiringEngine() {
    }

    public void autowire(Container container, Class<?> classz, Object classInstance) throws Exception {
        List<Field> autoWiredFields = getAutoWiredFields(classz);

        for (Field field : autoWiredFields) {
            String qualifier = getQualifierAnnotationValue(field);
            Object fieldInstance = container.getBeanInstance(field.getType(), field.getName(), qualifier);

            setFieldAccessibleAndSetValue(field, classInstance, fieldInstance);

            autowire(container, fieldInstance.getClass(), fieldInstance);
        }
    }

    public Object getConstructorInjectedInstance(Container container, Class<?> clazz) throws Exception {
        for (Constructor<?> constructor : clazz.getConstructors()) {
            if (constructor.isAnnotationPresent(Autowired.class)) {
                Object[] dependencies = resolveConstructorDependencies(container, constructor);

                return constructor.newInstance(dependencies);
            }
        }

        return clazz.getDeclaredConstructor().newInstance();
    }

    private List<Field> getAutoWiredFields(Class<?> clazz) {
        List<Field> autoWiredFields = new ArrayList<>();

        for (Field field : clazz.getDeclaredFields()) {
            if (field.isAnnotationPresent(Autowired.class)) {
                autoWiredFields.add(field);
            }
        }

        return autoWiredFields;
    }

    private String  getQualifierAnnotationValue(Field field) {
        return field.isAnnotationPresent(Qualifier.class) ? field.getAnnotation(Qualifier.class).value() : null;
    }
    private String  getQualifierAnnotationValue(Parameter field) {
        return field.isAnnotationPresent(Qualifier.class) ? field.getAnnotation(Qualifier.class).value() : null;
    }

    private void setFieldAccessibleAndSetValue(Field field, Object classInstance, Object fieldInstance) throws IllegalAccessException {
        boolean isAccessible = field.canAccess(classInstance);

        try {
            field.setAccessible(true);
            field.set(classInstance, fieldInstance);
        } finally {
            field.setAccessible(isAccessible);
        }
    }

    private Object[] resolveConstructorDependencies(Container container, Constructor<?> constructor) throws Exception {
        Object[] dependencies = new Object[constructor.getParameterCount()];

        for (int i = 0; i < constructor.getParameterCount(); i++) {
            Parameter param = constructor.getParameters()[i];
            String qualifier = getQualifierAnnotationValue(param);

            Class<?> beanClass = container.getImplementationClass(param.getType(), param.getName(), qualifier);
            dependencies[i] = getConstructorInjectedInstance(container, beanClass);
        }

        return dependencies;
    }
}
