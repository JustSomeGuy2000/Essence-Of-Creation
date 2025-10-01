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

    class EssentialExtractorPropertyDelegate(val eebe: EssentialExtractorBlockEntity?): PropertyDelegate {
        override fun get(index: Int): Int {
            if (eebe == null) return 0
            return when (index) {
                0 -> eebe.currentFuel
                1 -> eebe.maxFuel
                2 -> eebe.progress
                else -> 0
            }
        }

        override fun set(index: Int, value: Int) {
            if (eebe == null) return
            when (index) {
                0 -> eebe.currentFuel = value
                1 -> eebe.maxFuel = value
                2 -> eebe.progress = value
                else -> {}
            }
        }

        override fun size() = 3

        fun setCurrentFuel(value: Int) = set(0, value)
        fun setMaxFuel(value: Int) = set(1, value)
        fun setProgress(value: Int) = set(2, value)

        fun getCurrentFuel() = get(0)
        fun getMaxFuel() = get(1)
        fun getProgress() = get(2)
    }

    private val propertyDelegate = EssentialExtractorPropertyDelegate(this)

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
    }

    override fun writeData(view: WriteView) {
        super.writeData(view)
        Inventories.writeData(view, inv)
        view.putInt("current_fuel", this.currentFuel)
        view.putInt("max_fuel", this.maxFuel)
        view.putInt("progress", this.progress)
    }
}