package jehr.experiments.essenceOfCreation.blockEntities

import jehr.experiments.essenceOfCreation.screenHandlers.EssentialInfuserScreenHandler
import jehr.experiments.essenceOfCreation.utils.ImplementedInventory
import net.minecraft.block.BlockState
import net.minecraft.block.entity.BlockEntity
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.entity.player.PlayerInventory
import net.minecraft.item.ItemStack
import net.minecraft.screen.NamedScreenHandlerFactory
import net.minecraft.screen.PropertyDelegate
import net.minecraft.screen.ScreenHandler
import net.minecraft.storage.ReadView
import net.minecraft.storage.WriteView
import net.minecraft.text.Text
import net.minecraft.util.collection.DefaultedList
import net.minecraft.util.math.BlockPos

class EssentialInfuserBlockEntity(pos: BlockPos, state: BlockState): BlockEntity(EoCBlockEntities.essentialInfuserBlockEntity, pos, state), ImplementedInventory, NamedScreenHandlerFactory {

    var progress = 0

    private val inv: DefaultedList<ItemStack> = DefaultedList.ofSize(3, ItemStack.EMPTY)
    var source
        get() = inv[0]
        set(value) {inv[0] = value ; markDirty()}
    var essence
        get() = inv[1]
        set(value) {inv[1] = value ; markDirty()}
    var output
        get() = inv[2]
        set(value) {inv[2] = value ; markDirty()}

    private val propertyDelegate = object : PropertyDelegate {
        override fun get(index: Int) = this@EssentialInfuserBlockEntity.progress

        override fun set(index: Int, value: Int) {
            this@EssentialInfuserBlockEntity.progress = value
        }

        override fun size() = 1
    }

    override fun getItems() = inv

    override fun getDisplayName(): Text = Text.translatable(this.cachedState.block.translationKey)

    override fun createMenu(syncId: Int, playerInventory: PlayerInventory, player: PlayerEntity?) =
        EssentialInfuserScreenHandler(syncId, playerInventory, this, propertyDelegate)

    override fun readData(view: ReadView) {
        super.readData(view)
        view.getInt("progress", 0)
    }

    override fun writeData(view: WriteView) {
        super.writeData(view)
        view.putInt("progress", this.progress)
    }
}