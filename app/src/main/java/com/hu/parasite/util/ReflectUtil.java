package com.hu.parasite.util;

import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.NoSuchElementException;

/**
 * Created by HuJi on 2017/12/29.
 */

public class ReflectUtil {

    public static ClassLoader getClassLoader() {
        return Thread.currentThread().getContextClassLoader();
    }

    public static URL getResource(String name) {
        return getClassLoader().getResource(name);
    }

    public static InputStream getResourceAsStream(String name) {
        return getClassLoader().getResourceAsStream(name);
    }

    public static Class<?> loadClass(String className)
            throws ClassNotFoundException {
        return Class.forName(className, true, getClassLoader());
    }

    public static Method findMethod(String className, String methodName, Object... args)
            throws ClassNotFoundException, NoSuchMethodException {
        return findMethod(loadClass(className), methodName, args);
    }

    public static Method findMethod(Class<?> clazz, String methodName, Object... args)
            throws NoSuchMethodException {
        Class<?> tmp = clazz;
        for (; tmp != null; tmp = tmp.getSuperclass()) {
            Method[] methods = tmp.getDeclaredMethods();
            for (Method method : methods) {
                if (methodName.equals(method.getName()) && equalParams(method, args)) {
                    return method;
                }
            }
        }
        throw new NoSuchElementException(clazz.getName() + "." + methodName + "()");
    }

    public static boolean equalParams(Method method, Object... params) {
        Class<?>[] types = method.getParameterTypes();
        if (types.length != params.length) {
            return false;
        }
        for (int i = 0; i < types.length; i++) {
            if (params[i] == null && !types[i].isAssignableFrom(Object.class) ||
                params[i] != null && !types[i].isInstance(params[i])) {
                return false;
            }
        }
        return true;
    }

    public static boolean equalParams(Method method, Class<?>... types) {
        Class<?>[] parameterTypes = method.getParameterTypes();
        if (parameterTypes.length != types.length) {
            return false;
        }
        for (int i = 0; i < parameterTypes.length; i++) {
            if (parameterTypes[i].isAssignableFrom(types[i])) {
                return false;
            }
        }
        return true;
    }

    public static Method getMethod(String className, String methodName, Object... parameterType)
            throws ClassNotFoundException, NoSuchMethodException {
        return getMethod(loadClass(className), methodName, getClassType(parameterType));
    }

    public static Method getMethod(String className, String methodName, Class<?>... parameterType)
            throws ClassNotFoundException, NoSuchMethodException {
        return getMethod(loadClass(className), methodName, parameterType);
    }

    public static Method getMethod(Class<?> clazz, String methodName, Object... parameterType)
            throws ClassNotFoundException, NoSuchMethodException {
        return getMethod(clazz, methodName, getClassType(parameterType));
    }

    public static Method getMethod(Class<?> clazz, String methodName, Class<?>... parameterType)
            throws NoSuchMethodException {
        NoSuchMethodException exception = null;
        for (; clazz != null; clazz = clazz.getSuperclass()) {
            try {
                Method method = clazz.getDeclaredMethod(methodName, parameterType);
                if (!method.isAccessible()) {
                    method.setAccessible(true);
                }
                return method;
            } catch (NoSuchMethodException e) {
                if (exception == null) {
                    exception = e;
                }
            }
        }
        throw exception;
    }

    public static Object invoke(String className, String methodName)
            throws InvocationTargetException, IllegalAccessException, ClassNotFoundException, NoSuchMethodException {
        return getMethod(loadClass(className), methodName).invoke(null);
    }

    public static Object invoke(Class<?> clazz, String methodName)
            throws InvocationTargetException, IllegalAccessException, ClassNotFoundException, NoSuchMethodException {
        return getMethod(clazz, methodName).invoke(null);
    }

    public static Object invoke(Object object, String methodName)
            throws InvocationTargetException, IllegalAccessException, ClassNotFoundException, NoSuchMethodException {
        return getMethod(object.getClass(), methodName).invoke(object);
    }

    public static Object invoke(String className, String methodName, Object[] parameterType, Object[] args)
            throws InvocationTargetException, IllegalAccessException, ClassNotFoundException, NoSuchMethodException {
        return getMethod(loadClass(className), methodName).invoke(null, args);
    }

    public static Object invoke(Class<?> clazz, String methodName, Object[] parameterType, Object[] args)
            throws InvocationTargetException, IllegalAccessException, ClassNotFoundException, NoSuchMethodException {
        return getMethod(clazz, methodName, getClassType(parameterType)).invoke(null, args);
    }

    public static Object invoke(Object object, String methodName, Object[] parameterType, Object[] args)
            throws InvocationTargetException, IllegalAccessException, ClassNotFoundException, NoSuchMethodException {
        return getMethod(object.getClass(), methodName, getClassType(parameterType)).invoke(object, args);
    }

    public static Constructor<?> getConstructor(String className, Object... parameterType)
            throws ClassNotFoundException, NoSuchMethodException {
        return getConstructor(loadClass(className), getClassType(parameterType));
    }

    public static Constructor<?> getConstructor(String className, Class<?>... parameterType)
            throws ClassNotFoundException, NoSuchMethodException {
        return getConstructor(loadClass(className), parameterType);
    }

    public static Constructor<?> getConstructor(Class<?> clazz, Object... parameterType)
            throws ClassNotFoundException, NoSuchMethodException {
        return getConstructor(clazz, getClassType(parameterType));
    }

    public static Constructor<?> getConstructor(Class<?> clazz, Class<?>... parameterType)
            throws ClassNotFoundException, NoSuchMethodException {
        Constructor constructor = clazz.getConstructor(parameterType);
        if (!constructor.isAccessible()) {
            constructor.setAccessible(true);
        }
        return constructor;
    }

    public static Object newInstance(String className)
            throws ClassNotFoundException, NoSuchMethodException, IllegalAccessException,
            InvocationTargetException, InstantiationException {
        return getConstructor(loadClass(className)).newInstance();
    }

    public static Object newInstance(Class clazz)
            throws ClassNotFoundException, NoSuchMethodException, IllegalAccessException,
            InvocationTargetException, InstantiationException {
        return getConstructor(clazz).newInstance();
    }

    public static Object newInstance(String className, Object[] parameterType, Object[] args)
            throws ClassNotFoundException, NoSuchMethodException, IllegalAccessException,
            InvocationTargetException, InstantiationException {
        return getConstructor(loadClass(className), getClassType(parameterType)).newInstance(args);
    }

    public static Object newInstance(Class clazz, Object[] parameterType, Object[] args)
            throws ClassNotFoundException, NoSuchMethodException, IllegalAccessException,
            InvocationTargetException, InstantiationException {
        return getConstructor(clazz, getClassType(parameterType)).newInstance(args);
    }

    private static Class<?>[] getClassType(Object... parameterType)
            throws ClassNotFoundException {
        Class<?>[] type = new Class[parameterType != null ? parameterType.length : 0];
        for (int i = 0; i < type.length; i++) {
            if (parameterType[i] instanceof Class<?>) {
                type[i] = (Class<?>) parameterType[i];
            } else if (parameterType[i] instanceof String) {
                type[i] = loadClass((String) parameterType[i]);
            } else {
                type[i] = parameterType[i].getClass();
            }
        }
        return type;
    }

    public static Field getField(Object object, String fieldName)
            throws NoSuchFieldException {
        return getField(object.getClass(), fieldName);
    }

    public static Field getField(Class<?> clazz, String fieldName)
            throws NoSuchFieldException {
        NoSuchFieldException exception = null;
        for (; clazz != null; clazz = clazz.getSuperclass()) {
            try {
                Field field = clazz.getDeclaredField(fieldName);
                if (!field.isAccessible()) {
                    field.setAccessible(true);
                }
                return field;
            } catch (NoSuchFieldException e) {
                if (exception == null) {
                    exception = e;
                }
            }
        }
        throw exception;
    }

    public static Object get(String className, String fieldName)
            throws ClassNotFoundException, NoSuchFieldException, IllegalAccessException {
        return getField(loadClass(className), fieldName).get(null);
    }

    public static Object get(Class<?> clazz, String fieldName)
            throws NoSuchFieldException, IllegalAccessException {
        return getField(clazz, fieldName).get(null);
    }

    public static Object get(Object object, String fieldName)
            throws NoSuchFieldException, IllegalAccessException {
        return getField(object.getClass(), fieldName).get(object);
    }

    public static void set(String className, String fieldName, Object value)
            throws NoSuchFieldException, IllegalAccessException, ClassNotFoundException {
        getField(loadClass(className), fieldName).set(null, value);
    }

    public static void set(Class<?> clazz, String fieldName, Object value)
            throws NoSuchFieldException, IllegalAccessException {
        getField(clazz, fieldName).set(null, value);
    }

    public static void set(Object object, String fieldName, Object value)
            throws NoSuchFieldException, IllegalAccessException {
        getField(object.getClass(), fieldName).set(object, value);
    }

    public static boolean getBoolean(String className, String fieldName)
            throws ClassNotFoundException, NoSuchFieldException, IllegalAccessException {
        return getField(loadClass(className), fieldName).getBoolean(null);
    }

    public static boolean getBoolean(Class<?> clazz, String fieldName)
            throws NoSuchFieldException, IllegalAccessException {
        return getField(clazz, fieldName).getBoolean(null);
    }

    public static boolean getBoolean(Object object, String fieldName)
            throws NoSuchFieldException, IllegalAccessException {
        return getField(object.getClass(), fieldName).getBoolean(object);
    }

    public static void setBoolean(String className, String fieldName, boolean value)
            throws NoSuchFieldException, IllegalAccessException, ClassNotFoundException {
        getField(loadClass(className), fieldName).setBoolean(null, value);
    }

    public static void setBoolean(Class<?> clazz, String fieldName, boolean value)
            throws NoSuchFieldException, IllegalAccessException {
        getField(clazz, fieldName).setBoolean(null, value);
    }

    public static void setBoolean(Object object, String fieldName, boolean value)
            throws NoSuchFieldException, IllegalAccessException {
        getField(object.getClass(), fieldName).setBoolean(object, value);
    }

    public static byte getByte(String className, String fieldName)
            throws ClassNotFoundException, NoSuchFieldException, IllegalAccessException {
        return getField(loadClass(className), fieldName).getByte(null);
    }

    public static byte getByten(Class<?> clazz, String fieldName)
            throws NoSuchFieldException, IllegalAccessException {
        return getField(clazz, fieldName).getByte(null);
    }

    public static byte getByte(Object object, String fieldName)
            throws NoSuchFieldException, IllegalAccessException {
        return getField(object.getClass(), fieldName).getByte(object);
    }

    public static void setBoolean(String className, String fieldName, byte value)
            throws NoSuchFieldException, IllegalAccessException, ClassNotFoundException {
        getField(loadClass(className), fieldName).setByte(null, value);
    }

    public static void setBoolean(Class<?> clazz, String fieldName, byte value)
            throws NoSuchFieldException, IllegalAccessException {
        getField(clazz, fieldName).setByte(null, value);
    }

    public static void setByte(Object object, String fieldName, byte value)
            throws NoSuchFieldException, IllegalAccessException {
        getField(object.getClass(), fieldName).setByte(object, value);
    }

    public static char getChar(String className, String fieldName)
            throws ClassNotFoundException, NoSuchFieldException, IllegalAccessException {
        return getField(loadClass(className), fieldName).getChar(null);
    }

    public static char getChar(Class<?> clazz, String fieldName)
            throws NoSuchFieldException, IllegalAccessException {
        return getField(clazz, fieldName).getChar(null);
    }

    public static char getChar(Object object, String fieldName)
            throws NoSuchFieldException, IllegalAccessException {
        return getField(object.getClass(), fieldName).getChar(object);
    }

    public static void setBoolean(String className, String fieldName, char value)
            throws NoSuchFieldException, IllegalAccessException, ClassNotFoundException {
        getField(loadClass(className), fieldName).setChar(null, value);
    }

    public static void setBoolean(Class<?> clazz, String fieldName, char value)
            throws NoSuchFieldException, IllegalAccessException {
        getField(clazz, fieldName).setChar(null, value);
    }

    public static void setChar(Object object, String fieldName, char value)
            throws NoSuchFieldException, IllegalAccessException {
        getField(object.getClass(), fieldName).setChar(object, value);
    }

    public static short getShort(String className, String fieldName)
            throws ClassNotFoundException, NoSuchFieldException, IllegalAccessException {
        return getField(loadClass(className), fieldName).getShort(null);
    }

    public static short getShort(Class<?> clazz, String fieldName)
            throws NoSuchFieldException, IllegalAccessException {
        return getField(clazz, fieldName).getShort(null);
    }

    public static short getShort(Object object, String fieldName)
            throws NoSuchFieldException, IllegalAccessException {
        return getField(object.getClass(), fieldName).getShort(object);
    }

    public static void setShort(String className, String fieldName, short value)
            throws NoSuchFieldException, IllegalAccessException, ClassNotFoundException {
        getField(loadClass(className), fieldName).setShort(null, value);
    }

    public static void setShort(Class<?> clazz, String fieldName, short value)
            throws NoSuchFieldException, IllegalAccessException {
        getField(clazz, fieldName).setShort(null, value);
    }

    public static void setShort(Object object, String fieldName, short value)
            throws NoSuchFieldException, IllegalAccessException {
        getField(object.getClass(), fieldName).setShort(object, value);
    }

    public static int getInt(String className, String fieldName)
            throws ClassNotFoundException, NoSuchFieldException, IllegalAccessException {
        return getField(loadClass(className), fieldName).getInt(null);
    }

    public static int getInt(Class<?> clazz, String fieldName)
            throws NoSuchFieldException, IllegalAccessException {
        return getField(clazz, fieldName).getInt(null);
    }

    public static int getInt(Object object, String fieldName)
            throws NoSuchFieldException, IllegalAccessException {
        return getField(object.getClass(), fieldName).getInt(object);
    }

    public static void setInt(String className, String fieldName, int value)
            throws NoSuchFieldException, IllegalAccessException, ClassNotFoundException {
        getField(loadClass(className), fieldName).setInt(null, value);
    }

    public static void setInt(Class<?> clazz, String fieldName, int value)
            throws NoSuchFieldException, IllegalAccessException {
        getField(clazz, fieldName).setInt(null, value);
    }

    public static void setInt(Object object, String fieldName, int value)
            throws NoSuchFieldException, IllegalAccessException {
        getField(object.getClass(), fieldName).setInt(object, value);
    }

    public static long getLong(String className, String fieldName)
            throws ClassNotFoundException, NoSuchFieldException, IllegalAccessException {
        return getField(loadClass(className), fieldName).getLong(null);
    }

    public static long getLong(Class<?> clazz, String fieldName)
            throws NoSuchFieldException, IllegalAccessException {
        return getField(clazz, fieldName).getLong(null);
    }

    public static long getLong(Object object, String fieldName)
            throws NoSuchFieldException, IllegalAccessException {
        return getField(object.getClass(), fieldName).getLong(object);
    }

    public static void setLong(String className, String fieldName, long value)
            throws NoSuchFieldException, IllegalAccessException, ClassNotFoundException {
        getField(loadClass(className), fieldName).setLong(null, value);
    }

    public static void setLong(Class<?> clazz, String fieldName, long value)
            throws NoSuchFieldException, IllegalAccessException {
        getField(clazz, fieldName).setLong(null, value);
    }

    public static void setLong(Object object, String fieldName, long value)
            throws NoSuchFieldException, IllegalAccessException {
        getField(object.getClass(), fieldName).setLong(object, value);
    }

    public static float getFloat(String className, String fieldName)
            throws ClassNotFoundException, NoSuchFieldException, IllegalAccessException {
        return getField(loadClass(className), fieldName).getFloat(null);
    }

    public static float getFloat(Class<?> clazz, String fieldName)
            throws NoSuchFieldException, IllegalAccessException {
        return getField(clazz, fieldName).getFloat(null);
    }

    public static float getFloat(Object object, String fieldName)
            throws NoSuchFieldException, IllegalAccessException {
        return getField(object.getClass(), fieldName).getFloat(object);
    }

    public static void setFloat(String className, String fieldName, float value)
            throws NoSuchFieldException, IllegalAccessException, ClassNotFoundException {
        getField(loadClass(className), fieldName).setFloat(null, value);
    }

    public static void setFloat(Class<?> clazz, String fieldName, float value)
            throws NoSuchFieldException, IllegalAccessException {
        getField(clazz, fieldName).setFloat(null, value);
    }

    public static void setFloat(Object object, String fieldName, float value)
            throws NoSuchFieldException, IllegalAccessException {
        getField(object.getClass(), fieldName).setFloat(object, value);
    }

    public static double getDouble(String className, String fieldName)
            throws ClassNotFoundException, NoSuchFieldException, IllegalAccessException {
        return getField(loadClass(className), fieldName).getDouble(null);
    }

    public static double getDouble(Class<?> clazz, String fieldName)
            throws NoSuchFieldException, IllegalAccessException {
        return getField(clazz, fieldName).getDouble(null);
    }

    public static double getDouble(Object object, String fieldName)
            throws NoSuchFieldException, IllegalAccessException {
        return getField(object.getClass(), fieldName).getDouble(object);
    }

    public static void setDouble(String className, String fieldName, double value)
            throws NoSuchFieldException, IllegalAccessException, ClassNotFoundException {
        getField(loadClass(className), fieldName).setDouble(null, value);
    }

    public static void setDouble(Class<?> clazz, String fieldName, double value)
            throws NoSuchFieldException, IllegalAccessException {
        getField(clazz, fieldName).setDouble(null, value);
    }

    public static void setDouble(Object object, String fieldName, double value)
            throws NoSuchFieldException, IllegalAccessException {
        getField(object.getClass(), fieldName).setDouble(object, value);
    }
}
