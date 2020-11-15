package me.skidsense.gui.newclickgui.plane.impl;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.util.ResourceLocation;
import org.apache.commons.lang3.StringUtils;

import me.skidsense.Client;
import me.skidsense.gui.newclickgui.component.Component;
import me.skidsense.gui.newclickgui.impl.CategoryComponent;
import me.skidsense.gui.newclickgui.plane.Plane;
import me.skidsense.gui.util.GuiUtil;
import me.skidsense.gui.util.MouseUtil;
import me.skidsense.module.Mod;
import me.skidsense.module.ModuleType;

import java.awt.Color;
import java.util.ArrayList;

public class MainPlane extends Plane {
    private ModuleType selectedCategory = ModuleType.Fight;
    private ArrayList<Component> components = new ArrayList<>();
    public MainPlane(String label, float posX, float posY, float width, float height) {
        super(label, posX, posY, width, height);
    }

    @Override
    public void initializePlane() {
        super.initializePlane();
        for (ModuleType category : ModuleType.values()) {
            components.add(new CategoryComponent(category,getPosX(),getPosY(),46.5f,45f,getWidth() - 46.5f,getHeight() - 45f));
        }
        components.forEach(Component::initializeComponent);
    }

    @Override
    public void planeMoved(float movedX, float movedY) {
        super.planeMoved(movedX, movedY);
        components.forEach(component -> component.componentMoved(movedX, movedY));
    }

    @Override
    public void onDrawScreen(int mouseX, int mouseY, float partialTicks) {
        super.onDrawScreen(mouseX, mouseY, partialTicks);
        if (isDragging()) {
            setPosX(mouseX + getLastPosX());
            setPosY(mouseY + getLastPosY());
            planeMoved(getPosX(), getPosY());
        }
        if (getPosX() < 0) {
            setPosX(0);
            planeMoved(getPosX(), getPosY());
        }
        if (getPosX() + getWidth() > new ScaledResolution(Minecraft.getMinecraft()).getScaledWidth()) {
            setPosX(new ScaledResolution(Minecraft.getMinecraft()).getScaledWidth() - getWidth());
            planeMoved(getPosX(), getPosY());
        }
        if (getPosY() < 0) {
            setPosY(0);
            planeMoved(getPosX(), getPosY());
        }
        if (getPosY() + getHeight() > new ScaledResolution(Minecraft.getMinecraft()).getScaledHeight()) {
            setPosY(new ScaledResolution(Minecraft.getMinecraft()).getScaledHeight() - getHeight());
            planeMoved(getPosX(), getPosY());
        }
        GuiUtil.drawRoundedRect(getPosX(), getPosY(), getWidth(), getHeight(), 3, new Color(45, 45, 45, 255).getRGB());
        //GuiUtil.drawImage(new ResourceLocation("textures/client/logo.png"), getPosX() + 5.5f, getPosY() + 6, 32, 32);
        //GuiUtil.drawUnfilledCircle(getPosX() + 5f, getPosY() + 5f, 33, new Color(45, 45, 45, 255).getRGB());
        float categoryOffsetY = getPosY() + 13;
        for (ModuleType category : ModuleType.values()) {
            if (getSelectedCategory() == category) {
                GuiUtil.drawRect(getPosX(), categoryOffsetY - (Client.instance.fontManager.clickGuiIconFont.getHeight(category.getCharacter()) * 2.5f) / 4 - 4, 42.5f, Client.instance.fontManager.clickGuiIconFont.getHeight(category.getCharacter()) * 2.5f, new Color(35, 35, 35, 255).getRGB());
                GuiUtil.drawRect(getPosX(), categoryOffsetY - (Client.instance.fontManager.clickGuiIconFont.getHeight(category.getCharacter()) * 2.5f) / 4 - 4, 2, Client.instance.fontManager.clickGuiIconFont.getHeight(category.getCharacter()) * 2.5f, new Color(0xff4d4c).getRGB());
            }
            Client.instance.fontManager.clickGuiIconFont.drawStringWithShadow(category.getCharacter(), getPosX() + 12, categoryOffsetY, getSelectedCategory() == category ? new Color(0xff4d4c).getRGB() : new Color(229, 229, 223, 255).getRGB());
            categoryOffsetY += Client.instance.fontManager.clickGuiIconFont.getHeight(category.getCharacter()) * 2.5f;
        }
        GuiUtil.drawRect(getPosX() + 42.5, getPosY(), 100, getHeight(), new Color(35, 35, 35, 255).getRGB());
        Client.instance.fontManager.clickGuiTitleFont.drawStringWithShadow(StringUtils.capitalize(getSelectedCategory().name().toLowerCase()) + " (" + Client.instance.getModuleManager().getModulesInType(getSelectedCategory()).size() + ")", getPosX() + 50, getPosY() + 15, new Color(229, 229, 223, 255).getRGB());
        GuiUtil.drawRect(getPosX() + 42.5, getPosY() + 36, 100, 1, new Color(45, 45, 45, 255).getRGB());
        for (Component component : getComponents()) {
            if (component instanceof CategoryComponent) {
                final CategoryComponent categoryComponent = (CategoryComponent) component;
                if (categoryComponent.getCategory() == getSelectedCategory())
                    categoryComponent.onDrawScreen(mouseX, mouseY, partialTicks);
            }
        }
    }

    @Override
    public void onMouseClicked(int mouseX, int mouseY, int button) {
        super.onMouseClicked(mouseX, mouseY, button);
        final boolean hovered = MouseUtil.mouseWithinBounds(mouseX, mouseY, getPosX(), getPosY(), getWidth(), 15);
        if (button == 0) {
            if (hovered) {
                setLastPosX(getPosX() - mouseX);
                setLastPosY(getPosY() - mouseY);
                setDragging(true);
            }
            float categoryOffsetY = getPosY() + 13;
            for (ModuleType category : ModuleType.values()) {
                if (MouseUtil.mouseWithinBounds(mouseX, mouseY, getPosX(), categoryOffsetY - (Client.instance.fontManager.clickGuiIconFont.getHeight(category.getCharacter()) * 2.5f) / 4 - 4, 42.5f, Client.instance.fontManager.clickGuiIconFont.getHeight(category.getCharacter()) * 2.5f))
                    setSelectedCategory(category);
                categoryOffsetY += Client.instance.fontManager.clickGuiIconFont.getHeight(category.getCharacter()) * 2.5f;
            }
        }
        for (Component component : getComponents()) {
            if (component instanceof CategoryComponent) {
                final CategoryComponent categoryComponent = (CategoryComponent) component;
                if (categoryComponent.getCategory() == getSelectedCategory())
                    categoryComponent.onMouseClicked(mouseX, mouseY, button);
            }
        }
    }

    @Override
    public void onMouseReleased(int mouseX, int mouseY, int button) {
        super.onMouseReleased(mouseX, mouseY, button);
        if (button == 0 && isDragging()) {
            setDragging(false);
        }
        for (Component component : getComponents()) {
            if (component instanceof CategoryComponent) {
                final CategoryComponent categoryComponent = (CategoryComponent) component;
                if (categoryComponent.getCategory() == getSelectedCategory())
                    categoryComponent.onMouseReleased(mouseX, mouseY, button);
            }
        }
    }

    @Override
    public void onKeyTyped(char character, int keyCode) {
        super.onKeyTyped(character, keyCode);
        for (Component component : getComponents()) {
            if (component instanceof CategoryComponent) {
                final CategoryComponent categoryComponent = (CategoryComponent) component;
                if (categoryComponent.getCategory() == getSelectedCategory())
                    categoryComponent.onKeyTyped(character, keyCode);
            }
        }
    }

    @Override
    public void onGuiClosed() {
        super.onGuiClosed();
        for (Component component : getComponents()) {
            if (component instanceof CategoryComponent) {
                final CategoryComponent categoryComponent = (CategoryComponent) component;
                if (categoryComponent.getCategory() == getSelectedCategory()) categoryComponent.onGuiClosed();
            }
        }
    }

    public ModuleType getSelectedCategory() {
        return selectedCategory;
    }

    public void setSelectedCategory(ModuleType selectedCategory) {
        this.selectedCategory = selectedCategory;
    }

    public ArrayList<Component> getComponents() {
        return components;
    }
}
