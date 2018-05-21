package com.azzgil.coeditor.annotations;

import org.springframework.context.annotation.Scope;

import java.lang.annotation.*;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Scope("singleton")
public @interface SingletonScope {
}
