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
        if (this.stored != null) {
            view.put("stored", BlockState.CODEC, this.stored)
        } else {
            view.putBoolean("is_null", true)
        }
        super.writeData(view)
    }

    override fun readData(view: ReadView) {
        super.readData(view)
        if (!view.getBoolean("is_null", true)) {
          this.stored = view.read("stored", BlockState.CODEC).get()
        } else {
            this.stored = null
        }
    }

}