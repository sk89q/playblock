package com.skcraft.playblock.projector;

import java.util.ArrayList;
import java.util.List;

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

import com.skcraft.playblock.LKey;
import com.skcraft.playblock.media.MediaResolver;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;

/**
 * The GUI for the media queue.
 */
@SideOnly(Side.CLIENT)
public class ProjectorQueueGui extends GuiScreen {

    private static final int xSize = 247;
    private static final int ySize = 165;

    private GuiButton addButton;
    private GuiButton clearUriButton;
    private GuiButton removeButton;
    private GuiButton clearButton;
    private GuiTextField uriField;

    private float currentScroll = 0;
    private boolean isScrolling = false;
    private boolean wasClicking = false;

    private List<ProjectorQueueSlot> slots = new ArrayList<ProjectorQueueSlot>();
    private ProjectorQueueSlot selectedSlot;
    private String uri;

    /**
     * Adds the buttons (and other controls) to the screen in question.
     */
    @Override
    public void initGui() {
        this.controlList.clear();
        Keyboard.enableRepeatEvents(true);
        int left = (width - xSize) / 2;
        int top = (height - ySize) / 2;

        controlList.add(addButton = new GuiButton(0, left + 215, top + 14, 25, 
                20, LKey.ADD.toString()));
        controlList.add(clearUriButton = new GuiButton(1, left + 195, top + 14,
                17, 20, "X"));
        controlList.add(removeButton = new GuiButton(2, left + 4, top + 130,
                42, 20, LKey.REMOVE.toString()));
        controlList.add(clearButton = new GuiButton(3, left + 4, top + 100,
                42, 20, LKey.CLEAR.toString()));
        removeButton.enabled = false;

        uriField = new GuiTextField(fontRenderer, left + 35, top + 17, 157,
                fontRenderer.FONT_HEIGHT + 5);
        uriField.setVisible(true);
        uriField.setMaxStringLength(100);
        uriField.setEnableBackgroundDrawing(true);
        uriField.setCanLoseFocus(true);
        uriField.setFocused(false);
    }

    /**
     * Draws the screen and all the components in it.
     */
    @Override
    public void drawScreen(int mouseX, int mouseY, float par3) {
        int texture = mc.renderEngine.getTexture("/playblock/gui/queue_bg.png");
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        mc.renderEngine.bindTexture(texture);
        int left = (width - xSize) / 2;
        int top = (height - ySize) / 2;
        drawTexturedModalRect(left, top, 0, 0, xSize, ySize);

        uriField.drawTextBox();
        fontRenderer.drawString(LKey.URL.toString(), left + 10, top + 20, 0xff999999);

        boolean mouseDown = Mouse.isButtonDown(0);
        int scrollLeft = left + 199;
        int scrollRight = left + 204;
        int scrollTop = top + 54;
        int scrollBottom = top + 153;
        if(!wasClicking && mouseDown && mouseX >= scrollLeft && 
                mouseX < scrollRight && mouseY >= scrollTop && 
                mouseY < scrollBottom) {
            isScrolling = needsScrollBar();
        }

        if(!mouseDown) {
            isScrolling = false;
        }
        wasClicking = mouseDown;

        if(isScrolling) {
            currentScroll = ((float)(mouseY - scrollTop) - 7.5F) 
                    / ((float)(scrollBottom - scrollTop) - 15);

            if(currentScroll < 0) {
                currentScroll = 0;
            }
            else if(currentScroll > 1) {
                currentScroll = 1;
            }
        }

        mc.renderEngine.bindTexture(texture);
        drawTexturedModalRect(left + 199, top + (int)((scrollBottom - scrollTop - 32)
                * currentScroll) + 54, 0, ySize + 1, 5, 32);
        renderQueue(left, top);
        super.drawScreen(mouseX, mouseY, par3);
    }

    @Override
    public void actionPerformed(GuiButton button) {
        if(button.id == addButton.id) {
            String name = uriField.getText(); //Should be media.getTitle()
            if(fontRenderer.getStringWidth(name) > 134) {
                name = fontRenderer.trimStringToWidth(name, 128).concat("...");
            }
            
            createSlot(name);
            uriField.setText("");
            uriField.setFocused(true);
            //We need to send this update to the server
        }
        else if(button.id == removeButton.id) {
            if(selectedSlot != null) {
                slots.remove(selectedSlot);
                removeButton.enabled = false;
                //We need to send this update to the server
            }
        }
        else if(button.id == clearUriButton.id) {
            uriField.setText("");
            uriField.setFocused(true);
            uri = uriField.getText();
        }
        else if(button.id == clearButton.id) {
            slots.clear();
            currentScroll = 0;
            removeButton.enabled = false;
        }
    }

    @Override
    protected void keyTyped(char key, int par2) {
        super.keyTyped(key, par2);
        if(uriField.isFocused()) {
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
        int startIndex = (int)(currentScroll * unseenSlots + 0.5);
        if(!slots.isEmpty()) {
            for(int i = startIndex; i < startIndex + 7; i++) {
                if(i < slots.size()) {
                    slots.get(i).mouseClicked(x, y, buttonClicked);
                }
            }
        }

        uriField.mouseClicked(x, y, buttonClicked);
    }

    @Override
    public void handleMouseInput() {
        super.handleMouseInput();
        int wheelDelta = Mouse.getEventDWheel();

        if(wheelDelta != 0 && needsScrollBar()) {
            int unseenSlots = slots.size() - 7;

            if(wheelDelta < 0)
                wheelDelta = -1;
            else if(wheelDelta > 0)
                wheelDelta = 1;

            currentScroll -= (float)wheelDelta / (float)unseenSlots;

            if(currentScroll < 0) {
                currentScroll = 0;
            }
            else if(currentScroll > 1) {
                currentScroll = 1;
            }
        }
    }
    
    /**
     * Creates a slot with the given name.
     * 
     * @param name the name
     */
    public void createSlot(String name) {
        slots.add(new ProjectorQueueSlot(this, mc, name));
    }
    
    /**
     * Sets the selected slot.
     * 
     * @param slot the slot
     */
    public void setSelectedSlot(ProjectorQueueSlot slot) {
        for(ProjectorQueueSlot queueSlot : slots) {
            if(queueSlot != slot) {
                queueSlot.setSelected(false);
            }
            else {
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
        if(slots.isEmpty()) {
            return;
        }

        int unseenSlots = slots.size() - 7;
        int startIndex = (int)(currentScroll * unseenSlots + 0.5);
        for(int i = startIndex; i < startIndex + 7; i++) {
            if(i < slots.size()) {
                slots.get(i).drawSlot(i - startIndex, left, top);
            }
        }
    }
    
    /**
     * Determines if a scroll bar is needed.
     * 
     * @return whether or not the gui needs a scroll bar
     */
    private boolean needsScrollBar() {
        return slots.size() / 7.0 > 1;
    }

    @Override
    public void onGuiClosed() {
        Keyboard.enableRepeatEvents(false);
    }

    @Override
    public boolean doesGuiPauseGame() {
        return false;
    }
}
