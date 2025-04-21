package org.jboss.weld.lite.extension.translator;

import jakarta.enterprise.inject.build.compatible.spi.ScannedClasses;
import org.jboss.weld.lite.extension.translator.logging.LiteExtensionTranslatorLogger;

class ScannedClassesImpl implements ScannedClasses {
    private final jakarta.enterprise.inject.spi.BeforeBeanDiscovery bbd;
    private final ClassLoader cl;

    ScannedClassesImpl(jakarta.enterprise.inject.spi.BeforeBeanDiscovery bbd, ClassLoader cl) {
        this.bbd = bbd;
        this.cl = cl;
    }

    @Override
    public void add(String className) {
        try {
            bbd.addAnnotatedType(Class.forName(className, true, cl), className);
        } catch (ClassNotFoundException e) {
            throw LiteExtensionTranslatorLogger.LOG.cannotLoadClassByName(className, e.toString(), e);
        }
    }
}
