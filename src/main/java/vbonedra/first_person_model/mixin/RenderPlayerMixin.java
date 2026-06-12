package vbonedra.first_person_model.mixin;

import net.minecraft.*;
import org.lwjgl.opengl.GL11;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import vbonedra.first_person_model.config.FPMConfigs;

@Mixin(RenderPlayer.class)
public abstract class RenderPlayerMixin extends RendererLivingEntity {
    @Shadow
    private ModelBiped modelBipedMain;
    @Shadow
    private ModelBiped modelArmorChestplate;

    public RenderPlayerMixin(ModelBase par1ModelBase, float par2) {
        super(par1ModelBase, par2);
    }

    // renders armor on player hand, just a cool niche thing. TODO: map has broken render
    @Inject(method = "renderFirstPersonArm(Lnet/minecraft/EntityPlayer;)V", at = @At("TAIL"))
    private void renderArmorOnHUDHand(EntityPlayer player, CallbackInfo ci) {
        ItemStack itemStack = player.inventory.mainInventory[player.inventory.currentItem];
        if (itemStack != null && itemStack.getItem() == Item.map) return;
        if (FPMConfigs.RenderArmorOnHudHand.getBooleanValue()) {
            GL11.glPushMatrix();
            GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
            // maybe no need for it
            ItemStack chestplateStack = player.inventory.armorItemInSlot(2);
            if (chestplateStack == null || !(chestplateStack.getItem() instanceof ItemArmor)) {
                chestplateStack = player.inventory.armorItemInSlot(1);
            }

            if (chestplateStack != null && chestplateStack.getItem() instanceof ItemArmor armorItem) {
                ResourceLocation armorTexture = RenderBiped.func_110857_a(armorItem, 1);
//                ResourceLocation armorTexture = RenderBiped.func_110858_a(armorItem, 1, null);
                if (armorTexture != null) {
                    Minecraft.getMinecraft().getTextureManager().bindTexture(armorTexture);
                    syncArmAngles(this.modelBipedMain, this.modelArmorChestplate);
                    if (armorItem.getArmorMaterial() == Material.leather) {
                        int color = armorItem.getColor(chestplateStack);
                        float r = (float) (color >> 16 & 255) / 255.0F;
                        float g = (float) (color >> 8 & 255) / 255.0F;
                        float b = (float) (color & 255) / 255.0F;
                        GL11.glColor3f(r, g, b);
                    } else {
                        GL11.glColor3f(1.0F, 1.0F, 1.0F);
                    }
                    this.modelArmorChestplate.bipedRightArm.showModel = true;
                    this.modelArmorChestplate.bipedRightArm.render(0.0625F);
                    this.modelArmorChestplate.bipedLeftArm.showModel = true;
                    this.modelArmorChestplate.bipedLeftArm.render(0.0625F);
                }
            }
            GL11.glPopMatrix();
        }
    }
    @Unique
    private void syncArmAngles(ModelBiped source, ModelBiped target) {
        target.bipedRightArm.rotateAngleX = source.bipedRightArm.rotateAngleX;
        target.bipedRightArm.rotateAngleY = source.bipedRightArm.rotateAngleY;
        target.bipedRightArm.rotateAngleZ = source.bipedRightArm.rotateAngleZ;

        target.bipedRightArm.rotationPointX = source.bipedRightArm.rotationPointX;
        target.bipedRightArm.rotationPointY = source.bipedRightArm.rotationPointY;
        target.bipedRightArm.rotationPointZ = source.bipedRightArm.rotationPointZ;
    }

//    // no head, without it its impossible to use first person model
//    @Redirect(method = "renderSpecials", at = @At(value = "INVOKE", target = "Lnet/minecraft/TileEntitySkullRenderer;func_82393_a(FFFIFILjava/lang/String;)V"))
//    private void disableHeadIf1stPers(TileEntitySkullRenderer instance, float v, float par1, float par2, int par3, float par4, int par5, String par6, AbstractClientPlayer player) {
//        Minecraft mc = Minecraft.getMinecraft();
//        if (player == mc.thePlayer) return;
//        instance.func_82393_a(v, par1, par2, par3, par4, par5, par6);
//    }

    // remove map item in world, without it would render on player model center
    @Redirect(method = "renderSpecials", at = @At(value = "INVOKE", target = "Lnet/minecraft/ItemRenderer;renderItem(Lnet/minecraft/EntityLivingBase;Lnet/minecraft/ItemStack;I)V"))
    private void removeMapItem(ItemRenderer instance, EntityLivingBase entity, ItemStack itemStack, int renderPass) {
        Minecraft mc = Minecraft.getMinecraft();
        boolean isFirstPersonWorld = (entity == mc.thePlayer)
                && (mc.gameSettings.thirdPersonView == 0)
                && !vbonedra.first_person_model.util.FirstPersonState.isRenderingInventoryPuppet;
        if (isFirstPersonWorld) {
            if (itemStack != null
                && itemStack.itemID == Item.map.itemID
                && FPMConfigs.RenderFirstPersonModel.getBooleanValue()
                || FPMConfigs.RenderHudHandsInFirstPersonModel.getBooleanValue()) {
                return;
            }
        }
        instance.renderItem(entity, itemStack, renderPass);
    }

//    // show player hands if holding map
//    @Inject(method = "renderFirstPersonArm", at = @At(value = "INVOKE", target = "Lnet/minecraft/ModelRenderer;render(F)V"))
//    private void showFirstPersonArmIfMap(EntityPlayer player, CallbackInfo ci) {
//        Minecraft mc = Minecraft.getMinecraft();
//        ItemStack heldItem = mc.thePlayer.getHeldItemStack();
//
//        if (heldItem != null && (heldItem.itemID == Item.map.itemID)) {
//            this.modelBipedMain.bipedRightArm.showModel = true;
//            this.modelBipedMain.bipedLeftArm.showModel = true;
//        }
//    }

    // move camera (actually body)
    @Inject(method = "rotatePlayer", at = @At("HEAD"))
    private void offsetPlayerIn1stPerson(AbstractClientPlayer entity, float par2, float par3, float par4, CallbackInfo ci) {
        if (!FPMConfigs.RenderFirstPersonModel.getBooleanValue()) return;
        Minecraft mc = Minecraft.getMinecraft();
        if (entity == mc.thePlayer && entity.height >= 1.4f) {
            float v = (float) FPMConfigs.HeadOffset.getDoubleValue();

            float i = (float) (entity.rotationYaw % 360 / 90 * Math.PI / 2);

            GL11.glTranslatef((float) (Math.sin(i) * v / 16), (float) 0, (float) (-Math.cos(i) * v / 16));
        }
    }

    // bow render, without it bow looks annoying, player head clips into bow's model
    @ModifyArg(method = "renderSpecials", at = @At(value = "INVOKE", target = "Lorg/lwjgl/opengl/GL11;glRotatef(FFFF)V", ordinal = 11), index = 0)
    private float verticalBow(float angle) {
        if (!FPMConfigs.RenderFirstPersonModel.getBooleanValue()) return angle;
        return 0;
    }
    @ModifyArg(method = "renderSpecials", at = @At(value = "INVOKE", target = "Lorg/lwjgl/opengl/GL11;glTranslatef(FFF)V", ordinal = 6), index = 0)
    private float offsetBowInnerHand(float angle) {
        if (!FPMConfigs.RenderFirstPersonModel.getBooleanValue()) return angle;
        return 2 / 16f;
    }
    private float getBowPullProgress() {
        net.minecraft.Minecraft mc = net.minecraft.Minecraft.getMinecraft();
        if (mc.thePlayer != null && mc.thePlayer.isUsingItem() && mc.thePlayer.getItemInUse().getItem() instanceof net.minecraft.ItemBow) {
            int useCount = mc.thePlayer.getItemInUseCount();
            int maxUseCount = mc.thePlayer.getItemInUse().getMaxItemUseDuration();

            float duration = (float) (maxUseCount - useCount);
            float progress = duration / 20.0F;

            return Math.max(0.0F, Math.min(progress, 1.0F));
        }
        return 0.0F;
    }


}
