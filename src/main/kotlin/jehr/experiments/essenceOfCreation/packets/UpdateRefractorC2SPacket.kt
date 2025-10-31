package jehr.experiments.essenceOfCreation.packets

import jehr.experiments.essenceOfCreation.EoCMain
import net.minecraft.entity.effect.StatusEffect
import net.minecraft.network.RegistryByteBuf
import net.minecraft.network.codec.PacketCodec
import net.minecraft.network.codec.PacketCodecs
import net.minecraft.network.packet.CustomPayload
import net.minecraft.registry.entry.RegistryEntry
import net.minecraft.util.Identifier
import java.util.Optional

data class UpdateRefractorC2SPacket(val blessing: Optional<RegistryEntry<StatusEffect>>, val curse: Optional<RegistryEntry<StatusEffect>>): CustomPayload {

    companion object {
        val rawId: Identifier = Identifier.of(EoCMain.MOD_ID, "update_refractor")
        val payloadId = CustomPayload.Id<UpdateRefractorC2SPacket>(rawId)
        val codec: PacketCodec<RegistryByteBuf, UpdateRefractorC2SPacket> = PacketCodec.tuple(
            StatusEffect.ENTRY_PACKET_CODEC.collect(PacketCodecs::optional),
            UpdateRefractorC2SPacket::blessing,
            StatusEffect.ENTRY_PACKET_CODEC.collect(PacketCodecs::optional),
            UpdateRefractorC2SPacket::curse,
            ::UpdateRefractorC2SPacket
        )
    }

    override fun getId() = payloadId
}