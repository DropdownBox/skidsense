package optifine;

import net.minecraft.client.renderer.OpenGlHelper;

import java.util.concurrent.Callable;

public class CrashReportCpu implements Callable<String> {
	public String call() {
		return OpenGlHelper.getCpu();
	}
}
