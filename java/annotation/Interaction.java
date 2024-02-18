package com.cfa.sts.integration.common.annotation;

import com.cfa.sts.integration.common.enums.DirectionEnum;
import com.cfa.sts.integration.common.enums.FunctionEnum;
import com.cfa.sts.integration.common.enums.TransportTypeEnum;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Interaction {
    DirectionEnum direction();
    String context();
    FunctionEnum function() default FunctionEnum.NA;
    TransportTypeEnum transport() default TransportTypeEnum.NA;
}