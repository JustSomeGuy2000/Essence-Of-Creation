package jehr.experiments.essenceOfCreation.screenHandlers

import jehr.experiments.essenceOfCreation.blocks.EssentialInfuser
import jehr.experiments.essenceOfCreation.items.EoCItems
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.entity.player.PlayerInventory
import net.minecraft.inventory.Inventory
import net.minecraft.inventory.SimpleInventory
import net.minecraft.item.ItemStack
import net.minecraft.screen.ArrayPropertyDelegate
import net.minecraft.screen.PropertyDelegate
import net.minecraft.screen.ScreenHandler
import net.minecraft.screen.slot.Slot

class EssentialInfuserScreenHandler(syncId: Int, playerInv: PlayerInventory, val inv: Inventory, val delegate: PropertyDelegate): ScreenHandler(EoCScreenHandlers.essentialExtractorScreenHandler, syncId) {

    constructor(syncId: Int, playerInv: PlayerInventory): this(syncId, playerInv, SimpleInventory(3),
        ArrayPropertyDelegate(1)
    )

    init {
        checkSize(inv, 3)
        inv.onOpen(playerInv.player)
        this.addSlot(Slot(inv, 0, sourceSlotCoords.first, sourceSlotCoords.second))
        this.addSlot(Slot(inv, 1, essenceSlotCoords.first, essenceSlotCoords.second))
        this.addSlot(Slot(inv, 2, outputSlotCoords.first, outputSlotCoords.second))
        this.addPlayerSlots(playerInv, 8, 84)
        this.addProperties(delegate)
    }

    companion object {
        val sourceSlotCoords = Pair(45, 21)
        val essenceSlotCoords = Pair(45, 56)
        val outputSlotCoords = Pair(111, 39)
    }

    override fun quickMove(player: PlayerEntity?, slot: Int): ItemStack {
        val invSlot = this.slots.getOrNull(slot)
        if (invSlot != null && invSlot.hasStack()) {
            val stack = invSlot.stack
            if (slot <= 2) {
                if (!this.insertItem(stack, 3, 39, false)) return ItemStack.EMPTY
            } else {
                if (stack.item in EssentialInfuser.outputs) {
                    if (!this.insertItem(stack, 0, 1, false)) return ItemStack.EMPTY
                } else if (stack.isOf(EoCItems.essenceOfCreation)) {
                    if (!this.insertItem(stack, 1, 2, false)) return ItemStack.EMPTY
                } else {
                    if (!this.insertItem(stack, 2, 3, false)) return ItemStack.EMPTY
                }
            }
            invSlot.markDirty()
            return stack.copy()
        }
        return ItemStack.EMPTY
    }

    override fun canUse(player: PlayerEntity?) = this.inv.canPlayerUse(player)
}