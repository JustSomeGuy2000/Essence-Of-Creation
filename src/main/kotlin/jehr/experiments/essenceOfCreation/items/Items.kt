package jehr.experiments.essenceOfCreation.items

import jehr.experiments.essenceOfCreation.EoCMain
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents
import net.minecraft.item.Item
import net.minecraft.registry.Registries
import net.minecraft.registry.Registry
import net.minecraft.registry.RegistryKey
import net.minecraft.registry.RegistryKeys
import net.minecraft.util.Identifier

object EoCItems {

    val essenceOfCreation = register("essence_of_creation", ::EssenceOfCreation, Item.Settings())

    fun init() {
        ItemGroupEvents.modifyEntriesEvent(EoCMain.EoCItemGroupKey).register{ it.add(this.essenceOfCreation) }
    }

    fun register(name: String, factory: (Item.Settings) -> Item, settings: Item.Settings): Item {
        val itemKey = RegistryKey.of(RegistryKeys.ITEM, Identifier.of(EoCMain.MOD_ID, name))
        val item = factory(settings.registryKey(itemKey))
        Registry.register(Registries.ITEM, itemKey, item)
        return item
    }
}