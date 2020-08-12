package me.skidsense.module.collection.move;

import com.google.common.base.Stopwatch;
import me.skidsense.hooks.Sub;
import me.skidsense.hooks.events.EventMove;
import me.skidsense.hooks.events.EventPostUpdate;
import me.skidsense.hooks.events.EventPreUpdate;
import me.skidsense.hooks.events.EventTick;
import me.skidsense.hooks.value.Mode;
import me.skidsense.hooks.value.Numbers;
import me.skidsense.hooks.value.Option;
import me.skidsense.module.Mod;
import me.skidsense.module.ModuleType;
import me.skidsense.module.collection.player.AntiFall;
import me.skidsense.util.MathUtil;
import me.skidsense.util.MoveUtil;
import me.skidsense.util.PlayerUtil;
import me.skidsense.util.TimerUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.potion.Potion;


public class Flight extends Mod {
    public me.skidsense.hooks.value.Mode<Enum> Mode = new me.skidsense.hooks.value.Mode<Enum>("Mode", "Mode", FlightMode.values(), FlightMode.Vanilla);
    public Numbers<Double> MultiplySpeed = new Numbers<Double>("MultiplySpeed", "MultiplySpeed", 1.7, 1.0, 2.5, 0.1);
    public Numbers<Double> MultiplyTime = new Numbers<Double>("MultiplyTime", "MultiplyTime" , 800.0, 100.0, 1200.0, 50.0);
    public Option<Boolean> Multiplier = new Option<Boolean>("Multiplier", "Multiplier", true);
    public Option<Boolean> bob = new Option<Boolean>("Bob", "Bob", true);

    public Option<Boolean> UHC = new Option<Boolean>("UHC", "UHC", false);
    public Numbers<Double> vanillaSpeed = new Numbers<Double>("VanillaSpeed", "VanillaSpeed" , 5.0, 0.1, 7.0, 0.1);

    boolean jump;
    TimerUtil timer = new TimerUtil();
    int level;
    double moveSpeed;
    double lastDist;
    private int counter;
    private double y;
    private int zoom;
    private TimerUtil boosttimer = new TimerUtil();


    public Flight() {
        super("Flight",new String[]{"Fly"}, ModuleType.Move);
//        this.settings.add(option = new ValueMode<Mode>("Mode", Flight.Mode.values(), Flight.Mode.Zoom));
//        this.settings.add(bob = new ValueBoolean<Boolean>("Bobbing", true));
//        this.settings.add(vanillaSpeed = new ValueNumber("VanillaSpeed", 5.0, 0.1, 7.0, 0.1));
//        this.settings.add(UHC = new ValueBoolean<Boolean>("UHC", false));
//        this.settings.add(multiplier = new ValueBoolean<Boolean>("Multiplier", true));
//        this.settings.add(multiplyspeed = new ValueNumber<Double>("MultiplySpeed", 1.7, 1.0, 2.5, 0.05));
//        this.settings.add(multiplytime = new ValueNumber<Double>("MultiplyTime", 800.0, 100.0, 1200.0, 50.0));

    }

    enum FlightMode{
        Damage,Vanilla
    }

    public static void UHCdamage() {
        if (mc.thePlayer.onGround) {
            final double offset = 0.4122222218322211111111F;
            final NetHandlerPlayClient netHandler = mc.getNetHandler();
            final EntityPlayerSP player = mc.thePlayer;
            final double x = player.posX;
            final double y = player.posY;
            final double z = player.posZ;
            for (int i = 0; i < 9; i++) {
                netHandler.addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(x, y + offset, z, false));
                netHandler.addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(x, y + 0.000002737272, z, false));
                netHandler.addToSendQueue(new C03PacketPlayer(false));
            }
            netHandler.addToSendQueue(new C03PacketPlayer(true));
        }
    }

    public static void damage() {
        if (mc.thePlayer.onGround) {
            final double offset = 0.4122222218322211111111F;
            final NetHandlerPlayClient netHandler = mc.getNetHandler();
            final EntityPlayerSP player = mc.thePlayer;
            final double x = player.posX;
            final double y = player.posY;
            final double z = player.posZ;
            for (int i = 0; i < 9; i++) {
                netHandler.addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(x, y + offset, z, false));
                netHandler.addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(x, y + 0.000002737272, z, false));
                netHandler.addToSendQueue(new C03PacketPlayer(false));
            }
            netHandler.addToSendQueue(new C03PacketPlayer(true));
        }
    }

    @Override
    public void onEnable() {
        if (Mode.getValue() == FlightMode.Damage) {
            if (this.UHC.getValue().booleanValue()) {
                UHCdamage();
            } else {
                damage();
            }
        }
        EntityPlayerSP player = mc.thePlayer;
        mc.thePlayer.posY += 0.42;
        boosttimer.reset();
        this.y = 0.0;
        this.lastDist = 0.0;
        this.level = 1;
        this.counter = 0;
        player.stepHeight = 0.0f;
        player.motionX = 0.0;
        player.motionZ = 0.0;
    }

    @Override
    public void onDisable() {
        EntityPlayerSP player = mc.thePlayer;
        mc.timer.timerSpeed = 1.0f;
        player.stepHeight = 0.625f;
        player.motionX = 0.0;
        player.motionZ = 0.0;
        jump = false;
        if (Mode.getValue() == FlightMode.Damage) {
            player.setPosition(player.posX, player.posY + this.y, player.posZ);
        }
    }

    @Sub
    public void onPost(EventPostUpdate e) {
        if (Mode.getValue() == FlightMode.Damage) {
            double xDist = Minecraft.getMinecraft().thePlayer.posX - Minecraft.getMinecraft().thePlayer.prevPosX;
            double zDist = Minecraft.getMinecraft().thePlayer.posZ - Minecraft.getMinecraft().thePlayer.prevPosZ;
            lastDist = Math.sqrt(xDist * xDist + zDist * zDist);
        }
    }

    @Sub
    public final void onMove(EventMove e) {

        EntityPlayerSP player = mc.thePlayer;
        GameSettings gameSettings = mc.gameSettings;
        switch (Mode.getValue().toString()) {
            case "Damage": {
                if (level != 1 || Minecraft.getMinecraft().thePlayer.moveForward == 0.0F
                        && Minecraft.getMinecraft().thePlayer.moveStrafing == 0.0F) {
                    if (level == 2) {
                        this.moveSpeed = 1.3;
                        level = 3;
                    } else if (level == 3) {
                        level = 4;
                        double difference = (mc.thePlayer.ticksExisted % 2 == 0 ? 0.0203D : 0.0223D)
                                * (lastDist - MathUtil.getBaseMovementSpeed());
                        moveSpeed = lastDist - difference;
                    } else {
                        if (Minecraft.getMinecraft().theWorld.getCollidingBoundingBoxes(Minecraft.getMinecraft().thePlayer,
                                Minecraft.getMinecraft().thePlayer.boundingBox.offset(0.0D,
                                        Minecraft.getMinecraft().thePlayer.motionY, 0.0D))
                                .size() > 0 || Minecraft.getMinecraft().thePlayer.isCollidedVertically) {
                            level = 1;
                        }
                        moveSpeed = lastDist - lastDist / 159.0;
                    }
                } else {
                    level = 2;
                    int amplifier = Minecraft.getMinecraft().thePlayer.isPotionActive(Potion.moveSpeed)
                            ? Minecraft.getMinecraft().thePlayer.getActivePotionEffect(Potion.moveSpeed).getAmplifier() + 1
                            : 0;
                    double boost = Minecraft.getMinecraft().thePlayer.isPotionActive(Potion.moveSpeed) ? 1.5 : 2.034;
                    moveSpeed = boost * MathUtil.getBaseMovementSpeed();
                }
                moveSpeed = Mode.getValue() == FlightMode.Damage ? Math.max(moveSpeed, MoveUtil.getBaseMoveSpeed())
                        : MoveUtil.getBaseMoveSpeed();
                MoveUtil.setMotion(e, moveSpeed);
                break;
            }
            case "vanilla": {
                MoveUtil.setMotion(e, ((Double) this.vanillaSpeed.getValue()).intValue());
                break;
            }
        }
    }

    @Sub
    public final void onMotionUpdate(EventPreUpdate event) {
        if(!jump){
            mc.thePlayer.motionY = 0.41999998688697815D;
            jump = true;
        }
        if (bob.getValue()) {
            this.mc.thePlayer.cameraYaw = (float) 0.1;
        }
        EntityPlayerSP player = mc.thePlayer;
        net.minecraft.util.Timer timer = mc.timer;
        GameSettings gameSettings = mc.gameSettings;
        this.setSuffix(Mode.getValue().toString());
        switch (Mode.getValue().toString()) {
            case "Damage": {
                if (Multiplier.getValue()) {
                    if(!boosttimer.isDelayComplete(MultiplyTime.getValue().longValue())){
                        mc.timer.timerSpeed = (float) MultiplySpeed.getValue().doubleValue();
                    }
                }
                if (this.level <= 2)
                    break;
                mc.thePlayer.setPosition(mc.thePlayer.posX, mc.thePlayer.posY - 0.002,
                        mc.thePlayer.posZ);
                ++this.counter;
                double offset = 1.25E-3;
                switch (this.counter) {
                    case 1: {
                        this.y *= -0.93666665455465;
                        break;
                    }
                    case 2:
                    case 3:
                    case 4: {
                        this.y += 1.25E-3;
                        break;
                    }
                    case 5: {
                        this.y += 1.0E-3;
                        this.counter = 0;
                        break;
                    }
                }
                event.setY(mc.thePlayer.posY + this.y);
                if (this.level <= 2)
                    break;
                mc.thePlayer.setPosition(mc.thePlayer.posX, mc.thePlayer.posY + 0.002,
                        mc.thePlayer.posZ);
            }
            case "Vanilla": {
                player.motionY = 0.0;
                if (gameSettings.keyBindJump.isKeyDown()) {
                    player.motionY = 2.0;
                    break;
                }
                if (!gameSettings.keyBindSneak.isKeyDown())
                    break;
                player.motionY = -2.0;
                break;
            }
        }
        double xDif = player.posX - player.prevPosX;
        double zDif = player.posZ - player.prevPosZ;
        this.lastDist = Math.sqrt(xDif * xDif + zDif * zDif);

    }
}