package com.evo.middleware.db.router.util;

import org.springframework.core.env.Environment;
import org.springframework.core.env.PropertyResolver;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class PropertyUtil {

    private static int springBootVersion = 1;

    public PropertyUtil() {
    }

    public static <T> T handle(Environment environment, String prefix, Class<T> targetClass) {
        switch (springBootVersion) {
            case 1:
                return (T) v1(environment, prefix);
            default:
                return (T) v2(environment, prefix, targetClass);
        }
    }

    private static Object v1(Environment environment, String prefix) {
        try {
            Class<?> resolverClass = Class.forName("org.springframework.boot.bind.RelaxedPropertyResolver");
            Constructor<?> resolverConstructor = resolverClass.getDeclaredConstructor(PropertyResolver.class);
            Method getSubPropertiesMethod = resolverClass.getDeclaredMethod("getSubProperties", String.class);
            Object resolverObject = resolverConstructor.newInstance(environment);
            String prefixParam = prefix.endsWith(".") ? prefix : prefix + ".";
            return getSubPropertiesMethod.invoke(resolverObject, prefixParam);
        } catch (NoSuchMethodException | SecurityException | InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException | ClassNotFoundException var7) {
            throw new RuntimeException(var7.getMessage(), var7);
        }
    }

    private static Object v2(Environment environment, String prefix, Class<?> targetClass) {
        try {
            Class<?> binderClass = Class.forName("org.springframework.boot.context.properties.bind.Binder");
            Method getMethod = binderClass.getDeclaredMethod("get", Environment.class);
            Method bindMethod = binderClass.getDeclaredMethod("bind", String.class, Class.class);
            Object binderObject = getMethod.invoke((Object)null, environment);
            String prefixParam = prefix.endsWith(".") ? prefix.substring(0, prefix.length() - 1) : prefix;
            Object bindResultObject = bindMethod.invoke(binderObject, prefixParam, targetClass);
            Method resultGetMethod = bindResultObject.getClass().getDeclaredMethod("get");
            return resultGetMethod.invoke(bindResultObject);
        } catch (NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException | InvocationTargetException | ClassNotFoundException var10) {
            throw new RuntimeException(var10.getMessage(), var10);
        }
    }

    static {
        try {
            Class.forName("org.springframework.boot.bind.RelaxedPropertyResolver");
        } catch (ClassNotFoundException var1) {
            springBootVersion = 2;
        }
    }

}
