package jehr.experiments.essenceOfCreation.utils

import net.minecraft.text.RawFilteredPair
import net.minecraft.text.Text

/** Behold the millennia-long tale of a god and his people, told through the disjointed accounts of authors throughout history.*/
enum class RoggenLore(val header: String, val body: List<String>) {
    Ryevelations1("Book of Roggen \n\nRyevelations 1 \n\nHistory", listOf("During the last days of the Confounding, I was exiled to this desolate island for the continued profession of my faith, as is proper. The people, whose eyes have been blinded and hearts been filled with malice, crowded around my door demanding I renounce my faith. At"
        ," my refusal, I was dragged out of my house and beaten, which I survived by the grace of my lord. Finally, I was put on a ship and sent to here.\n Five weeks into my exile, while I was occupied in my daily period of prayer, a sudden taste and scent overwhelmed me, nothing like there",
        " ever was on that island. It was the scent and taste of rye, which I had not for many months experienced, and stronger, more divine than any earthly rye or rye concentrate could hope to achieve."
        , "I heard behind me a voice as the shifting of a field's worth of ripe stalks of rye, saying, \"Record that which you are shown, to show the peoples of this world that which will be.\" I turned around and saw the island's barren sand had been replaced with fallow soil, and the"
        ,"ocean filled up with the same. Although the soil had been tilled and pits made for seeds, only a few stalks of rye were visible. Standing in front of me was one wearing the vestments of the priests. He was a pitiful sight, old and hunched, eyes and cheeks sunken, bones clearly visible in his"
        ," hands. Yet, he had carried himself as if a prince or king.\nI fell before his feet as if dead. \"Arise,\" he told me. \"Do not be afraid.\""));

    companion object {
        const val TITLE = "Ancient Manuscript"

        fun toBookText(from: RoggenLore): List<RawFilteredPair<Text>> {
            val texts = mutableListOf<RawFilteredPair<Text>>()
            texts.add(RawFilteredPair.of(Text.translatable("lore.roggen.${from.name}.header")))
            for (num in 0..from.body.lastIndex) {
                texts.add(RawFilteredPair.of(Text.translatable("lore.roggen.${from.name}.page$num")))
            }
            return texts.toList()
        }
    }
}

/* OVERARCHING STORY
I: Genesis
Our tale starts thousands of years before the present. The beings we know as villagers were living in small groups as hunter-gatherers, never knowing when the next mob strike or failed hunt would spell their doom. One day, a spirit of the forests gave a group of villagers a gift, a nutritious plant formed of his power. This plant was known as rye, and the spirit as Roggen.

II: Expansion
With it, the villagers had a steady food source, and established stable towns. Rye spread across the world, and in its wake, villages flourished and grew. Eventually, all the villagers of the world were united under the banner of Roggen. They started paying him tribute, and established practices and rituals around him and his gift of rye. They traded with each other, constructed grand cities and monuments, and advanced their civilisation. For two thousand years did this last.

The continued adoration of an entire race changed Roggen. Previously belonging to the category of nature spirits (dependent local confluences), he morphed over time into an egregore (dependent conceptual confluence), requiring continued worship and adoration to live. He also changed from an altruistic being who wanted to help the poor villagers to an arrogant, narcissistic being.

III: Progression
Agriculture of other primary foods was taboo out of respect for Roggen. However, there were those who disregarded this and continued trying to grow thes anyways. Eventually, the agriculture of wheat was discovered, followed by potatoes. The villagers wanted to switch, at least partially, to these, for a more varied diet, crop rotations, and disease and pest protection. Roggen was angered and tried to stop this, but the villager chiefs decided to ignore him to focus on the interests of the community.

IV: Confounding
Seeing that the plan was underway and he was going to fade into obscurity and possibly die, Roggen in his rage unintentionally creating an indiscriminate plague that killed many and ravaged the minds of survivors. As it spread from the city of Zaffre, where he held the most power, villager society was thrown into chaos. There emerged three main groups. The Faithful pleaded to Roggen for mercy, but as the plague progressed and it became evident that no help was forthcoming, many recanted, and those that did not were antagonised. The Avoidant retreated en masse from society to try and not be infected despite its unknown vector. They all succumbed sooner or later. The Inquisitive researched ways to cure or prevent the spread of the plague. The last ones would attempt a risky ritual, becoming the first illagers. This period of time was called the Confounding.

V: Aftermath
In the end, all the villagers succumbed.
The Faithful abandoned their churches, desert temples, and jungle temples as they died or apostasised.
The Avoidant died in the secluded halls of their underground strongholds, vast nether fortresses and bastions, and faraway end cities.
The Inquisitive left behind their experimental stations in the form of igloos, dungeons and underground cities, and retreated to their woodland mansions and pillager outposts and witch huts, consumed by hatred for the people of Roggen.
Infrastructure eroded and broke down until it became manageable for the survivors to maintain. Villages shrank to their current proportions.
Underwater civilisations collapsed and left behind ruins, as well as their ocean monuments
Functional structures and constructs no longer had villagers to work them, so shipwrecks, abandoned portals, mineshafts, trial chambers and trail ruins became as they are.

VI: Future
Roggen could not stop his own plague. Devoid of faith, he weakened until near death. Only his lingering ties to the land ensured he did not die. Rye, as it relied on his power, disappeared from the world. Roggen still seeks sentient beings to give him faith, although now it is not out of generosity but desire for faith. He occasionally gathers enough power to manifest in a small way (statue of the rye god) and desperately search for worshippers, but it is unlikely he will succeed anytime soon.
*/