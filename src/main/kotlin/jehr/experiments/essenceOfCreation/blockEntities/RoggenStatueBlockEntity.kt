package jehr.experiments.essenceOfCreation.blockEntities

import net.minecraft.block.BlockState
import net.minecraft.block.entity.BlockEntity
import net.minecraft.storage.ReadView
import net.minecraft.storage.WriteView
import net.minecraft.util.math.BlockPos

class RoggenStatueBlockEntity(pos: BlockPos, state: BlockState): BlockEntity(EoCBlockEntities.roggenStatueBlockEntity, pos, state) {

    var ticksSinceLast = 0

    override fun writeData(view: WriteView) {
        super.writeData(view)
        view.putInt("timer", this.ticksSinceLast)
    }

    override fun readData(view: ReadView) {
        super.readData(view)
        this.ticksSinceLast = view.getInt("timer", 0)
    }
}