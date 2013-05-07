package com.skcraft.playblock.client;

import java.awt.Desktop;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;

import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;

import com.skcraft.playblock.LKey;
import com.skcraft.playblock.PlayBlock;
import com.skcraft.playblock.player.MediaManager;
import com.skcraft.playblock.player.MediaResolver;
import com.skcraft.playblock.player.ProjectorTileEntity;
import com.skcraft.playblock.util.GuiSlider;

import cpw.mods.fml.common.network.PacketDispatcher;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

/**
 * The options GUI.
 */
@SideOnly(Side.CLIENT)
public class OptionsGui extends GuiScreen {

    public static final int ID = 0;
    private static final int xSize = 195;
    private static final int ySize = 214;
    
    private final MediaManager mediaManager;
    
    private GuiSlider volumeSlider;
    private GuiButton openUrlButton;
    private GuiButton closeButton;

    public OptionsGui(MediaManager mediaManager) {
        this.mediaManager = mediaManager;
    }

    /**
     * Adds the buttons (and other controls) to the screen in question.
     */
    @Override
    public void initGui() {
        this.controlList.clear();
        Keyboard.enableRepeatEvents(true);
        
        int controlIndex = 0;
        int left = (width - xSize) / 2;
        int top = (height - ySize) / 2;
        
        int twoButtonWidth = (xSize - 24) / 2;

        this.controlList.add(closeButton = new GuiButton(controlIndex++,
                left + 10 + twoButtonWidth + 7, top + 170, twoButtonWidth, 20,
                LKey.DONE.toString()));

        this.controlList.add(openUrlButton = new GuiButton(controlIndex++,
                left + 10, top + 170, twoButtonWidth, 20,
                LKey.WEBSITE.toString()));

        this.controlList.add(volumeSlider = new VolumeSlider(controlIndex++,
                left + 10, top + 50, xSize - 20, 10, mediaManager));
    }

    @Override
    public void onGuiClosed() {
        Keyboard.enableRepeatEvents(false);
    }

    @Override
    public void actionPerformed(GuiButton button) {
        if (button.id == closeButton.id) {
            this.mc.displayGuiScreen((GuiScreen) null);
            this.mc.setIngameFocus();
            PlayBlock.getClientRuntime().getClientOptions().setFloat("volume",
                    volumeSlider.getValue() * 2);
        } else if (button.id == openUrlButton.id) {
            try {
                Desktop.getDesktop().browse(new URI("http://skcraft.com"));
            } catch (Throwable e) {
                this.mc.thePlayer.sendChatMessage(
                        "For more information about PlayBlock, see http://skcraft.com");
            }
            PlayBlock.getClientRuntime().getClientOptions().setFloat("volume",
                    volumeSlider.getValue() * 2);
            this.mc.displayGuiScreen((GuiScreen) null);
            this.mc.setIngameFocus();
        }
    }

    /**
     * Draws the screen and all the components in it.
     */
    @Override
    public void drawScreen(int mouseX, int mouseY, float par3) {
        int texture = mc.renderEngine.getTexture("/playblock/gui/options_bg.png");
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        mc.renderEngine.bindTexture(texture);
        int left = (width - xSize) / 2;
        int top = (height - ySize) / 2;
        drawTexturedModalRect(left, top, 0, 0, xSize, ySize);

        fontRenderer.drawString("Volume:", left + 10, top + 50, 0xff999999);
        
        int textWidth = fontRenderer.getStringWidth(LKey.PLAYBLOCK_OPTIONS.toString());
        fontRenderer.drawString(LKey.PLAYBLOCK_OPTIONS.toString(), 
                left + (xSize - textWidth) / 2, top + 20, 0xffffffff);

        super.drawScreen(mouseX, mouseY, par3);
    }

    @Override
    public boolean doesGuiPauseGame() {
        return false;
    }
}
