package me.skidsense.management;

import me.skidsense.Client;
import me.skidsense.SplashProgress;
import me.skidsense.hooks.EventManager;
import me.skidsense.hooks.Sub;
import me.skidsense.hooks.events.EventKey;
import me.skidsense.hooks.events.EventRender3D;
import me.skidsense.hooks.events.EventRenderGui;
import me.skidsense.hooks.value.Mode;
import me.skidsense.hooks.value.Numbers;
import me.skidsense.hooks.value.Option;
import me.skidsense.hooks.value.Value;
import me.skidsense.module.Mod;
import me.skidsense.module.ModuleType;
import me.skidsense.module.collection.combat.AntiBot;
import me.skidsense.module.collection.combat.AutoArmor;
import me.skidsense.module.collection.combat.AutoPotion;
import me.skidsense.module.collection.combat.AutoSword;
import me.skidsense.module.collection.combat.Critical;
import me.skidsense.module.collection.combat.KillAura;
import me.skidsense.module.collection.move.*;
import me.skidsense.module.collection.player.*;
import me.skidsense.module.collection.visual.*;
import me.skidsense.module.collection.world.*;
import me.skidsense.util.GLUtils;

import java.lang.reflect.Field;
import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.renderer.GlStateManager;
import org.lwjgl.input.Keyboard;

@SuppressWarnings("unchecked")
public class ModManager
implements Manager {
    public static List<Mod> mods = new ArrayList<Mod>();
    public static ArrayList<Mod> sortedModList = new ArrayList<Mod>();
    private boolean enabledNeededMod = true;

    public void addMod(Mod mod){
        for (Field field : mod.getClass().getDeclaredFields()) {
            if(!field.isAccessible()){
                field.setAccessible(true);
            }
            Object obj;
            try {
                if((obj = field.get(mod)) instanceof Value){

                        mod.addValue((Value<?>) obj);
                }
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        EventManager.getInstance().register(mod);
        mods.add(mod);
    }
    @Override
    public void init() {
    	SplashProgress.setProgress(5, "ModuleManager Init");
        addMod(new AutoArmor());
        addMod(new AutoPotion());
        addMod(new AntiBot());
        addMod(new AutoSword());
        addMod(new Critical());
        addMod(new KillAura());
        addMod(new AutoStrafe());
        
        addMod(new Jesus());
        addMod(new Flight());
        addMod(new InvMove());
        addMod(new Step());
        addMod(new SafeWalk());
        addMod(new Sprint());
        addMod(new Speed());
        addMod(new NoSlow());

        addMod(new AntiFall());
        addMod(new AutoGG());
        addMod(new AntiVelocity());
        addMod(new Teams());
        addMod(new NoFall());
        addMod(new InvCleaner());
        addMod(new Blink());
        addMod(new Scaffold());
        addMod(new AntiDesync());
        addMod(new Teleport());
        
        addMod(new Animations());
        addMod(new ArmorStatus());
        addMod(new ClickGui());
        addMod(new Fullbright());
        addMod(new ItemEsp());
        addMod(new ESP());
        addMod(new HUD());
        addMod(new ChestESP());
        addMod(new Nametags());
        addMod(new TargetHUD());
        addMod(new KidFace());
        addMod(new Waypoints());
        addMod(new Chams());
        addMod(new AutoL());
        addMod(new ChestStealer());
        addMod(new NoRotate());
        addMod(new AutoTool());
        addMod(new SpeedMine());
        addMod(new Disabler());
        addMod(new ChatCommands());
        addMod(new LightningFinder());
        this.readSettings();
        EventManager.getOtherEventManager().register(this);
        if(!Client.getModuleManager().getModuleByClass(ChatCommands.class).isEnabled()) {
        	Client.getModuleManager().getModuleByClass(ChatCommands.class).setEnabled(true);
        }
        //EventBus.getInstance().register(this);
    }

    public static List<Mod> getMods() {
        return mods;
    }

    public Mod getModuleByClass(Class<? extends Mod> cls) {
        for (Mod m : mods) {
            if (m.getClass() != cls) continue;
            return m;
        }
        return null;
    }

    public Mod getModuleByName(String name) {
        for (Mod m : mods) {
            if (!m.getName().equalsIgnoreCase(name)) continue;
            return m;
        }
        return null;
    }

    public Mod getAlias(String name) {
        for (Mod f : mods) {
            if (f.getName().equalsIgnoreCase(name)) {
                return f;
            }
            String[] alias = f.getAlias();
            int length = alias.length;
            int i = 0;
            while (i < length) {
                String s = alias[i];
                if (s.equalsIgnoreCase(name)) {
                    return f;
                }
                ++i;
            }
        }
        return null;
    }

    public List<Mod> getModulesInType(ModuleType t) {
        ArrayList<Mod> output = new ArrayList<>();
        for (Mod m : mods) {
            if (m.getType() != t) continue;
            output.add(m);
        }
        return output;
    }

    @Sub
    private void onKeyPress(EventKey e) {
        for (Mod m : mods) {
            if (m.getKey() != e.getKey()) continue;
            m.setEnabled(!m.isEnabled());
        }
    }

    @Sub
    private void onGLHack(EventRender3D e) {
        GlStateManager.getFloat(2982, (FloatBuffer)GLUtils.MODELVIEW.clear());
        GlStateManager.getFloat(2983, (FloatBuffer)GLUtils.PROJECTION.clear());
        //GlStateManager.glGetInteger(2978, (IntBuffer)GLUtils.VIEWPORT.clear());
    }

    @Sub
    private void on2DRender(EventRenderGui e) {
        if (this.enabledNeededMod) {
            this.enabledNeededMod = false;
            for (Mod m : mods) {
                if (!m.enabledOnStartup) continue;
                m.setEnabled(true);
            }
        }
    }

    private void readSettings() {
        List<String> binds = FileManager.read("Binds.txt");
        for (String v : binds) {
            String name = v.split(":")[0];
            String bind = v.split(":")[1];
            Mod m = Client.getModuleManager().getModuleByName(name);
            if (m == null) continue;
            m.setKey(Keyboard.getKeyIndex(bind.toUpperCase()));
        }
        List<String> enabled = FileManager.read("Enabled.txt");
        for (String v : enabled) {
            Mod m = Client.getModuleManager().getModuleByName(v);
            if (m == null) continue;
            m.enabledOnStartup = true;
        }
        List<String> vals = FileManager.read("Values.txt");
        for (String v : vals) {
            String name = v.split(":")[0];
            String values = v.split(":")[1];
            Mod m = Client.getModuleManager().getModuleByName(name);
            if (m == null) continue;
            for (Value value : m.getValues()) {
                if (!value.getName().equalsIgnoreCase(values)) continue;
                if (value instanceof Option) {
                    value.setValue(Boolean.parseBoolean(v.split(":")[2]));
                    continue;
                }
                if (value instanceof Numbers) {
                    value.setValue(Double.parseDouble(v.split(":")[2]));
                    continue;
                }
                ((Mode)value).setMode(v.split(":")[2]);
            }
        }
    }

}

