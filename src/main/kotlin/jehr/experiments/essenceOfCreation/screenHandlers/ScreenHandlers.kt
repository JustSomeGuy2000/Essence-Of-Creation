package jehr.experiments.essenceOfCreation.screenHandlers

import jehr.experiments.essenceOfCreation.EoCMain
import jehr.experiments.essenceOfCreation.blocks.EssentialExtractor
import net.minecraft.registry.Registries
import net.minecraft.registry.Registry
import net.minecraft.resource.featuretoggle.FeatureSet
import net.minecraft.screen.ScreenHandler
import net.minecraft.screen.ScreenHandlerType
import net.minecraft.util.Identifier


object EoCScreenHandlers {

    val essentialExtractorScreenHandler = register("${EssentialExtractor.ID}_screen_handler", ::EssentialExtractorScreenHandler)

    fun init() {}

    fun <T: ScreenHandler> register(name: String, factory: ScreenHandlerType.Factory<T>, featureSet: FeatureSet = FeatureSet.empty()): ScreenHandlerType<T> = Registry.register(Registries.SCREEN_HANDLER, Identifier.of(EoCMain.MOD_ID, name), ScreenHandlerType(factory, featureSet))
}