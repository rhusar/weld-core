package org.jboss.weld.tests.metadata.scanning;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.Asset;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.jboss.weld.tests.metadata.Qux;
import org.jboss.weld.tests.metadata.scanning.jboss.Baz;
import org.junit.Test;
import org.junit.runner.RunWith;

import jakarta.enterprise.inject.spi.BeanManager;

import static org.jboss.weld.tests.metadata.scanning.Utils.createBeansXml;
import static org.jboss.weld.tests.metadata.scanning.Utils.escapePackageName;

@RunWith(Arquillian.class)
public class PackagePatternIncludeTest {

    public static final Asset BEANS_XML = createBeansXml(
            "<weld:scan>" +
                    "<weld:include pattern=\"^" + escapePackageName(Bar.class.getPackage()) + "\\.\\w+$\"/>" +
                    "</weld:scan>");

    @Deployment
    public static Archive<?> deployment() {
        return ShrinkWrap.create(JavaArchive.class).addClass(Utils.class)
                .addClasses(Bar.class, Foo.class, Baz.class, Qux.class, Corge.class)
                .addAsManifestResource(BEANS_XML, "beans.xml");
    }

    @Test
    public void test(BeanManager beanManager) {
        assert beanManager.getBeans(Baz.class).size() == 0;
        assert beanManager.getBeans(Qux.class).size() == 0;
        assert beanManager.getBeans(Foo.class).size() == 1;
        assert beanManager.getBeans(Bar.class).size() == 1;
        assert beanManager.getBeans(Corge.class).size() == 1;
    }

}
