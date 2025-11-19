package src.main.java.com.exloran.totemquick;

import net.fabricmc.api.DedicatedServerModInitializer;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class TotemQuickServer implements DedicatedServerModInitializer {
    private static final Identifier SWAP_PACKET = new Identifier("totemquick", "swap");

    @Override
    public void onInitializeServer() {
        ServerPlayNetworking.registerGlobalReceiver(SWAP_PACKET, (server, player, handler, buf, responseSender) -> {
            server.execute(() -> {
                boolean found = false;

                for (int i = 0; i < player.getInventory().size(); i++) {
                    ItemStack stack = player.getInventory().getStack(i);
                    if (stack.getItem() == Items.TOTEM_OF_UNDYING) {
                        ItemStack offhand = player.getOffHandStack();
                        player.getInventory().setStack(i, offhand);
                        player.getInventory().offHand.set(0, stack);
                        found = true;
                        break;
                    }
                }

                if (!found) {
                    player.sendMessage(Text.of("§cEnvanterinde Totem yok!"), true);
                }
            });
        });
    }
}
