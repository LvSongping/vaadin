/* 
@VaadinApache2LicenseForJavaFiles@
 */

package com.vaadin.terminal.gwt.server;

import java.lang.reflect.Constructor;
import java.util.Properties;

import com.vaadin.terminal.DeploymentConfiguration;

public abstract class AbstractDeploymentConfiguration implements
        DeploymentConfiguration {

    private final Class<?> systemPropertyBaseClass;
    private final Properties applicationProperties = new Properties();

    public AbstractDeploymentConfiguration(Class<?> systemPropertyBaseClass) {
        this.systemPropertyBaseClass = systemPropertyBaseClass;
    }

    @Override
    public String getApplicationOrSystemProperty(String propertyName,
            String defaultValue) {

        String val = null;

        // Try application properties
        val = getApplicationProperty(propertyName);
        if (val != null) {
            return val;
        }

        // Try system properties
        val = getSystemProperty(propertyName);
        if (val != null) {
            return val;
        }

        return defaultValue;
    }

    /**
     * Gets an system property value.
     * 
     * @param parameterName
     *            the Name or the parameter.
     * @return String value or null if not found
     */
    protected String getSystemProperty(String parameterName) {
        String val = null;

        String pkgName;
        final Package pkg = systemPropertyBaseClass.getPackage();
        if (pkg != null) {
            pkgName = pkg.getName();
        } else {
            final String className = systemPropertyBaseClass.getName();
            pkgName = new String(className.toCharArray(), 0,
                    className.lastIndexOf('.'));
        }
        val = System.getProperty(pkgName + "." + parameterName);
        if (val != null) {
            return val;
        }

        // Try lowercased system properties
        val = System.getProperty(pkgName + "." + parameterName.toLowerCase());
        return val;
    }

    @Override
    public ClassLoader getClassLoader() {
        final String classLoaderName = getApplicationOrSystemProperty(
                "ClassLoader", null);
        ClassLoader classLoader;
        if (classLoaderName == null) {
            classLoader = getClass().getClassLoader();
        } else {
            try {
                final Class<?> classLoaderClass = getClass().getClassLoader()
                        .loadClass(classLoaderName);
                final Constructor<?> c = classLoaderClass
                        .getConstructor(new Class[] { ClassLoader.class });
                classLoader = (ClassLoader) c
                        .newInstance(new Object[] { getClass().getClassLoader() });
            } catch (final Exception e) {
                throw new RuntimeException(
                        "Could not find specified class loader: "
                                + classLoaderName, e);
            }
        }
        return classLoader;
    }

    /**
     * Gets an application property value.
     * 
     * @param parameterName
     *            the Name or the parameter.
     * @return String value or null if not found
     */
    protected String getApplicationProperty(String parameterName) {

        String val = applicationProperties.getProperty(parameterName);
        if (val != null) {
            return val;
        }

        // Try lower case application properties for backward compatibility with
        // 3.0.2 and earlier
        val = applicationProperties.getProperty(parameterName.toLowerCase());

        return val;
    }

    @Override
    public Properties getInitParameters() {
        return applicationProperties;
    }
}