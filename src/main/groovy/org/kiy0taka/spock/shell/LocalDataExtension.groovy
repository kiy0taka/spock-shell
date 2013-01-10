package org.kiy0taka.spock.shell

import org.apache.commons.io.FileUtils
import org.spockframework.runtime.extension.AbstractAnnotationDrivenExtension
import org.spockframework.runtime.extension.AbstractMethodInterceptor
import org.spockframework.runtime.extension.IMethodInvocation
import org.spockframework.runtime.model.FeatureInfo
import org.spockframework.runtime.model.SpecInfo

/**
 * @author Kiyotaka Oku
 */
class LocalDataExtension extends AbstractAnnotationDrivenExtension<LocalData> {

    @Override
    void visitFeatureAnnotation(LocalData annotation, FeatureInfo feature) {
        feature.getFeatureMethod().addInterceptor(new AbstractMethodInterceptor() {
            @Override
            void interceptFeatureMethod(IMethodInvocation invocation) throws Throwable {
                def res = feature.getParent().getReflection().getResource("/${annotation.value()}")
                if (res) {
                    FileUtils.copyDirectory(new File(res.toURI()), invocation.instance.workspace)
                } else {
                    throw new FileNotFoundException(annotation.value())
                }
                invocation.proceed()
            }
        })
    }
}
