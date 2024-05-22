package xyz.bluspring.tokeneaterorigin.mixin;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import xyz.bluspring.crimeutils4.CrimeUtilS4;
import xyz.bluspring.tokeneaterorigin.TokenEaterOrigin;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin {
    @Shadow public abstract void forceAddEffect(MobEffectInstance instance, @Nullable Entity entity);

    @Shadow @Nullable public abstract MobEffectInstance getEffect(MobEffect effect);

    @Inject(method = "addEatEffect", at = @At("HEAD"), cancellable = true)
    private void crimecraft$discardTokenEffectsIfTokenEater(ItemStack food, Level level, LivingEntity entity, CallbackInfo ci) {
        if (food.is(CrimeUtilS4.DOUBLOON_ITEM) && TokenEaterOrigin.GOT_TO_GO_POWER.isActive((LivingEntity) (Object) this)) {
            ci.cancel();

            if (level.isClientSide())
                return;

            var currentEffect = this.getEffect(TokenEaterOrigin.GOT_TO_GO_EFFECT);
            var amplifier = 0;

            if (currentEffect != null)
                amplifier = currentEffect.getAmplifier() + 1;

            this.forceAddEffect(new MobEffectInstance(TokenEaterOrigin.GOT_TO_GO_EFFECT, -1, amplifier), entity);
        }
    }
}
