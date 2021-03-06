package com.elmakers.mine.bukkit.protection;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

public class TownyManager {
	private boolean enabled = false;
	private TownyAPI towny = null;

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	public boolean isEnabled() {
		return enabled && towny != null;
	}

	public void initialize(Plugin plugin) {
		if (enabled) {
			try {
				Plugin townyPlugin = plugin.getServer().getPluginManager()
						.getPlugin("Towny");
				if (townyPlugin != null) {
					towny = new TownyAPI(townyPlugin);
				}
			} catch (Throwable ex) {
			}

			if (towny == null) {
				plugin.getLogger()
						.info("Towny not found, region protection and pvp checks will not be used.");
			} else {
				plugin.getLogger()
						.info("Towny found, will respect build permissions for construction spells");
			}
		} else {
			plugin.getLogger()
					.info("Towny manager disabled, region protection and pvp checks will not be used.");
			towny = null;
		}
	}

	public boolean isPVPAllowed(Location location) {
		if (!enabled || towny == null || location == null)
			return true;
		return towny.isPVPAllowed(location);
	}

	public boolean hasBuildPermission(Player player, Block block) {
		if (enabled && block != null && towny != null) {
			return towny.hasBuildPermission(player, block);
		}
		return true;
	}
}
