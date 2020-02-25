package cn.margele.mlproject;

import java.io.File;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Arrays;

public class MCLauncher {
	String[] args;

	public MCLauncher(String[] args) {
		this.args = args;
	}

	public void launch() {
		try {
			
			Class<?> clazz = Class.forName("net.minecraft.client.main.ModifyMain");
			Method m = clazz.getDeclaredMethod("launch", String[].class);
			m.invoke(clazz.newInstance(), (Object)concat(new String[] {"--version", "mcp", "--accessToken", "0", "--assetsDir", "assets\\", "--assetIndex", "1.8.9", "--userProperties", "{}" }, args));
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public <T> T[] concat(T[] first, T[] second) {
		T[] result = Arrays.copyOf(first, first.length + second.length);
		System.arraycopy(second, 0, result, first.length, second.length);
		return result;
	}
}
