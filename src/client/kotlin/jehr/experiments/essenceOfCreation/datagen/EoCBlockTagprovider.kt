package jehr.experiments.essenceOfCreation.datagen

import jehr.experiments.essenceOfCreation.tags.EoCTags
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider
import net.fabricmc.fabric.api.tag.convention.v2.ConventionalBlockTags
import net.minecraft.block.Blocks
import net.minecraft.registry.RegistryWrapper
import net.minecraft.registry.tag.BlockTags
import java.util.concurrent.CompletableFuture

class EoCBlockTagprovider(output: FabricDataOutput, registryLookup: CompletableFuture<RegistryWrapper.WrapperLookup>): FabricTagProvider.BlockTagProvider(output, registryLookup) {

	override fun getName()= "EssenceOfCreationBlockTagProvider"

	override fun configure(p0: RegistryWrapper.WrapperLookup?) {
		valueLookupBuilder(EoCTags.gunSwordBulletBreakable)
			.add(Blocks.GLOWSTONE)
			.add(Blocks.SEA_LANTERN)
			.forceAddTag(BlockTags.IMPERMEABLE)
			.forceAddTag(ConventionalBlockTags.GLASS_BLOCKS)
			.forceAddTag(ConventionalBlockTags.GLASS_PANES)
	}
}