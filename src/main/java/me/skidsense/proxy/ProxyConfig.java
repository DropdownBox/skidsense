/*
 * Decompiled with CFR 0_132.
 */
package me.skidsense.proxy;

import me.skidsense.Client;
import me.skidsense.hooks.EventHandler;
import me.skidsense.hooks.events.EventTick;
import me.skidsense.proxy.ConnectionInfo;
import me.skidsense.proxy.TransparentProxy;

import java.net.ServerSocket;
import java.util.Random;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiDisconnected;
import net.minecraft.client.gui.GuiMainMenu;
import net.minecraft.client.gui.GuiMultiplayer;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.IChatComponent;

public class ProxyConfig {
    public static int listenerPort = ProxyConfig.findFreePort(29999);
    public static TransparentProxy proxy = null;
    public static ConnectionInfo proxyAddr = null;

    public static ConnectionInfo connect(String ip, int port) {
        if (proxyAddr != null) {
            if (proxy == null) {
                proxy = new TransparentProxy(listenerPort);
            }
            if (proxy.isRunning()) {
                proxy.stop();
            }
            try {
                proxy.start(ProxyConfig.proxyAddr.ip, ProxyConfig.proxyAddr.port, ip, port);
                while (!proxy.isReady() && !proxy.hasFailed()) {
                    Thread.sleep(50L);
                }
                if (proxy.hasFailed()) {
                    throw proxy.getFailReason();
                }
                ConnectionInfo returnment = new ConnectionInfo();
                returnment.ip = "127.0.0.1";
                returnment.port = listenerPort;
                return returnment;
            }
            catch (Exception e) {
                e.printStackTrace();
                ConnectionInfo returnment = new ConnectionInfo();
                returnment.ip = ip;
                returnment.port = port;
                return returnment;
            }
        }
        ConnectionInfo returnment = new ConnectionInfo();
        returnment.ip = ip;
        returnment.port = port;
        return returnment;
    }

    @EventHandler
    public void onGlobalGameLoop(EventTick e) {
        if (proxy != null && proxy.isRunning() && proxy.hasFailed()) {
            boolean flag = Client.mc.isIntegratedServerRunning();
            boolean flag1 = Client.mc.isSingleplayer();
            Client.mc.theWorld.sendQuittingDisconnectingPacket();
            Client.mc.loadWorld(null);
            if (flag) {
                Client.mc.displayGuiScreen(new GuiDisconnected(new GuiMultiplayer(new GuiMainMenu()), "disconnect.closed", new ChatComponentText("[AC-Proxy] Lost Connection to server!")));
            }
        }
    }

    public static void stop() {
        if (proxy != null) {
            proxy.stop();
        }
    }

    private static int findFreePort(int start) {
        int cur = new Random().nextInt(10000) + start - 1;
        do {
            try {
                ServerSocket sock = new ServerSocket(cur, 1);
                sock.close();
                return cur;
            }
            catch (Exception var3) {
                cur = new Random().nextInt(10000) + start - 1;
                continue;
            }
        } while (true);
    }
}

