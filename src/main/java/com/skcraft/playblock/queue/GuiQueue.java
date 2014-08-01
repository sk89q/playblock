package com.skcraft.playblock.queue;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.util.ResourceLocation;

import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;

import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.skcraft.playblock.media.MediaResolver;
import com.skcraft.playblock.network.EnqueueResponse;
import com.skcraft.playblock.projector.GuiProjectorQueueSlot;
import com.skcraft.playblock.util.GuiScrollbar;
import com.skcraft.playblock.util.StringUtils;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

/**
 * The GUI for the media queue.
 */
@SideOnly(Side.CLIENT)
public class GuiQueue extends GuiScreen {

    private static final int xSize = 247;
    private static final int ySize = 165;

    private GuiButton addButton;
    private GuiButton clearUriButton;
    private GuiButton removeButton;
    private GuiButton clearButton;
    private GuiTextField uriField;
    private GuiScrollbar scrollbar;

    private final ExposedQueue queuable;
    private final List<GuiProjectorQueueSlot> slots = new ArrayList<GuiProjectorQueueSlot>();
    private GuiProjectorQueueSlot selectedSlot;
    private String uri;

    public GuiQueue(ExposedQueue queuable) {
        this.queuable = queuable;
    }

    /**
     * Adds the buttons (and other controls) to the screen in question.
     */
    @Override
    public void initGui() {
        this.buttonList.clear();
        Keyboard.enableRepeatEvents(true);
        int left = (width - xSize) / 2;
        int top = (height - ySize) / 2;

        buttonList.add(addButton = new GuiButton(0, left + 215, top + 14, 25, 20, StringUtils.translate("gui.add")));
        buttonList.add(clearUriButton = new GuiButton(1, left + 195, top + 14, 17, 20, "X"));
        buttonList.add(removeButton = new GuiButton(2, left + 4, top + 130, 42, 20, StringUtils.translate("gui.remove")));
        buttonList.add(clearButton = new GuiButton(3, left + 4, top + 100, 42, 20, StringUtils.translate("gui.clear")));
        removeButton.enabled = false;

        uriField = new GuiTextField(fontRendererObj, left + 35, top + 17, 157, fontRendererObj.FONT_HEIGHT + 5);
        uriField.setVisible(true);
        uriField.setMaxStringLength(100);
        uriField.setEnableBackgroundDrawing(true);
        uriField.setCanLoseFocus(true);
        uriField.setFocused(false);

        scrollbar = new GuiScrollbar(mc, left + 199, top + 54, 99, 0, ySize + 1, 5, 32, "playblock:textures/gui/queue_bg.png");
    }

    /**
     * Draws the screen and all the components in it.
     */
    @Override
    public void drawScreen(int mouseX, int mouseY, float par3) {
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        mc.renderEngine.bindTexture(new ResourceLocation("playblock:textures/gui/queue_bg.png"));
        int left = (width - xSize) / 2;
        int top = (height - ySize) / 2;
        drawTexturedModalRect(left, top, 0, 0, xSize, ySize);

        uriField.drawTextBox();
        fontRendererObj.drawString(StringUtils.translate("options.url"), left + 10, top + 20, 0xff999999);

        if (!scrollbar.isEnabled() && slots.size() / 7.0 > 1) {
            scrollbar.setEnabled(true);
        }
        else if(scrollbar.isEnabled() && slots.size() / 7.0 <= 1) {
            scrollbar.setEnabled(false);
        }

        scrollbar.drawScrollbar(mouseX, mouseY);
        renderQueue(left, top);
        super.drawScreen(mouseX, mouseY, par3);
    }

    @Override
    public void actionPerformed(GuiButton button) {
        if (button.id == addButton.id) {
            String name = uriField.getText(); // Should be media.getTitle()
            if (fontRendererObj.getStringWidth(name) > 134) {
                name = fontRendererObj.trimStringToWidth(name, 128).concat("...");
            }

            createSlot(name);
            submitEnqueue(uriField.getText());
            uriField.setText("");
            uriField.setFocused(true);
        } else if (button.id == removeButton.id) {
            if (selectedSlot != null) {
                slots.remove(selectedSlot);
                removeButton.enabled = false;
                // We need to send this update to the server
            }
        } else if (button.id == clearUriButton.id) {
            uriField.setText("");
            uriField.setFocused(true);
            uri = uriField.getText();
        } else if (button.id == clearButton.id) {
            slots.clear();
            scrollbar.setCurrentScroll(0);
            removeButton.enabled = false;
        }
    }

    @Override
    protected void keyTyped(char key, int par2) {
        super.keyTyped(key, par2);
        if (uriField.isFocused()) {
            uriField.textboxKeyTyped(key, par2);
            uri = uriField.getText();

            if (MediaResolver.canPlayUri(MediaResolver.cleanUri(uri))) {
                uriField.setTextColor(14737632);
            } else {
                uriField.setTextColor(0xffff0000);
            }
        }
    }

    @Override
    protected void mouseClicked(int x, int y, int buttonClicked) {
        super.mouseClicked(x, y, buttonClicked);

        int unseenSlots = slots.size() - 7;
        int startIndex = (int) (scrollbar.getCurrentScroll() * unseenSlots + 0.5);
        if (!slots.isEmpty()) {
            for (int i = startIndex; i < startIndex + 7; i++) {
                if (i < slots.size()) {
                    slots.get(i).mouseClicked(x, y, buttonClicked);
                }
            }
        }

        uriField.mouseClicked(x, y, buttonClicked);
    }

    @Override
    public void handleMouseInput() {
        super.handleMouseInput();
        scrollbar.handleMouseInput(slots.size(), 7);
    }

    /**
     * Creates a slot with the given name.
     * 
     * @param name
     *            the name
     */
    public void createSlot(String name) {
        slots.add(new GuiProjectorQueueSlot(this, mc, name));
    }

    /**
     * Sets the selected slot.
     * 
     * @param slot
     *            the slot
     */
    public void setSelectedSlot(GuiProjectorQueueSlot slot) {
        for (GuiProjectorQueueSlot queueSlot : slots) {
            if (queueSlot != slot) {
                queueSlot.setSelected(false);
            } else {
                queueSlot.setSelected(true);
            }
        }
        removeButton.enabled = true;
        selectedSlot = slot;
    }

    /**
     * Renders the media queue.
     * 
     * @param left
     * @param top
     */
    private void renderQueue(int left, int top) {
        if (slots.isEmpty()) {
            return;
        }

        int unseenSlots = Math.max(slots.size() - 7, 0);
        int startIndex = (int) (scrollbar.getCurrentScroll() * unseenSlots + 0.5);
        for (int i = startIndex; i < startIndex + 7; i++) {
            if (i < slots.size()) {
                slots.get(i).drawSlot(i - startIndex, left, top);
            }
        }
    }

    @Override
    public void onGuiClosed() {
        Keyboard.enableRepeatEvents(false);
    }

    @Override
    public boolean doesGuiPauseGame() {
        return false;
    }

    /**
     * Submit a URI for the queue.
     * 
     * @param uri
     *            the URI
     */
    protected void submitEnqueue(String uri) {
        ListenableFuture<EnqueueResponse> future = queuable.getQueueBehavior().sendEnqueueRequest(uri);

        // Called when we receive a response
        Futures.addCallback(future, new FutureCallback<EnqueueResponse>() {
            @Override
            public void onSuccess(EnqueueResponse result) {
            }

            @Override
            public void onFailure(Throwable t) {
            }
        });
    }
}
