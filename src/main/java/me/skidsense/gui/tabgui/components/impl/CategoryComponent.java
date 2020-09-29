package me.skidsense.gui.tabgui.components.impl;

import me.skidsense.Client;
import me.skidsense.gui.tabgui.TabMain;
import me.skidsense.gui.tabgui.components.Component;
import me.skidsense.gui.util.GuiUtil;
import me.skidsense.module.Mod;
import me.skidsense.module.ModuleType;
import net.minecraft.client.gui.ScaledResolution;
import org.apache.commons.lang3.StringUtils;
import org.lwjgl.input.Keyboard;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Comparator;

public class CategoryComponent extends Component {
    private ModuleType category;
    private TabMain tabMain;
    private Mod selectedModule;
    private ArrayList<Component> components = new ArrayList();
    private float largestString;

    public CategoryComponent(TabMain tabMain, ModuleType category, String label, float x, float y, float width, float height) {
        super(label, x, y, width, height);
        this.tabMain = tabMain;
        this.category = category;
    }

    public void init() {
        float moduleY = getY();
        largestString = mc.fontRendererObj.getStringWidth(StringUtils.capitalize((Client.instance.getModuleManager().getModulesInType(category).get(0).getName() != null ? Client.instance.getModuleManager().getModulesInType(category).get(0).getName() : Client.instance.getModuleManager().getModulesInType(category).get(0).getName())));
        for (int i = 0; i < Client.instance.getModuleManager().getModulesInType(category).size(); i++) {
            if (mc.fontRendererObj.getStringWidth(StringUtils.capitalize((Client.instance.getModuleManager().getModulesInType(category).get(i).getName() != null ? Client.instance.getModuleManager().getModulesInType(category).get(i).getName() : Client.instance.getModuleManager().getModulesInType(category).get(i).getName()))) > largestString) {
                largestString = mc.fontRendererObj.getStringWidth(StringUtils.capitalize((Client.instance.getModuleManager().getModulesInType(category).get(i).getName() != null ? Client.instance.getModuleManager().getModulesInType(category).get(i).getName() : Client.instance.getModuleManager().getModulesInType(category).get(i).getName())));
            }
        }
        ArrayList<Mod> mods = new ArrayList(Client.instance.getModuleManager().getModulesInType(category));
        mods.sort(Comparator.comparing(m -> m.getName()));
        for (Mod module : mods) {
            components.add(new ModuleComponent(this, module, StringUtils.capitalize(module.getName()), getX() + getWidth() + 6, moduleY, largestString + 18, 12));
            moduleY += 12;
        }
        selectedModule = mods.get(0);
        components.forEach(component -> component.init());
    }

    @Override
    public void onRender(ScaledResolution sr) {
        super.onRender(sr);
        if (tabMain.getSelectedCategory() == category)
            GuiUtil.drawRect(getX(), getY(), getWidth(), getHeight(), new Color(0xff4d4c).getRGB());
        mc.fontRendererObj.drawStringWithShadow(getName(), tabMain.getSelectedCategory() == category ? 7 : 5, getY() + 2, tabMain.getSelectedCategory() == category ? -1 : new Color(180, 180, 180).getRGB());
        if (tabMain.getSelectedCategory() == category && tabMain.isExtended()) {
            GuiUtil.drawBorderedRect(getX() + getWidth() + 5, getY() - 1, largestString + 20, (Client.instance.getModuleManager().getModulesInType(category).size() * 12) + 2, 1, new Color(0, 0, 0, 130).getRGB(), new Color(0, 0, 0, 180).getRGB());
            components.forEach(component -> component.onRender(sr));
        }
    }

    @Override
    public void onKeyPress(int key) {
        super.onKeyPress(key);
        ArrayList<Mod> mods = new ArrayList(Client.instance.getModuleManager().getModulesInType(category));
        mods.sort(Comparator.comparing(m -> m.getName()));
        components.forEach(component -> {
            if (tabMain.isExtendedValue()) {
                if (component instanceof ModuleComponent)
                    ((ModuleComponent) component).getComponents().forEach(component1 -> component1.onKeyPress(key));
            }
        });
        if (tabMain.getSelectedCategory() == category && tabMain.isExtended()) {
            switch (key) {
                case Keyboard.KEY_RIGHT:
                    if (tabMain.isExtended() && !tabMain.isExtendedValue() && !selectedModule.getValues().isEmpty()) {
                        tabMain.setExtendedvalue(true);
                    }
                    break;
                case Keyboard.KEY_DOWN:
                    if (!tabMain.isExtendedValue()) {
                        if (mods.indexOf(selectedModule) + 1 >= mods.size()) {
                            selectedModule = mods.get(0);
                            return;
                        }
                        selectedModule = mods.get(mods.indexOf(selectedModule) + 1);
                    }
                    break;
                case Keyboard.KEY_UP:
                    if (!tabMain.isExtendedValue()) {
                        if (mods.indexOf(selectedModule) <= 0) {
                            selectedModule = mods.get(mods.size() - 1);
                            return;
                        }
                        selectedModule = mods.get(mods.indexOf(selectedModule) - 1);
                    }
                    break;
                case Keyboard.KEY_RETURN:
                    if (!tabMain.isExtendedValue()) {
                        selectedModule.setEnabled(!selectedModule.isEnabled());
                    } else {
                        tabMain.setExtendedvalue(false);
                    }
                    break;
            }
            components.forEach(component -> component.onKeyPress(key));
        }
    }

    public ModuleType getCategory() {
        return category;
    }

    public TabMain getTabMain() {
        return tabMain;
    }

    public Mod getSelectedModule() {
        return selectedModule;
    }

    public ArrayList<Component> getComponents() {
        return components;
    }
}
