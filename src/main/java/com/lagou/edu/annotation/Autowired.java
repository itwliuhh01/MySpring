package com.lagou.edu.annotation;

import java.lang.annotation.*;


@Target({ElementType.FIELD,ElementType.METHOD})
@Documented
@Retention(RetentionPolicy.RUNTIME)
public @interface Autowired {

}
