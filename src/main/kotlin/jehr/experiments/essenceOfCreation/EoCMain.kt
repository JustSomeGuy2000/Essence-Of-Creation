package jehr.experiments.essenceOfCreation

import com.mojang.brigadier.arguments.IntegerArgumentType
import jehr.experiments.essenceOfCreation.blockEntities.EoCBlockEntities
import jehr.experiments.essenceOfCreation.blocks.EoCBlocks
import jehr.experiments.essenceOfCreation.blocks.ScaffoldSeed
import jehr.experiments.essenceOfCreation.blocks.ScaffoldStripper
import jehr.experiments.essenceOfCreation.criteria.EoCCriteria
import jehr.experiments.essenceOfCreation.items.EoCItems
import jehr.experiments.essenceOfCreation.statusEffects.BlessingOfRye
import jehr.experiments.essenceOfCreation.statusEffects.EoCStatusEffects
import net.fabricmc.api.ModInitializer
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup
import net.minecraft.item.ItemGroup
import net.minecraft.item.ItemStack
import net.minecraft.registry.Registries
import net.minecraft.registry.Registry
import net.minecraft.registry.RegistryKey
import net.minecraft.server.command.CommandManager
import net.minecraft.text.Text
import net.minecraft.util.Identifier
import org.slf4j.Logger
import org.slf4j.LoggerFactory

object EoCMain : ModInitializer {
	const val MOD_ID = "essence-of-creation"
    val logger: Logger = LoggerFactory.getLogger("essence-of-creation")

	val EoCItemGroupKey: RegistryKey<ItemGroup> = RegistryKey.of(Registries.ITEM_GROUP.getKey(), Identifier.of(MOD_ID, "item_group"))
	val EoCItemGroup: ItemGroup = FabricItemGroup.builder().icon{ ItemStack(EoCItems.essenceOfCreation) }.displayName(Text.translatable("itemGroup.${MOD_ID}.essence_of_creation")).build()

	override fun onInitialize() {
		// This code runs as soon as Minecraft is in a mod-load-ready state.
		// However, some things (like resources) may still be uninitialized.
		// Proceed with mild caution.
		logger.info("Hello Fabric world!")
		Registry.register(Registries.ITEM_GROUP, EoCItemGroupKey, EoCItemGroup)
		registerCommands()
		registerEvents()
		EoCItems.init()
		EoCStatusEffects.init()
		EoCBlocks.init()
		EoCBlockEntities.init()
		EoCCriteria.init()
	}

	fun registerCommands() {
		CommandRegistrationCallback.EVENT.register{ dispatcher, registryAccess, env ->
			dispatcher.register(CommandManager.literal("eoc_utils")
				.then(CommandManager.literal("scaffold_stripper_rate")
					.then(CommandManager.argument("rate", IntegerArgumentType.integer())
						.executes { context ->
					val rate = IntegerArgumentType.getInteger(context, "rate")
				if (rate >= 1) {
					ScaffoldStripper.moveRate = rate
					context.source.sendMessage(Text.literal("Scaffold Stripper move rate set to $rate t/b."))
					return@executes 1
				} else {
					context.source.sendMessage(Text.literal("Minumum value is 1."))
					return@executes -1
				}
			}))
				.then(CommandManager.literal("scaffold_growth_rate")
					.then(CommandManager.argument("rate", IntegerArgumentType.integer()).executes {
						context -> val rate = IntegerArgumentType.getInteger(context, "rate")
						if (rate >= 1) {
							ScaffoldSeed.Companion.Specs.accelDelay = rate
							context.source.sendMessage(Text.literal("Scaffold Seed growth rate set to $rate t/b."))
							return@executes 1
						} else {
							context.source.sendMessage(Text.literal("Minumum value is 1."))
							return@executes -1
						}
					}))
				.then(CommandManager.literal("rye_blessing_amp_time")
					.then(CommandManager.argument("ticks", IntegerArgumentType.integer()).executes {
						context -> val rate = IntegerArgumentType.getInteger(context, "ticks")
						if (rate >= 1) {
							BlessingOfRye.ampTime = rate
							context.source.sendMessage(Text.literal("Blessing of Rye amplification time set to $rate ticks."))
							return@executes 1
						} else {
							context.source.sendMessage(Text.literal("Minimum value is 1"))
							return@executes -1
						}
					})))
		}
	}

	fun registerEvents() {
	}
}