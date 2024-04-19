
// FindBestHorse.java
package net.xggxwolf.besthorsestats.utils;

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.entity.Entity;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.passive.HorseEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.world.World;
import org.lwjgl.glfw.GLFW;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;

import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class FindBestHorse {
    public static final String MOD_ID = "besthorsestats";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    private final KeyBinding keyBinding;

    public FindBestHorse() {
        keyBinding = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.besthorsestats.findfastesthorse", // The translation key of the keybinding's name
                InputUtil.Type.KEYSYM, // The type of the keybinding, KEYSYM for keyboard, MOUSE for mouse.
                GLFW.GLFW_KEY_K, // The keycode of the key
                "category.besthorsestats" // The translation key of the keybinding's category.
        ));
    }

    public void register() {
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            while (keyBinding.wasPressed()) {
                onKeyPress(client.player);
            }
        });
    }

    private void onKeyPress(PlayerEntity player) {
        World world = player.getWorld();
        if (world.isClient) {
            MinecraftClient minecraftClient = MinecraftClient.getInstance();
            int simulationDistance = minecraftClient.options.getSimulationDistance().getValue();

            double viewDistance = simulationDistance * 16.0;
            List<HorseEntity> entities = world.getEntitiesByClass(HorseEntity.class,
                    player.getBoundingBox().expand(viewDistance),
                    (nearbyEntity) -> true);

            HorseEntity fastestHorse = null;
            double maxSpeed = 0;

            double jumpHeight = 0;
            double health = 0;

            for (HorseEntity nearbyHorse : entities) {
                double movementSpeed = nearbyHorse.getAttributeValue(EntityAttributes.GENERIC_MOVEMENT_SPEED);
                jumpHeight = nearbyHorse.getAttributeValue(EntityAttributes.HORSE_JUMP_STRENGTH);
                health = nearbyHorse.getAttributeValue(EntityAttributes.GENERIC_MAX_HEALTH);

                if (movementSpeed > maxSpeed) {
                    maxSpeed = movementSpeed;
                    fastestHorse = nearbyHorse;
                }
            }

            if (fastestHorse != null) {
                int x = Math.round((float) fastestHorse.getX());
                int y = Math.round((float) fastestHorse.getY());
                int z = Math.round((float) fastestHorse.getZ());
                double speed = fastestHorse.getAttributeValue(EntityAttributes.GENERIC_MOVEMENT_SPEED) * 42.16;
                jumpHeight = (
                        -0.1817584952 * Math.pow(jumpHeight, 3) +
                                3.689713992 * Math.pow(jumpHeight, 2) +
                                2.128599134 * jumpHeight - 0.343930367
                ); // convert to blocks

                String message = String.format("Found Fastest Horse %d , %d , %d Movement Speed: %.2f Blocks/sec, Jump Height: %.2f, Max Health : %.2f", x, y, z, speed, jumpHeight, health);

                ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();
                executorService.schedule(() -> player.sendMessage(Text.literal(message), false), 100, TimeUnit.MILLISECONDS);
            }
        }
    }
}