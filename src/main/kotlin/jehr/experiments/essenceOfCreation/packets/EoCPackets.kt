package jehr.experiments.essenceOfCreation.packets

import jehr.experiments.essenceOfCreation.screenHandlers.RefractorScreenHandler
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking

object EoCPackets {

    fun init() {
        initPackets()
        initReceivers()
    }

    private fun initPackets() {
        PayloadTypeRegistry.playC2S().register(UpdateRefractorC2SPacket.payloadId, UpdateRefractorC2SPacket.codec)
    }

    private fun initReceivers() {
        ServerPlayNetworking.registerGlobalReceiver(UpdateRefractorC2SPacket.payloadId, {
            packet, context ->
            val csh = context.player().currentScreenHandler
            if (csh is RefractorScreenHandler) {
                csh.setEffectFromPacket(packet)
            }
        })
    }
}