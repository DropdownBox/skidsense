package me.skidsense.module;

import me.skidsense.Client;
import me.skidsense.command.Command;
import me.skidsense.hooks.EventBus;
import me.skidsense.hooks.value.Mode;
import me.skidsense.hooks.value.Numbers;
import me.skidsense.hooks.value.Option;
import me.skidsense.hooks.value.Value;
import me.skidsense.management.FileManager;
import me.skidsense.management.ModuleManager;
import me.skidsense.management.notifications.Notifications;
import me.skidsense.util.MathUtil;
import net.minecraft.client.Minecraft;
import org.lwjgl.input.Keyboard;

import java.util.*;

public class Module {
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

	public Module(String name, String[] alias, ModuleType type) {
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
		return Module.clientname;
	}

	public static String setClientName(String s) {
		Module.clientname = s;
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
			EventBus.getInstance().register(this);
			Notifications.getManager().post(this.getName() + " §aEnabled");
		} else {
			EventBus.getInstance().unregister(this);
			Notifications.getManager().post(this.getName() + " §cDisable");
			this.onDisable();
		}

	}

	public void setColor(int color) {
		this.color = color;
	}

	public int getColor() {
		return this.color;
	}

	protected void addValues(Value<?>... values) {
		int var4 = values.length;

		this.values.addAll(Arrays.asList(values).subList(0, var4));

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

		Module m;
		for (Iterator var4 = ModuleManager.getModules().iterator(); var4.hasNext(); content = content + String.format("%s:%s%s", m.getName(), Keyboard.getKeyName(m.getKey()), System.lineSeparator())) {
			m = (Module) var4.next();
		}

		FileManager.save("Binds.txt", content, false);
	}

	public void onEnable() {
	}

	public void onDisable() {
	}

	public void makeCommand() {
		if (this.values.size() > 0) {
			StringBuilder options = new StringBuilder();
			StringBuilder other = new StringBuilder();
			Iterator var4 = this.values.iterator();

			Value v;
			while (var4.hasNext()) {
				v = (Value) var4.next();
				if (!(v instanceof Mode)) {
					if (options.length() == 0) {
						options.append(v.getName());
					} else {
						options.append(String.format(", %s", v.getName()));
					}
				}
			}

			var4 = this.values.iterator();

			while (true) {
				do {
					if (!var4.hasNext()) {
						Client.instance.getCommandManager().add(new Module$1(this, this.name, this.alias, String.format("%s%s", (options.length() == 0) ? "" : String.format("%s,", options.toString()), (other.length() == 0) ? "" : String.format("%s", other.toString())), "Setup this module"));
						return;
					}

					v = (Value) var4.next();
				} while (!(v instanceof Mode));

				Mode mode = (Mode) v;
				Enum[] modes;
				int length = (modes = mode.getModes()).length;

				for (int i = 0; i < length; ++i) {
					Enum e = modes[i];
					if (other.length() == 0) {
						other.append(e.name().toLowerCase());
					} else {
						other.append(String.format(", %s", e.name().toLowerCase()));
					}
				}
			}
		}
	}
}

class Module$1 extends Command {
	private final Module m;
	final Module this$0;

	Module$1(Module var1, String $anonymous0, String[] $anonymous1, String $anonymous2, String $anonymous3) {
		super($anonymous0, $anonymous1, $anonymous2, $anonymous3);
		this.this$0 = var1;
		this.m = var1;
	}

	public String execute(String[] args) {
		Option option;
		if (args.length >= 2) {
			option = null;
			Numbers fuck = null;
			Mode xd = null;
			Iterator var6 = this.m.values.iterator();

			Value v;
			while (var6.hasNext()) {
				v = (Value) var6.next();
				if (v instanceof Option && v.getName().equalsIgnoreCase(args[0])) {
					option = (Option) v;
				}
			}

			if (option != null) {
				option.setValue(Boolean.valueOf(!((Boolean) option.getValue()).booleanValue()));
				Client.sendMessage(String.format("> %s has been set to %s", option.getName(), option.getValue()));
			} else {
				var6 = this.m.values.iterator();

				while (var6.hasNext()) {
					v = (Value) var6.next();
					if (v instanceof Numbers && v.getName().equalsIgnoreCase(args[0])) {
						fuck = (Numbers) v;
					}
				}

				if (fuck != null) {
					if (MathUtil.parsable(args[1], (byte) 4)) {
						double v1 = MathUtil.round(Double.parseDouble(args[1]), 1);
						fuck.setValue(Double.valueOf(v1 > fuck.getMaximum().doubleValue() ? fuck.getMaximum().doubleValue() : v1));
						Client.sendMessage(String.format("> %s has been set to %s", fuck.getName(), fuck.getValue()));
					} else {
						Client.sendMessage("> " + args[1] + " is not a number");
					}
				}

				var6 = this.m.values.iterator();

				while (var6.hasNext()) {
					v = (Value) var6.next();
					if (args[0].equalsIgnoreCase(v.getDisplayName()) && v instanceof Mode) {
						xd = (Mode) v;
					}
				}

				if (xd != null) {
					if (xd.isValid(args[1])) {
						xd.setMode(args[1]);
						Client.sendMessage(String.format("> %s set to %s", xd.getName(), xd.getModeAsString()));
					} else {
						Client.sendMessage("> " + args[1] + " is an invalid mode");
					}
				}
			}

			if (fuck == null && option == null && xd == null) {
				this.syntaxError("Valid .<module> <setting> <mode if needed>");
			}
		} else if (args.length >= 1) {
			option = null;
			Iterator xd1 = this.m.values.iterator();

			while (xd1.hasNext()) {
				Value fuck1 = (Value) xd1.next();
				if (fuck1 instanceof Option && fuck1.getName().equalsIgnoreCase(args[0])) {
					option = (Option) fuck1;
				}
			}

			if (option != null) {
				option.setValue(Boolean.valueOf(!((Boolean) option.getValue()).booleanValue()));
				String fuck2 = option.getName().substring(1);
				String xd2 = option.getName().substring(0, 1).toUpperCase();
				if (((Boolean) option.getValue()).booleanValue()) {
					Client.sendMessage(String.format("> %s has been set to \u00a7a%s", xd2 + fuck2, option.getValue()));
				} else {
					Client.sendMessage(String.format("> %s has been set to \u00a7c%s", xd2 + fuck2, option.getValue()));
				}
			} else {
				this.syntaxError("Valid .<module> <setting> <mode if needed>");
			}
		} else {
			Client.sendMessage(String.format("%s Values: \n %s", this.getName().substring(0, 1).toUpperCase() + this.getName().substring(1).toLowerCase(), this.getSyntax(), "false"));
		}

		return null;
	}
}

