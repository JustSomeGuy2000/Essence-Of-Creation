package jehr.experiments.essenceOfCreation.datagen

import jehr.experiments.essenceOfCreation.EoCMain
import jehr.experiments.essenceOfCreation.blocks.BalefulPoppy
import jehr.experiments.essenceOfCreation.blocks.EoCBlocks
import jehr.experiments.essenceOfCreation.blocks.EssentialExtractor
import jehr.experiments.essenceOfCreation.blocks.EssentialInfuser
import jehr.experiments.essenceOfCreation.blocks.Refractor
import jehr.experiments.essenceOfCreation.blocks.RoggenStatue
import jehr.experiments.essenceOfCreation.blocks.ScaffoldSeed
import jehr.experiments.essenceOfCreation.blocks.ScaffoldStripper
import jehr.experiments.essenceOfCreation.blocks.ScaffoldTrunk
import jehr.experiments.essenceOfCreation.blocks.SpatialDisplacer
import jehr.experiments.essenceOfCreation.enchantmentEffects.FulminationEffect
import jehr.experiments.essenceOfCreation.items.BalefulSnowballItem
import jehr.experiments.essenceOfCreation.items.EoCItems
import jehr.experiments.essenceOfCreation.items.EoCPotions
import jehr.experiments.essenceOfCreation.items.EssenceOfCreation
import jehr.experiments.essenceOfCreation.items.GunSword
import jehr.experiments.essenceOfCreation.items.HandheldInfuser
import jehr.experiments.essenceOfCreation.items.SuperBoneMeal
import jehr.experiments.essenceOfCreation.statusEffects.BlessingOfRye
import jehr.experiments.essenceOfCreation.tags.EoCTags
import jehr.experiments.essenceOfCreation.utils.RoggenLore
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput
import net.fabricmc.fabric.api.datagen.v1.provider.FabricLanguageProvider
import net.minecraft.registry.RegistryWrapper
import java.util.concurrent.CompletableFuture

class EoCLangProviderEnUs(dataOutput: FabricDataOutput, registryLookup: CompletableFuture<RegistryWrapper.WrapperLookup>): FabricLanguageProvider(dataOutput, registryLookup) {
	val id = EoCMain.MOD_ID

	override fun getName() = "EssenceOfCreationLanguageProvider(en-us)"

	override fun generateTranslations(wrapperLookup: RegistryWrapper.WrapperLookup, builder: TranslationBuilder) {
		builder.add("item.$id.${EssenceOfCreation.Companion.ID}", "Essence of Creation")
		builder.add("itemGroup.$id.essence_of_creation", "Essence Of Creation")
		builder.add("itemTooltip.$id.essence_of_creation", "Have you ever felt that the world wasn't enough?")
		builder.add("block.$id.${ScaffoldSeed.Companion.ID}", "Scaffold Seed")
		builder.add("item.$id.${ScaffoldSeed.Companion.ID}", "Scaffold Seed")
		builder.add("block.$id.${ScaffoldTrunk.Companion.ID}", "Scaffold Trunk")
		builder.add("item.$id.${ScaffoldTrunk.Companion.ID}", "Scaffold Trunk")
		builder.add("block.$id.${ScaffoldStripper.Companion.ID}", "Scaffold Stripper")
		builder.add("item.$id.${ScaffoldStripper.Companion.ID}", "Scaffold Stripper")
		builder.add("block.$id.${SpatialDisplacer.Companion.ID}", "Spatial Displacer")
		builder.add("item.$id.${SpatialDisplacer.Companion.ID}", "Spatial Displacer")
		builder.add("block.$id.${RoggenStatue.Companion.ID}", "Statue of the Rye God")
		builder.add("item.$id.${RoggenStatue.Companion.ID}", "Statue of the Rye God")
		builder.add("effect.$id.${BlessingOfRye.Companion.ID}", "Blessing of Rye")
		builder.add("item.$id.rye", "Rye")
		builder.add("block.$id.${EoCBlocks.RYE_BALE_ID}", "Rye Bale")
		builder.add("item.$id.${EoCBlocks.RYE_BALE_ID}", "Rye Bale")
		builder.add("lore.roggen.book_title", RoggenLore.Companion.TITLE)
		for (lore in RoggenLore.entries) {
			builder.add("lore.roggen.${lore.name}.header", lore.header)
			for ((num, page) in lore.body.withIndex()) {
				builder.add("lore.roggen.${lore.name}.page$num", page)
			}
		}
		builder.add("item.$id.${EoCItems.TOR_ID}", "Totem of Unrying")
		builder.add("block.$id.${EssentialExtractor.Companion.ID}", "Essential Extractor")
		builder.add("item.$id.${EssentialExtractor.Companion.ID}", "Essential Extractor")
		builder.add("item.$id.${EssentialInfuser.Companion.ID}", "Essential Infuser")
		builder.add("block.$id.${EssentialInfuser.Companion.ID}", "Essential Infuser")
		builder.add("item.$id.${HandheldInfuser.Companion.ID}", "Handheld Infuser")
		builder.add("itemTooltip.$id.${HandheldInfuser.Companion.ID}.title", "Usage")
		builder.add("itemTooltip.$id.${HandheldInfuser.Companion.ID}.content", "Left-click on entity to attempt.")
		builder.add("item.$id.${EoCItems.GOD_APPLE_ID}", "God Apple")
		builder.add("item.$id.${SuperBoneMeal.Companion.ID}", "Super Bone Meal")
		builder.add("item.$id.${EoCItems.CANE_ID}", "Cane")
		builder.add("death.attack.gun_sword", "%s was fatally shot.")
		builder.add("death.attack.gun_sword.item", "%s was fatally shot by %s using %s.")
		builder.add("death.attack.gun_sword.player", "%s was fatally shot while trying to escape %s.")
		builder.add("item.$id.${EoCItems.IRON_GUN_SWORD_ID}", "Iron Gun-Sword")
		builder.add("item.$id.${EoCItems.GOLD_GUN_SWORD_ID}", "Gold Gun-Sword")
		builder.add("item.$id.${EoCItems.DIAMOND_GUN_SWORD_ID}", "Diamond Gun-Sword")
		builder.add("item.$id.${EoCItems.NETHERITE_GUN_SWORD_ID}", "Netherite Gun-Sword")
		builder.add("item.$id.${GunSword.Amethyst.ID}", "Amethyst Gun-Sword")
		builder.add("item.$id.${GunSword.BreezeRod.ID}", "Breezy Gun-Sword")
		builder.add("item.$id.${GunSword.EchoShard.ID}", "Sonic Gun-Sword")
		builder.add("item.$id.${GunSword.Emerald.ID}", "Emerald Gun-Sword")
		builder.add("item.$id.${EoCItems.SUPER_GUN_SWORD_ID}", "Super Gun-Sword")
		builder.add("tag.item.$id.${EoCTags.upgradeableGunSword.id}", "upgradeable_gunsword")
		builder.add("tag.item.$id.${EoCTags.gunSwordBulletBreakable.id}", "breakble_by_gunsword_bullet")
		builder.add("item.$id.${BalefulPoppy.Companion.ID}", "Baleful Poppy")
		builder.add("item.$id.${BalefulSnowballItem.Companion.ID}", "Baleful Snowball")
		builder.add("item.$id.${EoCItems.INTERITOR_HEART_ID}", "Interitor Heart")
		builder.add("item.$id.${Refractor.Companion.ID}", "Refractor")
		builder.add("block.$id.${Refractor.Companion.ID}", "Refractor")
		builder.add("container.refractor", "Refractor")
		builder.add("block.$id.${Refractor.Companion.ID}.blessing", "For Thyself")
		builder.add("block.$id.${Refractor.Companion.ID}.curse", "For Thy Foes")
		builder.add("enchantment.$id.${FulminationEffect.ID}", "Fulmination")
		builder.add("item.$id.${EoCPotions.PERPLEXING_BREW_ID}", "Perplexing Brew")
	}
}