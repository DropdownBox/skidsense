package me.skidsense.module.collection.visual.clickgui.paste.panel.component.impl;

import java.awt.Color;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.newdawn.slick.opengl.TextureLoader;

import me.skidsense.hooks.value.Mode;
import me.skidsense.hooks.value.Numbers;
import me.skidsense.hooks.value.Option;
import me.skidsense.hooks.value.Value;
import me.skidsense.management.animation.AnimationUtil;
import me.skidsense.module.Mod;
import me.skidsense.module.collection.visual.clickgui.paste.ClickGuiScreen;
import me.skidsense.module.collection.visual.clickgui.paste.ColorUtils;
import me.skidsense.module.collection.visual.clickgui.paste.panel.AnimationState;
import me.skidsense.module.collection.visual.clickgui.paste.panel.Panel;
import me.skidsense.module.collection.visual.clickgui.paste.panel.component.Component;
import me.skidsense.util.RenderUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.util.ResourceLocation;

public final class ModuleComponent
extends Component {
    private static final Color BACKGROUND_COLOR = new Color(23, 23, 23);
    private static final Color TOGGLE_COLOR = new Color(150, 150, 150);
    public final List<Component> components = new ArrayList<Component>();
    private final Mod module;
    private final List<Component> children = new ArrayList<Component>();
    private int opacity = 120;
    private int childrenHeight;
    private double scissorBoxHeight;
    private AnimationState state = AnimationState.STATIC;

    public ModuleComponent(Mod module, Panel parent, int x, int y, int width, int height) {
        super(parent, x, y, width, height);
        this.module = module;
        int y2 = height;
        List<Value<?>> options = module.getValues();
        int optionsSize = options.size();
        for (int i = 0; i < optionsSize; ++i) {
        	Value<?> option = options.get(i);
            if (option instanceof Mode<?>) {
                this.children.add(new EnumOptionComponent((Mode)option, this.getPanel(), x, y + y2, width, height));
            } else if (option instanceof Option<?>) {
                this.children.add(new BoolOptionComponent((Option)option, this.getPanel(), x, y + y2, width, height));
            } else if (option instanceof Numbers<?>) {
                this.children.add(new NumberOptionComponent((Numbers)option, this.getPanel(), x, y + y2, width, 16));
            }
            y2 += height;
        }
        this.calculateChildrenHeight();
    }

    @Override
    public double getOffset() {
        return this.scissorBoxHeight;
    }

    private void drawChildren(int mouseX, int mouseY) {
        int childY = 15;
        List<Component> children = this.children;
        int componentListSize = children.size();
        for (int i = 0; i < componentListSize; ++i) {
            Component child = children.get(i);
            if (child.isHidden()) continue;
            child.setY(this.getY() + childY);
            child.onDraw(mouseX, mouseY);
            childY += 15;
        }
    }

    private int calculateChildrenHeight() {
        int height = 0;
        List<Component> children = this.children;
        int childrenSize = children.size();
        for (int i = 0; i < childrenSize; ++i) {
            Component component = children.get(i);
            if (component.isHidden()) continue;
            height = (int)((double)height + ((double)component.getHeight() + component.getOffset()));
        }
        return height;
    }

    @Override
    public void onDraw(int mouseX, int mouseY) {
        Panel parent = this.getPanel();
        int x = parent.getX() + this.getX();
        int y = parent.getY() + this.getY();
        int height = this.getHeight();
        int width = this.getWidth();
        boolean hovered = this.isMouseOver(mouseX, mouseY);
        this.handleScissorBox();
        this.childrenHeight = this.calculateChildrenHeight();
        if (hovered) {
            if (this.opacity < 200) {
                this.opacity += 5;
            }
        } else if (this.opacity > 120) {
            this.opacity -= 5;
        }
        int opacity = this.opacity;
        //RenderUtil.drawGradientRect(x, y, x + width, (float) ((y + height) + this.getOffset()), this.module.isEnabled() ? new Color(192,192,250,150).getRGB() : new Color(230,0,0,255).getRGB(), this.module.isEnabled() ? new Color(192,192,192,150).getRGB() : new Color(192,192,192,150).getRGB());
        Gui.drawRect(x, y, x + width, (double)(y + height) + this.getOffset(), module.isEnabled() ? ColorUtils.getColorWithOpacity(TOGGLE_COLOR, 255 - opacity).getRGB() : ColorUtils.getColorWithOpacity(BACKGROUND_COLOR, 255 - opacity).getRGB());
        int color = this.module.isEnabled() ? ClickGuiScreen.getColor().getRGB() : new Color(opacity, opacity, opacity).getRGB();
        FONT_RENDERER.drawStringWithShadow(this.module.getName(), (float)x + 2.0f, (float)y + (float)height / 2.0f - 4.0f, color);
        if (this.scissorBoxHeight > 0.0) {
            if (parent.state != AnimationState.RETRACTING) {
                RenderUtil.prepareScissorBox(x, y, x + width, (float)((double)y + Math.min(this.scissorBoxHeight, parent.scissorBoxHeight) + (double)height));
            }
            this.drawChildren(mouseX, mouseY);
        }
        if(!this.children.isEmpty()) {
        	RenderUtil.drawImage(new ResourceLocation("skidsense/settings.png"), (x + width) - 13, y + 1, 12, 12);
        }
    }

    @Override
    public void onMouseClick(int mouseX, int mouseY, int mouseButton) {
        if (this.scissorBoxHeight > 0.0) {
            List<Component> componentList = this.children;
            int componentListSize = componentList.size();
            for (int i = 0; i < componentListSize; ++i) {
                componentList.get(i).onMouseClick(mouseX, mouseY, mouseButton);
            }
        }
        if (this.isMouseOver(mouseX, mouseY)) {
            if (mouseButton == 0) {
                this.module.setEnabled(!this.module.isEnabled());
            } else if (mouseButton == 1 && !this.children.isEmpty()) {
                if (this.scissorBoxHeight > 0.0 && (this.state == AnimationState.EXPANDING || this.state == AnimationState.STATIC)) {
                    this.state = AnimationState.RETRACTING;
                } else if (this.scissorBoxHeight < (double)this.childrenHeight && (this.state == AnimationState.EXPANDING || this.state == AnimationState.STATIC)) {
                    this.state = AnimationState.EXPANDING;
                }
            }
        }
    }

    @Override
    public void onMouseRelease(int mouseX, int mouseY, int mouseButton) {
        if (this.scissorBoxHeight > 0.0) {
            List<Component> componentList = this.children;
            int componentListSize = componentList.size();
            for (int i = 0; i < componentListSize; ++i) {
                componentList.get(i).onMouseRelease(mouseX, mouseY, mouseButton);
            }
        }
    }

    @Override
    public void onKeyPress(int typedChar, int keyCode) {
        if (this.scissorBoxHeight > 0.0) {
            List<Component> componentList = this.children;
            int componentListSize = componentList.size();
            for (int i = 0; i < componentListSize; ++i) {
                componentList.get(i).onKeyPress(typedChar, keyCode);
            }
        }
    }

    private void handleScissorBox() {
        int childrenHeight = this.childrenHeight;
        switch (this.state) {
            case EXPANDING: {
                if (this.scissorBoxHeight < (double)childrenHeight) {
                    this.scissorBoxHeight = AnimationUtil.animate(childrenHeight, this.scissorBoxHeight, 0.05);
                } else if (this.scissorBoxHeight >= (double)childrenHeight) {
                    this.state = AnimationState.STATIC;
                }
                this.scissorBoxHeight = this.clamp(this.scissorBoxHeight, childrenHeight);
                break;
            }
            case RETRACTING: {
                if (this.scissorBoxHeight > 0.0) {
                    this.scissorBoxHeight = AnimationUtil.animate(0.0, this.scissorBoxHeight, 0.05);
                } else if (this.scissorBoxHeight <= 0.0) {
                    this.state = AnimationState.STATIC;
                }
                this.scissorBoxHeight = this.clamp(this.scissorBoxHeight, childrenHeight);
                break;
            }
            case STATIC: {
                if (this.scissorBoxHeight > 0.0 && this.scissorBoxHeight != (double)childrenHeight) {
                    this.scissorBoxHeight = AnimationUtil.animate(childrenHeight, this.scissorBoxHeight, 0.05);
                }
                this.scissorBoxHeight = this.clamp(this.scissorBoxHeight, childrenHeight);
            }
        }
    }

    private double clamp(double a, double max) {
        if (a < 0.0) {
            return 0.0;
        }
        if (a > max) {
            return max;
        }
        return a;
    }
}

