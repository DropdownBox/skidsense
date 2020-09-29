package me.skidsense.gui.newclickgui.impl.subcomponents;

import java.awt.Color;

import me.skidsense.Client;
import me.skidsense.gui.newclickgui.component.Component;
import me.skidsense.gui.util.GuiUtil;
import me.skidsense.gui.util.MouseUtil;
import me.skidsense.hooks.value.Option;

public class BooleanComponent extends Component {
    private Option booleanValue;
    public BooleanComponent(Option booleanValue, float posX, float posY, float offsetX, float offsetY, float width, float height) {
        super(booleanValue.getName(), posX, posY, offsetX, offsetY, width, height);
        this.booleanValue = booleanValue;
    }

    @Override
    public void componentMoved(float movedX, float movedY) {
        super.componentMoved(movedX, movedY);
    }

    @Override
    public void onDrawScreen(int mouseX, int mouseY, float partialTicks) {
        super.onDrawScreen(mouseX, mouseY, partialTicks);
        Client.instance.fontManager.clickGuiFont.drawStringWithShadow(getLabel(), getPosX(), getPosY() + 3, new Color(229, 229, 223, 255).getRGB());
        GuiUtil.drawOutlinedRoundedRect(getPosX() + getWidth() - 15,getPosY(),10,10,3,1,new Color(0xff689FFF).getRGB());
        if ((boolean) booleanValue.getValue()) {
            GuiUtil.drawRoundedRect(getPosX() + getWidth() - 15,getPosY(),10,10,3,new Color(0xff689FFF).getRGB());
            GuiUtil.drawCheckMark(getPosX() + getWidth() - 8,getPosY() - 1,8, new Color(229, 229, 223, 255).getRGB());
        }
    }

    @Override
    public void onMouseClicked(int mouseX, int mouseY, int button) {
        super.onMouseClicked(mouseX, mouseY, button);
        if (button == 0 && MouseUtil.mouseWithinBounds(mouseX,mouseY,getPosX() + getWidth() - 15,getPosY(),10,10)) {
            booleanValue.setValue(!(boolean)booleanValue.getValue());
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

    public Option getBooleanValue() {
        return booleanValue;
    }
}
