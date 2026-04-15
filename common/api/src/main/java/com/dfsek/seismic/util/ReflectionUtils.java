package com.dfsek.seismic.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.annotation.Annotation;
import java.lang.reflect.AccessibleObject;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Field;
import java.lang.reflect.GenericArrayType;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.lang.reflect.WildcardType;
import java.util.Arrays;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;


public class ReflectionUtils {
    private static final Logger LOGGER = LoggerFactory.getLogger(ReflectionUtils.class);

    private static final ConcurrentHashMap<String, Class<?>> reflectedClasses = new ConcurrentHashMap<>();
    private static final ConcurrentHashMap<ClassMethod, Method> reflectedMethods = new ConcurrentHashMap<>();
    private static final ConcurrentHashMap<ClassField, Field> reflectedFields = new ConcurrentHashMap<>();

    public static Class<?> getClass(String clazz) {
        return reflectedClasses.computeIfAbsent(clazz, ReflectionUtils::getReflectedClass);
    }

    public static Method getMethod(Class<?> clazz, String method) {
        return reflectedMethods.computeIfAbsent(new ClassMethod(clazz, method), ReflectionUtils::getReflectedMethod);
    }

    public static Field getField(Class<?> clazz, String field) {
        return reflectedFields.computeIfAbsent(new ClassField(clazz, field), ReflectionUtils::getReflectedField);
    }

    public static void setMethodToPublic(Method method) {
        setAccessibleObjectToPublic(method);
    }

    public static void setFieldToPublic(Field field) {
        setAccessibleObjectToPublic(field);
    }

    private static void setAccessibleObjectToPublic(AccessibleObject object) {
        if(object == null) return;
        try {
            object.setAccessible(true);
        } catch(Exception e) {
            try {
                object.trySetAccessible();
            } catch(Exception ignored) {
                LOGGER.debug("Failed to make {} accessible", object, e);
            }
        }
    }

    private static Field getReflectedField(ClassField classField) {
        Class<?> current = classField.clss;
        while(current != null) {
            try {
                Field field = current.getDeclaredField(classField.field);
                setFieldToPublic(field);
                return field;
            } catch(NoSuchFieldException ignored) {
                current = current.getSuperclass();
            }
        }
        try {
            Field field = classField.clss.getField(classField.field);
            setFieldToPublic(field);
            return field;
        } catch(NoSuchFieldException e) {
            LOGGER.error("Field {} not found in class {}", classField.field, classField.clss.getName());
            return null;
        }
    }

    private static Method getReflectedMethod(ClassMethod classMethod) {
        Class<?> current = classMethod.clss;
        while(current != null) {
            for(Method method : current.getDeclaredMethods()) {
                if(method.getName().equals(classMethod.method) && method.getParameterCount() == 0) {
                    setMethodToPublic(method);
                    return method;
                }
            }
            current = current.getSuperclass();
        }
        try {
            Method method = classMethod.clss.getMethod(classMethod.method);
            setMethodToPublic(method);
            return method;
        } catch(NoSuchMethodException e) {
            LOGGER.error("Method {} not found in class {}", classMethod.method, classMethod.clss.getName());
            return null;
        }
    }

    private static Class<?> getReflectedClass(String clazz) {
        try {
            int separator = clazz.indexOf('$');
            if(separator > -1) {
                Class<?> outer = Class.forName(clazz.substring(0, separator));
                return getNestedClass(outer, clazz.substring(separator + 1));
            }
            return Class.forName(clazz);
        } catch(ClassNotFoundException e) {
            LOGGER.error("Class {} not found", clazz);
            return null;
        }
    }

    private static Class<?> getNestedClass(Class<?> outerClass, String nestedName) {
        for(Class<?> nestedClass : outerClass.getDeclaredClasses()) {
            if(nestedClass.getName().equals(outerClass.getName() + "$" + nestedName)) {
                return nestedClass;
            }
        }
        return null;
    }

    public static <T extends Annotation> void ifAnnotationPresent(AnnotatedElement element,
                                                                  Class<? extends T> annotation,
                                                                  Consumer<T> consumer) {
        T value = element.getAnnotation(annotation);
        if(value != null) {
            consumer.accept(value);
        }
    }

    public static Class<?> getRawType(Type type) {
        if(type instanceof Class<?> clazz) {
            return clazz;
        }
        if(type instanceof ParameterizedType parameterizedType) {
            return (Class<?>) parameterizedType.getRawType();
        }
        if(type instanceof GenericArrayType genericArrayType) {
            return java.lang.reflect.Array.newInstance(getRawType(genericArrayType.getGenericComponentType()), 0).getClass();
        }
        if(type instanceof TypeVariable<?>) {
            return Object.class;
        }
        if(type instanceof WildcardType wildcardType) {
            return getRawType(wildcardType.getUpperBounds()[0]);
        }
        String typeName = type == null ? "null" : type.getClass().getName();
        throw new IllegalArgumentException(type + " is of unsupported type " + typeName);
    }

    public static String typeToString(Type type) {
        return type instanceof Class<?> clazz ? clazz.getName() : type.toString();
    }

    public static boolean equals(Type a, Type b) {
        if(a == b) return true;
        if(a instanceof Class<?>) return a.equals(b);
        if(a instanceof ParameterizedType parameterizedA) {
            if(!(b instanceof ParameterizedType parameterizedB)) return false;
            return Objects.equals(parameterizedA.getOwnerType(), parameterizedB.getOwnerType())
                   && parameterizedA.getRawType().equals(parameterizedB.getRawType())
                   && Arrays.equals(parameterizedA.getActualTypeArguments(), parameterizedB.getActualTypeArguments());
        }
        if(a instanceof GenericArrayType arrayA) {
            if(!(b instanceof GenericArrayType arrayB)) return false;
            return equals(arrayA.getGenericComponentType(), arrayB.getGenericComponentType());
        }
        if(a instanceof WildcardType wildcardA) {
            if(!(b instanceof WildcardType wildcardB)) return false;
            return Arrays.equals(wildcardA.getUpperBounds(), wildcardB.getUpperBounds())
                   && Arrays.equals(wildcardA.getLowerBounds(), wildcardB.getLowerBounds());
        }
        if(a instanceof TypeVariable<?> typeVariableA) {
            if(!(b instanceof TypeVariable<?> typeVariableB)) return false;
            return typeVariableA.getGenericDeclaration() == typeVariableB.getGenericDeclaration()
                   && typeVariableA.getName().equals(typeVariableB.getName());
        }
        return false;
    }

    private record ClassMethod(Class<?> clss, String method) {
    }

    private record ClassField(Class<?> clss, String field) {
    }
}
