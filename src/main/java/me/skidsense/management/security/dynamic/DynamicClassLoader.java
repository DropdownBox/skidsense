package me.skidsense.management.security.dynamic;

import java.net.URL;
import java.net.URLClassLoader;

public class DynamicClassLoader {

    public DynamicClass load(String path, String mainClass) throws Throwable{
        ClassLoader authorizedLoader = URLClassLoader.newInstance(new URL[] { new URL(path) });
        DynamicClass authorizedPlugin = (DynamicClass) authorizedLoader.loadClass(mainClass).newInstance();
        authorizedPlugin.onEnable();
        return authorizedPlugin;
    }
}