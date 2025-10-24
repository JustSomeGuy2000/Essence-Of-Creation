package jehr.experiments.mixin;

import jehr.experiments.essenceOfCreation.EoCMain;
import jehr.experiments.essenceOfCreation.criteria.EoCCriteria;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(LivingEntity.class)
public class LivingEntityMixin {
    @Inject(method = "tryUseDeathProtector", at = @At(value = "INVOKE", target = "Lnet/minecraft/advancement/criterion/UsedTotemCriterion;trigger(Lnet/minecraft/server/network/ServerPlayerEntity;Lnet/minecraft/item/ItemStack;)V"), locals = LocalCapture.CAPTURE_FAILSOFT)
    public void triggerTotemOfUnryingCondition(DamageSource source, CallbackInfoReturnable<Boolean> cir, ItemStack itemStack2) {
        EoCMain.INSTANCE.getLogger().warn("Aryse triggered! With: " + itemStack2.getItem().toString());
        if ((LivingEntity) (Object) this instanceof ServerPlayerEntity) {
            EoCCriteria.INSTANCE.getRyeTotemCriterion().trigger((ServerPlayerEntity) (Object) this, itemStack2);
        }
    }
}
