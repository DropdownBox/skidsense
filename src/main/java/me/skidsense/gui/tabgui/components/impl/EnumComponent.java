package me.skidsense.gui.tabgui.components.impl;

import com.mojang.realmsclient.gui.ChatFormatting;

import me.skidsense.gui.tabgui.components.Component;
import me.skidsense.gui.util.GuiUtil;
import me.skidsense.hooks.value.Mode;
import me.skidsense.hooks.value.Option;
import me.skidsense.util.RenderUtil;
import net.minecraft.client.gui.ScaledResolution;

import java.awt.Color;

import org.apache.commons.lang3.StringUtils;
import org.lwjgl.input.Keyboard;

public class EnumComponent extends Component {
    private Mode<Enum> value;
    private ModuleComponent modulecomp;

    public EnumComponent(ModuleComponent modulecomp, Mode value, String label, float x, float y, float width, float height) {
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
        mc.fontRendererObj.drawStringWithShadow(getName() + ": " + ChatFormatting.GRAY + StringUtils.capitalize(value.getValue().toString().toLowerCase()), modulecomp.getSelectedValue() == value ? getX() + 4 : getX() + 2, getY() + 2, -1);
    }

    @Override
    public void onKeyPress(int key) {
        switch (key) {
            case Keyboard.KEY_RIGHT:
                if (modulecomp.getSelectedValue() == value && modulecomp.getCategorycomp().getSelectedModule() == modulecomp.getModule()) {
                    Mode theme = (Mode)value;
                    Enum current = (Enum)theme.getValue();
                    int next = current.ordinal() + 1 >= theme.getModes().length ? 0 : current.ordinal() + 1;
                    value.setValue(theme.getModes()[next]);
                }
                break;
            case Keyboard.KEY_LEFT:
                if (modulecomp.getSelectedValue() == value && modulecomp.getCategorycomp().getTabMain().getSelectedCategory() == modulecomp.getCategorycomp().getTabMain().getSelectedCategory() && modulecomp.getCategorycomp().getSelectedModule() == modulecomp.getModule()) {
                    Mode theme = (Mode)value;
                    Enum current = (Enum)theme.getValue();
                    int next = current.ordinal() - 1 < 0 ? theme.getModes().length - 1 : current.ordinal() - 1;
                    value.setValue(theme.getModes()[next]);
                }
                break;
        }
    }

    public ModuleComponent getModuleComp() {
        return modulecomp;
    }
}
