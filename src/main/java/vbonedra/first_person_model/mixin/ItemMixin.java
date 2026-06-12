package vbonedra.first_person_model.mixin;

import net.minecraft.EntityPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(EntityPlayer.class)
public class ItemMixin {
    // swing on all item bobbing
    @Inject(method = "bobItem()V", at = @At("HEAD"))
    private void injectThrowableSwingViaBob(CallbackInfo ci) {
        EntityPlayer player = (EntityPlayer) (Object) this;
        player.swingArm();
    }
}
