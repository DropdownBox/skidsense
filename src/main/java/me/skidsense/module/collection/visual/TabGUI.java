package me.skidsense.module.collection.visual;

import java.awt.Color;
import java.util.List;

import me.skidsense.Client;
import me.skidsense.SplashProgress;
import me.skidsense.hooks.EventBus;
import me.skidsense.hooks.EventHandler;
import me.skidsense.hooks.events.EventRender2D;
import me.skidsense.hooks.events.EventKey;
import me.skidsense.hooks.value.Mode;
import me.skidsense.hooks.value.Numbers;
import me.skidsense.hooks.value.Option;
import me.skidsense.hooks.value.Value;
import me.skidsense.management.Manager;
import me.skidsense.management.ModuleManager;
import me.skidsense.management.fontRenderer.UnicodeFontRenderer;
import me.skidsense.module.Module;
import me.skidsense.module.ModuleType;
import me.skidsense.module.collection.visual.HUD;
import me.skidsense.util.MathUtil;
import me.skidsense.util.RenderUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.settings.GameSettings;

public class TabGUI
implements Manager {
    private Section section = Section.TYPES;
    private ModuleType selectedType = ModuleType.values()[0];
    private Module selectedModule = null;
    private Value selectedValue = null;
    private int currentType = 0;
    private int currentModule = 0;
    private int currentValue = 0;
    private int height = 12;
    private int maxType;
    private int maxModule;
    private int maxValue;
    private static /* synthetic */ int[] $SWITCH_TABLE$com$enjoytheban$module$modules$render$UI$TabUI$Section;

    @Override
    public void init() {
    	SplashProgress.setProgress(4, "TabGui Init");
        ModuleType[] arrmoduleType = ModuleType.values();
        int n = arrmoduleType.length;
        int n2 = 0;
        while (n2 < n) {
            ModuleType mt = arrmoduleType[n2];
            if (this.maxType <= Client.mc.fontRendererObj.getStringWidth(mt.name().toUpperCase()) + 4) {
                this.maxType = Client.mc.fontRendererObj.getStringWidth(mt.name().toUpperCase()) + 4;
            }
            ++n2;
        }
        Client.instance.getModuleManager();
        for (Module m : ModuleManager.getModules()) {
        	UnicodeFontRenderer font = Client.fontManager.verdana16;
            if (this.maxModule > font.getStringWidth(m.getName().toUpperCase()) + 4) continue;
            this.maxModule = font.getStringWidth(m.getName().toUpperCase()) + 4;
        }
        Client.instance.getModuleManager();
        for (Module m : ModuleManager.getModules()) {
            if (m.getValues().isEmpty()) continue;
            for (Value val : m.getValues()) {
                if (this.maxValue > Client.mc.fontRendererObj.getStringWidth(val.getDisplayName().toUpperCase()) + 4) continue;
                this.maxValue = Client.mc.fontRendererObj.getStringWidth(val.getDisplayName().toUpperCase()) + 4;
            }
        }
        this.maxModule += 12;
        this.maxValue += 24;
        boolean highestWidth = false;
        this.maxType = this.maxType < this.maxModule ? this.maxModule : this.maxType;
        this.maxModule += this.maxType;
        this.maxValue += this.maxModule;
        EventBus.getInstance().register(this);
    }

    private void resetValuesLength() {
        this.maxValue = 0;
        for (Value val : this.selectedModule.getValues()) {
            int off;
            int n = off = val instanceof Option ? 6 : Client.mc.fontRendererObj.getStringWidth(String.format(" \u00a77%s", val.getValue().toString())) + 6;
            if (this.maxValue > Client.mc.fontRendererObj.getStringWidth(val.getDisplayName().toUpperCase()) + off) continue;
            this.maxValue = Client.mc.fontRendererObj.getStringWidth(val.getDisplayName().toUpperCase()) + off;
        }
        this.maxValue += this.maxModule;
    }

    @EventHandler
    private void renderTabGUI(EventRender2D e) {
                if (!Client.mc.gameSettings.showDebugInfo && Client.instance.getModuleManager().getModuleByClass(HUD.class).isEnabled()) {
                int categoryY = this.height;
                int moduleY = categoryY;
                int valueY = categoryY;
            	UnicodeFontRenderer font = Client.fontManager.zeroarr;
            	//Gui.drawRect(2, 2, 49, 13, new Color(0, 0, 0, 100).getRGB());
            	 Gui.drawRect(3,15, this.maxType+18, categoryY+this.maxType+21,new Color(16, 16, 16, 250).getRGB());
               // RenderUtil.drawRect(4,12,47,13,new Color(255,255,255).getRGB());
                ModuleType[] moduleArray = ModuleType.values();
                int mA = moduleArray.length;
                int mA2 = 0;
                while (mA2 < mA) {
                    ModuleType mt = moduleArray[mA2];
                    if (this.selectedType == mt) {
                    	 Gui.drawRect(5, (double)categoryY+5, (double)this.maxType-34, (double)(categoryY + font.FONT_HEIGHT) +4, new Color(220,0,0).getRGB());
                        moduleY = categoryY;
                    }
                    if (this.selectedType == mt) {
                    	Client.fontManager.zeroarr.drawStringWithShadow(mt.name(), 8.0f, categoryY + (float)4, -1);
                    } else {
                    	Client.fontManager.zeroarr.drawStringWithShadow(mt.name(), 6.0f, categoryY + (float)4, new Color(180, 180, 180).getRGB());
                    }
                    categoryY += 11;
                    ++mA2;
                }
                if (this.section == Section.MODULES || this.section == Section.VALUES) {
                    RenderUtil.drawRect(this.maxType+19, moduleY, this.maxModule+70, moduleY + 12 * Client.instance.getModuleManager().getModulesInType(this.selectedType).size()+4, new Color(20, 20, 20, 220).getRGB());
                    for (Module m : Client.instance.getModuleManager().getModulesInType(this.selectedType)) {
                        if (this.selectedModule == m) {
                        	RenderUtil.rectangle((double)this.maxType+20, (double)moduleY +2, (double)this.maxModule +9, (double)(moduleY + Client.mc.fontRendererObj.FONT_HEIGHT) + 5, new Color(200,0,0).getRGB());
                            valueY = moduleY;
                        }
                        if (this.selectedModule == m) {
                            Client.fontManager.zeroarr.drawStringWithShadow(m.getName(), this.maxType+22, moduleY + 2, m.isEnabled() ? -1 : 11184810);
                        } else {
                        	Client.fontManager.zeroarr.drawStringWithShadow(m.getName(), this.maxType+22, moduleY + 2, m.isEnabled() ? -1 : 11184810);
                        }
                        if (!m.getValues().isEmpty()) {
                            if (this.section == Section.VALUES && this.selectedModule == m) {
                                RenderUtil.drawRect(this.maxModule +72, valueY, this.maxValue +70, valueY + 12 * this.selectedModule.getValues().size()+4, new Color(20,20,20,220).getRGB());
                                for (Value val : this.selectedModule.getValues()) {
                                	if(this.selectedValue == val) {
                                	RenderUtil.rectangle((double)this.maxModule +74, (double)valueY+2, (double)this.maxValue +68, (double)(valueY + Client.mc.fontRendererObj.FONT_HEIGHT) + 5, new Color(200,0,0,120).getRGB());
                                	}if (val instanceof Option) {
                                    	Client.fontManager.zeroarr.drawStringWithShadow(val.getDisplayName(), this.selectedValue == val ? this.maxModule +75 : this.maxModule +75, valueY + 2, (Boolean)val.getValue() != false ? new Color(255,255, 255).getRGB() : 11184810);
                                    } else {
                                        if (this.selectedValue == val) {
                                        	Client.fontManager.zeroarr.drawStringWithShadow(val.getDisplayName()+":"+val.getValue(), this.maxModule+75, valueY + 2, -1);
                                        } else {
                                        	Client.fontManager.zeroarr.drawStringWithShadow(val.getDisplayName()+":"+val.getValue(), this.maxModule+75, valueY + 2, -1);
                                        }
                                    }
                                    valueY += 12;
                                }
                            }
                        }
                        moduleY += 12;
                    }
                }
            	//Gui.drawRect(2, 2, 49, 13, new Color(0, 0, 0, 100).getRGB());
            }
        }

    @EventHandler
    private void onKey(EventKey e) {
        if (!Client.mc.gameSettings.showDebugInfo) {
            block0 : switch (e.getKey()) {
                case 208: {
                    switch (TabGUI.$SWITCH_TABLE$com$enjoytheban$module$modules$render$UI$TabUI$Section()[this.section.ordinal()]) {
                        case 1: {
                            ++this.currentType;
                            if (this.currentType > ModuleType.values().length - 1) {
                                this.currentType = 0;
                            }
                            this.selectedType = ModuleType.values()[this.currentType];
                            break block0;
                        }
                        case 2: {
                            ++this.currentModule;
                            if (this.currentModule > Client.instance.getModuleManager().getModulesInType(this.selectedType).size() - 1) {
                                this.currentModule = 0;
                            }
                            this.selectedModule = Client.instance.getModuleManager().getModulesInType(this.selectedType).get(this.currentModule);
                            break block0;
                        }
                        case 3: {
                            ++this.currentValue;
                            if (this.currentValue > this.selectedModule.getValues().size() - 1) {
                                this.currentValue = 0;
                            }
                            this.selectedValue = this.selectedModule.getValues().get(this.currentValue);
                        }
                    }
                    break;
                }
                case 200: {
                    switch (TabGUI.$SWITCH_TABLE$com$enjoytheban$module$modules$render$UI$TabUI$Section()[this.section.ordinal()]) {
                        case 1: {
                            --this.currentType;
                            if (this.currentType < 0) {
                                this.currentType = ModuleType.values().length - 1;
                            }
                            this.selectedType = ModuleType.values()[this.currentType];
                            break block0;
                        }
                        case 2: {
                            --this.currentModule;
                            if (this.currentModule < 0) {
                                this.currentModule = Client.instance.getModuleManager().getModulesInType(this.selectedType).size() - 1;
                            }
                            this.selectedModule = Client.instance.getModuleManager().getModulesInType(this.selectedType).get(this.currentModule);
                            break block0;
                        }
                        case 3: {
                            --this.currentValue;
                            if (this.currentValue < 0) {
                                this.currentValue = this.selectedModule.getValues().size() - 1;
                            }
                            this.selectedValue = this.selectedModule.getValues().get(this.currentValue);
                        }
                    }
                    break;
                }
                case 205: {
                    switch (TabGUI.$SWITCH_TABLE$com$enjoytheban$module$modules$render$UI$TabUI$Section()[this.section.ordinal()]) {
                        case 1: {
                            this.currentModule = 0;
                            this.selectedModule = Client.instance.getModuleManager().getModulesInType(this.selectedType).get(this.currentModule);
                            this.section = Section.MODULES;
                            break block0;
                        }
                        case 2: {
                            if (this.selectedModule.getValues().isEmpty()) break block0;
                            this.resetValuesLength();
                            this.currentValue = 0;
                            this.selectedValue = this.selectedModule.getValues().get(this.currentValue);
                            this.section = Section.VALUES;
                            break block0;
                        }
                        case 3: {
                            if (Client.onServer("enjoytheban")) break block0;
                            if (this.selectedValue instanceof Option) {
                                this.selectedValue.setValue((Boolean)this.selectedValue.getValue() == false);
                            } else if (this.selectedValue instanceof Numbers) {
                                Numbers value = (Numbers)this.selectedValue;
                                double inc = (Double)value.getValue();
                                inc += ((Double)value.getIncrement()).doubleValue();
                                if ((inc = MathUtil.toDecimalLength(inc, 1)) > (Double)value.getMaximum()) {
                                    inc = (Double)((Numbers)this.selectedValue).getMinimum();
                                }
                                this.selectedValue.setValue(inc);
                            } else if (this.selectedValue instanceof Mode) {
                                Mode theme = (Mode)this.selectedValue;
                                Enum current = (Enum)theme.getValue();
                                int next = current.ordinal() + 1 >= theme.getModes().length ? 0 : current.ordinal() + 1;
                                this.selectedValue.setValue(theme.getModes()[next]);
                            }
                            this.resetValuesLength();
                        }
                    }
                    break;
                }
                case 28: {
                    switch (TabGUI.$SWITCH_TABLE$com$enjoytheban$module$modules$render$UI$TabUI$Section()[this.section.ordinal()]) {
                        case 1: {
                            break block0;
                        }
                        case 2: {
                            this.selectedModule.setEnabled(!this.selectedModule.isEnabled());
                            break block0;
                        }
                        case 3: {
                            this.section = Section.MODULES;
                        }
                    }
                    break;
                }
                case 203: {
                    switch (TabGUI.$SWITCH_TABLE$com$enjoytheban$module$modules$render$UI$TabUI$Section()[this.section.ordinal()]) {
                        case 1: {
                            break block0;
                        }
                        case 2: {
                            this.section = Section.TYPES;
                            this.currentModule = 0;
                            break block0;
                        }
                        case 3: {
                            if (Client.onServer("enjoytheban")) break block0;
                            if (this.selectedValue instanceof Option) {
                                this.selectedValue.setValue((Boolean)this.selectedValue.getValue() == false);
                            } else if (this.selectedValue instanceof Numbers) {
                                Numbers value = (Numbers)this.selectedValue;
                                double inc = (Double)value.getValue();
                                inc -= ((Double)value.getIncrement()).doubleValue();
                                if ((inc = MathUtil.toDecimalLength(inc, 1)) < (Double)value.getMinimum()) {
                                    inc = (Double)((Numbers)this.selectedValue).getMaximum();
                                }
                                this.selectedValue.setValue(inc);
                            } else if (this.selectedValue instanceof Mode) {
                                Mode theme = (Mode)this.selectedValue;
                                Enum current = (Enum)theme.getValue();
                                int next = current.ordinal() - 1 < 0 ? theme.getModes().length - 1 : current.ordinal() - 1;
                                this.selectedValue.setValue(theme.getModes()[next]);
                            }
                            this.maxValue = 0;
                            for (Value val : this.selectedModule.getValues()) {
                                int off;
                                int n = off = val instanceof Option ? 6 : Minecraft.getMinecraft().fontRendererObj.getStringWidth(String.format(" \u00a77%s", val.getValue().toString())) + 6;
                                if (this.maxValue > Minecraft.getMinecraft().fontRendererObj.getStringWidth(val.getDisplayName().toUpperCase()) + off) continue;
                                this.maxValue = Minecraft.getMinecraft().fontRendererObj.getStringWidth(val.getDisplayName().toUpperCase()) + off;
                            }
                            this.maxValue += this.maxModule;
                        }
                    }
                }
            }
        }
    }

    static /* synthetic */ int[] $SWITCH_TABLE$com$enjoytheban$module$modules$render$UI$TabUI$Section() {
        int[] arrn;
        int[] arrn2 = $SWITCH_TABLE$com$enjoytheban$module$modules$render$UI$TabUI$Section;
        if (arrn2 != null) {
            return arrn2;
        }
        arrn = new int[Section.values().length];
        try {
            arrn[Section.MODULES.ordinal()] = 2;
        }
        catch (NoSuchFieldError noSuchFieldError) {}
        try {
            arrn[Section.TYPES.ordinal()] = 1;
        }
        catch (NoSuchFieldError noSuchFieldError) {}
        try {
            arrn[Section.VALUES.ordinal()] = 3;
        }
        catch (NoSuchFieldError noSuchFieldError) {}
        $SWITCH_TABLE$com$enjoytheban$module$modules$render$UI$TabUI$Section = arrn;
        return $SWITCH_TABLE$com$enjoytheban$module$modules$render$UI$TabUI$Section;
    }

    public static enum Section {
        TYPES,
        MODULES,
        VALUES;
    }

}

