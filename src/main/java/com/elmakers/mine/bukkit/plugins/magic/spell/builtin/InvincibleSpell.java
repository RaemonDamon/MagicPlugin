package com.elmakers.mine.bukkit.plugins.magic.spell.builtin;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.event.entity.EntityDamageEvent;

import com.elmakers.mine.bukkit.api.spell.SpellResult;
import com.elmakers.mine.bukkit.plugins.magic.spell.SpellEventType;
import com.elmakers.mine.bukkit.plugins.magic.spell.TargetingSpell;

public class InvincibleSpell extends TargetingSpell 
{
	protected float protectAmount = 0;
	protected int amount = 100;

	@Override
	public SpellResult onCast(ConfigurationSection parameters) 
	{
		amount = parameters.getInt("amount", amount);

		if (protectAmount != 0)
		{
			deactivate();
			return SpellResult.COST_FREE;
		}
		else
		{
			activate();
		}

		return SpellResult.CAST;
	}

	@Override
	public void onDeactivate()
	{
		controller.unregisterEvent(SpellEventType.PLAYER_DAMAGE, this); 
		protectAmount = 0;
	}
	
	@Override
	public void onActivate()
	{
		controller.registerEvent(SpellEventType.PLAYER_DAMAGE, this);
		protectAmount = (float)amount / 100;
	}
	
	@Override
	public void onPlayerDamage(EntityDamageEvent event)
	{
		if (protectAmount > 0)
		{
			if (protectAmount >= 1)
			{
				event.setCancelled(true);
			}
			else
			{
				int newDamage = (int)Math.floor((1.0f - protectAmount) * event.getDamage());
				if (newDamage == 0) newDamage = 1;
				event.setDamage(newDamage);
			}
		}
	}
}