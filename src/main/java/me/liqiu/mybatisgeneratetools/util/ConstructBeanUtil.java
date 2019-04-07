package me.liqiu.mybatisgeneratetools.util;

import com.google.common.collect.Streams;
import org.apache.commons.beanutils.ConvertUtils;
import org.apache.commons.beanutils.converters.ArrayConverter;
import org.apache.commons.beanutils.converters.StringConverter;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;

public abstract class ConstructBeanUtil {

    private static final ArrayConverter arrayConverter = new ArrayConverter(String[].class, new StringConverter());

    public static <T> T constructObjectWithCommaListArgs(String className, String commaListArgs) {
        String[] args = arrayConverter.convert(String[].class, commaListArgs);
        return (T) constructObject(className, args);
    }

    public static <T> T constructObject(String className, String... args) {
        try {
            Class<?> clazz = Class.forName(className);
            Constructor<?> constructor = getConstructorByArgsLength(clazz, args.length);
            Object[] params = Streams.zip(
                    Arrays.stream(args),
                    Arrays.stream(constructor.getParameterTypes()),
                    ConvertUtils::convert).toArray();
            return (T) constructor.newInstance(params);
        } catch (ClassNotFoundException
                    | InstantiationException
                    | IllegalAccessException
                    | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }

    private static Constructor<?> getConstructorByArgsLength(Class<?> clazz, int length) {
        Constructor<?>[] constructors = clazz.getDeclaredConstructors();
        Constructor<?>[] matchedConstructors = Arrays.stream(constructors)
                .filter(constructor -> constructor.getParameterCount() == length)
                .toArray(Constructor[]::new);
        if(matchedConstructors.length != 1) {
            throw new RuntimeException("No matched constructor for given argument length");
        }
        return matchedConstructors[0];
    }
}
