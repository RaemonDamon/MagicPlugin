package com.elmakers.mine.bukkit.action.builtin;

import com.elmakers.mine.bukkit.api.action.GeneralAction;
import com.elmakers.mine.bukkit.api.spell.SpellResult;
import com.elmakers.mine.bukkit.spell.BaseSpell;
import com.elmakers.mine.bukkit.spell.BaseSpellAction;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;

import java.util.Arrays;
import java.util.Collection;

public class TimeAction extends BaseSpellAction implements GeneralAction
{
	private String timeType = "day";

	@Override
	public SpellResult perform(ConfigurationSection parameters) {
		World world = getWorld();
		if (world == null) {
			return SpellResult.WORLD_REQUIRED;
		}

		long targetTime = 0;
		timeType = parameters.getString("time", "day");
		if (timeType.equalsIgnoreCase("toggle")) {
			long currentTime = world.getTime();
			if (currentTime > 13000) {
				timeType = "day";
			} else {
				timeType = "night";
			}
		}

		if (timeType.equalsIgnoreCase("night"))
		{
			targetTime = 13000;
		}
		else
		{
			try
			{
				targetTime = Long.parseLong(timeType);
				timeType = "raw(" + targetTime + ")";
			}
			catch (NumberFormatException ex)
			{
				targetTime = 0;
			}
		}
		if (parameters.getBoolean("cycle_moon_phase", false))
		{

			long currentTime = world.getFullTime();
			currentTime = ((currentTime % 24000) + 1) * 24000 + targetTime;
			world.setFullTime(currentTime);
			return SpellResult.CAST;
		}

		world.setTime(targetTime);
		return SpellResult.CAST;
	}
	
	@Override
	public String transformMessage(String message) {
		return message.replace("$time", timeType);
	}

	@Override
	public void getParameterNames(Collection<String> parameters) {
		super.getParameterNames(parameters);
		parameters.add("time");
	}

	@Override
	public void getParameterOptions(Collection<String> examples, String parameterKey) {
		if (parameterKey.equals("time")) {
			examples.add("day");
			examples.add("night");
			examples.add("toggle");
			examples.add("0");
			examples.add("130000");
		} else if (parameterKey.equals("cycle_moon_phase")) {
			examples.addAll(Arrays.asList((BaseSpell.EXAMPLE_BOOLEANS)));
		} else {
			super.getParameterOptions(examples, parameterKey);
		}
	}
}
