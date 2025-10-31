package jehr.experiments.essenceOfCreation.clientUtils

import net.minecraft.client.gl.RenderPipelines
import net.minecraft.client.gui.DrawContext
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder
import net.minecraft.client.gui.widget.PressableWidget
import net.minecraft.screen.ScreenTexts
import net.minecraft.text.Text
import net.minecraft.util.Identifier

interface HasButtonTextures {
    /**WHen the button cannot be interacted with*/
    var buttonDisabledTexture: Identifier
    /**When the mouse is hovering over the button*/
    var buttonSelectedTexture: Identifier
    /**When the mouse is clicking the button*/
    var buttonHighlightedTexture: Identifier
    /**When the button is active but not interacting with the mouse*/
    var buttonNormalTexture: Identifier
}

abstract class BaseButton<T: HasButtonTextures>(val source: T, x: Int, y: Int, width: Int, height: Int, message: Text): PressableWidget(x, y, width, height, message) {

    constructor(source: T, x: Int, y: Int): this(source, x, y, DEFAULT_WIDTH, DEFAULT_HEIGHT, ScreenTexts.EMPTY)

    constructor(source: T, x: Int, y: Int, message: Text): this(source, x, y, DEFAULT_WIDTH, DEFAULT_HEIGHT, message)

    var chosen = false

    companion object {
        const val DEFAULT_WIDTH = 16
        const val DEFAULT_HEIGHT = 16
    }

    override fun renderWidget(context: DrawContext, mouseX: Int, mouseY: Int, deltaTicks: Float) {
        val id = if (!this.active) {
            this.source.buttonDisabledTexture
        } else if (this.chosen) {
            this.source.buttonSelectedTexture
        } else if (this.isSelected) {
            this.source.buttonHighlightedTexture
        } else {
            this.source.buttonNormalTexture
        }

        context.drawGuiTexture(RenderPipelines.GUI_TEXTURED, id, this.x, this.y, this.width, this.height)
        this.renderExtra(context, mouseX, mouseY, deltaTicks)
    }

    abstract fun renderExtra(context: DrawContext, mX: Int, mY: Int, dT: Float)
    override fun appendClickableNarrations(builder: NarrationMessageBuilder?) = this.appendDefaultNarrations(builder)
    override fun onPress() {chosen = true}
    open fun tickWithInt(level: Int) {}

}

abstract class IconButton<T: HasButtonTextures>(source: T, val texture: Identifier, x: Int, y: Int, message: Text, val iconOffset: Pair<Int, Int> = defaultIconOffset, val iconDims: Pair<Int, Int> = defaultIconDims): BaseButton<T>(source, x, y, message) {

    companion object {
        val defaultIconOffset = Pair(2, 2)
        val defaultIconDims = Pair(12, 12)
    }

    override fun renderExtra(context: DrawContext, mX: Int, mY: Int, dT: Float) {
        context.drawGuiTexture(RenderPipelines.GUI_TEXTURED, texture, this.x + iconOffset.first, this.y + iconOffset.second, iconDims.first, iconDims.second)
    }
}

/**Might be kinda inefficient and kinda useless*/
abstract class PropertyLinkedButton<T: HasButtonTextures, P>(source: T, texture: Identifier, x: Int, y: Int, val value: () -> P, val setter: (P) -> Unit, offset: Pair<Int, Int> = defaultIconOffset, dims: Pair<Int, Int> = defaultIconDims, message: Text = ScreenTexts.EMPTY): IconButton<T>(source, texture, x, y, message, offset, dims) {

    override fun onPress() {
        super.onPress()
        setter(value())
    }
}