package jehr.experiments.essenceOfCreation.blockEntities

import net.minecraft.block.BlockState
import net.minecraft.block.entity.BlockEntity
import net.minecraft.storage.ReadView
import net.minecraft.storage.WriteView
import net.minecraft.util.math.BlockPos

class ScaffoldStripperBlockEntity(pos: BlockPos, state: BlockState): BlockEntity(EoCBlockEntities.scaffoldStripperBlockEntity, pos, state) {

    private var stored: BlockState? = null

    fun getStored() = stored

    fun changeStored(new: BlockState?) {
        this.stored = new
        markDirty()
    }

    override fun writeData(view: WriteView) {
        super.writeData(view)
        // TODO
    }

    override fun readData(view: ReadView?) {
        super.readData(view)
        // TODO
    }

}