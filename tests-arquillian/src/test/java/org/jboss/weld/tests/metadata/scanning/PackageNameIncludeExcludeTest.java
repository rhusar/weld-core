package org.jboss.weld.tests.metadata.scanning;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.Asset;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.jboss.weld.tests.metadata.Qux;
import org.jboss.weld.tests.metadata.scanning.jboss.Baz;
import org.jboss.weld.tests.metadata.scanning.jboss.Garply;
import org.junit.Test;
import org.junit.runner.RunWith;

import jakarta.enterprise.inject.spi.BeanManager;

import static org.jboss.weld.tests.metadata.scanning.Utils.createBeansXml;

@RunWith(Arquillian.class)
public class PackageNameIncludeExcludeTest {

    public static final Asset BEANS_XML = createBeansXml(
            "<weld:scan>" +
                    "<weld:exclude name=\"" + Bar.class.getPackage().getName() + ".*\"/>" +
                    "<weld:include name=\"" + Baz.class.getPackage().getName() + ".*\"/>" +
                    "</weld:scan>");

    @Deployment
    public static Archive<?> deployment() {
        return ShrinkWrap.create(JavaArchive.class).addClass(Utils.class)
                .addClasses(Bar.class, Foo.class, Baz.class, Qux.class, Corge.class, Garply.class)
                .addAsManifestResource(BEANS_XML, "beans.xml");
    }

    @Test
    public void test(BeanManager beanManager) {
        assert beanManager.getBeans(Qux.class).size() == 0;
        assert beanManager.getBeans(Foo.class).size() == 0;
        assert beanManager.getBeans(Baz.class).size() == 1;
        assert beanManager.getBeans(Garply.class).size() == 1;
        assert beanManager.getBeans(Bar.class).size() == 0;
        assert beanManager.getBeans(Corge.class).size() == 0;
    }

}
