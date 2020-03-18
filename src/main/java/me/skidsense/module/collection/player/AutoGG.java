
package me.skidsense.module.collection.player;

import me.skidsense.Client;
import me.skidsense.hooks.Sub;
import me.skidsense.hooks.events.EventPacketRecieve;
import me.skidsense.module.Mod;
import me.skidsense.module.ModuleType;
import java.awt.Color;

import me.skidsense.util.ChatUtil;
import net.minecraft.network.play.server.S45PacketTitle;
import net.minecraft.util.IChatComponent;

public class AutoGG extends Mod {

    public AutoGG() {
        super("Auto GG", new String[]{"AutoGG"}, ModuleType.Player);
        this.setColor(new Color(191, 191, 191).getRGB());
    }

    @Sub
    private void onGGPacket(EventPacketRecieve e) {
        if (e.getPacket() instanceof S45PacketTitle) {
            IChatComponent i = ((S45PacketTitle) e.getPacket()).getMessage();
            if(i != null){
                String text = i.getUnformattedText();
                Client.sendMessageWithoutPrefix(text);
                if (text.contains("VICTORY") || text.contains("胜利") || text.contains("勝利") || text.contains("ПОБЕДА")) {
                    ChatUtil.sendChat_NoFilter("/achat gg");
                }
            }

        }
    }
}

