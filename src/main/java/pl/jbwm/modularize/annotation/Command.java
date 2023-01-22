package pl.jbwm.modularize.annotation;


import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface Command {

    String name();
    String usage();
    String permission() default "";
    String permissionMessage() default "";
    String[] aliases() default {};



}
