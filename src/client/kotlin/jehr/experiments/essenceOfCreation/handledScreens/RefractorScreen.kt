package jehr.experiments.essenceOfCreation.handledScreens

import jehr.experiments.essenceOfCreation.EoCMain
import jehr.experiments.essenceOfCreation.blockEntities.RefractorBlockEntity
import jehr.experiments.essenceOfCreation.blocks.Refractor
import jehr.experiments.essenceOfCreation.clientUtils.BaseButton
import jehr.experiments.essenceOfCreation.clientUtils.HasButtonTextures
import jehr.experiments.essenceOfCreation.clientUtils.IconButton
import jehr.experiments.essenceOfCreation.clientUtils.PropertyLinkedButton
import jehr.experiments.essenceOfCreation.screenHandlers.RefractorScreenHandler
import net.minecraft.client.gl.RenderPipelines
import net.minecraft.client.gui.DrawContext
import net.minecraft.client.gui.hud.InGameHud
import net.minecraft.client.gui.screen.ingame.HandledScreen
import net.minecraft.client.gui.tooltip.Tooltip
import net.minecraft.entity.effect.StatusEffect
import net.minecraft.entity.player.PlayerInventory
import net.minecraft.item.ItemStack
import net.minecraft.item.Items
import net.minecraft.registry.entry.RegistryEntry
import net.minecraft.screen.BeaconScreenHandler
import net.minecraft.screen.ScreenTexts
import net.minecraft.text.Text
import net.minecraft.util.Identifier
import kotlin.math.sign

class RefractorScreen(handler: RefractorScreenHandler, playerInv: PlayerInventory, title: Text): HandledScreen<RefractorScreenHandler>(handler, playerInv, Text.literal("")) {

    companion object: HasButtonTextures {
        val backgroundTexture: Identifier = Identifier.of(EoCMain.MOD_ID, "textures/gui/container/refractor.png")
        val blessingTextCoords = Pair(62, 10)
        val blessingText: Text = Text.translatable("block.${EoCMain.MOD_ID}.${Refractor.ID}.blessing")
        val curseTextCoords = Pair(169,10)
        val curseText: Text = Text.translatable("block.${EoCMain.MOD_ID}.${Refractor.ID}.curse")
        const val TEXT_COLOUR = -2039584

        override var buttonDisabledTexture: Identifier = Identifier.ofVanilla("container/beacon/button_disabled")
        override var buttonSelectedTexture: Identifier = Identifier.ofVanilla("container/beacon/button_selected")
        override var buttonHighlightedTexture: Identifier = Identifier.ofVanilla("container/beacon/button_highlighted")
        override var buttonNormalTexture: Identifier = Identifier.ofVanilla("container/beacon/button")

        val cancelTexture: Identifier = Identifier.ofVanilla("container/beacon/cancel")
        val cancelButtonCoords = Pair(190, 107)
        val doneTexture: Identifier = Identifier.ofVanilla("container/beacon/confirm")
        val doneButtonCoords = Pair(164, 107)
        const val BLESSING_PYRAMID_X = 70
        const val CURSE_PYRAMID_X = 185
        const val PYRAMIDS_Y = 20
        const val PYRAMID_ROW_SPACING = 8
        const val PYRAMID_COLUMN_SPACING = 4
    }

    val delegate = this.handler.delegate
    val buttons  = mutableListOf<BaseButton<*>>()
    val chosenBlessing: RegistryEntry<StatusEffect>? = null
    val chosenCurse: RegistryEntry<StatusEffect>? = null

    init {
        this.backgroundWidth = 230
        this.backgroundHeight = 219
    }

    override fun init() {
        super.init()
        this.titleX = (backgroundWidth - textRenderer.getWidth(title)) / 2
        this.buttons.clear()
        this.addButton(CancelButton())
        this.addButton(DoneButton())
        this.buildPyramid(RefractorBlockEntity.blessingsByLevel, BLESSING_PYRAMID_X + this.x, true)
        this.buildPyramid(RefractorBlockEntity.cursesEffectsByLevel, CURSE_PYRAMID_X + this.x, false)
    }

    fun <T: BaseButton<*>> addButton(button: T) {
        this.buttons.add(button)
        this.addDrawableChild(button)
    }

    fun buildPyramid(items: List<List<RegistryEntry<StatusEffect>>>, startMidX: Int,blessing: Boolean, startY: Int = PYRAMIDS_Y + this.y, rowSpacing: Int = PYRAMID_ROW_SPACING, columnSpacing: Int = PYRAMID_COLUMN_SPACING, buttonDims: Pair<Int, Int> = IconButton.defaultIconDims) {
        for ((levelInd, levelList) in items.withIndex()) {
            val len = levelList.size
            for ((effectInd, effect) in levelList.withIndex()) {
                val currentX = startMidX + (effectInd - (len / 2.0)) * buttonDims.first + columnSpacing * effectInd * sign(effectInd - (len / 2.0))
                val currentY = startY + levelInd * (buttonDims.second + rowSpacing)
                val button = EffectButton(blessing, effect, currentX.toInt(), currentY, {this.delegate.set(if (blessing) RefractorBlockEntity.INDEX_BLESSING else RefractorBlockEntity.INDEX_CURSE, BeaconScreenHandler.getRawIdForStatusEffect(it))}, levelInd)
                this.addButton(button)
            }
        }
    }

    override fun drawForeground(context: DrawContext, mouseX: Int, mouseY: Int) {
        super.drawForeground(context, mouseX, mouseY)
        context.drawCenteredTextWithShadow(this.textRenderer, blessingText, blessingTextCoords.first, blessingTextCoords.second, TEXT_COLOUR)
        context.drawCenteredTextWithShadow(this.textRenderer, curseText, curseTextCoords.first, curseTextCoords.second, TEXT_COLOUR)
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
        super.render(context, mouseX, mouseY, deltaTicks)
        drawMouseoverTooltip(context, mouseX,mouseY)
    }

    override fun handledScreenTick() {
        super.handledScreenTick()
        this.tickButtons()
    }

    fun tickButtons() {
        for (button in this.buttons) {
            button.tickWithInt(this.delegate.get(RefractorBlockEntity.INDEX_LEVEL))
        }
    }

    inner class CancelButton(): IconButton<Companion>(RefractorScreen, cancelTexture, this@RefractorScreen.x + cancelButtonCoords.first, this@RefractorScreen.y + cancelButtonCoords.second, ScreenTexts.CANCEL) {

        override fun onPress() = this@RefractorScreen.client?.player?.closeHandledScreen() ?: Unit
    }

    inner class DoneButton(): IconButton<Companion>(RefractorScreen, doneTexture,  this@RefractorScreen.x + doneButtonCoords.first, this@RefractorScreen.y + doneButtonCoords.second, ScreenTexts.DONE) {

        override fun onPress() {
            // TODO: See if networking is needed like in the BeaconScreen function
            // TODO: Update selected blessing and curse for the block entity
            this@RefractorScreen.client?.player?.closeHandledScreen()
        }

        override fun tickWithInt(level: Int) {
            this.active = this@RefractorScreen.handler.hasPayment() && this@RefractorScreen.chosenCurse != null && this@RefractorScreen.chosenBlessing != null
        }
    }

    inner class EffectButton(val blessing: Boolean, val effect: RegistryEntry<StatusEffect>, x: Int, y: Int, setter: (RegistryEntry<StatusEffect>) -> Unit, val level: Int): PropertyLinkedButton<Companion, RegistryEntry<StatusEffect>>(RefractorScreen, InGameHud.getEffectTexture(effect), x, y, {effect}, setter) {

        init {
            this.setTooltip(Tooltip.of(Text.translatable(effect.value().translationKey)))
        }

        override fun onPress() {
            super.onPress()
            this@RefractorScreen.tickButtons()
        }

        override fun tickWithInt(level: Int) {
            this.active = this.level <= level
            this.chosen = this.effect == if (this.blessing) this@RefractorScreen.chosenBlessing else this@RefractorScreen.chosenCurse
        }
    }
}