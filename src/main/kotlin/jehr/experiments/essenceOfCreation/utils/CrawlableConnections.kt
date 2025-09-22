package jehr.experiments.essenceOfCreation.utils

import net.minecraft.block.BlockState
import net.minecraft.server.world.ServerWorld
import net.minecraft.state.property.BooleanProperty
import net.minecraft.state.property.EnumProperty
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Direction

interface CrawlableConnection {
    var supportedFrom: EnumProperty<Direction>
    var junction: BooleanProperty

    /**Return a list of `Direction`s this block connects to.*/
    fun getOutgoing(world: ServerWorld, state: BlockState, pos: BlockPos): List<Direction>
}