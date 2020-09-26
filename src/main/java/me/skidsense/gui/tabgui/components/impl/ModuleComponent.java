package me.skidsense.gui.tabgui.components.impl;

import net.minecraft.client.gui.ScaledResolution;
import org.apache.commons.lang3.StringUtils;
import org.lwjgl.input.Keyboard;

import me.skidsense.gui.tabgui.components.Component;
import me.skidsense.gui.tabgui.util.TabGuiUtil;
import me.skidsense.hooks.value.Mode;
import me.skidsense.hooks.value.Numbers;
import me.skidsense.hooks.value.Option;
import me.skidsense.hooks.value.Value;
import me.skidsense.module.Mod;
import me.skidsense.util.RenderUtil;

import java.awt.Color;
import java.util.ArrayList;

public class ModuleComponent extends Component {
    private Mod module;
    private CategoryComponent categorycomp;
    private Value selectedValue;
    private ArrayList<Component> components = new ArrayList();

    public ModuleComponent(CategoryComponent categorycomp, Mod module, String label, float x, float y, float width, float height) {
        super(label, x, y, width, height);
        this.categorycomp = categorycomp;
        this.module = module;
    }

    public void init() {
        if (module.getValues().isEmpty()) return;
        float valueY = getY();
        for (Value value : module.getValues()) {
            if (value instanceof Option) {
                components.add(new BooleanComponent(this, ((Option)value), value.getName(), getX() + getWidth() + 6, valueY, 100, 12));
            } else if (value instanceof Numbers) {
                components.add(new NumberComponent(this, ((Numbers)value), value.getName(), getX() + getWidth() + 6, valueY, 100, 12));
            } else if (value instanceof Mode) {
                components.add(new EnumComponent(this, ((Mode)value), value.getName(), getX() + getWidth() + 6, valueY, 100, 12));
            }
            valueY += 12;
        }
        selectedValue = module.getValues().get(0);
        components.forEach(component -> component.init());
    }

    @Override
    public void onRender(ScaledResolution sr) {
        super.onRender(sr);
        if (categorycomp.getSelectedModule() == module)
        	TabGuiUtil.drawRect(getX(), getY(), getWidth(), getHeight(), new Color(0xff4d4c).getRGB());
        mc.fontRendererObj.drawStringWithShadow(getName(), categorycomp.getSelectedModule() == module ? getX() + 4 : getX() + 2, getY() + 2, module.isEnabled() ? -1 : 11184810);
        if (!module.getValues().isEmpty())
        	TabGuiUtil.drawRect(getX() + getWidth() - 1, getY() + 1, 1, getHeight() - 2, new Color(219, 85, 84).getRGB());
        if (categorycomp.getSelectedModule() == module && categorycomp.getTabMain().isExtendedValue() && !module.getValues().isEmpty()) {
            float largestString = mc.fontRendererObj.getStringWidth(module.getValues().get(0).getName() + (module.getValues().get(0) instanceof Option ? "" : ": " + module.getValues().get(0).getValue().toString()));
            for (int i = 0; i < module.getValues().size(); i++) {
                if (mc.fontRendererObj.getStringWidth(module.getValues().get(i).getName() + (module.getValues().get(i) instanceof Option ? "" : ": " + module.getValues().get(i).getValue().toString())) > largestString) {
                    largestString = mc.fontRendererObj.getStringWidth(module.getValues().get(i).getName() + (module.getValues().get(i) instanceof Option ? "" : ": " + module.getValues().get(i).getValue().toString()));
                }
            }
            TabGuiUtil.drawBorderedRect(getX() + getWidth() + 5, getY() - 1, largestString + 20, (module.getValues().size() * 12) + 2, 1, new Color(0, 0, 0, 130).getRGB(), new Color(0, 0, 0, 180).getRGB());
            components.forEach(component -> component.onRender(sr));
        }
    }

    @Override
    public void onKeyPress(int key) {
        switch (key) {
            case Keyboard.KEY_DOWN:
                if (categorycomp.getTabMain().isExtendedValue() && categorycomp.getSelectedModule() == module) {
                    if (module.getValues().indexOf(selectedValue) + 1 >= module.getValues().size()) {
                        selectedValue = module.getValues().get(0);
                        return;
                    }
                    selectedValue = module.getValues().get(module.getValues().indexOf(selectedValue) + 1);
                }
                break;
            case Keyboard.KEY_UP:
                if (categorycomp.getTabMain().isExtendedValue() && categorycomp.getSelectedModule() == module) {
                    if (module.getValues().indexOf(selectedValue) <= 0) {
                        selectedValue = module.getValues().get(module.getValues().size() - 1);
                        return;
                    }
                    selectedValue = module.getValues().get(module.getValues().indexOf(selectedValue) - 1);
                }
                break;
        }
        super.onKeyPress(key);
    }

    public CategoryComponent getCategorycomp() {
        return categorycomp;
    }

    public Value getSelectedValue() {
        return selectedValue;
    }

    public ArrayList<Component> getComponents() {
        return components;
    }

    public Mod getModule() {
        return module;
    }

}
