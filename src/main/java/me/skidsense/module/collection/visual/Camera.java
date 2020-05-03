package me.skidsense.module.collection.visual;

import me.skidsense.hooks.Sub;
import me.skidsense.hooks.events.EventAttack;
import me.skidsense.hooks.value.Numbers;
import me.skidsense.hooks.value.Option;
import me.skidsense.module.Mod;
import me.skidsense.module.ModuleType;
import net.minecraft.util.EnumParticleTypes;

public class Camera extends Mod {
    public static Numbers<Double> CrackSize = new Numbers<Double>("CrackSize", "CrackSize", 2.0, 0.0, 10.0, 1.0);
    public static Option<Boolean> Crit = new Option<Boolean>("CritParticle", "CritParticle", true);
    public static Option<Boolean> Normal = new Option<Boolean>("NormalParticle", "NormalParticle", true);
    public Camera() {
        super("Camera", new String[] { "camera","morevisuals","moreparticle" }, ModuleType.Player);
    }
    @Sub
    public void onAttack(EventAttack e) {
        for (int index = 0; index < CrackSize.getValue().intValue(); ++index) {
            if (Crit.getValue())
                this.mc.effectRenderer.emitParticleAtEntity(e.targetEntity, EnumParticleTypes.CRIT);
            if (Normal.getValue())
                this.mc.effectRenderer.emitParticleAtEntity(e.targetEntity, EnumParticleTypes.CRIT_MAGIC);
        }
    }
}
