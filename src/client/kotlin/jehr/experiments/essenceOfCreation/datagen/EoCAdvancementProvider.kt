package jehr.experiments.essenceOfCreation.datagen

import jehr.experiments.essenceOfCreation.EoCMain
import jehr.experiments.essenceOfCreation.blocks.EoCBlocks
import jehr.experiments.essenceOfCreation.blocks.EssentialInfuser
import jehr.experiments.essenceOfCreation.criteria.AnthropogenicCriterion
import jehr.experiments.essenceOfCreation.criteria.EoCCriteria
import jehr.experiments.essenceOfCreation.criteria.RyeNotCriterion
import jehr.experiments.essenceOfCreation.criteria.RyeTotemCriterion
import jehr.experiments.essenceOfCreation.items.EoCItems
import jehr.experiments.essenceOfCreation.tags.EoCTags
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput
import net.fabricmc.fabric.api.datagen.v1.provider.FabricAdvancementProvider
import net.minecraft.advancement.Advancement
import net.minecraft.advancement.AdvancementEntry
import net.minecraft.advancement.AdvancementFrame
import net.minecraft.advancement.AdvancementRequirements
import net.minecraft.advancement.criterion.InventoryChangedCriterion
import net.minecraft.item.Item
import net.minecraft.item.Items
import net.minecraft.predicate.NumberRange
import net.minecraft.predicate.component.ComponentPredicateTypes
import net.minecraft.predicate.component.ComponentsPredicate
import net.minecraft.predicate.item.EnchantmentPredicate
import net.minecraft.predicate.item.EnchantmentsPredicate
import net.minecraft.predicate.item.ItemPredicate
import net.minecraft.registry.RegistryKeys
import net.minecraft.registry.RegistryWrapper
import net.minecraft.text.Text
import net.minecraft.util.Identifier
import java.util.Optional
import java.util.UUID
import java.util.concurrent.CompletableFuture
import java.util.function.Consumer

class EoCAdvancementProvider(dataOutput: FabricDataOutput, registryLookup: CompletableFuture<RegistryWrapper.WrapperLookup>): FabricAdvancementProvider(dataOutput, registryLookup) {

	override fun getName(): String?  = "EssenceOfCreationAdvancementProvider"

	override fun generateAdvancement(wrapperLookup: RegistryWrapper.WrapperLookup, exporter: Consumer<AdvancementEntry?>?) {
		val essenceOfCreation = Advancement.Builder.create()
			.display(EoCItems.essenceOfCreation, Text.literal("Essence Of Creation"), Text.literal("Expand your horizons."), Identifier.ofVanilla("gui/advancements/backgrounds/adventure"), AdvancementFrame.TASK, true, true, false)
			.criterion("get_eoc",
			InventoryChangedCriterion.Conditions.items(EoCItems.essenceOfCreation))
			.build(exporter, Identifier.of(EoCMain.MOD_ID, "got_eoc").toString())

		val ryesAndShine = Advancement.Builder.create().parent(essenceOfCreation)
			.display(EoCItems.rye, Text.literal("Ryes And Shine"), Text.literal("Roggen smiles upon you."), null, AdvancementFrame.TASK, true, true, false)
			.criterion("get_rye", InventoryChangedCriterion.Conditions.items(EoCItems.rye))
			.build(exporter, Identifier.of(EoCMain.MOD_ID, "get_rye").toString())

		val ryeNot = Advancement.Builder.create().parent(ryesAndShine)
			.display(EoCBlocks.ryeBale, Text.literal("Rye Not?"), Text.literal("Rye not, indeed?"), null, AdvancementFrame.GOAL, true, true, false)
			.criterion("inv_of_rye", EoCCriteria.ryeNotCriterion.create(RyeNotCriterion.Conditions(Optional.empty())))
			.build(exporter, Identifier.of(EoCMain.MOD_ID, "inventory_of_rye").toString())

		val aryse = Advancement.Builder.create().parent(ryesAndShine)
			.display(EoCItems.totemOfUnrying, Text.literal("Aryse"), Text.literal("A force pulls you back from the brink..."), null, AdvancementFrame.GOAL, true, true, false)
			.criterion("use_totem_of_unrying", EoCCriteria.ryeTotemCriterion.create(
                RyeTotemCriterion.Conditions(
                    Optional.empty())))
			.build(exporter, Identifier.of(EoCMain.MOD_ID, "aryse").toString())

		val enchantsReg = wrapperLookup.getOrThrow(RegistryKeys.ENCHANTMENT)
		val anthropogenic = Advancement.Builder.create().parent(essenceOfCreation)
			.display(EoCBlocks.essentialInfuser, Text.literal("Anthropogenic"), Text.literal("Obtain something made with Essence of Creation."), null, AdvancementFrame.TASK, true, true, false)
			.oneFromItemListCriterion(EssentialInfuser.Companion.outputs.values)
			/*TODO: .criterion("has_upgraded_enchantment",
				InventoryChangedCriterion.Conditions.items(ItemPredicate.Builder().items(
					wrapperLookup.getOrThrow(RegistryKeys.ITEM), Items.ENCHANTED_BOOK)
					.components(
						ComponentsPredicate.Builder.create().partial(
							ComponentPredicateTypes.ENCHANTMENTS,
							EnchantmentsPredicate.enchantments(
								EssentialInfuser.enchantUpgrades.values.map{
									EnchantmentPredicate(
										enchantsReg.getOrThrow(it),
										NumberRange.IntRange.atLeast(0)) })).build()).build()))
			.criteriaMerger(AdvancementRequirements.CriterionMerger.OR)*/
			.criterion("used_handheld_infuser", EoCCriteria.anthropogenicCriterion.create(
                AnthropogenicCriterion.Conditions(
                    Optional.empty())))
			.criteriaMerger(AdvancementRequirements.CriterionMerger.OR)
			.build(exporter, Identifier.of(EoCMain.MOD_ID, "anthropogenic").toString())

		val ambrosia = Advancement.Builder.create().parent(anthropogenic)
			.display(EoCItems.godApple, Text.literal("Ambrosia"), Text.literal("Food of the gods."), null, AdvancementFrame.GOAL, true, true, false)
			.criterion("has_god_apple", InventoryChangedCriterion.Conditions.items(EoCItems.godApple))
			.build(exporter, Identifier.of(EoCMain.MOD_ID, "ambrosia").toString())

		val intentOfCreation = Advancement.Builder.create().parent(anthropogenic)
			.display(EoCBlocks.balefulPoppy.asItem(), Text.literal("Intent of Creation"), Text.literal("The universe knows when you create just to destroy."), null, AdvancementFrame.GOAL, true, true, false)
			.criterion("triggered_intent_of_creation", InventoryChangedCriterion.Conditions.items(
                ItemPredicate.Builder.create().tag(wrapperLookup.getOrThrow(
                    RegistryKeys.ITEM), EoCTags.triggersIntentOfCreation)))
			.build(exporter, Identifier.of(EoCMain.MOD_ID, "intent_of_creation").toString())
	}

	/**Yet another cursed solution since datagen is so POORLY DOCUMENTED. It works, though, so I won't complain.*/
	private fun Advancement.Builder.oneFromItemListCriterion(items: Collection<Item>): Advancement.Builder {
		for (item in items) {
			this.criterion(UUID.randomUUID().toString(), InventoryChangedCriterion.Conditions.items(item))
		}
		return this.criteriaMerger(AdvancementRequirements.CriterionMerger.OR)
	}
}