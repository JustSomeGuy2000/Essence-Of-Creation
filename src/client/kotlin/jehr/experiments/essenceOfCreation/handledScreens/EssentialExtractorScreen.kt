package jehr.experiments.essenceOfCreation.handledScreens

import jehr.experiments.essenceOfCreation.EoCMain
import jehr.experiments.essenceOfCreation.blocks.EssentialExtractor
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
        val backgroundTexture: Identifier = Identifier.ofVanilla("textures/gui/container/furnace.png")
        val progressTexture: Identifier = Identifier.ofVanilla("container/furnace/burn_progress")
        val fuelTexture: Identifier = Identifier.of(EoCMain.MOD_ID, "container/essential_extractor/lit_progress")
    }

    val delegate = this.handler.delegate

    override fun init() {
        super.init()
        titleX = (backgroundWidth - textRenderer.getWidth(title)) / 2
    }

    override fun drawBackground(context: DrawContext, deltaTicks: Float, mouseX: Int, mouseY: Int) {
        val baseX = this.x
        val baseY = this.y
        context.drawTexture(RenderPipelines.GUI_TEXTURED, backgroundTexture, baseX, baseY, 0.0f, 0.0f, this.backgroundWidth, this.backgroundHeight, 256, 256)
        if (delegate[0] != 0 && delegate[2] != 0) {
            // fuel texture is 14 pixels tall, with 1 pixel being the shadow at the bottom.
            val offset = MathHelper.ceil((delegate[0].toDouble()/delegate[1])*13) + 1
            context.drawGuiTexture(
                RenderPipelines.GUI_TEXTURED,
                fuelTexture,
                14,
                14,
                0,
                14 - offset,
                baseX + 56,
                baseY + 36 + 14 - offset,
                14,
                offset
            )
        }

        // progress texture is 24 pixels long, with 2 pixels of background (one on either end)
        val offset = MathHelper.ceil((delegate[2].toDouble() / EssentialExtractor.EXTRACT_TIME) * 24)
        context.drawGuiTexture(
            RenderPipelines.GUI_TEXTURED,
            progressTexture,
            24,
            16,
            0,
            0,
            baseX + 79,
            baseY + 34,
            offset,
            16
        )
    }

    override fun render(context: DrawContext, mouseX: Int, mouseY: Int, deltaTicks: Float) {
        renderBackground(context, mouseX, mouseY, deltaTicks)
        super.render(context, mouseX, mouseY, deltaTicks)
        drawMouseoverTooltip(context, mouseX, mouseY)
    }
}