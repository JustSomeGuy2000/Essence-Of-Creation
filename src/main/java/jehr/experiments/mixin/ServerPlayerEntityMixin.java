package jehr.experiments.mixin;

import jehr.experiments.essenceOfCreation.blocks.EoCBlocks;
import jehr.experiments.essenceOfCreation.statusEffects.EoCStatusEffects;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.server.network.ServerPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerPlayerEntity.class)
public class ServerPlayerEntityMixin {
    @Inject(method = "onDeath", at = @At("HEAD"))
    public void spawnStatue(DamageSource damageSource, CallbackInfo ci) {
        ServerPlayerEntity player = (ServerPlayerEntity) (Object) this;
        if (player.hasStatusEffect(EoCStatusEffects.INSTANCE.getBlessingOfRye()) && player.getWorld() != null) {
            player.getWorld().setBlockState(player.getBlockPos(), EoCBlocks.INSTANCE.getRoggenStatue().getDefaultState());
            player.getInventory().clear();
            player.setExperienceLevel(0);
        }
    }
}
