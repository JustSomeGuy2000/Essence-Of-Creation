package jehr.experiments.essenceOfCreation.items

import jehr.experiments.essenceOfCreation.EoCMain
import net.minecraft.component.type.TooltipDisplayComponent
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.item.tooltip.TooltipType
import net.minecraft.text.Text
import java.util.function.Consumer

class EssenceOfCreation(settings: Settings): Item(settings) {

    @Deprecated("Deprecated in Java")
    override fun appendTooltip(stack: ItemStack?, context: TooltipContext?, displayComponent: TooltipDisplayComponent?,
         textConsumer: Consumer<Text?>?, type: TooltipType?) {
        textConsumer?.accept(Text.translatable("itemTooltip.${EoCMain.modId}.essence_of_creation"))
    }
}