package me.skidsense.gui.newclickgui.impl.subcomponents;

import java.awt.Color;

import org.apache.commons.lang3.StringUtils;

import me.skidsense.Client;
import me.skidsense.gui.newclickgui.component.Component;
import me.skidsense.gui.util.GuiUtil;
import me.skidsense.gui.util.MouseUtil;
import me.skidsense.hooks.value.Mode;


public class EnumComponent extends Component {
    private Mode<Enum> enumValue;
    private boolean extended;

    public EnumComponent(Mode enumValue, float posX, float posY, float offsetX, float offsetY, float width, float height) {
        super(enumValue.getName(), posX, posY, offsetX, offsetY, width, height);
        this.enumValue = enumValue;
    }

    @Override
    public void componentMoved(float movedX, float movedY) {
        super.componentMoved(movedX, movedY);
    }

    @Override
    public void onDrawScreen(int mouseX, int mouseY, float partialTicks) {
        super.onDrawScreen(mouseX, mouseY, partialTicks);
        Client.instance.fontManager.clickGuiFont.drawStringWithShadow(getLabel(), getPosX(), getPosY() + 3, new Color(229, 229, 223, 255).getRGB());
        if (isExtended()) {
            setHeight(20 + (15 * (enumValue.getModes().length - 1)));
            GuiUtil.drawRoundedRect(getPosX() + getWidth() - 80, getPosY() + 13.5f, 72, (15 * (enumValue.getModes().length - 1)), 3, new Color(55, 55, 55, 255).getRGB());
            //GuiUtil.drawOutlinedRoundedRect(getPosX() + getWidth() - 80, getPosY() + 12.5f, 72, 1 + (15 * (enumValue.getModes().length - 1)), 3, 0.5f, new Color(0xff689FFF).getRGB());
            float enumY = getPosY() + 20f;
            for (Enum enoom : enumValue.getModes()) {
                if (enoom != enumValue.getValue()) {
                    Client.instance.fontManager.clickGuiFont.drawStringWithShadow(StringUtils.capitalize(enoom.name().toLowerCase()), getPosX() + getWidth() - 44 - Client.instance.fontManager.clickGuiFont.getStringWidth(StringUtils.capitalize(enoom.name().toLowerCase())) / 2, enumY, new Color(229, 229, 223, 255).getRGB());
                    enumY += 14;
                }
            }
        } else {
            setHeight(20);
        }
        GuiUtil.drawRoundedRect(getPosX() + getWidth() - 84, getPosY() - 1.5f, 80, 15, 3, new Color(50, 50, 50, 255).getRGB());
        GuiUtil.drawOutlinedRoundedRect(getPosX() + getWidth() - 84, getPosY() - 1.5f, 80, 15, 3, 0.5f, new Color(0xff689FFF).getRGB());
        Client.instance.fontManager.clickGuiFont.drawStringWithShadow(StringUtils.capitalize(enumValue.getValue().toString().toLowerCase()), getPosX() + getWidth() - 82, getPosY() + 2, new Color(229, 229, 223, 255).getRGB());
        GuiUtil.drawArrow(getPosX() + getWidth() - 14, getPosY() + 4, isExtended(), new Color(229, 229, 223, 255).getRGB());
    }

    @Override
    public void onMouseClicked(int mouseX, int mouseY, int button) {
        super.onMouseClicked(mouseX, mouseY, button);
        if (button == 0 && MouseUtil.mouseWithinBounds(mouseX, mouseY, getPosX() + getWidth() - 84, getPosY() - 1.5f, 80, 15)) {
            setExtended(!isExtended());
        }
        if (button == 0 && isExtended()) {
            float enumY = getPosY() + 20f;
            for (Enum enoom : enumValue.getModes()) {
                if (enoom != enumValue.getValue()) {
                    if (MouseUtil.mouseWithinBounds(mouseX,mouseY,getPosX() + getWidth() - 80,enumY -4,72,15)) {
                        enumValue.setValue(enoom);
                        setExtended(false);
                    }
                    enumY += 15;
                }
            }
        }
    }

    @Override
    public void onMouseReleased(int mouseX, int mouseY, int button) {
        super.onMouseReleased(mouseX, mouseY, button);
    }

    @Override
    public void onKeyTyped(char character, int keyCode) {
        super.onKeyTyped(character, keyCode);
    }

    public boolean isExtended() {
        return extended;
    }

    public void setExtended(boolean extended) {
        this.extended = extended;
    }

    public Mode<Enum> getEnumValue() {
        return enumValue;
    }
}
