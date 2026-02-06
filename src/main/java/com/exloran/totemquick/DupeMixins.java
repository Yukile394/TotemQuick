package.exloran.totemquick.mixin;

import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.PlayerScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.screen.slot.SlotAction;
import net.minecraft.screen.slot.SlotActionType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callbackallbackInfo;

@Mixin(PlayerScreenHandler.class)
public abstract class DupeMixin {

 @Inject(method = "onSlotClick", at = @At("HEAD"), cancellable = true)
 private void onSlotClick(int syncId, int slotId, int button, SlotActionType action, PlayerEntity player, CallbackInfo ci) {
 if (actionType == SlotActionType.PICKUP) {
 Slot slot = ((PlayerScreenHandler) (Object) this).getSlot(slotId);
 if (slot != null && slot.hasStack()) {
 ItemStack = slot.getStack().copy();
 player.getInventory().offerOrDrop(stack);
 MinecraftClient.getInstance().player.sendMessage(Text.literal("§aItem duped!"), false);
 ci.cancel(); // Cancel the original slot click action
 }
 }
 }
}
