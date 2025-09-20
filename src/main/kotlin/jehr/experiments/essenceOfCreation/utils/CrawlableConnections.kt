package jehr.experiments.essenceOfCreation.utils

import net.minecraft.block.BlockState
import net.minecraft.state.property.EnumProperty
import net.minecraft.util.StringIdentifiable
import net.minecraft.util.math.Direction

interface CrawlableConnection {
    var up: EnumProperty<ConnectionStatus>
    var down: EnumProperty<ConnectionStatus>
    var north: EnumProperty<ConnectionStatus>
    var south: EnumProperty<ConnectionStatus>
    var east: EnumProperty<ConnectionStatus>
    var west: EnumProperty<ConnectionStatus>

    fun getConnections(state: BlockState): Map<Direction, ConnectionStatus>

    /**Return a list of `Direction`s from which blocks are connected to this one.*/
    fun getIncoming(state: BlockState): List<Direction>

    /**Return a list of `Direction`s this block connects to.*/
    fun getOutgoing(state: BlockState): List<Direction>
}

enum class ConnectionStatus: StringIdentifiable {
    NONE {
        override fun asString(): String = "no_connection"
    }, INCOMING {
        override fun asString(): String = "incoming"
    }, OUTGOING {
        override fun asString(): String = "outgoing"
    }
}

class ConnAlterer(private val state: BlockState, private val type: CrawlableConnection) {

    fun getState() = this.state

    fun modifyConnections(argUp: ConnectionStatus? = null, argDown: ConnectionStatus? = null, argNorth: ConnectionStatus? = null, argSouth: ConnectionStatus? = null, argEast: ConnectionStatus? = null, argWest: ConnectionStatus? = null): ConnAlterer =
        ConnAlterer(state.with(type.up, argUp ?: state.get(type.up)).with(type.down, argDown ?: state.get(type.down)).with(type.north, argNorth ?: state.get(type.north)).with(type.south, argSouth ?: state.get(type.south)).with(type.east, argEast ?: state.get(type.east)).with(type.west, argWest ?: state.get(type.west)), this.type)

    fun setConnections(argUp: ConnectionStatus = ConnectionStatus.NONE, argDown: ConnectionStatus= ConnectionStatus.NONE, argNorth: ConnectionStatus = ConnectionStatus.NONE, argSouth: ConnectionStatus = ConnectionStatus.NONE, argEast: ConnectionStatus = ConnectionStatus.NONE, argWest: ConnectionStatus = ConnectionStatus.NONE): ConnAlterer =
        modifyConnections(argUp, argDown, argNorth, argSouth, argEast, argWest)

    fun setIncoming(incoming: List<Direction>): ConnAlterer {
        val i = ConnectionStatus.INCOMING
        val n = ConnectionStatus.NONE
        return setConnections(if (Direction.UP in incoming) i else n, if (Direction.DOWN in incoming) i else n, if (Direction.NORTH in incoming) i else n, if (Direction.SOUTH in incoming) i else n, if (Direction.EAST in incoming) i else n, if (Direction.WEST in incoming) i else n)
    }

    fun setOutgoing(outgoing: List<Direction>): ConnAlterer {
        val i = ConnectionStatus.OUTGOING
        val n = ConnectionStatus.NONE
        return setConnections(if (Direction.UP in outgoing) i else n, if (Direction.DOWN in outgoing) i else n, if (Direction.NORTH in outgoing) i else n, if (Direction.SOUTH in outgoing) i else n, if (Direction.EAST in outgoing) i else n, if (Direction.WEST in outgoing) i else n)
    }
}