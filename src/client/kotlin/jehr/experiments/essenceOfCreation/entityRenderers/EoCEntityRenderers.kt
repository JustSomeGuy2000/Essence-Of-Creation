package jehr.experiments.essenceOfCreation.entityRenderers

import jehr.experiments.essenceOfCreation.entities.EoCEntities
import jehr.experiments.essenceOfCreation.entityModels.GunSwordBulletModel
import net.fabricmc.fabric.api.client.rendering.v1.EntityModelLayerRegistry
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry

object EoCEntityRenderers {

    fun init() {
        EntityRendererRegistry.register(EoCEntities.gunSwordBullet) { GunSwordBulletRenderer(it) }
        EntityRendererRegistry.register(EoCEntities.gunSwordBulletWC) { GunSwordBulletRenderer(it) }
        EntityRendererRegistry.register(EoCEntities.gunSwordBulletSB) { GunSwordBulletRenderer(it) }
        EntityModelLayerRegistry.registerModelLayer(GunSwordBulletModel.layer, GunSwordBulletModel::getTexturedModelData)
    }
}

/* On the ways in which parts of the rendering system interact
Every entity has four parts: the Model, Renderer, RenderState and Texture.

The Model extends EntityModel,
and is a description of how the entity looks.
It has a companion method to retrieve the appearance data,
and companion variables declaring names for different parts of the model as EntityModelLayers.
The Layers have to be registered in the EntityModelLayerRegistry.
The companion method adds children to the base model using names,
which are accessed by that name.for animation.
Model animation is also done here using the setAngles method. (BeeEntityModel is a good example)
This is where the RenderState is used.

The Renderer extends EntityRenderer,
and renders the model and provides related data to the game.
It must be registered in the EntityRendererRegistry.
It creates a RenderState to pass to the game in Renderer::createRenderState.
Some dynamic rendering is done in the render method using a bunch of matrix transforms.
(or so it seems; it is unclear how this differs from EntityModel::setAngles).
Otherwise, rendering is handled by the superclass.
The base implementation of ::render doesn't seem to do much, but it seems to work.

The RenderState is a data class (in the sense that it holds data).
It stores information for rendering.

The Texture is an image that stores how the faces of the Model look.
Frankly, I don't know how it works.
There is a certain format that must be followed, but what that is is beyond me.
 */