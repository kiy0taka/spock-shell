package org.kiy0taka.spock.shell

import org.spockframework.runtime.extension.ExtensionAnnotation
import org.spockframework.runtime.extension.builtin.TimeoutExtension

import java.lang.annotation.Documented
import java.lang.annotation.ElementType
import java.lang.annotation.Inherited
import java.lang.annotation.Retention
import java.lang.annotation.RetentionPolicy
import java.lang.annotation.Target

/**
 * @author Kiyotaka Oku
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@ExtensionAnnotation(LocalDataExtension)
public @interface LocalData {

    String value()

}
