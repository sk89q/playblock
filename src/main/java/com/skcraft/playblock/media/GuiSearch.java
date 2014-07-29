package com.skcraft.playblock.media;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.util.ResourceLocation;

import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;

import com.skcraft.playblock.util.GuiScrollbar;
import com.skcraft.playblock.util.StringUtils;

public class GuiSearch extends GuiScreen {

    private static final int xSize = 247;
    private static final int ySize = 165;
    private static final int RESULTS_PER_PAGE = 15;
    private static final int SHOWN_RESULTS = 3;

    private GuiButton searchButton;
    private GuiTextField queryField;
    private GuiScrollbar scrollbar;

    private final List<SearchResult> searchResults = new ArrayList<SearchResult>();
    private SearchResult selectedResult;
    private Media[] mediaResults;
    private boolean errorOccured = false;

    /**
     * Adds the buttons (and other controls) to the screen in question.
     */
    @Override
    public void initGui() {
        this.buttonList.clear();
        Keyboard.enableRepeatEvents(true);
        int left = (width - xSize) / 2;
        int top = (height - ySize) / 2;

        buttonList.add(searchButton = new GuiButton(0, left + 179, top + 14, 43, 20, StringUtils.translate("gui.search")));

        queryField = new GuiTextField(fontRendererObj, left + 38, top + 19, 150, fontRendererObj.FONT_HEIGHT + 5);
        queryField.setVisible(true);
        queryField.setMaxStringLength(100);
        queryField.setEnableBackgroundDrawing(true);
        queryField.setCanLoseFocus(true);
        queryField.setFocused(false);
        queryField.setEnableBackgroundDrawing(false);

        scrollbar = new GuiScrollbar(mc, left + 217, top + 44, 114, 0, ySize + 1, 8, 15, "/playblock/gui/search_bg.png");
    }

    /**
     * Draws the screen and all the components in it.
     */
    @Override
    public void drawScreen(int mouseX, int mouseY, float par3) {
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        mc.renderEngine.bindTexture(new ResourceLocation("playblock:gui/search_bg.png"));
        int left = (width - xSize) / 2;
        int top = (height - ySize) / 2;
        drawTexturedModalRect(left, top, 0, 0, xSize, ySize);

        queryField.drawTextBox();

        if (!scrollbar.isEnabled() && searchResults.size() / (float) SHOWN_RESULTS > 1) {
            scrollbar.setEnabled(true);
        } else if (scrollbar.isEnabled() && searchResults.size() / (float) SHOWN_RESULTS < 1) {
            scrollbar.setEnabled(false);
        }

        if (errorOccured) {
            fontRendererObj.drawString("An error has occured!", left + 70, top + 50, 14737632);
        }

        scrollbar.drawScrollbar(mouseX, mouseY);
        renderResults(left, top);
        super.drawScreen(mouseX, mouseY, par3);
    }

    @Override
    public void actionPerformed(GuiButton button) {
        if (button.id == searchButton.id) {
            search();
        }
    }

    @Override
    public void handleMouseInput() {
        super.handleMouseInput();
        scrollbar.handleMouseInput(searchResults.size(), SHOWN_RESULTS);
    }

    @Override
    protected void keyTyped(char key, int keycode) {
        super.keyTyped(key, keycode);
        if (queryField.isFocused()) {
            queryField.textboxKeyTyped(key, keycode);
            if (keycode == 28 || keycode == 156) {
                search();
            }
        }
    }

    @Override
    protected void mouseClicked(int x, int y, int buttonClicked) {
        super.mouseClicked(x, y, buttonClicked);

        int unseenSlots = searchResults.size() - SHOWN_RESULTS;
        int startIndex = (int) (scrollbar.getCurrentScroll() * unseenSlots + 0.5);
        if (!searchResults.isEmpty()) {
            for (int i = startIndex; i < startIndex + SHOWN_RESULTS; i++) {
                if (i < searchResults.size()) {
                    searchResults.get(i).mouseClicked(x, y, buttonClicked);
                }
            }
        }
        queryField.mouseClicked(x, y, buttonClicked);
    }

    public void setSelectedResult(SearchResult newSelected) {
        for (SearchResult result : searchResults) {
            if (result != newSelected) {
                result.setSelected(false);
            } else {
                result.setSelected(true);
            }
        }
        selectedResult = newSelected;
    }

    private void addResult(Media media) {
        searchResults.add(new SearchResult(this, media, mc));
    }

    private void renderResults(int left, int top) {
        if (searchResults.isEmpty()) {
            return;
        }

        int unseenSlots = searchResults.size() - SHOWN_RESULTS;
        int startIndex = (int) (scrollbar.getCurrentScroll() * unseenSlots + 0.5);
        for (int i = startIndex; i < startIndex + SHOWN_RESULTS; i++) {
            if (i < searchResults.size()) {
                searchResults.get(i).drawResult(i - startIndex, left, top);
            }
        }
    }

    private void search() {
        if (!queryField.getText().isEmpty()) {
            errorOccured = false;
            scrollbar.setCurrentScroll(0);
            searchResults.clear();
            YouTube yt = new YouTube();
            try {
                mediaResults = yt.search(queryField.getText(), 1, RESULTS_PER_PAGE);
                for (int i = 0; i < mediaResults.length; i++) {
                    addResult(mediaResults[i]);
                }
            } catch (IOException e) {
                errorOccured = true;
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
}
