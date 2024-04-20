
// FindBestHorse.java
package net.xggxwolf.besthorsestats.utils;

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.passive.HorseEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.Text;
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

    private final KeyBinding keyBinding1;
    private final KeyBinding keyBinding2;
    public FindBestHorse() {
        keyBinding1 = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "Find Best Horse", // The translation key of the keybinding's name
                InputUtil.Type.KEYSYM, // The type of the keybinding, KEYSYM for keyboard, MOUSE for mouse.
                GLFW.GLFW_KEY_K, // The keycode of the key
                "Best Horse Stats" // The translation key of the keybinding's category.
        ));

        keyBinding2 = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "Switch Types", // The translation key of the keybinding's name
                InputUtil.Type.KEYSYM, // The type of the keybinding, KEYSYM for keyboard, MOUSE for mouse.
                GLFW.GLFW_KEY_J, // The keycode of the key
                "Best Horse Stats" // The translation key of the keybinding's category.
        ));
    }

    public void register() {
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            while (keyBinding1.wasPressed()) {
                assert client.player != null;
                onKeyPress(client.player);
            }
            while (keyBinding2.wasPressed()) {
                assert client.player != null;
                onKeyPress2(client.player);
            }
        });
    }
    int type = 0;
    private void onKeyPress2(ClientPlayerEntity player) {
        type = (type + 1) % 3;
        String message = "Searching for ";
        switch (type) {
            case 0:
                message += "Fastest Horse";
                break;
            case 1:
                message += "Healthiest Horse";
                break;
            case 2:
                message += "Horse with Highest Jump Height";
                break;
        }
        ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();
        String finalMessage = message;
        executorService.schedule(() -> player.sendMessage(Text.literal(finalMessage), true), 100, TimeUnit.MILLISECONDS);
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

            HorseEntity lfHorse = null;
            double maxSpeed = 0;
            double maxHealth = 0;
            double maxJump = 0;

            String lf = "";


            double jumpHeight = 0;
            double health = 0;

            for (HorseEntity nearbyHorse : entities) {
                double movementSpeed = nearbyHorse.getAttributeValue(EntityAttributes.GENERIC_MOVEMENT_SPEED);
                jumpHeight = nearbyHorse.getAttributeValue(EntityAttributes.HORSE_JUMP_STRENGTH);
                health = nearbyHorse.getAttributeValue(EntityAttributes.GENERIC_MAX_HEALTH);
                switch (type) {
                    case 0:
                        if (movementSpeed > maxSpeed) {
                            maxSpeed = movementSpeed;
                            lfHorse = nearbyHorse;
                            lf = "Fastest";
                        }
                        break;
                    case 1:
                        if (health > maxHealth) {
                            maxHealth = health;
                            lfHorse = nearbyHorse;
                            lf = "Healthiest";
                        }
                        break;
                    case 2:
                        if (jumpHeight > maxJump) {
                            maxJump = jumpHeight;
                            lfHorse = nearbyHorse;
                            lf = "Highest Jump";
                        }
                        break;
                }
            }

            if (lfHorse != null) {
                int x = Math.round((float) lfHorse.getX());
                int y = Math.round((float) lfHorse.getY());
                int z = Math.round((float) lfHorse.getZ());
                double speed = lfHorse.getAttributeValue(EntityAttributes.GENERIC_MOVEMENT_SPEED) * 42.16;
                double jump = lfHorse.getAttributeValue(EntityAttributes.HORSE_JUMP_STRENGTH);
                double hp = lfHorse.getAttributeValue(EntityAttributes.GENERIC_MAX_HEALTH);
                jump = -0.1817584952 * Math.pow(jump, 3) + 3.689713992 * Math.pow(jump, 2) + 2.128599134 * jump - 0.343930367;

                String message = String.format("Found %s Horse at : %d , %d , %d : Movement Speed: %.2f m/s , Jump Height: %.2f, Health : %d", lf , x, y, z, speed, jump, (int)hp);

                ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();
                executorService.schedule(() -> player.sendMessage(Text.literal(message), false), 100, TimeUnit.MILLISECONDS);
            }
        }
    }
}