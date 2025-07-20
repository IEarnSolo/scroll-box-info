package com.scrollboxinfo;

import com.google.inject.Provides;
import javax.inject.Inject;

import com.scrollboxinfo.data.ClueCountStorage;
import com.scrollboxinfo.overlay.ClueWidgetItemOverlay;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.*;
import net.runelite.api.events.GameTick;
import net.runelite.api.widgets.Widget;
import net.runelite.api.widgets.WidgetInfo;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.events.ConfigChanged;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.ui.overlay.OverlayManager;
import net.runelite.api.events.ItemContainerChanged;
import net.runelite.api.InventoryID;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@PluginDescriptor(
	name = "Scroll Box Info",
	description = "Keep track of how many clues you have, your current clue stack limit, and how many clues until next stack limit unlock",
	tags = {"scroll", "watson", "case"}
)
public class ScrollBoxInfoPlugin extends Plugin
{
	@Inject
	private Client client;
	@Inject
	private ScrollBoxInfoConfig config;
	@Inject
	private QuestChecker questChecker;
	@Inject
	private ClueCountStorage clueCountStorage;
	@Inject
	private ClueCounter clueCounter;
	@Inject
	private ClueWidgetItemOverlay clueWidgetItemOverlay;
	@Inject
	private OverlayManager overlayManager;
	@Inject
	private ConfigManager configManager;

	private boolean bankWasOpenLastTick = false;
	private boolean bankIsOpen = false;
	private boolean depositBoxIsOpen = false;
	private boolean depositBoxWasOpenLastTick = false;
	private final Map<ClueTier, Boolean> previousClueScrollInventoryState = new HashMap<>();
	private final Map<ClueTier, Boolean> previousChallengeScrollInventoryState = new HashMap<>();
	private final Map<ClueTier, Integer> previousInventoryScrollBoxCount = new HashMap<>();
	private final Map<ClueTier, Integer> previousBankScrollBoxCount = new HashMap<>();
	private final Map<ClueTier, Boolean> previousBankClueScrollState = new HashMap<>();
	private final Map<ClueTier, Boolean> previousBankChallengeScrollState = new HashMap<>();

	@Override
	protected void startUp() throws Exception
	{
		log.info("Example started!");
		clueCountStorage.loadBankCountsFromConfig();
		overlayManager.add(clueWidgetItemOverlay);
	}

	@Override
	protected void shutDown() throws Exception
	{
		log.info("Example stopped!");
		overlayManager.remove(clueWidgetItemOverlay);
		clueWidgetItemOverlay.resetMarkedStacks();
	}

	@Subscribe
	public void onConfigChanged(ConfigChanged event)
	{
		if (!event.getGroup().equals("scrollboxinfo"))
			return;

		if (event.getKey().equals("highlightWhenCapped") && !config.highlightWhenCapped())
		{
			clueWidgetItemOverlay.resetMarkedStacks();
		}
	}

	@Subscribe
	public void onGameTick(GameTick tick)
	{
		bankWasOpenLastTick = bankIsOpen;
		depositBoxWasOpenLastTick = depositBoxIsOpen;

		Widget bankWidget = client.getWidget(WidgetInfo.BANK_CONTAINER);
		bankIsOpen = bankWidget != null && !bankWidget.isHidden();

		Widget depositBoxWidget = client.getWidget(WidgetInfo.DEPOSIT_BOX_INVENTORY_ITEMS_CONTAINER);
		depositBoxIsOpen = depositBoxWidget != null && !depositBoxWidget.isHidden();
	}

	@Subscribe
	public void onItemContainerChanged(ItemContainerChanged event)
	{
		ItemContainer inventoryContainer = client.getItemContainer(InventoryID.INVENTORY);
		ItemContainer bankContainer = client.getItemContainer(InventoryID.BANK);

		for (ClueTier tier : ClueTier.values())
		{
			ClueCounts inventory = clueCounter.getClueCounts(tier, inventoryContainer);
			ClueCounts bank = clueCounter.getClueCounts(tier, bankContainer);

			boolean clueEnteredInv = inventory.hasClueScroll() && !previousClueScrollInventoryState.getOrDefault(tier, false);
			boolean clueLeftInv = !inventory.hasClueScroll() && previousClueScrollInventoryState.getOrDefault(tier, false);
			boolean challengeEnteredInv = inventory.hasChallengeScroll() && !previousChallengeScrollInventoryState.getOrDefault(tier, false);
			boolean challengeLeftInv = !inventory.hasChallengeScroll() && previousChallengeScrollInventoryState.getOrDefault(tier, false);
			boolean scrollBoxEnteredInv = inventory.scrollBoxCount() > previousInventoryScrollBoxCount.getOrDefault(tier, 0);
			boolean scrollBoxLeftInv = inventory.scrollBoxCount() < previousInventoryScrollBoxCount.getOrDefault(tier, 0);

			boolean bankedClueScroll = previousBankClueScrollState.getOrDefault(tier, false);
			boolean bankedChallengeScroll = previousBankChallengeScrollState.getOrDefault(tier, false);
			int assumedBankedScrollBoxCount = previousBankScrollBoxCount.getOrDefault(tier, 0);

			int count = inventory.scrollBoxCount();
			if (inventory.hasClueScroll())
				count++;
			if (inventory.hasChallengeScroll())
				count++;

			if (bankContainer != null) {
				bankedClueScroll = bank.hasClueScroll();
				bankedChallengeScroll = bank.hasChallengeScroll();
				assumedBankedScrollBoxCount = bank.scrollBoxCount();

				int bankCount = assumedBankedScrollBoxCount;
				bankCount += bankedClueScroll ? 1 : 0;
				bankCount += bankedChallengeScroll ? 1 : 0;

				if (bankedChallengeScroll && bankedClueScroll)
					bankCount -= 1;

				clueCountStorage.setBankCount(tier, bankCount);
			} else if (bankWasOpenLastTick || depositBoxIsOpen || depositBoxWasOpenLastTick) {
				if (scrollBoxLeftInv)
					assumedBankedScrollBoxCount += 1;
				else if (scrollBoxEnteredInv)
					assumedBankedScrollBoxCount -= 1;

				if (clueEnteredInv) {
					bankedClueScroll = false;
				} else if (clueLeftInv) {
					bankedClueScroll = true;
				}
				if (challengeEnteredInv) {
					bankedChallengeScroll = false;
				} else if (challengeLeftInv) {
					bankedChallengeScroll = true;
				}

				int assumedBankCount = assumedBankedScrollBoxCount;
				if (bankedChallengeScroll || bankedClueScroll)
					assumedBankCount += 1;

				clueCountStorage.setBankCount(tier, assumedBankCount);
			}

			count += clueCountStorage.getBankCount(tier);
			if ((inventory.hasClueScroll() && inventory.hasChallengeScroll())
				|| (inventory.hasClueScroll() && bankedChallengeScroll)
				|| (inventory.hasChallengeScroll() && bankedClueScroll))
				count -= 1;
			clueCountStorage.setCount(tier, count);

			previousBankClueScrollState.put(tier, bankedClueScroll);
			previousBankChallengeScrollState.put(tier, bankedChallengeScroll);
			previousClueScrollInventoryState.put(tier, inventory.hasClueScroll());
			previousChallengeScrollInventoryState.put(tier, inventory.hasChallengeScroll());
			previousInventoryScrollBoxCount.put(tier, inventory.scrollBoxCount());
			previousBankScrollBoxCount.put(tier, assumedBankedScrollBoxCount);
		}
	}









	@Provides
	ScrollBoxInfoConfig provideConfig(ConfigManager configManager)
	{
		return configManager.getConfig(ScrollBoxInfoConfig.class);
	}
}
