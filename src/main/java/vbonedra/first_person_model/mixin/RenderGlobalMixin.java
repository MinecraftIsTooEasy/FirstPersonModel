package vbonedra.first_person_model.mixin;


import net.minecraft.*;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import vbonedra.first_person_model.config.FPMConfigs;
import vbonedra.first_person_model.util.FirstPersonState;

@Mixin(value = RenderGlobal.class)
public abstract class RenderGlobalMixin {

    @Shadow private Minecraft mc;

    // TODO: idk which one better
    // renders player no matter what
    @Redirect(method = "renderEntities", at = @At(value = "FIELD", target = "Lnet/minecraft/GameSettings;thirdPersonView:I"))
    private int redirectThirdPersonView(GameSettings settings) {
        if (settings.thirdPersonView == 0) {
            if (FPMConfigs.RenderFirstPersonModel.getBooleanValue()) {
                FirstPersonState.isRenderingFirstPersonModel = true;
                return 1;
            }
        }
        FirstPersonState.isRenderingFirstPersonModel = false;
        return settings.thirdPersonView;
    }

//    @Inject(method = "renderEntities", at = @At(value = "INVOKE", target = "Lnet/minecraft/Profiler;endStartSection(Ljava/lang/String;)V", ordinal = 2))
//    private void renderPlayerIn1stPerson(Vec3 par1Vec3, ICamera par2ICamera, float par3, CallbackInfo ci) {
//        RenderManager.instance.renderEntity(this.mc.thePlayer, par3);
//    }
//    @Redirect(method = "renderEntities", at = @At(value = "INVOKE", target = "Lnet/minecraft/RenderManager;renderEntity(Lnet/minecraft/Entity;F)V"))
//    private void isPlayerRendered(RenderManager instance, Entity par1Entity, float par2) {
//        instance.renderEntity(par1Entity, par2);
//    }
}
