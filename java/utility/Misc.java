package com.cj.ssi.dropbox.loader.util;

import org.apache.commons.lang3.SerializationUtils;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Misc {

    public static <T extends Serializable> T DeepClone(final T object) {
        return SerializationUtils.clone(object);
    }

    // A string formatter that uses named parameters
    // String template = "Hello, ${fname} ${lname}"
    // Map<String, Object> parameters = new HashMap<>() {{
    //            put("fname", "foo");
    //            put("lname", "bar);
    //        }};
    public static String StringFormat(String template, Map<String, Object> parameters) {
        StringBuilder newTemplate = new StringBuilder(template);
        List<Object> valueList = new ArrayList<>();

        Matcher matcher = Pattern.compile("[$][{](\\w+)}").matcher(template);

        while (matcher.find()) {
            String key = matcher.group(1);

            String paramName = "${" + key + "}";
            int index = newTemplate.indexOf(paramName);
            if (index != -1) {
                newTemplate.replace(index, index + paramName.length(), "%s");
                valueList.add(parameters.get(key));
            }
        }

        return String.format(newTemplate.toString(), valueList.toArray());
    }

    public static boolean classContainsField(Class<?> clazz, String fieldName) {
        return getFieldFromClass(clazz, fieldName).isPresent();
    }

    public static boolean classContainsFieldOfType(Class<?> clazz, String fieldName, Class<?> fieldType) {
        for (Field field : clazz.getDeclaredFields()) {
            if (field.getName().equalsIgnoreCase(fieldName) &&
                field.getType().equals(fieldType)) {
                    return true;
            }
        }
        return false;
    }

    public static Optional<Field> getFieldFromClass(Class<?> clazz, String fieldName) {
        for (Field field : clazz.getDeclaredFields()) {
            if (field.getName().equalsIgnoreCase(fieldName)) {
                return Optional.of(field);
            }
        }
        return Optional.empty();
    }

    public static Optional<Field> getFieldFromClass(Class<?> clazz, String fieldName, Class<?> fieldType) {
        for (Field field : clazz.getDeclaredFields()) {
            if (field.getName().equalsIgnoreCase(fieldName) &&
                field.getType().equals(fieldType)) {
                return Optional.of(field);
            }
        }
        return Optional.empty();
    }
}
