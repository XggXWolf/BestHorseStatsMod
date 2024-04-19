package net.xggxwolf.besthorsestats;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.event.player.UseEntityCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.passive.HorseEntity;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.xggxwolf.besthorsestats.utils.FindBestHorse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class BestHorseStats implements ClientModInitializer {
    public static final String MOD_ID = "besthorsestats";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    @Override
    public void onInitializeClient() {
        new FindBestHorse().register();
        MinecraftClient minecraftClient = MinecraftClient.getInstance();
        UseEntityCallback.EVENT.register((player, world, hand, entity, hitResult) -> {
            if (world.isClient && entity instanceof HorseEntity clicked) {
                double maxSpeed = clicked.getAttributeValue(EntityAttributes.GENERIC_MOVEMENT_SPEED) * 42.16;
                double jumpStrength = clicked.getAttributeValue(EntityAttributes.HORSE_JUMP_STRENGTH);
                double health = clicked.getAttributeValue(EntityAttributes.GENERIC_MAX_HEALTH);
                double jumpHeight = -0.1817584952 * Math.pow(jumpStrength, 3) + 3.689713992 * Math.pow(jumpStrength, 2) + 2.128599134 * jumpStrength - 0.343930367;

                String message = String.format("Movement Speed: %.2f Blocks/sec, Jump Height: %.2f Blocks, Max Health : %.2f", maxSpeed, jumpHeight, health);

                ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();
                executorService.schedule(() -> player.sendMessage(Text.literal(message), true), 100, TimeUnit.MILLISECONDS);
            }
                return ActionResult.PASS;
        });

    }
}