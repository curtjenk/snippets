package com.example.util;

import java.lang.invoke.*;
import java.lang.reflect.Field;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
/*
A way to read object fields without using java reflection
 */
public final class FastFieldReaderUtil {

    // Caching standard Function objects allows the JVM to inline the lambda execution completely
    private static final Map<String, Function<Object, Object>> LAMBDA_CACHE = new ConcurrentHashMap<>();

    private FastFieldReaderUtil() {}

    /**
     * Reads a field value using high-performance generated lambdas.
     */
    @SuppressWarnings("unchecked")
    public static Object getFieldValue(Object target, String fieldName) {
        if (target == null) {
            throw new IllegalArgumentException("Target object cannot be null");
        }

        Class<?> clazz = target.getClass();
        String cacheKey = clazz.getName() + "#" + fieldName;

        // Retrieve or generate the ultra-fast lambda bridge
        Function<Object, Object> getterLambda = LAMBDA_CACHE.computeIfAbsent(cacheKey, key -> {
            try {
                // 1. Target the field using legacy reflection first to find its type
                Field field = clazz.getDeclaredField(fieldName);
                Class<?> fieldType = field.getType();

                // 2. Escalate lookup privileges to private mode
                MethodHandles.Lookup privateLookup = MethodHandles.privateLookupIn(clazz, MethodHandles.lookup());

                // 3. Unreflect the field into a MethodHandle getter
                MethodHandle fieldGetterHandle = privateLookup.unreflectGetter(field);

                // 4. Spin a dynamic call site matching java.util.function.Function
                // Functional signature: Object apply(Object target)
                CallSite callSite = LambdaMetafactory.metafactory(
                        privateLookup,
                        "apply",
                        MethodType.methodType(Function.class),
                        MethodType.methodType(Object.class, Object.class), // Erasure signature
                        fieldGetterHandle,
                        MethodType.methodType(fieldType, clazz)           // Exact signature
                );

                // 5. Extract the generated Function instance
                return (Function<Object, Object>) callSite.getTarget().invokeExact();

            } catch (Throwable t) {
                throw new RuntimeException("Failed to generate fast lambda accessor for field: " + fieldName, t);
            }
        });

        // 6. Direct execution: This path compiles down to direct memory offsets via the JIT
        return getterLambda.apply(target);
    }
}
