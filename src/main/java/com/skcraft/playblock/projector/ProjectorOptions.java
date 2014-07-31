package com.skcraft.playblock.projector;

import java.io.DataInputStream;
import java.io.IOException;

import io.netty.buffer.ByteBufInputStream;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ChatComponentText;

import com.sk89q.forge.AbstractBehavior;
import com.sk89q.forge.BehaviorPayload;
import com.skcraft.playblock.network.BehaviorType;
import com.skcraft.playblock.network.ProjectorUpdate;
import com.skcraft.playblock.player.MediaPlayer;
import com.skcraft.playblock.util.AccessList;
import com.skcraft.playblock.util.DoubleThresholdRange;

public class ProjectorOptions extends AbstractBehavior {

    private final MediaPlayer mediaPlayer;
    private final DoubleThresholdRange range;
    private AccessList accessList = null;

    public ProjectorOptions(MediaPlayer mediaPlayer, DoubleThresholdRange range) {
        this.mediaPlayer = mediaPlayer;
        this.range = range;
    }

    public void useAccessList(boolean use) {
        if (use == true) {
            if (accessList == null) {
                accessList = new AccessList();
            }
        } else {
            accessList = null;
        }
    }

    public AccessList getAccessList() {
        return accessList;
    }

    @Override
    public void readPayload(EntityPlayer player, BehaviorPayload payload, ByteBufInputStream in) throws IOException {
        if (payload.isType(BehaviorType.UPDATE)) {
            if (getAccessList() == null || getAccessList().checkAndForget(player)) {
                ProjectorUpdate update = new ProjectorUpdate();
                update.read(in);

                mediaPlayer.setUri(update.getUri());
                mediaPlayer.setWidth(update.getWidth());
                mediaPlayer.setHeight(update.getHeight());
                range.setTriggerRange(update.getTriggerRange());
                range.setFadeRange(update.getFadeRange());

                // Update everyone
                NBTTagCompound tag = new NBTTagCompound();
                mediaPlayer.writeNetworkedNBT(tag);
                range.writeNetworkedNBT(tag);
                mediaPlayer.fireNetworkedNbt(tag);
            } else {
                player.addChatMessage(new ChatComponentText("Sorry, you don't have permission " + "to modify that projector."));
            }
        }
    }

    public void sendUpdate(String uri, float width, float height, float triggerRange, float fadeRange) {
        ProjectorUpdate update = new ProjectorUpdate();
        update.setUri(uri);
        update.setWidth(width);
        update.setHeight(height);
        update.setTriggerRange(triggerRange);
        update.setFadeRange(fadeRange);
        firePayloadSend(new BehaviorPayload(BehaviorType.UPDATE, update), null);
    }

}