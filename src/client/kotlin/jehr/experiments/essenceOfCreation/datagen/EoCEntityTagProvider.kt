package jehr.experiments.essenceOfCreation.datagen

import jehr.experiments.essenceOfCreation.entities.EoCEntities
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider
import net.minecraft.registry.RegistryWrapper
import net.minecraft.registry.tag.EntityTypeTags
import java.util.concurrent.CompletableFuture

class EoCEntityTagProvider(output: FabricDataOutput, registryLookup: CompletableFuture<RegistryWrapper.WrapperLookup>): FabricTagProvider.EntityTypeTagProvider(output, registryLookup) {
	override fun getName() = "EssenceOfCreationEntityTypeTagProvider"

	override fun configure(p0: RegistryWrapper.WrapperLookup?) {
		valueLookupBuilder(EntityTypeTags.IMPACT_PROJECTILES)
			.add(EoCEntities.gunSwordBullet)
	}
}