package me.skidsense.gui.tabgui.components.impl;

import me.skidsense.gui.tabgui.components.Component;
import me.skidsense.gui.util.GuiUtil;
import me.skidsense.hooks.value.Option;
import net.minecraft.client.gui.ScaledResolution;

import java.awt.Color;

import org.lwjgl.input.Keyboard;

public class BooleanComponent extends Component {
    private Option value;
    private ModuleComponent modulecomp;

    public BooleanComponent(ModuleComponent modulecomp, Option value, String label, float x, float y, float width, float height) {
        super(label, x, y, width, height);
        this.modulecomp = modulecomp;
        this.value = value;
    }

    public void init() {

    }

    @Override
    public void onRender(ScaledResolution sr) {
        super.onRender(sr);
        float largestString = mc.fontRendererObj.getStringWidth(modulecomp.getModule().getValues().get(0).getName() + (modulecomp.getModule().getValues().get(0) instanceof Option ? "" : ": " + modulecomp.getModule().getValues().get(0).getValue().toString()));
        for (int i = 0; i < modulecomp.getModule().getValues().size(); i++) {
            if (mc.fontRendererObj.getStringWidth(modulecomp.getModule().getValues().get(i).getName() + (modulecomp.getModule().getValues().get(i) instanceof Option ? "" : ": " + modulecomp.getModule().getValues().get(i).getValue().toString())) > largestString) {
                largestString = mc.fontRendererObj.getStringWidth(modulecomp.getModule().getValues().get(i).getName() + (modulecomp.getModule().getValues().get(i) instanceof Option ? "" : ": " + modulecomp.getModule().getValues().get(i).getValue().toString()));
            }
        }
        if (modulecomp.getSelectedValue() == value)
            GuiUtil.drawRect(getX(), getY(), largestString + 18, getHeight(), new Color(0xff4d4c).getRGB());
        mc.fontRendererObj.drawStringWithShadow(getName(), modulecomp.getSelectedValue() == value ? getX() + 4 : getX() + 2, getY() + 2, (boolean) value.getValue() ? new Color(0xFF8A80).getRGB() : 11184810);
    }

    @Override
    public void onKeyPress(int key) {
        switch (key) {
            case Keyboard.KEY_RIGHT:
                if (modulecomp.getSelectedValue() == value && modulecomp.getCategorycomp().getSelectedModule() == modulecomp.getModule()) {
                	value.setValue(!(Boolean)value.getValue());
                }
                break;
            case Keyboard.KEY_LEFT:
                if (modulecomp.getSelectedValue() == value && modulecomp.getCategorycomp().getTabMain().getSelectedCategory() == modulecomp.getCategorycomp().getTabMain().getSelectedCategory() && modulecomp.getCategorycomp().getSelectedModule() == modulecomp.getModule()) {
                    value.setValue(!(Boolean)value.getValue());
                }
                break;
        }
    }

    public ModuleComponent getModuleComp() {
        return modulecomp;
    }
}
