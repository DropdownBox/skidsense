package me.skidsense.module;

import me.skidsense.Client;
import me.skidsense.hooks.value.Mode;
import me.skidsense.hooks.value.Numbers;
import me.skidsense.hooks.value.Option;
import me.skidsense.hooks.value.Value;
import me.skidsense.management.FileManager;
import me.skidsense.management.ModManager;
import me.skidsense.management.animation.Translate;
import me.skidsense.management.notifications.Notifications;
import me.skidsense.util.ChatUtil;
import me.skidsense.util.MathUtil;
import net.minecraft.client.Minecraft;
import org.lwjgl.input.Keyboard;

import java.util.*;

public class Mod {
	public boolean keepReg;
	public String name;
	public static String clickguicategory;
	public static String clickguimodname;
	public static String clickguivaluename;
	public static String clientname = "Hide";
	public static int lastX;
	public static int lastY;
	private String suffix;
	private int color;
	private String[] alias;
	private boolean enabled;
	public boolean enabledOnStartup = false;
	private int key, anim;
	public List<Value<?>> values;
	public ModuleType type;
	protected boolean removed;
	public static Minecraft mc = Minecraft.getMinecraft();
	public static Random random = new Random();
	public Translate translate = new Translate(0.0F, 0.0F);

	public Mod(String name, String[] alias, ModuleType type) {
		this.name = name;
		this.alias = alias;
		this.type = type;
		this.suffix = "";
		this.key = 0;
		this.removed = false;
		this.enabled = false;
		this.keepReg = false;
		this.values = new ArrayList<>();
	}

	public String getName() {
		return this.name;
	}

	public static String getClientName() {
		return Mod.clientname;
	}

	public static String setClientName(String s) {
		Mod.clientname = s;
		return null;
	}

	public String[] getAlias() {
		return this.alias;
	}

	public ModuleType getType() {
		return this.type;
	}

	public boolean isEnabled() {
		return this.enabled;
	}

	public boolean wasRemoved() {
		return this.removed;
	}

	public void setRemoved(boolean removed) {
		this.removed = removed;
	}

	public String getSuffix() {
		return this.suffix;
	}

	public void setSuffix(Object obj) {
		String suffix = obj.toString();
		this.suffix = " " + suffix;

	}

	public int getAnim() {
		return anim;
	}

	public void setAnim(int anim) {
		this.anim = anim;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
		if (enabled) {
			this.onEnable();
			if (anim == -1) {
				anim = 0;
			}
			//EventBus.getInstance().register(this);
			Notifications.getManager().post(this.getName() + " §aEnabled");
		} else {
			//EventBus.getInstance().unregister(this);
			Notifications.getManager().post(this.getName() + " §cDisabled");
			this.onDisable();
		}

	}
    
	public void setColor(int color) {
		this.color = color;
	}

	public int getColor() {
		return this.color;
	}

	//public void addValues(Value<?>... values) {
		//int var4 = values.length;

	//	this.values.addAll(Arrays.asList(values));

	//}
	public void addValue(Value<?> value){
		this.values.add(value);
	}

	public List<Value<?>> getValues() {
		return this.values;
	}

	public int getKey() {
		return this.key;
	}

	public void setKey(int key) {
		this.key = key;
		String content = "";

		Mod m;
		for (Iterator var4 = Client.instance.getModuleManager().getMods().iterator(); var4.hasNext(); content = content + String.format("%s:%s%s", m.getName(), Keyboard.getKeyName(m.getKey()), System.lineSeparator())) {
			m = (Mod) var4.next();
		}

		FileManager.save("Binds.txt", content, false);
	}

	public void onEnable() {
	}

	public void onDisable() {
	}
}

