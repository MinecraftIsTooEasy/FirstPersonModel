package vbonedra.first_person_model.register;

import vbonedra.first_person_model.FirstPersonModelMod;
import huix.glacier.api.entrypoint.IGameRegistry;
import huix.glacier.api.registry.MinecraftRegistry;

public class FirstPersonModelRegistryInit implements IGameRegistry {
	// Registrar instance, using this mod's first_person_model as the namespace
	public static final MinecraftRegistry registry = new MinecraftRegistry(FirstPersonModelMod.MOD_ID).initAutoItemRegister();

	// Create an instance of the item
	// public static Item EXAMPLE_ITEM;

	@Override
	public void onGameRegistry() {
		// Register items, bind texture and create localized key
		// registry.registerItem(FirstPersonModelMod.MOD_ID + ":example_item", "exampleItem", EXAMPLE_ITEM);
	}
}
