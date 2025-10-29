package jehr.experiments.essenceOfCreation.handledScreens

import jehr.experiments.essenceOfCreation.EoCMain
import jehr.experiments.essenceOfCreation.screenHandlers.RefractorScreenHandler
import net.minecraft.client.gl.RenderPipelines
import net.minecraft.client.gui.DrawContext
import net.minecraft.client.gui.screen.ingame.HandledScreen
import net.minecraft.entity.player.PlayerInventory
import net.minecraft.item.ItemStack
import net.minecraft.item.Items
import net.minecraft.text.Text
import net.minecraft.util.Identifier

class RefractorScreen(handler: RefractorScreenHandler, playerInv: PlayerInventory, title: Text): HandledScreen<RefractorScreenHandler>(handler, playerInv, title) {

    companion object {
        val backgroundTexture: Identifier = Identifier.of(EoCMain.MOD_ID, "textures/gui/container/refractor.png")
    }

    val delegate = this.handler.delegate

    init {
        this.backgroundWidth = 230
        this.backgroundHeight = 219
    }

    override fun init() {
        super.init()
        this.titleX = (backgroundWidth - textRenderer.getWidth(title)) / 2
    }

    override fun drawBackground(context: DrawContext, deltaTicks: Float, mouseX: Int, mouseY: Int) {
        val baseX = (this.width - this.backgroundWidth) / 2
        val baseY = (this.height - this.backgroundHeight) / 2
        context.drawTexture(RenderPipelines.GUI_TEXTURED, backgroundTexture, this.x, this.y, 0F, 0F, this.backgroundWidth, this.backgroundHeight, 256, 256)
        context.drawItem(ItemStack(Items.NETHERITE_INGOT), baseX + 20, baseY + 109)
        context.drawItem(ItemStack(Items.EMERALD), baseX + 41, baseY + 109)
        context.drawItem(ItemStack(Items.DIAMOND), baseX + 41 + 22, baseY + 109)
        context.drawItem(ItemStack(Items.GOLD_INGOT), baseX + 42 + 44, baseY + 109)
        context.drawItem(ItemStack(Items.IRON_INGOT), baseX + 42 + 66, baseY + 109)
    }

    override fun render(context: DrawContext?, mouseX: Int, mouseY: Int, deltaTicks: Float) {
        // TODO: Figure out if this is needed: renderBackground(context, mouseX, mouseY, deltaTicks)
        super.render(context, mouseX, mouseY, deltaTicks)
        drawMouseoverTooltip(context, mouseX,mouseY)
    }
}