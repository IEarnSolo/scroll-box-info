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

import java.util.EnumMap;
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

	private final EnumMap<ClueTier, Integer> previousInventoryCounts = new EnumMap<>(ClueTier.class);
	private boolean bankWasOpenLastTick = false;
	private boolean bankIsOpen = false;
	private boolean depositBoxIsOpen = false;
	private boolean depositBoxWasOpenLastTick = false;
	private boolean hadInventoryClueScroll = false;
	private boolean hadInventoryChallengeScroll = false;
	private final Map<ClueTier, Boolean> previousClueScrollInventoryState = new HashMap<>();
	private final Map<ClueTier, Boolean> previousChallengeScrollInventoryState = new HashMap<>();
	private final Map<ClueTier, Boolean> previousBankClueScrollState = new HashMap<>();
	private final Map<ClueTier, Boolean> previousBankChallengeScrollState = new HashMap<>();



	private static class ClueState {
		boolean hadClueScroll;
		boolean hadChallengeScroll;

		ClueState(boolean clue, boolean challenge) {
			this.hadClueScroll = clue;
			this.hadChallengeScroll = challenge;
		}
	}

	private final Map<ClueTier, ClueState> previousInventoryStates = new HashMap<>();



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

/*	@Subscribe
	public void onItemContainerChanged(ItemContainerChanged event)
	{
		ItemContainer inventory = client.getItemContainer(InventoryID.INVENTORY);
		ItemContainer bank = client.getItemContainer(InventoryID.BANK);

		for (ClueTier tier : ClueTier.values())
		{
			ClueCounter.ClueCounts inv = clueCounter.getClueCounts(tier, inventory);
			ClueCounter.ClueCounts bnk = clueCounter.getClueCounts(tier, bank);

			boolean invHasClue = inv.hasClueScroll();
			boolean invHasChallenge = inv.hasChallengeScroll();
			boolean bankHasClue = bnk.hasClueScroll();
			boolean bankHasChallenge = bnk.hasChallengeScroll();
			int scrollBoxesInInv = inv.scrollBoxCount();
			int scrollBoxesInBank = bnk.scrollBoxCount();

			boolean hasClueOrChallengeScrollInBank = Boolean.TRUE.equals(
					configManager.getRSProfileConfiguration("scrollboxinfo", "hasClueOrChallengeScrollInBank_" + tier.name(), Boolean.class));

			int clueValueInv = (invHasClue || invHasChallenge) ? 1 : 0;
			int clueValueBank = (bankHasClue || bankHasChallenge) ? 1 : 0;
			int newInvCount = scrollBoxesInInv + clueValueInv;

			if ((invHasClue && bankHasChallenge) || (invHasChallenge && bankHasClue))
				newInvCount -= 1;
			else if (bank == null && hasClueOrChallengeScrollInBank && (invHasClue || invHasChallenge))
			{
				newInvCount -= 1;
				log.info("Bank closed - using saved clue/challenge scroll state for {}", tier.name());
				log.info("MEDIUM: hasClueOrChallengeScrollInBank: {}", hasClueOrChallengeScrollInBank);
			}

			int oldInvCount = previousInventoryCounts.getOrDefault(tier, 0);
			previousInventoryCounts.put(tier, newInvCount);
			clueCountStorage.setCount(tier, newInvCount);

			int newBankCount = scrollBoxesInBank + clueValueBank;

			if (bank != null)
			{
				boolean hasAnyScroll = bankHasClue || bankHasChallenge;
				configManager.setRSProfileConfiguration("scrollboxinfo", "hasClueOrChallengeScrollInBank_" + tier.name(), hasAnyScroll);
				clueCountStorage.setBankCount(tier, newBankCount);
				log.info("Bank is open - updated bank count directly");
			}

			int total;
			int storedBankCount = clueCountStorage.getBankCount(tier);

			ClueState prevState = previousInventoryStates.getOrDefault(tier, new ClueState(false, false));
			boolean hadAnyScrollBefore = prevState.hadClueScroll || prevState.hadChallengeScroll;
			boolean hadClueScroll = prevState.hadClueScroll;
			boolean hadChallengeScroll = prevState.hadChallengeScroll;
			boolean hasAnyScrollNow = invHasClue || invHasChallenge;

			if (bank != null)
			{
				total = newInvCount + newBankCount;
			}
			else if (depositBoxIsOpen || depositBoxWasOpenLastTick || bankWasOpenLastTick)
			{
				int delta = newInvCount - oldInvCount;
				int adjustedBank = storedBankCount + ((depositBoxIsOpen || depositBoxWasOpenLastTick) ? (oldInvCount - newInvCount) : -delta);

				if (!hadAnyScrollBefore && hasAnyScrollNow)
				{
					if (adjustedBank > 0)
					{
						if (!hasClueOrChallengeScrollInBank) {
							adjustedBank--;
						}
						configManager.setRSProfileConfiguration("scrollboxinfo", "hasClueOrChallengeScrollInBank_" + tier.name(), false);
						log.info("Scroll withdrawn - decremented bank count for {}", tier.name());
					}
				}
				else if ((hadAnyScrollBefore && !hasClueOrChallengeScrollInBank))
				{
					if(hasAnyScrollNow) {
						adjustedBank++ ;
					}
					if (hadAnyScrollBefore && hasAnyScrollNow) {
						previousInventoryCounts.put(tier, newInvCount--);
						clueCountStorage.setCount(tier, newInvCount--);
					}
					configManager.setRSProfileConfiguration("scrollboxinfo", "hasClueOrChallengeScrollInBank_" + tier.name(), true);
					log.info("Scroll deposited - incremented bank count for {}", tier.name());
				}

				clueCountStorage.setBankCount(tier, adjustedBank);
				total = newInvCount + adjustedBank;

				if (depositBoxIsOpen || depositBoxWasOpenLastTick)
					log.info("Deposit box recently open - adjusted bank by delta {}", oldInvCount - newInvCount);
				else
					log.info("Bank was open last tick - adjusted bank by delta {}", -delta);
			}
			else
			{
				total = newInvCount + storedBankCount;
			}

			log.info("Updated {} count: inv={}, bank={}, total={}", tier.name(), clueCountStorage.getCount(tier), clueCountStorage.getBankCount(tier), total);

			// Save state per tier
			previousInventoryStates.put(tier, new ClueState(invHasClue, invHasChallenge));
		}
	}*/

	@Subscribe
	public void onItemContainerChanged(ItemContainerChanged event)
	{
		ItemContainer inventory = client.getItemContainer(InventoryID.INVENTORY);
		ItemContainer bank = client.getItemContainer(InventoryID.BANK);

		for (ClueTier tier : ClueTier.values())
		{
			ClueCounter.ClueCounts inv = clueCounter.getClueCounts(tier, inventory);
			ClueCounter.ClueCounts bnk = clueCounter.getClueCounts(tier, bank);

			boolean invHasClue = inv.hasClueScroll();
			boolean invHasChallenge = inv.hasChallengeScroll();
			boolean bankHasClue = bnk.hasClueScroll();
			boolean bankHasChallenge = bnk.hasChallengeScroll();
			int scrollBoxesInInv = inv.scrollBoxCount();
			int scrollBoxesInBank = bnk.scrollBoxCount();

			boolean hasClueOrChallengeScrollInBank = Boolean.TRUE.equals(
					configManager.getRSProfileConfiguration("scrollboxinfo", "hasClueOrChallengeScrollInBank_" + tier.name(), Boolean.class)
			);

			int clueValueInv = (invHasClue || invHasChallenge) ? 1 : 0;
			int clueValueBank = (bankHasClue || bankHasChallenge) ? 1 : 0;

			int newInvCount = scrollBoxesInInv + clueValueInv;
			int newBankCount = scrollBoxesInBank + clueValueBank;
			int oldInvCount = previousInventoryCounts.getOrDefault(tier, 0);
			boolean previousBankHasClue = previousBankClueScrollState.getOrDefault(tier, false);
			boolean previousBankHasChallenge = previousBankChallengeScrollState.getOrDefault(tier, false);
			previousBankClueScrollState.put(tier, bankHasClue);
			previousBankChallengeScrollState.put(tier, bankHasChallenge);

			// Compare current and previous states
			boolean clueEnteredInv = invHasClue && !previousClueScrollInventoryState.getOrDefault(tier, false);
			boolean challengeEnteredInv = invHasChallenge && !previousChallengeScrollInventoryState.getOrDefault(tier, false);
			boolean clueLeftInv = !invHasClue && previousClueScrollInventoryState.getOrDefault(tier, false);
			boolean challengeLeftInv = !invHasChallenge && previousChallengeScrollInventoryState.getOrDefault(tier, false);

			int delta = newInvCount - oldInvCount;

			if (bank != null)
			{
				boolean hasAnyScroll = bankHasClue || bankHasChallenge;
				configManager.setRSProfileConfiguration("scrollboxinfo", "hasClueOrChallengeScrollInBank_" + tier.name(), hasAnyScroll);
				previousBankClueScrollState.put(tier, bankHasClue);
				previousBankChallengeScrollState.put(tier, bankHasChallenge);
				clueCountStorage.setBankCount(tier, newBankCount);
				log.info("{}: Bank open - bank count updated directly: {}", tier.name(), newBankCount);
				log.info("{}: Bank open - hasClueOrChallengeScrollInBank_" + tier.name() + ": {}", tier.name(), hasAnyScroll);
			}
			else if (depositBoxIsOpen || depositBoxWasOpenLastTick || bankWasOpenLastTick)
			{
				int oldBankCount = clueCountStorage.getBankCount(tier);
				int assumedBankCount = oldBankCount - delta;

				boolean bothScrollsEnteredInv = clueEnteredInv && challengeEnteredInv;
				boolean bothScrollsLeftInv = clueLeftInv && challengeLeftInv;

				boolean bankHadOnlyOneScroll =
						(previousBankHasClue ^ previousBankHasChallenge); // only one of them was true

				// Determine bank scroll flag & count change
				if (bothScrollsEnteredInv ||
						(clueEnteredInv && !previousBankHasChallenge) ||
						(challengeEnteredInv && !previousBankHasClue))
				{
					// Withdraw - remove flag, decrease bank count by 1
					configManager.setRSProfileConfiguration("scrollboxinfo", "hasClueOrChallengeScrollInBank_" + tier.name(), false);
					log.info("{}: Withdrew scroll - no scrolls remain in bank, disabling flag", tier.name());
					if (hasClueOrChallengeScrollInBank)
					{
						assumedBankCount = Math.max(assumedBankCount - 1, 0);
						clueCountStorage.setBankCount(tier, assumedBankCount);
						newInvCount++;
					}
				}
				else if (bothScrollsLeftInv ||
						(clueLeftInv && !previousBankHasChallenge) ||
						(challengeLeftInv && !previousBankHasClue))
				{
					// Deposit - set flag, increase bank count by 1
					configManager.setRSProfileConfiguration("scrollboxinfo", "hasClueOrChallengeScrollInBank_" + tier.name(), true);
					log.info("{}: Deposited scroll - enabling hasClueOrChallengeScrollInBank flag", tier.name());
					if (!hasClueOrChallengeScrollInBank)
					{
						assumedBankCount++;
						clueCountStorage.setBankCount(tier, assumedBankCount);
						newInvCount--;
					}
				} else {

					clueCountStorage.setBankCount(tier, assumedBankCount);
				}
			}

			// Adjust inventory count when clue and challenge are split between bank and inv
			if ((invHasClue && bankHasChallenge) || (invHasChallenge && bankHasClue))
			{
				newInvCount -= 1;
			}
			else if (bank == null && hasClueOrChallengeScrollInBank && (invHasClue || invHasChallenge) && (!clueEnteredInv || !challengeEnteredInv))
			{
				newInvCount -= 1;
				log.info("Bank closed - using saved clue/challenge scroll state for {}", tier.name());
			}

			previousInventoryCounts.put(tier, newInvCount);
			clueCountStorage.setCount(tier, newInvCount);

			// Save scroll presence state for next comparison
			previousClueScrollInventoryState.put(tier, invHasClue);
			previousChallengeScrollInventoryState.put(tier, invHasChallenge);
		}
	}









	@Provides
	ScrollBoxInfoConfig provideConfig(ConfigManager configManager)
	{
		return configManager.getConfig(ScrollBoxInfoConfig.class);
	}
}
