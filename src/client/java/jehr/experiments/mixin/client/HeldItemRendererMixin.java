package jehr.experiments.mixin.client;

import com.chocohead.mm.api.ClassTinkerers;
import com.llamalad7.mixinextras.sugar.Local;
import jehr.experiments.essenceOfCreation.EoCMain;
import jehr.experiments.essenceOfCreation.utils.EarlyRiser;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.item.HeldItemRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemDisplayContext;
import net.minecraft.item.ItemStack;
import net.minecraft.item.consume.UseAction;
import net.minecraft.util.Arm;
import net.minecraft.util.Hand;
import net.minecraft.util.math.RotationAxis;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(HeldItemRenderer.class)
public abstract class HeldItemRendererMixin {

    @Shadow
    protected abstract void applyEquipOffset(MatrixStack matrices, Arm arm, float equipProgress);

    @Shadow
    public abstract void renderItem(LivingEntity entity, ItemStack stack, ItemDisplayContext renderMode, MatrixStack matrices, VertexConsumerProvider vertexConsumer, int light);

    @Inject(method = "renderFirstPersonItem", at = @At(value = "INVOKE", target = "Lnet/minecraft/item/ItemStack;getUseAction()Lnet/minecraft/item/consume/UseAction;", shift = At.Shift.AFTER), cancellable = true)
    public void renderGunSword(AbstractClientPlayerEntity player, float tickProgress, float pitch, Hand hand, float swingProgress, ItemStack item, float equipProgress, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, CallbackInfo ci, @Local(ordinal = 1) boolean bl2) {
        if (item.getUseAction() == ClassTinkerers.getEnum(UseAction.class, EarlyRiser.GUNSWORD_ENUM)) {
            this.applyGunSwordRender(player, hand, equipProgress, matrices);
            this.renderItem(player, item, bl2 ? ItemDisplayContext.FIRST_PERSON_RIGHT_HAND : ItemDisplayContext.FIRST_PERSON_LEFT_HAND, matrices, vertexConsumers, light);
            matrices.pop();
            ci.cancel();
        }
    }

    @Unique
    private void applyGunSwordRender(AbstractClientPlayerEntity player, Hand hand, float equipProgress, MatrixStack matrices) {
        boolean isMain = hand == Hand.MAIN_HAND;
        Arm arm = isMain ? player.getMainArm() : player.getMainArm().getOpposite();
        int dirMul = isMain ? 1 : -1;
        matrices.translate(dirMul * EoCMain.INSTANCE.getGsOffsetX(), EoCMain.INSTANCE.getGsOffsetY(), EoCMain.INSTANCE.getGsOffsetZ());
        matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(EoCMain.INSTANCE.getGsRotX()));
        matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(dirMul * EoCMain.INSTANCE.getGsRotY()));
        matrices.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(dirMul * EoCMain.INSTANCE.getGsRotZ()));
        this.applyEquipOffset(matrices, arm, equipProgress);
    }
}
