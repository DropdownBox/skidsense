package me.skidsense.module.collection.move;

import me.skidsense.Client;
import me.skidsense.hooks.Sub;
import me.skidsense.hooks.events.EventMove;
import me.skidsense.hooks.events.EventPreUpdate;
import me.skidsense.hooks.value.Mode;
import me.skidsense.hooks.value.Numbers;
import me.skidsense.hooks.value.Option;
import me.skidsense.module.Mod;
import me.skidsense.module.ModuleType;
import me.skidsense.module.collection.player.NoFall;
import me.skidsense.util.PlayerUtil;
import me.skidsense.util.TimerUtil;
import net.minecraft.block.Block;
import net.minecraft.block.BlockGlass;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.potion.Potion;
import net.minecraft.util.BlockPos;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MovementInput;

public class Flight
        extends Mod {
    int counter;
    int level;
    public static int fastFlew;
    public static double hypixel;
    private Mode<Enum> hypixelmode = new Mode<Enum>("DamageMode","DamageMode", (Enum[]) flyhypmode.values(), (Enum) flyhypmode.Hypixel);
    private Mode<Enum> mode = new Mode<Enum>("Mode","Mode", (Enum[]) flymode.values(), (Enum) flymode.Motion);
    private static Numbers<Double> speed = new Numbers<Double>("Speed","Speed", 4.5, 1.0, 7.0, 0.1);
    public static Numbers<Double> zoomspeed = new Numbers<Double>("ZoomSpeed","ZoomSpeed", 2.0, 0.1, 15.0, 0.1);
    public static Numbers<Double> timer = new Numbers<Double>("Timer","Timer", 2.0, 1.0, 7.0, 0.1);
    public static Numbers<Double> timerduration = new Numbers<Double>("TimerDuration","TimerDuration",400.0, 0.0, 1000.0, 50.0);
    public static Option<Boolean> damage = new Option<Boolean>("Damage","Damage", true);
    private Option<Boolean> timerboost = new Option<Boolean>("TimerBoost","TimerBoost", true);
    private Option<Boolean> boost = new Option<Boolean>("Boost","Boost", true);
    private Option<Boolean> bob = new Option<Boolean>("ViewBob","ViewBob", false);

    TimerUtil time;

    public Flight() {
        super("Flight",new String[] {"fly"}, ModuleType.Move);
        this.time = new TimerUtil();

    }



    @Override
    public void onEnable() {
        if (this.mode.getValue()==flymode.Hypixel) {
            if (this.damage.getValue()) {
                if (this.hypixelmode.getValue()==flyhypmode.Hypixel) {
                    int i = 0;
                    while (i <= 48) {
                        this.mc.thePlayer.sendQueue.getNetworkManager().sendPacket(new C03PacketPlayer.C04PacketPlayerPosition(this.mc.thePlayer.posX, this.mc.thePlayer.posY + 0.0514865, this.mc.thePlayer.posZ, false));
                        this.mc.thePlayer.sendQueue.getNetworkManager().sendPacket(new C03PacketPlayer.C04PacketPlayerPosition(this.mc.thePlayer.posX, this.mc.thePlayer.posY + 0.0618865, this.mc.thePlayer.posZ, false));
                        this.mc.thePlayer.sendQueue.getNetworkManager().sendPacket(new C03PacketPlayer.C04PacketPlayerPosition(this.mc.thePlayer.posX, this.mc.thePlayer.posY + 1.0E-12, this.mc.thePlayer.posZ, false));
                        ++i;
                    }
                    this.mc.thePlayer.sendQueue.getNetworkManager().sendPacket(new C03PacketPlayer.C04PacketPlayerPosition(this.mc.thePlayer.posX, this.mc.thePlayer.posY, this.mc.thePlayer.posZ, true));
                }
                else if (this.hypixelmode.getValue()==flyhypmode.HypixelCN) {
                    this.damagePlayer(1);
                }
                else if (this.hypixelmode.getValue()==flyhypmode.UHC) {
                    int j = 0;
                    while (j <= 64) {
                        this.mc.thePlayer.sendQueue.getNetworkManager().sendPacket(new C03PacketPlayer.C04PacketPlayerPosition(this.mc.thePlayer.posX, this.mc.thePlayer.posY + 0.0514865, this.mc.thePlayer.posZ, false));
                        this.mc.thePlayer.sendQueue.getNetworkManager().sendPacket(new C03PacketPlayer.C04PacketPlayerPosition(this.mc.thePlayer.posX, this.mc.thePlayer.posY + 0.0618865, this.mc.thePlayer.posZ, false));
                        this.mc.thePlayer.sendQueue.getNetworkManager().sendPacket(new C03PacketPlayer.C04PacketPlayerPosition(this.mc.thePlayer.posX, this.mc.thePlayer.posY + 1.0E-12, this.mc.thePlayer.posZ, false));
                        ++j;
                    }
                    this.mc.thePlayer.sendQueue.getNetworkManager().sendPacket(new C03PacketPlayer.C04PacketPlayerPosition(this.mc.thePlayer.posX, this.mc.thePlayer.posY, this.mc.thePlayer.posZ, true));
                }
                else if (this.hypixelmode.getValue()==flyhypmode.MW) {
                    int k = 0;
                    while (k < 70) {
                        this.mc.thePlayer.sendQueue.addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(this.mc.thePlayer.posX, this.mc.thePlayer.posY + 0.06, this.mc.thePlayer.posZ, false));
                        this.mc.thePlayer.sendQueue.addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(this.mc.thePlayer.posX, this.mc.thePlayer.posY, this.mc.thePlayer.posZ, false));
                        ++k;
                    }
                    this.mc.thePlayer.sendQueue.addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(this.mc.thePlayer.posX, this.mc.thePlayer.posY + 0.1, this.mc.thePlayer.posZ, false));
                }
            }
            PlayerUtil.setMotion(defaultSpeed() + this.getSpeedEffect() * 0.05f);
            fastFlew = 25;
            hypixel = zoomspeed.getValue() + this.speed.getValue();
            if (this.timerboost.getValue()) {
                this.mc.timer.timerSpeed = this.timer.getValue().floatValue();
            }
            this.time.reset();
        }
        super.onEnable();
    }

    @Override
    public void onDisable() {
        this.mc.timer.timerSpeed = 1.0f;
        final EntityPlayerSP thePlayer = this.mc.thePlayer;
        thePlayer.motionX *= 0.0;
        final EntityPlayerSP thePlayer2 = this.mc.thePlayer;
        thePlayer2.motionZ *= 0.0;
        super.onDisable();
    }

    @Sub
    public void Move(EventMove event) {
        if (this.mode.getValue()==flymode.Motion) {
            final double doubleValue = this.speed.getValue();
            final MovementInput movementInput = this.mc.thePlayer.movementInput;
            double n = MovementInput.moveForward;
            final MovementInput movementInput2 = this.mc.thePlayer.movementInput;
            double n2 = MovementInput.moveStrafe;
            float rotationYaw = this.mc.thePlayer.rotationYaw;
            if (n == 0.0 && n2 == 0.0) {
                event.setX(0.0);
                event.setZ(0.0);
            }
            else {
                if (n != 0.0) {
                    if (n2 > 0.0) {
                        final float n3 = rotationYaw;
                        int n4;
                        if (n > 0.0) {
                            n4 = -45;
                        }
                        else {
                            n4 = 45;
                        }
                        rotationYaw = n3 + n4;
                    }
                    else if (n2 < 0.0) {
                        final float n5 = rotationYaw;
                        int n6;
                        if (n > 0.0) {
                            n6 = 45;
                        }
                        else {
                            n6 = -45;
                        }
                        rotationYaw = n5 + n6;
                    }
                    n2 = 0.0;
                    if (n > 0.0) {
                        n = 1.0;
                    }
                    else if (n < 0.0) {
                        n = -1.0;
                    }
                }
                event.setX(n * doubleValue * Math.cos(Math.toRadians(rotationYaw + 90.0f)) + n2 * doubleValue * Math.sin(Math.toRadians(rotationYaw + 90.0f)));
                event.setZ(n * doubleValue * Math.sin(Math.toRadians(rotationYaw + 90.0f)) - n2 * doubleValue * Math.cos(Math.toRadians(rotationYaw + 90.0f)));
            }
        }
    }

    @Sub
    public void onMotion(final EventPreUpdate eventMotion) {
        this.setSuffix(mode.getValue());
        if (this.mode.getValue()==flymode.Motion) {
            this.mc.thePlayer.onGround = false;
            boolean onGround;
            if (this.isOnGround(0.001) || Client.getModuleManager().getModuleByClass(NoFall.class).isEnabled()) {
                onGround = true;
            }
            else {
                onGround = false;
            }
            eventMotion.setOnGround(onGround);
            if (this.mc.thePlayer.movementInput.jump) {
                this.mc.thePlayer.motionY = this.speed.getValue() * 0.6;
            }
            else if (this.mc.thePlayer.movementInput.sneak) {
                this.mc.thePlayer.motionY = -this.speed.getValue() * 0.6;
            }
            else {
                this.mc.thePlayer.motionY = 0.0;
            }
        }
        if (this.mode.getValue()==flymode.Hypixel) {
            ++fastFlew;
            final Block blockUnderPlayer = getBlockUnderPlayer(this.mc.thePlayer, 0.2);
            if (!this.isOnGround(1.0E-7) && !blockUnderPlayer.isFullBlock() && !(blockUnderPlayer instanceof BlockGlass)) {
                this.mc.thePlayer.motionY = 0.0;
                this.mc.thePlayer.motionX = 0.0;
                this.mc.thePlayer.motionZ = 0.0;
                if (this.bob.getValue()) {
                    this.mc.thePlayer.cameraYaw = 0.1f;
                }
                float n = 0.29f + this.getSpeedEffect() * 0.06f;
                if (hypixel > 0.0) {
                    if ((this.mc.thePlayer.moveForward == 0.0f && this.mc.thePlayer.moveStrafing == 0.0f) || this.mc.thePlayer.isCollidedHorizontally) {
                        hypixel = 0.0;
                    }
                    n += (float)(hypixel / 18.0);
                    hypixel -= 0.165 + this.getSpeedEffect() * 0.006;
                }
                if (this.boost.getValue()) {
                    PlayerUtil.setMotion(n);
                }
                else {
                    PlayerUtil.setMotion(defaultSpeed());
                }
                this.mc.thePlayer.jumpMovementFactor = 0.0f;
                this.mc.thePlayer.onGround = false;
                switch (++this.counter) {
                    case 1: {
                        this.mc.thePlayer.setPosition(this.mc.thePlayer.posX, this.mc.thePlayer.posY + 1.0E-12, this.mc.thePlayer.posZ);
                        break;
                    }
                    case 2: {
                        this.mc.thePlayer.setPosition(this.mc.thePlayer.posX, this.mc.thePlayer.posY - 1.0E-12, this.mc.thePlayer.posZ);
                        break;
                    }
                    case 3: {
                        this.mc.thePlayer.setPosition(this.mc.thePlayer.posX, this.mc.thePlayer.posY + 1.0E-12, this.mc.thePlayer.posZ);
                        this.counter = 0;
                        break;
                    }
                }
                if (this.time.delay(this.timerduration.getValue().intValue())) {
                    this.mc.timer.timerSpeed = 1.0f;
                }
            }
        }

    }

    public static Block getBlockUnderPlayer(final EntityPlayerSP entityPlayerMP, final double n) {
        return Minecraft.getMinecraft().theWorld.getBlockState(new BlockPos(entityPlayerMP.posX, entityPlayerMP.posY - n, entityPlayerMP.posZ)).getBlock();
    }

    public boolean isOnGround(final double n) {
        return !this.mc.theWorld.getCollidingBoundingBoxes(this.mc.thePlayer, this.mc.thePlayer.getEntityBoundingBox().offset(0.0, -n, 0.0)).isEmpty();
    }

    public void damagePlayer(int damage) {
        if (damage < 1) {
            damage = 1;
        }
        if (damage > MathHelper.floor_double((double) mc.thePlayer.getMaxHealth())) {
            damage = MathHelper.floor_double((double) mc.thePlayer.getMaxHealth());
        }
        final double offset = 0.0625;
        if (mc.thePlayer != null && mc.getNetHandler() != null && mc.thePlayer.onGround) {
            for (int i = 0; i <= (3 + damage) / offset; ++i) {
                mc.getNetHandler()
                        .addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX,
                                mc.thePlayer.posY + offset, mc.thePlayer.posZ, false));
                mc.getNetHandler()
                        .addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX,
                                mc.thePlayer.posY, mc.thePlayer.posZ, i == (3 + damage) / offset));
            }
        }
    }

    public static double defaultSpeed() {
        double n = 0.2873;
        if (Minecraft.getMinecraft().thePlayer.isPotionActive(Potion.moveSpeed)) {
            n *= 1.0 + 0.2 * (Minecraft.getMinecraft().thePlayer.getActivePotionEffect(Potion.moveSpeed).getAmplifier() + 1);
        }
        return n;
    }

    public int getSpeedEffect() {
        if (this.mc.thePlayer.isPotionActive(Potion.moveSpeed)) {
            return this.mc.thePlayer.getActivePotionEffect(Potion.moveSpeed).getAmplifier() + 1;
        }
        return 0;
    }

    static {
        hypixel = 0.0;
    }
    enum flymode{
        Motion,Hypixel;
    }
    enum flyhypmode{
        MW,UHC,Hypixel,HypixelCN
    }
}
