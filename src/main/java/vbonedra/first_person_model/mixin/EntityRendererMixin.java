package vbonedra.first_person_model.mixin;


import net.minecraft.*;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import vbonedra.first_person_model.config.FPMConfigs;

// fancy camera movement
@Mixin(EntityRenderer.class)
public abstract class EntityRendererMixin {
    @Shadow private Minecraft mc;

    // hides hud hands in some cases
    @Inject(method = "renderHand", at = @At("HEAD"), cancellable = true)
    private void disableFirstPersonHandUnlessHoldingMap(float par1, int par2, CallbackInfo ci) {
        ItemStack item = mc.thePlayer.getHeldItemStack();
        if (mc.gameSettings.thirdPersonView == 0) {
            if (FPMConfigs.RenderFirstPersonModel.getBooleanValue()
                    && !(item != null && item.itemID == Item.map.itemID)
                    && !FPMConfigs.RenderHudHandsInFirstPersonModel.getBooleanValue()
            ) ci.cancel();
        }
    }
}
