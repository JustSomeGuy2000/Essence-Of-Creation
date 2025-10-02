package jehr.experiments.essenceOfCreation.blockEntities

import jehr.experiments.essenceOfCreation.screenHandlers.EssentialExtractorScreenHandler
import jehr.experiments.essenceOfCreation.utils.ImplementedInventory
import net.minecraft.block.BlockState
import net.minecraft.block.entity.BlockEntity
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.entity.player.PlayerInventory
import net.minecraft.inventory.Inventories
import net.minecraft.item.ItemStack
import net.minecraft.screen.NamedScreenHandlerFactory
import net.minecraft.screen.PropertyDelegate
import net.minecraft.storage.ReadView
import net.minecraft.storage.WriteView
import net.minecraft.text.Text
import net.minecraft.util.collection.DefaultedList
import net.minecraft.util.math.BlockPos

class EssentialExtractorBlockEntity(pos: BlockPos, state: BlockState): BlockEntity(EoCBlockEntities.essentialExtractorBlockEntity, pos, state), ImplementedInventory, NamedScreenHandlerFactory {

    var currentFuel = 0
    var maxFuel = 0
    var progress = 0
    var accumulator = 0.0

    private val inv: DefaultedList<ItemStack> = DefaultedList.ofSize(3, ItemStack.EMPTY)
    var source
        get() = inv[0]
        set(value) { inv[0] = value ; markDirty() }
    var fuel
        get() = inv[1]
        set(value) { inv[1] = value ; markDirty() }
    var output
        get() = inv[2]
        set(value) { inv[2] = value ; markDirty() }

    private val propertyDelegate = object : PropertyDelegate {
        override fun get(index: Int): Int {
            return when (index) {
                0 -> this@EssentialExtractorBlockEntity.currentFuel
                1 -> this@EssentialExtractorBlockEntity.maxFuel
                2 -> this@EssentialExtractorBlockEntity.progress
                else -> 0
            }
        }

        override fun set(index: Int, value: Int) {
            when (index) {
                0 -> this@EssentialExtractorBlockEntity.currentFuel = value
                1 -> this@EssentialExtractorBlockEntity.maxFuel = value
                2 -> this@EssentialExtractorBlockEntity.progress = value
                else -> {}
            }
        }

        override fun size() = 3
    }

    override fun getItems() = inv

    override fun createMenu(syncId: Int, playerInventory: PlayerInventory, player: PlayerEntity?) =
        EssentialExtractorScreenHandler(syncId, playerInventory, this, propertyDelegate)

    override fun getDisplayName(): Text = Text.translatable(cachedState.block.translationKey)

    override fun readData(view: ReadView) {
        super.readData(view)
        Inventories.readData(view, inv)
        this.currentFuel = view.getInt("current_fuel", 0)
        this.maxFuel = view.getInt("max_fuel", 0)
        this.progress = view.getInt("progress", 0)
        this.accumulator = view.getDouble("accumulator", 0.0)
    }

    override fun writeData(view: WriteView) {
        super.writeData(view)
        Inventories.writeData(view, inv)
        view.putInt("current_fuel", this.currentFuel)
        view.putInt("max_fuel", this.maxFuel)
        view.putInt("progress", this.progress)
        view.putDouble("accumulator", this.accumulator)
    }
}