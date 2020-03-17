import cf.kody.main.Main;

import java.util.Arrays;

public class Start {

	// 用GBK的是傻逼。如果你用GBK我杀了你的妈
	// 额 你能看到说明你使用了UTF-8，感谢你。

	public static void main(String[] args) {
		Main.main(concat(new String[]{"--version", "mcp", "--accessToken", "0", "--assetsDir", "C:/Users/Enterman/AppData/Roaming/.minecraft/assets", "--assetIndex", "1.8", "--userProperties", "{}"}, args));
	}

	public static <T> T[] concat(T[] first, T[] second) {
		T[] result = Arrays.copyOf(first, first.length + second.length);
		System.arraycopy(second, 0, result, first.length, second.length);
		return result;
	}
}
