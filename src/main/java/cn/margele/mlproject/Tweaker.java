package cn.margele.mlproject;

import java.io.File;
import java.util.List;

import net.minecraft.launchwrapper.ITweaker;
import net.minecraft.launchwrapper.LaunchClassLoader;

public class Tweaker implements ITweaker {
	public Tweaker() {
		System.out.println("Target Tweaker");
		new MCLauncher(new String[] {}).launch();
	}

	@Override
	public void acceptOptions(List<String> arg0, File arg1, File arg2, String arg3) {
		System.out.println(arg0);
		System.out.println(arg1);
		System.out.println(arg2);
		System.out.println(arg3);
	}

	@Override
	public String[] getLaunchArguments() {
		return new String[] {};
	}

	@Override
	public String getLaunchTarget() {
		return "";
	}

	@Override
	public void injectIntoClassLoader(LaunchClassLoader arg0) {

	}
}
