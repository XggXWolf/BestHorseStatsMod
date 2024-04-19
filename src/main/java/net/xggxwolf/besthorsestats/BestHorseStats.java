package net.xggxwolf.besthorsestats;
import java.util.List;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientEntityEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.passive.HorseEntity;
import net.fabricmc.fabric.api.event.player.UseEntityCallback;
import net.minecraft.util.ActionResult;
import net.minecraft.text.Text;
import java.util.concurrent.*;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BestHorseStats implements ModInitializer {
	public static final String MOD_ID = "besthorsestats";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {
		MinecraftClient minecraftClient = MinecraftClient.getInstance();

		UseEntityCallback.EVENT.register((player, world, hand, entity, hitResult) -> {
			if (world.isClient && entity instanceof HorseEntity) {
				// The player has right-clicked on a horse
				int simulationDistance = minecraftClient.options.getSimulationDistance().getValue();

				double viewDistance = simulationDistance * 16.0;
				List<HorseEntity> entities = world.getEntitiesByClass(HorseEntity.class,
						player.getBoundingBox().expand(viewDistance),
						(nearbyEntity) -> true);

				HorseEntity clicked = (HorseEntity) entity;
				HorseEntity fastestHorse = null;
				double maxSpeed = 0;

				double jumpHeight = 0;
				double health = 0;

				for (HorseEntity nearbyHorse : entities) {
					double movementSpeed = nearbyHorse.getAttributeValue(EntityAttributes.GENERIC_MOVEMENT_SPEED);
					 jumpHeight = nearbyHorse.getAttributeValue(EntityAttributes.HORSE_JUMP_STRENGTH);
					 health = nearbyHorse.getAttributeValue(EntityAttributes.GENERIC_MAX_HEALTH);

					if (movementSpeed > maxSpeed){
						maxSpeed = movementSpeed;
						fastestHorse = nearbyHorse;
					};


				}
				if(fastestHorse != null){
					int x = Math.round((float)fastestHorse.getX());
					int y = Math.round((float)fastestHorse.getY());
					int z = Math.round((float)fastestHorse.getZ());
					double speed = fastestHorse.getAttributeValue(EntityAttributes.GENERIC_MOVEMENT_SPEED) * 42.16;
					jumpHeight = (
							- 0.1817584952 * Math.pow(jumpHeight, 3) +
									3.689713992 * Math.pow(jumpHeight, 2) +
									2.128599134 * jumpHeight - 0.343930367
					); // convert to blocks

					String message = String.format("Horse %d , %d , %d Movement Speed: %.2f Blocks/sec, Jump Height: %.2f, Max Health : %.2f", x, y, z, speed, jumpHeight, health);

					ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();
					executorService.schedule(() -> player.sendMessage(Text.of(message), true), 100, TimeUnit.MILLISECONDS);
				}


			}
			return ActionResult.PASS;
		});


	}

}