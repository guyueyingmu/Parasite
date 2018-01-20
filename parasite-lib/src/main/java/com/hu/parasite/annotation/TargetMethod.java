package com.hu.parasite.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by HuJi on 2018/1/20.
 *
 * <p>
 * e.g. <br/>
 * &nbsp class: "android.content.pm.IPackageManager$Stub$Proxy" <br/>
 * &nbsp method: "getApplicationInfo" <br/>
 * &nbsp params: "java.lang.String", "int", "int" <br/>
 * &nbsp value: "android.content.pm.IPackageManager$Stub$Proxy##getApplicationInfo##java.lang.String,int,int"
 * </p>
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface TargetMethod {
    String value();
}
