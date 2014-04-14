package com.elmakers.mine.bukkit.block;

import com.elmakers.mine.bukkit.plugins.magic.Mage;

public class CleanupBlocksTask implements Runnable
{
	protected Mage mage;
	protected BlockList undoBlocks;

	public CleanupBlocksTask(Mage mage, BlockList cleanup)
	{
		this.undoBlocks = cleanup;
		this.mage = mage;
	}

	public void run()
	{
		undoBlocks.onScheduledCleanup(mage);
	}
}