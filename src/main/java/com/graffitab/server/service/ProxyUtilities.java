package com.graffitab.server.service;

import org.hibernate.proxy.HibernateProxy;
import org.hibernate.proxy.LazyInitializer;
import org.hibernate.proxy.pojo.javassist.JavassistLazyInitializer;

public class ProxyUtilities {


	public static Class<?> unwrapProxyAndGetClass(Object mayBeProxy) {

        Class<?> entityClass;
        Class<?> objectClass = mayBeProxy.getClass();

        if (mayBeProxy.getClass() == JavassistLazyInitializer.class || mayBeProxy instanceof HibernateProxy) {
            LazyInitializer lazyInitializer;

            if (mayBeProxy instanceof HibernateProxy) {
                lazyInitializer = ((HibernateProxy) mayBeProxy).getHibernateLazyInitializer();
            } else {
                lazyInitializer = (LazyInitializer) mayBeProxy;
            }

            entityClass = lazyInitializer.getImplementation().getClass();

        } else if (objectClass.getName().contains("$$")) {
        	// "$$" => this is a CGLib proxy
        	// CGLib generates subclasses of the desired class, so
        	// pull the superclass to get the actual class.
        	entityClass = objectClass.getSuperclass();

        } else {
            entityClass = mayBeProxy.getClass();
        }

        return entityClass;
    }

	public static Object unwrapProxy(Object proxy) {

		if (proxy == null) {
			return null;
		}

        if (proxy instanceof HibernateProxy) {
            LazyInitializer lazyInitializer = ((HibernateProxy) proxy).getHibernateLazyInitializer();
            return lazyInitializer.getImplementation();
        }
        return proxy;
    }
}
