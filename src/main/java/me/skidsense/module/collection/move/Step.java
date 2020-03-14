package me.skidsense.module.collection.move;


import me.skidsense.hooks.value.Option;
import me.skidsense.Client;
import me.skidsense.hooks.EventHandler;
import me.skidsense.hooks.events.EventStep;
import me.skidsense.hooks.value.Numbers;
import me.skidsense.hooks.value.Option;
import me.skidsense.module.Module;
import me.skidsense.module.ModuleType;
import java.awt.Color;

import net.minecraft.network.play.client.C03PacketPlayer;

public class Step
extends Module {
    private Numbers<Double> height = new Numbers<Double>("Height", "height", 1.0, 0.0, 10.0, 0.5);
    private Option<Boolean> ncp = new Option<Boolean>("Hypixel", "Hypixel", false);
    boolean reset;
    public Step() {
        super("Step", new String[]{"step"}, ModuleType.Move);
        this.setColor(new Color(165, 238, 65).getRGB());
        this.addValues(height,ncp);
    }

    @Override
    public void onDisable() {
        mc.thePlayer.stepHeight = 0.6f;
    }

    public boolean canStep(){
        //Flight fly = (Flight) Client.getModuleManager().getModuleByClass(Flight.class);
        return !Client.getModuleManager().getModuleByClass(Flight.class).isEnabled();
    }

    @EventHandler
    private void onUpdate(EventStep e) {
        this.setSuffix("NCP");
        if(reset){
            mc.timer.timerSpeed=1;
            reset=false;
        }
        if(canStep())
        if(e.isPre()){
            if(mc.thePlayer.isCollidedVertically && !mc.gameSettings.keyBindJump.pressed){
                double stepValue = ncp.getValue()?(height.getValue()<2.5?height.getValue():2.5):height.getValue();
                e.setStepHeight(stepValue);
                e.setActive(true);
            }
        }else{
            if(this.ncp.getValue()){
                double rheight = mc.thePlayer.getEntityBoundingBox().minY - mc.thePlayer.posY;
                boolean canStep = rheight >= 0.625;

                if(canStep){
                    mc.timer.timerSpeed = 1 - (rheight >= 1 ? Math.abs(1-(float)rheight)*((float)1*0.55f) : 0);
                    if(mc.timer.timerSpeed <= 0.05f){
                        mc.timer.timerSpeed = 0.05f;
                    }
                    reset=true;
                    this.ncpStep(rheight);
                }
            }
        }
    }
    void ncpStep(double height){
        double[] offset = new double[]{0.42,0.333,0.248,0.083,-0.078};
        double posX = mc.thePlayer.posX; double posZ = mc.thePlayer.posZ;
        double y = mc.thePlayer.posY;
        if(height < 1.1){
            double first = 0.42;
            double second = 0.75;
            if(height != 1){
                first *= height;
                second *= height;
                if(first > 0.425){
                    first = 0.425;
                }
                if(second > 0.78){
                    second = 0.78;
                }
                if(second < 0.49){
                    second = 0.49;
                }
            }
            if(first == 0.42)
                first = 0.41999998688698;
            mc.thePlayer.sendQueue.addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(posX, y + first, posZ, false));
            if(y+second < y + height)
                mc.thePlayer.sendQueue.addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(posX, y + second, posZ, false));
            return;
        }else if(height <1.6){
            for(int i = 0; i < offset.length; i++){
                double off = offset[i];
                y += off;
                mc.thePlayer.sendQueue.addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(posX, y, posZ, false));
            }
        }else if(height < 2.1){
            double[] heights = {0.425,0.821,0.699,0.599,1.022,1.372,1.652,1.869};
            for(double off : heights){
                mc.thePlayer.sendQueue.addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(posX, y + off, posZ, false));
            }
        }else{
            double[] heights = {0.425,0.821,0.699,0.599,1.022,1.372,1.652,1.869,2.019,1.907};
            for(double off : heights){
                mc.thePlayer.sendQueue.addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(posX, y + off, posZ, false));
            }
        }

    }
}

