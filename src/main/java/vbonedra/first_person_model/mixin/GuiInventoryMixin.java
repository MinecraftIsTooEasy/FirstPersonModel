package vbonedra.first_person_model.mixin;

import net.minecraft.EntityLivingBase;
import net.minecraft.GuiInventory;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import vbonedra.first_person_model.util.FirstPersonState;

@Mixin(GuiInventory.class)
public class GuiInventoryMixin {

    @Inject(method = "func_110423_a(IIIFFLnet/minecraft/EntityLivingBase;)V", at = @At("HEAD"))
    private static void onDrawPlayerStart(int par0, int par1, int par2, float par3, float par4, EntityLivingBase par5, CallbackInfo ci) {
        FirstPersonState.isRenderingInventoryPuppet = true;
    }
    @Inject(method = "func_110423_a(IIIFFLnet/minecraft/EntityLivingBase;)V", at = @At("RETURN"))
    private static void onDrawPlayerEnd(int par0, int par1, int par2, float par3, float par4, EntityLivingBase par5, CallbackInfo ci) {
        FirstPersonState.isRenderingInventoryPuppet = false;
    }
}