package vbonedra.first_person_model.mixin;

import net.minecraft.Gui;
import net.minecraft.GuiIngame;
import net.minecraft.Minecraft;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(GuiIngame.class)
public abstract class GuiInGameMixin extends Gui {
    @Shadow
    @Final
    private Minecraft mc;

//    @Redirect(
//            method = "renderGameOverlay(FZII)V",
//            at = @At(
//                    value = "INVOKE",
//                    target = "Lnet/minecraft/GuiIngame;drawTexturedModalRect(IIIIII)V",
//                    ordinal = 2
//            ),
//            require = 1,
//            allow = 1
//    )
//    private void disableCrosshairWhenF5(GuiIngame instance, int x, int y, int u, int v, int width, int height) {
//        if (this.mc.gameSettings.thirdPersonView == 0) {
//            instance.drawTexturedModalRect(x, y, u, v, width, height);
//        }
//    }

}
