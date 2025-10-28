package jehr.experiments.essenceOfCreation.screenHandlers

import jehr.experiments.essenceOfCreation.blocks.Refractor
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.entity.player.PlayerInventory
import net.minecraft.inventory.Inventory
import net.minecraft.inventory.SimpleInventory
import net.minecraft.item.ItemStack
import net.minecraft.screen.ArrayPropertyDelegate
import net.minecraft.screen.PropertyDelegate
import net.minecraft.screen.ScreenHandler

class RefractorScreenHandler(syncId: Int, playerInv: PlayerInventory, inv: Inventory, delegate: PropertyDelegate): ScreenHandler(EoCScreenHandlers.refractorScreenHandler, syncId) {

    constructor(syncId: Int, playerInv: PlayerInventory): this(syncId, playerInv, SimpleInventory(3),
        ArrayPropertyDelegate(1)
    )

    companion object {
        const val ID = "${Refractor.ID}_screen_handler"
    }

    override fun canUse(player: PlayerEntity?): Boolean {
        TODO("Not yet implemented")
    }

    override fun quickMove(player: PlayerEntity?, slot: Int): ItemStack? {
        TODO("Not yet implemented")
    }
}