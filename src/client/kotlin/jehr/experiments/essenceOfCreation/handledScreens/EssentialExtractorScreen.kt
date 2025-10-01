package jehr.experiments.essenceOfCreation.handledScreens

import jehr.experiments.essenceOfCreation.screenHandlers.EssentialExtractorScreenHandler
import net.minecraft.client.gl.RenderPipelines
import net.minecraft.client.gui.DrawContext
import net.minecraft.client.gui.screen.ingame.HandledScreen
import net.minecraft.entity.player.PlayerInventory
import net.minecraft.text.Text
import net.minecraft.util.Identifier
import net.minecraft.util.math.MathHelper

class EssentialExtractorScreen(handler: EssentialExtractorScreenHandler, playerInv: PlayerInventory, title: Text): HandledScreen<EssentialExtractorScreenHandler>(handler, playerInv, title) {

    companion object {
        val backgroundTexture = Identifier.ofVanilla("textures/gui/container/furnace.png")
        val progressTexture = Identifier.ofVanilla("container/furnace/burn_progress")
        val fuelTexture = Identifier.ofVanilla("container/furnace/lit_progress")
    }

    val delegate = this.handler.delegate

    override fun init() {
        super.init()
        titleX = (backgroundWidth - textRenderer.getWidth(title)) / 2
    }

    override fun drawBackground(context: DrawContext, deltaTicks: Float, mouseX: Int, mouseY: Int) {
        val i = this.x
        val j = this.y
        context.drawTexture(RenderPipelines.GUI_TEXTURED, backgroundTexture, i, j, 0.0f, 0.0f, this.backgroundWidth, this.backgroundHeight, 256, 256)
        if (delegate.getCurrentFuel() != 0 && delegate.getProgress() != 0) {
            val k = 14
            val l = MathHelper.ceil(delegate.getCurrentFuel() * 13.0f) + 1
            context.drawGuiTexture(
                RenderPipelines.GUI_TEXTURED,
                fuelTexture,
                14,
                14,
                0,
                14 - l,
                i + 56,
                j + 36 + 14 - l,
                14,
                l
            )
        }

        val k = 24
        val l = MathHelper.ceil(delegate.getProgress() * 24.0f)
        context.drawGuiTexture(
            RenderPipelines.GUI_TEXTURED,
            progressTexture,
            24,
            16,
            0,
            0,
            i + 79,
            j + 34,
            l,
            16
        )
    }

    override fun render(context: DrawContext, mouseX: Int, mouseY: Int, deltaTicks: Float) {
        renderBackground(context, mouseX, mouseY, deltaTicks)
        super.render(context, mouseX, mouseY, deltaTicks)
        drawMouseoverTooltip(context, mouseX, mouseY)
    }
}