package com.skcraft.playblock.projector;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;

import org.lwjgl.opengl.GL11;

import com.skcraft.playblock.player.MediaManager;
import com.skcraft.playblock.player.MediaPlayerClient;
import com.skcraft.playblock.player.MediaRenderer;
import com.skcraft.playblock.player.RendererState;
import com.skcraft.playblock.util.DrawUtils;
import com.skcraft.playblock.util.MathUtils;

/**
 * Renders the screen for the projector blocks.
 */
public class RenderProjector extends TileEntitySpecialRenderer {

    private static final float TEXT_SCALE = 0.045f;
    private static final int LINE_SPACING = 2;
    private static final float LOGO_SCALE = 0.4f;
    private static final float LOGO_DURATION = 3000;
    private static final int LOGO_WIDTH = 512;
    private static final int LOGO_HEIGHT = 128;
    private static final float SPINNER_SCALE = 0.1f;
    private static final float SPINNER_ANGLE = 30f;
    private static final float STATUS_LOGO_SCALE = 0.5f;

    private final MediaManager mediaManager;

    /**
     * Construct a new instance.
     * 
     * @param mediaManager
     *            the media manager.
     */
    public RenderProjector(MediaManager mediaManager) {
        this.mediaManager = mediaManager;
    }

    @Override
    public void renderTileEntityAt(TileEntity tileEntity, double x, double y, double z, float direction) {

        TileEntityProjector projector = ((TileEntityProjector) tileEntity);
        MediaPlayerClient mediaPlayer = (MediaPlayerClient) projector.getMediaPlayer();

        float width = mediaPlayer.getWidth();
        float height = mediaPlayer.getHeight();
        int metadata = tileEntity.getBlockMetadata();
        float rot;

        // Calculate the rotation
        switch (metadata) {
        case 0:
            rot = 180;
            break;
        case 2:
            rot = 0;
            break;
        default:
            rot = metadata * 90;
        }

        GL11.glPushMatrix();
        { // TODO: Consider using FBOs?
            GL11.glTranslatef((float) x + 0.5f, (float) y + 0.5f, (float) z + 0.5f);
            GL11.glRotatef(rot, 0, 1, 0);
            GL11.glTranslatef((float) (width / 2.0), (float) (height / 2.0), -0.51f);
            GL11.glScalef(-1, -1, 1);
            GL11.glDisable(GL11.GL_LIGHTING);
            drawScreen(projector, width, height);
            GL11.glRotatef(180, 0, 1, 0);
            // TODO: draw an actual texture
            DrawUtils.drawRect(0, 0, -width, height, 0xff333333);
            GL11.glEnable(GL11.GL_LIGHTING);
        }
        GL11.glPopMatrix();
    }

    /**
     * Draw the video screen, which may include error messages.
     * 
     * @param projector
     *            the projector
     * @param width
     *            the width
     * @param height
     *            the height
     */
    private void drawScreen(TileEntityProjector projector, float width, float height) {
        if (!mediaManager.isSupported()) {
            if (projector.inRange()) {
                drawPlayBlockStatus(width, height);
            } else {
                DrawUtils.drawRect(0, 0, width, height, 0xff000000);
            }
        } else {
            MediaPlayerClient mediaPlayer = ((MediaPlayerClient) projector.getMediaPlayer());
            MediaRenderer renderer = mediaPlayer.getRenderer();

            if (renderer != null) {
                renderer.drawMedia(0, 0, width, height);
                RendererState status = renderer.getState();

                if (!mediaPlayer.hasSomethingToPlay()) {
                    drawTextBox(0, 0, width, height, true, 0xffff0000, "No video set!");
                } else if (status == RendererState.INITIALIZING) {
                    double t = System.currentTimeMillis() - renderer.getCreationTime();
                    if (drawLogo(t, width, height)) {
                        drawSpinner(t, width, height);
                    }
                } else if (status == RendererState.BUFFERING) {
                    double t = System.currentTimeMillis() - renderer.getCreationTime();
                    if (drawLogo(t, width, height)) {
                        drawSpinner(t, width, height);
                    }
                } else if (status == RendererState.ERROR) {
                    drawTextBox(0, 0, width, height, true, 0xffff0000, "Couldn't play video;", "maybe bad URL?");
                } else if (status == RendererState.PAUSED) {
                    drawTextBox(0, 0, width, height, true, 0xffff0000, "Paused.");
                } else if (status == RendererState.STOPPED) {
                    drawTextBox(0, 0, width, height, true, 0xffff0000, "Stopped.");
                }
            } else {
                DrawUtils.drawRect(0, 0, width, height, 0xff000000);
            }
        }
    }

    /**
     * Draw the screen indicating the status of the PlayBlock installation.
     * 
     * @param width
     *            the width of the screen
     * @param height
     *            the height of the screen
     */
    private void drawPlayBlockStatus(float width, float height) {
        DrawUtils.drawRect(0, 0, width, height, 0xff717171);

        GL11.glPushMatrix();
        {
            GL11.glEnable(GL11.GL_BLEND);
            GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

            GL11.glTranslatef(0, 0, -0.01f);
            GL11.glColor4f(1, 1, 1, 1);

            bindTexture(new ResourceLocation("playblock:textures/misc/screen_logo.png"));

            float x = width * 0.04f;
            float y = height * 0.04f;
            float logoWidth = width * STATUS_LOGO_SCALE;
            float logoHeight = width * STATUS_LOGO_SCALE * (LOGO_HEIGHT / (float) LOGO_WIDTH);

            DrawUtils.drawTexture(x, y, logoWidth, logoHeight);
            drawTextBox(x, logoHeight + y * 2, width - x, height - logoHeight - y * 2, false, 0xff333333, mediaManager.getUnsupportedMessage());

            GL11.glDisable(GL11.GL_BLEND);
        }
        GL11.glPopMatrix();
    }

    /**
     * Draw the logo animation.
     * 
     * @param t
     *            the time offset
     * @param width
     *            the width of the entire screen
     * @param height
     *            the height of the entire screen
     * @return true if the logo has finished animating
     */
    private boolean drawLogo(double t, float width, float height) {
        if (t > LOGO_DURATION) {
            return true;
        }

        float alpha = (float) (MathUtils.easeInQuad(t, 0, 1, LOGO_DURATION * 0.4) - // Entrance
        MathUtils.easeInOutCubic(t - LOGO_DURATION * 0.6, 0, 1, LOGO_DURATION * 0.4)); // Exit

        GL11.glPushMatrix();
        {
            GL11.glEnable(GL11.GL_BLEND);
            GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

            GL11.glTranslatef(0, 0, -0.01f);
            GL11.glColor4f(1, 1, 1, alpha);

            bindTexture(new ResourceLocation("playblock:textures/misc/screen_logo.png"));

            float x = width / 2.0f;
            float y = height / 2.0f;

            if (width >= height) {
                DrawUtils.drawCenteredTexture(x, y, width * LOGO_SCALE, width * LOGO_SCALE * (LOGO_HEIGHT / (float) LOGO_WIDTH));
            } else {
                DrawUtils.drawCenteredTexture(x, y, height * LOGO_SCALE * (LOGO_WIDTH / (float) LOGO_HEIGHT), height * LOGO_SCALE);
            }

            GL11.glDisable(GL11.GL_BLEND);
        }
        GL11.glPopMatrix();

        return false;
    }

    /**
     * Draw the spinner animation.
     * 
     * @param t
     *            the time offset
     * @param width
     *            the width of the entire screen
     * @param height
     *            the height of the entire screen
     */
    private void drawSpinner(double t, float width, float height) {

        float x = width / 2.0f;
        float y = height / 2.0f;

        GL11.glPushMatrix();
        {
            // May want to convert this to OpenGL drawing calls rather than a
            // texture if we stick with this spinner

            GL11.glEnable(GL11.GL_BLEND);
            GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

            float angle = (int) ((t / 7 / (360 / SPINNER_ANGLE)) % (360 / SPINNER_ANGLE)) * -SPINNER_ANGLE;

            GL11.glTranslatef(x, y, -0.01f);
            GL11.glRotatef(angle, 0, 0, -0.01f);
            GL11.glColor4f(1, 1, 1, 1);

            bindTexture(new ResourceLocation("playblock:textures/misc/spinner.png"));
            if (width >= height) {
                DrawUtils.drawCenteredTexture(0, 0, width * SPINNER_SCALE, width * SPINNER_SCALE);
            } else {
                DrawUtils.drawCenteredTexture(0, 0, height * SPINNER_SCALE, height * SPINNER_SCALE);
            }

            GL11.glDisable(GL11.GL_BLEND);
        }
        GL11.glPopMatrix();
    }

    /**
     * Draws a textbox at a location.
     * 
     * @param x0
     *            top left x
     * @param y0
     *            top left y
     * @param x1
     *            bottom right X
     * @param y1
     *            bottom right Y
     * @param centered
     *            true if centered
     * @param bgColor
     *            background color
     * @param lines
     *            text lines
     */
    private void drawTextBox(float x0, float y0, float x1, float y1, boolean centered, int bgColor, String... lines) {

        float width = x1 - x0;
        float height = y1 - y0;
        float[] textWidths = new float[lines.length];
        float largestTextWidth = 0;
        int textHeight = getFontRenderer().FONT_HEIGHT;
        float totalHeight = lines.length * (getFontRenderer().FONT_HEIGHT + LINE_SPACING);

        // Get the width of every line
        for (int i = 0; i < lines.length; i++) {
            textWidths[i] = getFontRenderer().getStringWidth(lines[i]);
            if (textWidths[i] > largestTextWidth) {
                largestTextWidth = textWidths[i];
            }
        }

        float scale = TEXT_SCALE; // Start with the default scale
        if (scale * largestTextWidth > width) {
            scale = width / largestTextWidth; // Scale it down
        }

        // Center?
        if (centered) {
            x0 += (float) ((width - largestTextWidth * scale) / 2.0);
            y0 += (float) ((height - totalHeight * scale) / 2.0);
        }

        GL11.glPushMatrix();
        {
            GL11.glTranslatef(x0, y0, -0.01f);
            GL11.glScalef(scale, scale, 1f);

            for (int i = 0; i < lines.length; i++) {
                float a = i * (textHeight + LINE_SPACING);
                DrawUtils.drawRect(0, a, textWidths[i], textHeight + a, bgColor);
            }
        }
        GL11.glPopMatrix();

        GL11.glPushMatrix();
        {
            GL11.glTranslatef(x0, y0, -0.02f);
            GL11.glScalef(scale, scale, 1f);

            for (int i = 0; i < lines.length; i++) {
                getFontRenderer().drawString(lines[i], 0, i * (textHeight + LINE_SPACING), 0xffffffff, false);
            }
        }
        GL11.glPopMatrix();
    }

    private FontRenderer getFontRenderer() {
        return Minecraft.getMinecraft().fontRenderer;
    }

}