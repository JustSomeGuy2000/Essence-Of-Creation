# About
- Author: JEHR
- Minecraft: Java 1.21.8
- Loader: Fabric 0.17.2
- Dependencies: Fabric Language Kotlin

# Essence of Creation
It is said that nestled deeply in every human creation is a spark, a quantum of creation imbued ito the object by merit of being created. It is this spark that enables materials to come together into more complex systems. Without it, bricks would revert to clay, glass to sand, torches into wood and coal. It is termed Essence of Creation.

For ages Essence of Creation has been irretrievably locked away, but no longer. With the power of the Essential Extractor and Essential Infuser, the common man can harness its massive power for their own purposes. What are you waiting for? There is much to be done.

# Installation (coming soon)
## Github (requires Java compiler)
## Modrinth
## CurseForge

# To Do
- Finish writing the Roggen Lore Books
- Figure out why the recipe generator keeps crashing
- Essential Extractor fuels and sources
- Make Essential Extractor model better (more contrast for the cobble lines, increase height of hotizontal separator band, fix single misplaced pixel on top)
- Fix Essential Extractor datagen to use proper methods instead of the current cursed implementation
- Make Blessing of Rye persist after death, instead of the current subpar implementation
- Allow Scaffold Trunk and Seed to be climbed like ladders
- Essential Infuser particles in randomDisplayTick
- Stacks of diamond blocks exhibit strange behaviour when shift-clicked into the essential infuser. True scope is unscertain.

# Features
## Blocks
- Scaffold Seed (Scaffolding): Grows of its own volition, leaving behind Scaffold Trunk. Occasionally splits. Can get out of hand very quickly. Destroys itself if its source of support is destroyed.
- Scaffold Trunk: Acts like Scaffolding. Can be climbed, and breaks if its source of support is broken. Can be directly substituted by blocks.
- Scaffold Stripper: When placed next to a Scaffold Trunk, enters it and follows its path. Splits at branches. Destroys Scaffold Seeds.
- Spatial Displacer (Purpur Pillar): Any entity that touches its top gets teleported away. Starts with 8 blocks of range, increasing by 8 blocks for every Spatial Displacer below it.
- Statue of the Rye God: Inflicts Blessing of Rye onto nearby players. Clears the Blessing when broken.
- Rye Bale: Hay Bale for Rye.
- Essential Extractor: Allows Essence of Creation to be extracted from objects. Takes in two inputs: a souce and a fuel. Different items have different fuel values or extraction efficiencies (look at EssentialExtractor.Companion.sources/fuels for a list). Both are used up in the process.
- Essential Infuser: **Developing...**
## Items
- Essence of Creation: The core component of the mod. Can be extracted from and infused into some items and blocks.
- Rye: A long lost grain. Obtained by being afflicted with Blessing of Rye.
- Totem of Unrying: Revives on death with more potent effects than the normal Totem, but also gives Blessing of Rye.
## Status Effects
- Blessing of Rye: Gives you Rye based on its power every second. Automatically increases its power over time. Is not cleared by milk. Cleared by death, but at a cost. A new Statue of the Rye God will spawn, and all your levels and items will be lost as sacrifice to it.
## Advancements
- Essence of Creation (root): Obtain an Essence of Creation.
- Ryes and Shine (Essence of Creation): Obtain Rye.
- Rye Not? (Ryes and Shine): Fill your inventory and offhand with stacks of Rye.
- Aryse (Ryes and Shine): Activate a Totem of Unrying.
## Recipes
- Scaffold Stripper
- Totem of Unrying
- Essential Extractor