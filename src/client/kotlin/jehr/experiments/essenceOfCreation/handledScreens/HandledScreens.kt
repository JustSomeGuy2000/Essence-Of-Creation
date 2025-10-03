package jehr.experiments.essenceOfCreation.handledScreens

import jehr.experiments.essenceOfCreation.screenHandlers.EoCScreenHandlers
import net.minecraft.client.gui.screen.ingame.HandledScreens

object EoCHandledScreens {

    fun init() {
        HandledScreens.register(EoCScreenHandlers.essentialExtractorScreenHandler, ::EssentialExtractorScreen)
        HandledScreens.register(EoCScreenHandlers.essentialInfuserScreenHandler, ::EssentialInfuserScreen)
    }
}