import net.fabricmc.fabric.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.util.PacketByteBuf;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.RegistryKey;

import net.minecraft.text.Text;

import org.lwjgl.glfw.GLFW;

public class AutoTotemDuplication implements ClientModInitializer {
    private static final Identifier SWAP_PACKET = new Identifier("auto_totem_duplication", "swap");

    @Override
    public void onInitializeClient() {
        // K tuşu ile totem takası
        totemKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
            "key.auto_totem_duplication.swap",
            InputUtil.Type.KEYSYM,
            GLFW.GLFW_KEY_K,
            "category.auto_totem_duplication.main"
        ));
        
        // Her tick'te tuş kontrolü
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            while (totemKey.wasPressed()) {
                if (client.player != null) {
                    client.player.sendMessage(Text.literal("Totem takası aktif!"), false);
                    // Buraya totem takas kodu gelecek
                    swapItems();
                }
            }
        });
    }

    private void swapItems() {
        MinecraftClient client = MinecraftClient.getInstance();
        ClientPlayerEntity player = client.player;
        Inventory inventory = player.inventory;

        ItemStack hotbarTotem = inventory.getStack(45); // Hotbar'daki totem
        ItemStack offhandTotem = inventory.getStack(44); // Elindeki totem

        if (!hotbarTotem.isEmpty() && !offhandTotem.isEmpty()) {
            inventory.setStack(45, offhandTotem);
            inventory.setStack(44, hotbarTotem);
        }
    }
}
