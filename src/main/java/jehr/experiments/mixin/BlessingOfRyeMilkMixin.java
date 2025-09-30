package jehr.experiments.mixin;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import jehr.experiments.essenceOfCreation.statusEffects.EoCStatusEffects;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.item.ItemStack;
import net.minecraft.item.consume.ClearAllEffectsConsumeEffect;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(ClearAllEffectsConsumeEffect.class)
public class BlessingOfRyeMilkMixin {
    @WrapMethod(method = "onConsume")
    public boolean maintainBlessing(World world, ItemStack itemStack, LivingEntity user, Operation<Boolean> original) {
        StatusEffectInstance blessing = null;
        if (user.hasStatusEffect(EoCStatusEffects.INSTANCE.getBlessingOfRye())) {
            blessing = user.getStatusEffect(EoCStatusEffects.INSTANCE.getBlessingOfRye());
        }
        boolean ret = original.call(world, itemStack, user);
        if (blessing != null) {
            user.addStatusEffect(blessing);
        }
        return ret;
    }
}
