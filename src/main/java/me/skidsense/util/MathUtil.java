/*
 * Decompiled with CFR 0_132.
 */
package me.skidsense.util;

import java.util.List;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

import me.skidsense.Client;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.entity.Entity;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.MathHelper;

public class MathUtil {
    public static Random random = new Random();

    public static double toDecimalLength(double in, int places) {
        return Double.parseDouble(String.format("%." + places + "f", in));
    }

    public static double round(double in, int places) {
        places = (int)MathHelper.clamp_double(places, 0.0, 2.147483647E9);
        return Double.parseDouble(String.format("%." + places + "f", in));
    }

    public static boolean parsable(String s, byte type) {
        try {
            switch (type) {
                case 0: {
                    Short.parseShort(s);
                    break;
                }
                case 1: {
                    Byte.parseByte(s);
                    break;
                }
                case 2: {
                    Integer.parseInt(s);
                    break;
                }
                case 3: {
                    Float.parseFloat(s);
                    break;
                }
                case 4: {
                    Double.parseDouble(s);
                    break;
                }
                case 5: {
                    Long.parseLong(s);
                }
                default: {
                    break;
                }
            }
        }
        catch (NumberFormatException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public static double square(double in) {
        return in * in;
    }

    public static double randomDouble(double min, double max) {
        return ThreadLocalRandom.current().nextDouble(min, max);
    }

    public static double getBaseMovementSpeed() {
        double baseSpeed = 0.2873;
        if (Client.mc.thePlayer.isPotionActive(Potion.moveSpeed)) {
            int amplifier = Client.mc.thePlayer.getActivePotionEffect(Potion.moveSpeed).getAmplifier();
            baseSpeed *= 1.0 + 0.2 * (double)(amplifier + 1);
        }
        return baseSpeed;
    }

    public static double getHighestOffset(double max) {
        double i = 0.0;
        while (i < max) {
            int[] arrn = new int[5];
            arrn[0] = -2;
            arrn[1] = -1;
            arrn[3] = 1;
            arrn[4] = 2;
            int[] arrn2 = arrn;
            int n = arrn.length;
            int n2 = 0;
            while (n2 < n) {
                int offset = arrn2[n2];
                if (Client.mc.theWorld.getCollidingBoundingBoxes(Client.mc.thePlayer, Client.mc.thePlayer.getEntityBoundingBox().offset(Client.mc.thePlayer.motionX * (double)offset, i, Client.mc.thePlayer.motionZ * (double)offset)).size() > 0) {
                    return i - 0.01;
                }
                ++n2;
            }
            i += 0.01;
        }
        return max;
    }

    public static int getMiddle(int i, int i1) {
        return (i + i1) / 2;
    }


    public static class NumberType {
        public static final byte SHORT = 0;
        public static final byte BYTE = 1;
        public static final byte INT = 2;
        public static final byte FLOAT = 3;
        public static final byte DOUBLE = 4;
        public static final byte LONG = 5;

        public static byte getByType(Class cls) {
            if (cls == Short.class) {
                return 0;
            }
            if (cls == Byte.class) {
                return 1;
            }
            if (cls == Integer.class) {
                return 2;
            }
            if (cls == Float.class) {
                return 3;
            }
            if (cls == Double.class) {
                return 4;
            }
            if (cls == Long.class) {
                return 5;
            }
            return -1;
        }
    }

    public static double getRandomInRange(double max, double min) {
        return min + (max - min) * random.nextDouble();
    }

    public static int getRandomInRange(int max, int min) {
        return (int) (min + (max - min) * random.nextDouble());
    }

	public static int getRandom(final int floor, final int cap) {
		return floor + random.nextInt(cap - floor + 1);
	}
	public static double getRandom(final double floor, final double cap) {
		return floor + random.nextInt((int) (cap - floor + 1));
	}

	public static float getRandomInRange(float min, float max) {
		Random random = new Random();
		float range = max - min;
		float scaled = random.nextFloat() * range;
		float shifted = scaled + min;
		return shifted;
	}

}

