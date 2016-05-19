package com.skcraft.playblock.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;

import org.lwjgl.input.Keyboard;

import com.skcraft.playblock.GuiHandler;
import com.skcraft.playblock.PlayBlock;
import com.skcraft.playblock.util.StringUtils;

import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent.KeyInputEvent;

/**
 * Handles key presses.
 */
public class KeyHandler {

    private static Minecraft mc = Minecraft.getMinecraft();
    private static KeyBinding keyOptions = new KeyBinding(StringUtils.translate("options"), Keyboard.KEY_F4, "PlayBlock");

    public KeyHandler() {
        ClientRegistry.registerKeyBinding(keyOptions);
    }

    @SubscribeEvent
    public void onKey(KeyInputEvent evt) {
        if (keyOptions.isPressed() && mc.thePlayer != null && mc.currentScreen == null) {
            mc.thePlayer.openGui(PlayBlock.instance, GuiHandler.OPTIONS, mc.theWorld, 0, 0, 0);
        }
    }

}
