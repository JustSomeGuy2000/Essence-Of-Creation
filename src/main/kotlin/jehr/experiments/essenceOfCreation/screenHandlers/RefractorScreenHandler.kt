package jehr.experiments.essenceOfCreation.screenHandlers

import jehr.experiments.essenceOfCreation.blocks.EoCBlocks
import jehr.experiments.essenceOfCreation.blocks.Refractor
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.entity.player.PlayerInventory
import net.minecraft.inventory.Inventory
import net.minecraft.inventory.SimpleInventory
import net.minecraft.item.ItemStack
import net.minecraft.registry.tag.ItemTags
import net.minecraft.screen.ArrayPropertyDelegate
import net.minecraft.screen.PropertyDelegate
import net.minecraft.screen.ScreenHandler
import net.minecraft.screen.ScreenHandlerContext
import net.minecraft.screen.slot.Slot

class RefractorScreenHandler(syncId: Int, playerInv: PlayerInventory, val delegate: PropertyDelegate, val context: ScreenHandlerContext): ScreenHandler(EoCScreenHandlers.refractorScreenHandler, syncId) {

    constructor(syncId: Int, playerInv: PlayerInventory): this(syncId, playerInv, ArrayPropertyDelegate(EXPECTED_DELEGATE_SIZE), ScreenHandlerContext.EMPTY)

    val paymentHolder = object: SimpleInventory(1) {
        override fun isValid(slot: Int, stack: ItemStack) = stack.isIn(ItemTags.BEACON_PAYMENT_ITEMS)

        override fun getMaxCountPerStack() = 1
    }
    val paymentSlot = PaymentSlot(this.paymentHolder, 0, paymentSlotCoords)

    init {
        this.addProperties(delegate)
        this.addSlot(this.paymentSlot)
        this.addPlayerSlots(playerInv, playerInvCoords.first, playerInvCoords.second)
    }

    companion object {
        const val ID = "${Refractor.ID}_screen_handler"
        const val EXPECTED_DELEGATE_SIZE = 3

        val paymentSlotCoords = Pair(136, 110)
        val playerInvCoords = Pair(36, 137)
    }

    override fun canUse(player: PlayerEntity?) = canUse(this.context, player, EoCBlocks.refractor)

    override fun quickMove(player: PlayerEntity?, slot: Int): ItemStack {
        val invSlot = this.slots.getOrNull(slot)
        if (invSlot != null && invSlot.hasStack()) {
            val stack = invSlot.stack
            val copied = stack.copy()
            if (slot == 0) {
                if (!this.insertItem(stack, 3, 39, false)) return ItemStack.EMPTY
            } else if (stack.isIn(ItemTags.BEACON_PAYMENT_ITEMS)) {
                if (!this.insertItem(stack, 0, 1, false)) return ItemStack.EMPTY
            }
            invSlot.markDirty()
            return copied
        }
        return ItemStack.EMPTY
    }

    class PaymentSlot(inv: Inventory, index: Int, coords: Pair<Int, Int>): Slot(inv, index, coords.first, coords.second) {

        override fun canInsert(stack: ItemStack) = stack.isIn(ItemTags.BEACON_PAYMENT_ITEMS)

        override fun getMaxItemCount() = 1
    }
}