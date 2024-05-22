package xyz.bluspring.tokeneaterorigin.mixin;

import net.minecraft.client.gui.screens.inventory.CreativeModeInventoryScreen;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerListener;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import xyz.bluspring.tokeneaterorigin.events.ContainerEvents;

@Mixin(AbstractContainerMenu.class)
public abstract class AbstractContainerMenuMixin {
    @Shadow
    public abstract void addSlotListener(ContainerListener listener);

    @Shadow public abstract ItemStack getCarried();

    @Inject(method = "<init>", at = @At("TAIL"))
    private void addContainerListener(MenuType menuType, int containerId, CallbackInfo ci) {
        if (!(((Object) this) instanceof CreativeModeInventoryScreen.ItemPickerMenu))
            this.addSlotListener(ContainerEvents.LISTENER);
    }
}

