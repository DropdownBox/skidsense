package me.skidsense.gui.newclickgui.impl;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import org.apache.commons.lang3.StringUtils;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

import me.skidsense.Client;
import me.skidsense.gui.newclickgui.component.Component;
import me.skidsense.gui.newclickgui.impl.subcomponents.ModuleComponent;
import me.skidsense.gui.util.GuiUtil;
import me.skidsense.gui.util.MouseUtil;
import me.skidsense.module.Mod;
import me.skidsense.module.ModuleType;

import java.awt.Color;
import java.util.ArrayList;
import java.util.stream.Collectors;

public class CategoryComponent extends Component {
    private ModuleType category;
    private ArrayList<Component> components = new ArrayList<>();
    private int scrollY;

    public CategoryComponent(ModuleType category, float posX, float posY, float offsetX, float offsetY, float width, float height) {
        super(StringUtils.capitalize(category.name().toLowerCase()), posX, posY, offsetX, offsetY, width, height);
        this.category = category;
    }

    @Override
    public void initializeComponent() {
        super.initializeComponent();
        float moduleOffsetY = 0;
        for (Mod module : Client.instance.getModuleManager().getModulesInType(category)) {
            components.add(new ModuleComponent(this, module, getPosX(), getPosY(), 0, moduleOffsetY, 90, 20));
            moduleOffsetY += 20;
        }
        components.forEach(Component::initializeComponent);
    }

    @Override
    public void componentMoved(float movedX, float movedY) {
        super.componentMoved(movedX, movedY);
        components.forEach(component -> component.componentMoved(getPosX(), getPosY()));
    }

    @Override
    public void onDrawScreen(int mouseX, int mouseY, float partialTicks) {
        super.onDrawScreen(mouseX, mouseY, partialTicks);
        double scrollbarHeight = (getHeight() / getComponentHeight()) * getHeight();
        if (MouseUtil.mouseWithinBounds(mouseX, mouseY, getPosX() - 2, getPosY(), 95, getHeight()) && getComponentHeight() >= getHeight()) {
            int wheel = Mouse.getDWheel();
            if (wheel < 0) {
                if (getScrollY() - 6 < -(getComponentHeight() - getHeight()))
                    setScrollY((int) -(getComponentHeight() - getHeight()));
                else setScrollY(getScrollY() - 6);
            } else if (wheel > 0) {
                setScrollY(getScrollY() + 6);
            }
        }
        if (getScrollY() > 0) setScrollY(0);
        GL11.glPushMatrix();
        GL11.glEnable(GL11.GL_SCISSOR_TEST);
        GuiUtil.prepareScissorBox(new ScaledResolution(Minecraft.getMinecraft()), getPosX() - 2, getPosY() - 2, getWidth(), getHeight());
        for (Component component : getComponents()) {
            if (component instanceof ModuleComponent) {
                final ModuleComponent moduleComponent = (ModuleComponent) component;
                moduleComponent.onDrawScreen(mouseX, mouseY, partialTicks);
                moduleComponent.setOffsetY(component.getOriginalOffsetY() + getScrollY());
                moduleComponent.componentMoved(getPosX(), getPosY());
            }
        }
        if (getComponentHeight() >= getHeight()) {
            GuiUtil.drawRect(getPosX() + 94, getPosY() - 6, 2, getHeight() + 6, new Color(40, 40, 40, 255).getRGB());
            GuiUtil.drawRect(getPosX() + 94, getPosY() - 6 - (((getHeight() - (scrollbarHeight - 4)) / (getComponentHeight() - (getHeight()))) * getScrollY()), 2, scrollbarHeight, new Color(55, 55, 55, 255).getRGB());
        }
        GL11.glDisable(GL11.GL_SCISSOR_TEST);
        GL11.glPopMatrix();
        if (getComponents().stream().filter(component -> component instanceof ModuleComponent && ((ModuleComponent) component).isExtended()).toArray().length > 0) {
            final ModuleComponent moduleComponent = (ModuleComponent) getComponents().stream().filter(component -> component instanceof ModuleComponent && ((ModuleComponent) component).isExtended()).collect(Collectors.toList()).get(0);
            Client.instance.fontManager.clickGuiSmallFont.drawStringWithShadow(StringUtils.capitalize(moduleComponent.getModule().getType().name().toLowerCase()) + "/" + moduleComponent.getModule().getName(), getPosX() + 105, getPosY() - 19, new Color(155, 155, 155, 255).getRGB());
        }
    }


    @Override
    public void onMouseClicked(int mouseX, int mouseY, int button) {
        super.onMouseClicked(mouseX, mouseY, button);
        if (MouseUtil.mouseWithinBounds(mouseX, mouseY, getPosX() - 2, getPosY() - 2, getWidth(), getHeight())) {
            for (Component component : getComponents()) {
                if (component instanceof ModuleComponent) {
                    final ModuleComponent moduleComponent = (ModuleComponent) component;
                    moduleComponent.onMouseClicked(mouseX, mouseY, button);
                }
            }
        }
    }

    @Override
    public void onMouseReleased(int mouseX, int mouseY, int button) {
        super.onMouseReleased(mouseX, mouseY, button);
        for (Component component : getComponents()) {
            if (component instanceof ModuleComponent) {
                final ModuleComponent moduleComponent = (ModuleComponent) component;
                moduleComponent.onMouseReleased(mouseX, mouseY, button);
            }
        }
    }

    @Override
    public void onKeyTyped(char character, int keyCode) {
        super.onKeyTyped(character, keyCode);
        for (Component component : getComponents()) {
            if (component instanceof ModuleComponent) {
                final ModuleComponent moduleComponent = (ModuleComponent) component;
                moduleComponent.onKeyTyped(character, keyCode);
            }
        }
    }

    public ModuleType getCategory() {
        return category;
    }

    public ArrayList<Component> getComponents() {
        return components;
    }

    public int getScrollY() {
        return scrollY;
    }

    public void setScrollY(int scrollY) {
        this.scrollY = scrollY;
    }

    public int getComponentHeight() {
        int h = 0;
        for (Component component : getComponents()) {
            h += component.getHeight();
        }
        return h;
    }
}
