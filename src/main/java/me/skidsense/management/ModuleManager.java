package me.skidsense.management;

import me.skidsense.Client;
import me.skidsense.SplashProgress;
import me.skidsense.hooks.EventBus;
import me.skidsense.hooks.EventHandler;
import me.skidsense.hooks.events.EventKey;
import me.skidsense.hooks.events.EventRender2D;
import me.skidsense.hooks.events.EventRender3D;
import me.skidsense.hooks.value.Mode;
import me.skidsense.hooks.value.Numbers;
import me.skidsense.hooks.value.Option;
import me.skidsense.hooks.value.Value;
import me.skidsense.management.FileManager;
import me.skidsense.management.Manager;
import me.skidsense.module.Module;
import me.skidsense.module.ModuleType;
import me.skidsense.module.collection.combat.AntiBot;
import me.skidsense.module.collection.combat.AutoArmor;
import me.skidsense.module.collection.combat.AutoSword;
import me.skidsense.module.collection.combat.Critical;
import me.skidsense.module.collection.combat.KillAura;
import me.skidsense.module.collection.move.AutoStrafe;
import me.skidsense.module.collection.move.Flight;
import me.skidsense.module.collection.move.InvMove;
import me.skidsense.module.collection.move.Jesus;
import me.skidsense.module.collection.move.NoSlow;
import me.skidsense.module.collection.move.SafeWalk;
import me.skidsense.module.collection.move.Speed;
import me.skidsense.module.collection.move.Sprint;
import me.skidsense.module.collection.move.Step;
import me.skidsense.module.collection.player.AntiFall;
import me.skidsense.module.collection.player.AntiVelocity;
import me.skidsense.module.collection.player.AutoTool;
import me.skidsense.module.collection.player.Blink;
import me.skidsense.module.collection.player.InvCleaner;
import me.skidsense.module.collection.player.NoFall;
import me.skidsense.module.collection.player.Scaffold;
import me.skidsense.module.collection.player.Teams;
import me.skidsense.module.collection.visual.*;
import me.skidsense.module.collection.world.AutoL;
import me.skidsense.module.collection.world.ChestStealer;
import me.skidsense.module.collection.world.NoRotate;
import me.skidsense.module.collection.world.SpeedMine;
import me.skidsense.util.GLUtils;

import java.lang.reflect.Field;
import java.nio.Buffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.renderer.GlStateManager;
import org.lwjgl.input.Keyboard;

public class ModuleManager
implements Manager {
    public static List<Module> modules = new ArrayList<Module>();
    public static ArrayList<Module> sortedModList = new ArrayList<Module>();
    private boolean enabledNeededMod = true;

    public void reflectionExample(Module module){
        for (Field field : module.getClass().getFields()) {
            if(field.getType().isAssignableFrom(Value.class)){
                // doSomething();
            }
        }
    }
    @Override
    public void init() {
    	SplashProgress.setProgress(5, "ModuleManager Init");
        this.modules.add(new AutoArmor());
        this.modules.add(new AntiBot());
        this.modules.add(new AutoSword());
        this.modules.add(new Critical());
        this.modules.add(new KillAura());
        this.modules.add(new AutoStrafe());
        
        this.modules.add(new Jesus());
        this.modules.add(new Flight());
        this.modules.add(new InvMove());
        this.modules.add(new Step());
        this.modules.add(new SafeWalk());
        this.modules.add(new Sprint());
        this.modules.add(new Speed());
        this.modules.add(new NoSlow());
        
        this.modules.add(new AntiFall());
        this.modules.add(new AntiVelocity());
        this.modules.add(new Teams());
        this.modules.add(new NoFall());
        this.modules.add(new InvCleaner());
        this.modules.add(new Blink());
        this.modules.add(new Scaffold());       
        
        this.modules.add(new Animations());
        this.modules.add(new ClickGui());
        this.modules.add(new Xray());
        this.modules.add(new ItemEsp());
        this.modules.add(new ESP());
        this.modules.add(new HUD());
        this.modules.add(new ChestESP());
        this.modules.add(new Nametags());
        this.modules.add(new TargetHUD());
        this.modules.add(new KidFace());
        
        this.modules.add(new AutoL());
        this.modules.add(new ChestStealer());
        this.modules.add(new NoRotate());
        this.modules.add(new AutoTool());
        this.modules.add(new SpeedMine());
        
        this.readSettings();
        for (Module m : modules) {
            m.makeCommand();
        }
        EventBus.getInstance().register(this);
    }

    public static List<Module> getModules() {
        return modules;
    }

    public Module getModuleByClass(Class<? extends Module> cls) {
        for (Module m : modules) {
            if (m.getClass() != cls) continue;
            return m;
        }
        return null;
    }

    public Module getModuleByName(String name) {
        for (Module m : modules) {
            if (!m.getName().equalsIgnoreCase(name)) continue;
            return m;
        }
        return null;
    }

    public Module getAlias(String name) {
        for (Module f : modules) {
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

    public List<Module> getModulesInType(ModuleType t) {
        ArrayList<Module> output = new ArrayList<Module>();
        for (Module m : modules) {
            if (m.getType() != t) continue;
            output.add(m);
        }
        return output;
    }

    @EventHandler
    private void onKeyPress(EventKey e) {
        for (Module m : modules) {
            if (m.getKey() != e.getKey()) continue;
            m.setEnabled(!m.isEnabled());
        }
    }

    @EventHandler
    private void onGLHack(EventRender3D e) {
        GlStateManager.getFloat(2982, (FloatBuffer)GLUtils.MODELVIEW.clear());
        GlStateManager.getFloat(2983, (FloatBuffer)GLUtils.PROJECTION.clear());
        GlStateManager.glGetInteger(2978, (IntBuffer)GLUtils.VIEWPORT.clear());
    }

    @EventHandler
    private void on2DRender(EventRender2D e) {
        if (this.enabledNeededMod) {
            this.enabledNeededMod = false;
            for (Module m : modules) {
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
            Module m = Client.getModuleManager().getModuleByName(name);
            if (m == null) continue;
            m.setKey(Keyboard.getKeyIndex((String)bind.toUpperCase()));
        }
        List<String> enabled = FileManager.read("Enabled.txt");
        for (String v : enabled) {
            Module m = Client.getModuleManager().getModuleByName(v);
            if (m == null) continue;
            m.enabledOnStartup = true;
        }
        List<String> vals = FileManager.read("Values.txt");
        for (String v : vals) {
            String name = v.split(":")[0];
            String values = v.split(":")[1];
            Module m = Client.getModuleManager().getModuleByName(name);
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

