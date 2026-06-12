package vbonedra.first_person_model.mixin;


import net.minecraft.*;
import org.lwjgl.opengl.GL11;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import vbonedra.first_person_model.config.FPMConfigs;

// fancy camera movement
@Mixin(EntityRenderer.class)
public abstract class EntityRendererMixin {
    @Unique
    float bobbing = 0;
    @Unique
    float jumpAngle = 0;
    @Unique
    float forwAngle = 0;
    @Unique
    float prevYaw = 0;
    @Unique
    float lastTick = 0;
    @Shadow
    private Minecraft mc;

    @Unique
    private static float incrementUntilGoal(float currentValue, float goalValue, float easeFactor) {
        float difference = goalValue - currentValue;
        float stepSize = difference * easeFactor;
        return currentValue + stepSize;
    }

    // custom view rolling
    @Redirect(method = "setupViewBobbing", at = @At(value = "INVOKE", target = "Lorg/lwjgl/opengl/GL11;glRotatef(FFFF)V", ordinal = 0))
    private void customBobbingRoll(float angle, float x, float y, float z) {
        if (FPMConfigs.CameraBobbingRoll.getBooleanValue()) {
            if (mc.gameSettings.thirdPersonView == 0) {

                float delta = 0.1f;

                EntityClientPlayerMP player = mc.thePlayer;

//            float strafingMul = (float) LMS_Settings.STRAFING_MULTIPLIER.getDouble();
//            float cameraMul = (float) LMS_Settings.CAMERA_MULTIPLIER.getDouble();
//            float eatingMul = (float) LMS_Settings.EATING_MULTIPLIER.getDouble();
//            float swingMul = (float) LMS_Settings.SWING_MULTIPLIER.getDouble();
                float strafingMul = (float) 1.0;
                float cameraMul = (float) 1.0;
                float eatingMul = (float) 1.0;
                float swingMul = (float) 1.0;

                ItemStack heldItem = player.getHeldItemStack();

                boolean isEating = player.isEating() &&
                        heldItem != null &&
                        (heldItem.getItem() instanceof ItemFood || heldItem.getItem() instanceof ItemPotion);

                float currentSwingProgress = player.prevSwingProgress + (player.swingProgress - player.prevSwingProgress) * delta;
                float goal = (float) ((player.moveStrafing != 0 ? -1.5 * Math.pow(player.moveStrafing, 3) : 0) * strafingMul +
                        (1.5 * swingMul * currentSwingProgress) +
                        (0.125 * (player.rotationYaw - prevYaw)) * cameraMul +
                        (isEating ? 0.5f * eatingMul * Math.cos(player.ticksExisted) : 0));


                float factor = player.moveStrafing == 0 || player.rotationYaw - prevYaw == 0 ? 0.25f : 0.005f;

                if (this.mc.thePlayer.ticksExisted > lastTick) {
                    prevYaw = player.rotationYaw;
                }

                lastTick = this.mc.thePlayer.ticksExisted;

                bobbing = MathHelper.clamp_float(incrementUntilGoal(bobbing, goal, delta * factor), -10, 10);

//            float mul = (float) LMS_Settings.BOBBING_MULTIPLIER.getDouble();
                float mul = (float) FPMConfigs.CameraBobbingRollIntensity.getDoubleValue();

                GL11.glRotatef(angle * 3f * mul + bobbing, x, y, z);
            }
        }
    }

    // fancy view pitching (bobbing)
    @Redirect(method = "setupViewBobbing", at = @At(value = "INVOKE", target = "Lorg/lwjgl/opengl/GL11;glRotatef(FFFF)V", ordinal = 1))
    private void customBobbingPitch(float angle, float x, float y, float z) {
        if (FPMConfigs.CameraBobbingPitch.getBooleanValue()) {
            if (mc.gameSettings.thirdPersonView == 0) {
                float delta = 1f;

                EntityClientPlayerMP player = Minecraft.getMinecraft().thePlayer;

                float goal = player.moveForward != 0 ? player.isSprinting()
                        ? 3.75f * player.moveForward
                        : 2.5f * player.moveForward : 0;
                float jumpGoal = 8 * (float) (player.posY - player.prevPosY);

                boolean still = player.moveForward == 0 || player.motionY == 0;

                float factor1 = still ? 0.2f : 0.04f;
                float factor2 = 0.4f;

                forwAngle = MathHelper.clamp_float(incrementUntilGoal(forwAngle, goal, delta * factor1), -5, 10);
                jumpAngle = MathHelper.clamp_float(incrementUntilGoal(jumpAngle, jumpGoal, delta * factor2), -5, 8);

//            float mul = (float) LMS_Settings.BOBBING_MULTIPLIER.getDouble();
//            float forwardMul = (float) LMS_Settings.FORWARD_MULTIPLIER.getDouble();
//            float jumpMul = (float) LMS_Settings.JUMP_MULTIPLIER.getDouble();
                float mul = (float)FPMConfigs.CameraBobbingPitchIntensity.getDoubleValue();
                float forwardMul = (float) 1.0;
                float jumpMul = (float) 1.0;

                GL11.glRotatef(angle * 2f * mul + forwAngle * forwardMul, x, y, z);
                GL11.glRotatef(jumpAngle * jumpMul, x, y, z);
            }
        }
    }

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
