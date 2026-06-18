package vbonedra.first_person_model.mixin;

import net.minecraft.*;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import vbonedra.first_person_model.config.FPMConfigs;

@Mixin(ModelBiped.class)
public abstract class ModelBipedMixin {
    @Shadow public ModelRenderer bipedBody;
    @Shadow public ModelRenderer bipedRightArm;
    @Shadow public ModelRenderer bipedLeftArm;
    @Shadow public ModelRenderer bipedRightLeg;
    @Shadow public ModelRenderer bipedLeftLeg;
    @Shadow public abstract void setRotationAngles(float f, float f1, float f2, float f3, float f4, float f5, Entity entity);

    // eating animation
    @Inject(method = "setRotationAngles(FFFFFFLnet/minecraft/Entity;)V", at = @At("TAIL"))
    private void injectEatingAnimation(float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scale, Entity entity, CallbackInfo ci) {
        if (entity instanceof EntityPlayer player) {
            ItemStack heldItem = player.getItemInUse();
            if (heldItem != null && player.getItemInUseCount() > 0) {
                EnumItemInUseAction action = heldItem.getItemInUseAction(player);
                if (action == EnumItemInUseAction.EAT || action == EnumItemInUseAction.DRINK) {
                    float speedModifier = 0.65F*2;
                    float swingWave = MathHelper.sin(ageInTicks * speedModifier) * 0.15F;
                    this.bipedRightArm.rotateAngleX = -1.1F + swingWave;
                    this.bipedRightArm.rotateAngleY = -0.4F;
                    this.bipedRightArm.rotateAngleZ = MathHelper.cos(ageInTicks * speedModifier) * 0.04F;
                }
            }
        }
    }

    // removes head, hides hand when map used and renders armor
    @Inject(method = "render", at = @At("HEAD"), cancellable = true)
    private void injectCustomRender(Entity par1Entity, float par2, float par3, float par4, float par5, float par6, float par7, CallbackInfo ci) {
        if (!FPMConfigs.RenderFirstPersonModel.getBooleanValue()) return;
        Minecraft mc = Minecraft.getMinecraft();

        boolean isFirstPerson = (par1Entity == mc.thePlayer)
                && (mc.gameSettings.thirdPersonView == 0)
                && !vbonedra.first_person_model.util.FirstPersonState.isRenderingInventoryPuppet;

        if (!isFirstPerson) {
            return;
        }

        boolean isHoldingMap = false;
        if (mc.thePlayer != null) {
            ItemStack heldItem = mc.thePlayer.getHeldItemStack();
            isHoldingMap = heldItem != null && heldItem.itemID == Item.map.itemID;
        }

        this.setRotationAngles(par2, par3, par4, par5, par6, par7, par1Entity);

        this.bipedBody.render(par7);

        if (!isHoldingMap && !FPMConfigs.RenderHudHandsInFirstPersonModel.getBooleanValue()) {
            this.bipedRightArm.render(par7);
            this.bipedLeftArm.render(par7);
        }

        this.bipedRightLeg.render(par7);
        this.bipedLeftLeg.render(par7);

        ci.cancel();
    }
}
