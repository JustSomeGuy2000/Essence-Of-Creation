package jehr.experiments.essenceOfCreation.screenHandlers

import jehr.experiments.essenceOfCreation.blocks.EssentialExtractor
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

class EssentialExtractorScreenHandler(syncID: Int, playerInv: PlayerInventory, inv: Inventory, delegate: PropertyDelegate): ScreenHandler(EoCScreenHandlers.essentialExtractorScreenHandler, syncID) {

    constructor(syncID: Int, playerInv: PlayerInventory): this(syncID, playerInv, SimpleInventory(9), ArrayPropertyDelegate(3)
    )

    val inventory = inv
    val delegate = delegate

    init {
        checkSize(inv, 3)
        inv.onOpen(playerInv.player)
        this.addSlot(Slot(inv, 0, 56, 17))
        this.addSlot(Slot(inv, 1, 56, 53))
        this.addSlot(Slot(inv, 2, 116, 35))
        this.addPlayerSlots(playerInv, 8, 84)
        this.addProperties(delegate)
    }

    override fun quickMove(player: PlayerEntity, slot: Int): ItemStack {
        var newStack: ItemStack
        val invSlot = this.slots.getOrNull(slot)
        if (invSlot != null && invSlot.hasStack()) {
            val originalStack = invSlot.stack
            newStack = originalStack.copy()
            if (slot <= 2)  { // moving from extractor slots, move to inventory
                if (!this.insertItem(originalStack, 3, 39, false)) {
                    return ItemStack.EMPTY
                }
            } else { // moving from player inventory slot
                if (originalStack.item in EssentialExtractor.sources) {
                    if (!this.insertItem(originalStack, 0, 1, false)) {
                        return ItemStack.EMPTY
                    }
                } else if (originalStack.item in EssentialExtractor.fuels) {
                    if (!this.insertItem(originalStack, 1, 2, false)) {
                        return ItemStack.EMPTY
                    }
                } else if (originalStack.isOf(EoCItems.essenceOfCreation)) {
                    if (!this.insertItem(originalStack, 2, 3, false)) {
                        return ItemStack.EMPTY
                    }
                } else {
                    return ItemStack.EMPTY
                }
            }
            invSlot.markDirty()
            return newStack
        }
        return ItemStack.EMPTY
    }

    override fun canUse(player: PlayerEntity?) = this.inventory.canPlayerUse(player)
}