package jehr.experiments.essenceOfCreation.handledScreens

import jehr.experiments.essenceOfCreation.EoCMain
import jehr.experiments.essenceOfCreation.blocks.EssentialInfuser
import jehr.experiments.essenceOfCreation.screenHandlers.EssentialInfuserScreenHandler
import net.minecraft.client.gl.RenderPipelines
import net.minecraft.client.gui.DrawContext
import net.minecraft.client.gui.screen.ingame.HandledScreen
import net.minecraft.entity.player.PlayerInventory
import net.minecraft.text.Text
import net.minecraft.util.Identifier
import kotlin.math.ceil

class EssentialInfuserScreen(handler: EssentialInfuserScreenHandler, playerInv: PlayerInventory, title: Text): HandledScreen<EssentialInfuserScreenHandler>(handler, playerInv, title) {

    companion object {
        val backgroundTexture: Identifier = Identifier.of(EoCMain.MOD_ID, "textures/gui/container/essential_infuser.png")
        val progressTexture: Identifier = Identifier.of(EoCMain.MOD_ID, "container/essential_infuser/infuse_progress")
    }

    val delegate = this.handler.delegate

    override fun init() {
        super.init()
        //this.titleX = (backgroundWidth - textRenderer.getWidth(title)) / 2
        this.title = Text.literal("")
    }

    override fun drawBackground(context: DrawContext, deltaTicks: Float, mouseX: Int, mouseY: Int) {
        val baseX = this.x
        val baseY = this.y
        context.drawTexture(RenderPipelines.GUI_TEXTURED, backgroundTexture, baseX, baseY, 0.0f, 0.0f, this.backgroundWidth, this.backgroundHeight, 256, 256)

        if (delegate[0] != 0) {
            val progressPercent = ceil(delegate[0] / EssentialInfuser.INFUSE_TIME_F * 51).toInt() + 1
            context.drawGuiTexture(RenderPipelines.GUI_TEXTURED, progressTexture, 51, 51, 0, 0, baseX + 51, baseY + 38, progressPercent, 17)
        }
    }

    override fun render(context: DrawContext?, mouseX: Int, mouseY: Int, deltaTicks: Float) {
        renderBackground(context, mouseX, mouseY, deltaTicks)
        super.render(context, mouseX, mouseY, deltaTicks)
        drawMouseoverTooltip(context, mouseX,mouseY)
    }
}