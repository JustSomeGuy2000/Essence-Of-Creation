package jehr.experiments.essenceOfCreation.utils

import com.chocohead.mm.api.ClassTinkerers

class EarlyRiser: Runnable {

    companion object {
        const val GUNSWORD_ENUM = "GUNSWORD"
    }

    override fun run() {
        val adder = ClassTinkerers.enumBuilder("net.minecraft.item.consume.UseAction", Int::class.java, String::class.java)
        adder.addEnum(GUNSWORD_ENUM, 11, "gunsword")
        adder.build()
        println("[sometime in the present] [main?/INFO] (EarlyRiser) Rising early!")
    }
}