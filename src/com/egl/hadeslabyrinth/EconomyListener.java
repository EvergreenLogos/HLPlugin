package com.egl.hadeslabyrinth;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

import com.djrapitops.vaultevents.events.economy.BankDepositEvent;
import com.djrapitops.vaultevents.events.economy.BankWithdrawEvent;
import com.djrapitops.vaultevents.events.economy.PlayerDepositEvent;
import com.djrapitops.vaultevents.events.economy.PlayerWithdrawEvent;

public class EconomyListener implements Listener {

    HLPlugin plugin;

    public EconomyListener(HLPlugin plugin) {
	this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onPlayerDeposit(PlayerDepositEvent e) {
	String name = e.getOfflinePlayer().getName();
	plugin.pm.getPlayer(name).updateScoreboard();
	plugin.getLogger().info("PLayerDepositEvent");
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onPlayerWithdraw(PlayerWithdrawEvent e) {
	String name = e.getOfflinePlayer().getName();
	plugin.pm.getPlayer(name).updateScoreboard();
	plugin.getLogger().info("PLayerWithdrawEvent");
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onBankDeposit(BankDepositEvent e) {
	plugin.getLogger().info("BankDepositEvent");
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onBankWithdraw(BankWithdrawEvent e) {
	plugin.getLogger().info("BankWithdrawEvent");
    }

}
