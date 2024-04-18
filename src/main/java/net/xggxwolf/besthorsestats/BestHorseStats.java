package net.xggxwolf.besthorsestats;
import java.util.List;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientEntityEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.passive.HorseEntity;
import net.fabricmc.fabric.api.event.player.UseEntityCallback;
import net.minecraft.util.ActionResult;
import net.minecraft.text.Text;
import net.minecraft.util.hit.HitResult;
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



				for (HorseEntity nearbyHorse : entities) {
					double movementSpeed = nearbyHorse.getAttributeValue(EntityAttributes.GENERIC_MOVEMENT_SPEED);

					if (movementSpeed > maxSpeed){
						maxSpeed = movementSpeed;
						fastestHorse = nearbyHorse;
					};


				}
				if(fastestHorse != null){
					player.sendMessage(Text.of("Horse " + fastestHorse.getX() + " , " + fastestHorse.getY() + " , " + fastestHorse.getZ() + " Movement Speed: " + fastestHorse.getAttributeValue(EntityAttributes.GENERIC_MOVEMENT_SPEED) * 42.16 + "Blocks/sec"), false);

				}

			}
			return ActionResult.PASS;
		});


	}
}