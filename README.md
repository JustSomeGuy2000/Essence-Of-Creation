# About
- Author: JEHR
- Minecraft: Java 1.21.8
- Loader: Fabric 0.17.2
- Dependencies: Fabric Language Kotlin

# Essence of Creation
It is said that nestled deeply in every human creation is a spark, a quantum of creation imbued ito the object by merit of being created. It is this spark that enables materials to come together into more complex systems. Without it, bricks would revert to clay, glass to sand, torches into wood and coal. It is termed Essence of Creation.

For ages Essence of Creation has been irretrievably locked away, but no longer. With the power of the Essential Extractor and Essential Infuser, the common man can harness its massive power for their own purposes. What are you waiting for? There is much to be done.

# Installation (coming soon)
## Github releases
## Modrinth
## CurseForge
## Self-compile (requires JDK)

# To Do
- Finish writing the Roggen Lore Books
- Essential Extractor fuels and sources
- Make Essential Extractor model better (more contrast for the cobble lines, increase height of horizontal separator band, fix single misplaced pixel on top)
- Fix Essential Extractor datagen to use proper methods instead of the current cursed implementation
- Make Blessing of Rye persist after death, instead of the current subpar implementation
- Allow Scaffold Trunk and Seed to be climbed like ladders
- Stacks of diamond blocks exhibit strange behaviour when shift-clicked into the essential infuser. True scope is unscertain.
- Animated texture for Essential Infuser (top part pulses)
- Rework datagen to eliminate duplicate texture images
- 3D model for Handheld Infuser
- Scatter fertilisation for Super Bone Meal
- Particles for Super Bone Meal don't work
- Make blocks only be able to be mined with certain material levels
- Add sound to the Gun-Sword

# Features
## Blocks
- Scaffold Seed (Scaffolding): Grows of its own volition, leaving behind Scaffold Trunk. Occasionally splits. Can get out of hand very quickly. Destroys itself if its source of support is destroyed.
- Scaffold Trunk: Acts like Scaffolding. Can be climbed, and breaks if its source of support is broken. Can be directly substituted by blocks.
- Scaffold Stripper: When placed next to a Scaffold Trunk, enters it and follows its path. Splits at branches. Destroys Scaffold Seeds.
- Spatial Displacer (Purpur Pillar): Any entity that touches its top gets teleported away. Starts with 8 blocks of range, increasing by 8 blocks for every Spatial Displacer below it.
- Statue of the Rye God: Inflicts Blessing of Rye onto nearby players. Clears the Blessing when broken.
- Rye Bale: Hay Bale for Rye.
- Essential Extractor: Allows Essence of Creation to be extracted from objects. Takes in two inputs: a souce and a fuel. Different items have different fuel values or extraction efficiencies (look at EssentialExtractor.Companion.sources/fuels for a list). Both are used up in the process.
- Essential Infuser: Allows Essence of Creation to be infused into items. Takes in two inputs: a source and an Essence. Each Essence may be infused into one and only one item. Infusable items are shown here with brackets after thieir name showing their source item.
## Items
- Essence of Creation: The core component of the mod. Can be extracted from and infused into some items and blocks.
- Rye: A long lost grain. Obtained by being afflicted with Blessing of Rye.
- Totem of Unrying: Revives on death with more potent effects than the normal Totem, but also gives Blessing of Rye.
- Handheld Infuser: Hit an entity with Essence of Creation in your inventory to infuse it (if possible). See HandheldInfuser.Companion.infusables for a list.
- God Apple (Enchanted Golden Apple): An even better version of the item usually termed "God Apple".
- Super Bone Meal (Bone Meal): Applies Bone Meal 16 times to every block in a 5x5 are around the targeted block.
- Cane (Stick): A weak melee weapon that gives a speed and knockback boost.
- Gun-Sword (Sword): A weapon that can be used as a melee on left-click, or a ranged on right-click. Inaccuracy decreases with aiming time. Different materials have different stats, wiht Iron being the minimum. Diamond and Netherite Gun-Swords can also be combined with 1 of certain materials in a Crafting Table to produce special Gun-Swords. Currently available materials are Emerald, Amethyst, Echo Shard and Breeze Rod.
## Entities
- Gun Sword Bullet: The bullet of the Gun-Sword.
## Status Effects
- Blessing of Rye: Gives you Rye based on its power every second. Automatically increases its power over time. Is not cleared by milk. Cleared by death, but at a cost. A new Statue of the Rye God will spawn, and all your levels and items will be lost as sacrifice to it.
## Advancements
- Essence of Creation (root): Obtain an Essence of Creation.
- Ryes and Shine (Essence of Creation): Obtain Rye.
- Rye Not? (Ryes and Shine): Fill your inventory and offhand with stacks of Rye.
- Aryse (Ryes and Shine): Activate a Totem of Unrying.
- Anthropogenesis (Essence of Creation): Infuse an item or entity with Essence of Creation.
- Ambrosia (Anthropogenesis): Obtain a God Apple.
## Recipes
- Rye Bale (Shapeless Crafting): Rye x9 
- Scaffold Stripper (Shaped Crafting):
iqi
qsq
iqi
i: Iron Ingot, s: Scaffold Seed, q: Nether Quartz
- Totem of Unrying (Shapeless Crafting): Totem of Undying x1 + Rye Bale x8
- Essential Extractor (Shaped Crafting):
ddd
ifi
ddd
d: Cobbled Deepslate, i: Diamond, f: Furnace
- Essential Infuser (Shaped Crafting):
isi
geg
ooo
i: Iron Ingot, s: Empty, g: Glass, e: Essence of Creation, o: Obsidian
- Handheld Infuser (Shaped Crafting):
sss
ice
shi
s: Empty, i: Iron Ingot, c: Crossbow, e: Essential Infuser, h: Chest