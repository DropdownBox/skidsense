package me.skidsense.gui.tabgui.components.impl;

import com.mojang.realmsclient.gui.ChatFormatting;

import me.skidsense.gui.tabgui.components.Component;
import me.skidsense.gui.tabgui.util.TabGuiUtil;
import me.skidsense.hooks.value.Numbers;
import me.skidsense.hooks.value.Option;
import me.skidsense.util.MathUtil;
import me.skidsense.util.RenderUtil;
import net.minecraft.client.gui.ScaledResolution;

import java.awt.Color;

import org.apache.commons.lang3.StringUtils;
import org.lwjgl.input.Keyboard;


/**
 * made by oHare for ETB Reloaded
 *
 * @since 6/28/2019
 **/
public class NumberComponent extends Component {
    private Numbers<Number> value;
    private ModuleComponent modulecomp;

    public NumberComponent(ModuleComponent modulecomp, Numbers value, String label, float x, float y, float width, float height) {
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
        	TabGuiUtil.drawRect(getX(), getY(), largestString + 18, getHeight(), new Color(0xff4d4c).getRGB());
        mc.fontRendererObj.drawStringWithShadow(getName() + ": " + ChatFormatting.GRAY + value.getValue().toString(), modulecomp.getSelectedValue() == value ? getX() + 4 : getX() + 2, getY() + 2, -1);
    }

    @Override
    public void onKeyPress(int key) {
        switch (key) {
            case Keyboard.KEY_RIGHT:
                if (modulecomp.getSelectedValue() == value && modulecomp.getCategorycomp().getSelectedModule() == modulecomp.getModule()) {
                	//Numbers 1
                    Numbers value = (Numbers)this.value;
                    double inc = (Double)value.getValue();
                    inc += ((Double)value.getIncrement()).doubleValue();
                    if ((inc = MathUtil.toDecimalLength(inc, 1)) > (Double)value.getMaximum()) {
                        inc = (Double)((Numbers)this.value).getMinimum();
                    }
                    this.value.setValue(inc);
                }
                break;
            case Keyboard.KEY_LEFT:
                if (modulecomp.getSelectedValue() == value && modulecomp.getCategorycomp().getTabMain().getSelectedCategory() == modulecomp.getCategorycomp().getTabMain().getSelectedCategory() && modulecomp.getCategorycomp().getSelectedModule() == modulecomp.getModule()) {
                	//Numbers 2
                    Numbers value = (Numbers)this.value;
                    double inc = (Double)value.getValue();
                    inc -= ((Double)value.getIncrement()).doubleValue();
                    if ((inc = MathUtil.toDecimalLength(inc, 1)) < (Double)value.getMinimum()) {
                        inc = (Double)((Numbers)this.value).getMaximum();
                    }
                    this.value.setValue(inc);
                }
                break;
        }
    }

    public ModuleComponent getModuleComp() {
        return modulecomp;
    }
}
