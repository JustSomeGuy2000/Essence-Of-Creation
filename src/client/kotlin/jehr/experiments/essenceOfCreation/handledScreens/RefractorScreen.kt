package jehr.experiments.essenceOfCreation.handledScreens

import jehr.experiments.essenceOfCreation.screenHandlers.RefractorScreenHandler
import net.minecraft.client.gui.DrawContext
import net.minecraft.client.gui.screen.ingame.HandledScreen
import net.minecraft.entity.player.PlayerInventory
import net.minecraft.text.Text

class RefractorScreen(handler: RefractorScreenHandler, playerInv: PlayerInventory, title: Text): HandledScreen<RefractorScreenHandler>(handler, playerInv, title) {

    override fun drawBackground(context: DrawContext, deltaTicks: Float, mouseX: Int, mouseY: Int) {
        TODO("Not yet implemented")
    }
}