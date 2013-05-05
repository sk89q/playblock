package com.skcraft.playblock.client;

import java.util.EnumSet;

import com.skcraft.playblock.LKey;
import com.skcraft.playblock.PlayBlock;

import net.minecraft.client.settings.KeyBinding;
import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.client.registry.KeyBindingRegistry.KeyHandler;
import cpw.mods.fml.common.TickType;

/**
 * Handles key presses.
 */
public class GlobalKeyHandler extends KeyHandler {

	private EnumSet<TickType> ticks = EnumSet.of(TickType.CLIENT);

	public GlobalKeyHandler(KeyBinding[] keyBindings, boolean[] repeatings) {
		super(keyBindings, repeatings);
	}

	@Override
	public String getLabel() {
		return LKey.PLAYBLOCK_OPTIONS.toString();
	}

	@Override
	public EnumSet<TickType> ticks() {
		return ticks;
	}

	@Override
	public void keyUp(EnumSet<TickType> types, KeyBinding kb, boolean tickEnd) {
		if (tickEnd && FMLClientHandler.instance().getClient().currentScreen == null) {
		    OptionsGui gui = new OptionsGui(
		            PlayBlock.instance.getClientRuntime().getMediaManager());
		    
			FMLClientHandler.instance().getClient().displayGuiScreen(gui);
		}
	}

	@Override
	public void keyDown(EnumSet<TickType> types, KeyBinding kb,
	        boolean tickEnd, boolean isRepeat) {

	}

}
