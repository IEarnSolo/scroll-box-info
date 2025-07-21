package com.scrollboxinfo;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;
import net.runelite.client.config.ConfigSection;

import java.awt.*;

@ConfigGroup("scrollboxinfo")
public interface ScrollBoxInfoConfig extends Config
{
	@ConfigSection(
			name = "Tooltip overlay",
			description = "Customize how clue scroll information is shown in the tooltip overlay",
			position = 0
	)
	String tooltipOverlay = "tooltipOverlay";

	@ConfigSection(
			name = "Item overlay",
			description = "Customize how clue scroll information is shown in the item overlay",
			position = 10
	)
	String itemOverlay = "itemOverlay";

	@ConfigSection(
			name = "Infobox",
			description = "Customize how clue scroll information is shown in the infobox",
			position = 20
	)
	String infobox = "infobox";

	@ConfigSection(
			name = "Chat message",
			description = "Customize how chat messages are sent",
			position = 30
	)
	String chatMessage = "chatMessage";

	@ConfigItem(
			keyName = "showBanked",
			name = "Show banked",
			description = "Display the number of scroll boxes and clues banked",
			position = 1,
			section = tooltipOverlay
	)
	default boolean showBanked()
	{
		return true;
	}

	@ConfigItem(
			keyName = "showCurrent",
			name = "Show current total",
			description = "Display the total number of scroll boxes and clue scrolls currently owned",
			position = 2,
			section = tooltipOverlay
	)
	default boolean showCurrent()
	{
		return true;
	}

	@ConfigItem(
			keyName = "showCap",
			name = "Show stack limit",
			description = "Display the stack limit amount of how many scroll boxes you can hold of the same tier",
			position = 3,
			section = tooltipOverlay
	)
	default boolean showCap()
	{
		return true;
	}

	@ConfigItem(
			keyName = "showNextUnlock",
			name = "Show next unlock",
			description = "Display how many clue completions until next stack limit unlock",
			position = 4,
			section = tooltipOverlay
	)
	default boolean showNextUnlock()
	{
		return true;
	}

	@ConfigItem(
			keyName = "markFullStack",
			name = "Mark full stacks",
			description = "Mark the scroll box amount red when you’ve hit your stack limit",
			position = 11,
			section = itemOverlay
	)
	default boolean markFullStack()
	{
		return true;
	}

	@ConfigItem(
			name = "Show tier label",
			keyName = "showTierLabel",
			description = "Show the clue tier name on clue items",
			position = 12,
			section = itemOverlay
	)
	default boolean showTierLabel()
	{
		return true;
	}

	@ConfigItem(
			name = "Color tier label",
			keyName = "colorTierLabel",
			description = "Color the tier labels over clue items",
			position = 13,
			section = itemOverlay
	)
	default boolean colorTierLabel()
	{
		return true;
	}

	@ConfigItem(
			keyName = "beginnerTierColor",
			name = "Beginner tier color",
			description = "Text color for beginner clues",
			position = 14,
			section = itemOverlay
	)
	default Color beginnerTierColor() {
		return new Color(0xc3bbba);
	}

	@ConfigItem(
			keyName = "easyTierColor",
			name = "Easy tier color",
			description = "Text color for easy clues",
			position = 15,
			section = itemOverlay
	)
	default Color easyTierColor() {
		return new Color(0x2b952f);
	}

	@ConfigItem(
			keyName = "mediumTierColor",
			name = "Medium tier color",
			description = "Text color for medium clues",
			position = 16,
			section = itemOverlay
	)
	default Color mediumTierColor() {
		return new Color(0x5ea4a7);
	}

	@ConfigItem(
			keyName = "hardTierColor",
			name = "Hard tier color",
			description = "Text color for hard clues",
			position = 17,
			section = itemOverlay
	)
	default Color hardTierColor() {
		return new Color(0xc870e0);
	}

	@ConfigItem(
			keyName = "eliteTierColor",
			name = "Elite tier color",
			description = "Text color for elite clues",
			position = 18,
			section = itemOverlay
	)
	default Color eliteTierColor() {
		return new Color(0xc2aa18);
	}

	@ConfigItem(
			keyName = "masterTierColor",
			name = "Master tier color",
			description = "Text color for master clues",
			position = 17,
			section = itemOverlay
	)
	default Color masterTierColor() {
		return new Color(0xa7342a);
	}

	@ConfigItem(
			keyName = "showFullStackInfobox",
			name = "Show full stack",
			description = "Display an infobox when you've reached your clue stack limit",
			position = 21,
			section = infobox
	)
	default boolean showFullStackInfobox()
	{
		return true;
	}

	@ConfigItem(
			keyName = "showBeginnerInfobox",
			name = "Show beginner",
			description = "Show infobox for beginner clues",
			position = 22,
			section = infobox
	)
	default boolean showBeginnerInfobox() { return true; }

	@ConfigItem(
			keyName = "showEasyInfobox",
			name = "Show easy",
			description = "Show infobox for easy clues",
			position = 23,
			section = infobox
	)
	default boolean showEasyInfobox() { return true; }

	@ConfigItem(
			keyName = "showMediumInfobox",
			name = "Show medium",
			description = "Show infobox for medium clues",
			position = 24,
			section = infobox
	)
	default boolean showMediumInfobox() { return true; }

	@ConfigItem(
			keyName = "showHardInfobox",
			name = "Show hard",
			description = "Show infobox for hard clues",
			position = 25,
			section = infobox
	)
	default boolean showHardInfobox() { return true; }

	@ConfigItem(
			keyName = "showEliteInfobox",
			name = "Show elite",
			description = "Show infobox for elite clues",
			position = 26,
			section = infobox
	)
	default boolean showEliteInfobox() { return true; }

	@ConfigItem(
			keyName = "showMasterInfobox",
			name = "Show master",
			description = "Show infobox for master clues",
			position = 27,
			section = infobox
	)
	default boolean showMasterInfobox() { return true; }


	@ConfigItem(
			keyName = "showChatMessage",
			name = "Show chat message",
			description = "Send a chat message of your current scroll box/clue scroll total when a scroll box is received",
			position = 31,
			section = chatMessage
	)
	default boolean showChatMessage()
	{
		return true;
	}


	// ===== Persistent Banked Clue Counts =====

	@ConfigItem(
			keyName = "bankedBeginner",
			name = "",
			description = "",
			hidden = true
	)
	default int bankedBeginner() { return 0; }

	@ConfigItem(
			keyName = "bankedBeginner",
			name = "",
			description = "",
			hidden = true
	)
	void setBankedBeginner(int value);

	@ConfigItem(
			keyName = "bankedEasy",
			name = "",
			description = "",
			hidden = true
	)
	default int bankedEasy() { return 0; }

	@ConfigItem(
			keyName = "bankedEasy",
			name = "",
			description = "",
			hidden = true
	)
	void setBankedEasy(int value);

	@ConfigItem(
			keyName = "bankedMedium",
			name = "",
			description = "",
			hidden = true
	)
	default int bankedMedium() { return 0; }

	@ConfigItem(
			keyName = "bankedMedium",
			name = "",
			description = "",
			hidden = true
	)
	void setBankedMedium(int value);

	@ConfigItem(
			keyName = "bankedHard",
			name = "",
			description = "",
			hidden = true
	)
	default int bankedHard() { return 0; }

	@ConfigItem(
			keyName = "bankedHard",
			name = "",
			description = "",
			hidden = true
	)
	void setBankedHard(int value);

	@ConfigItem(
			keyName = "bankedElite",
			name = "",
			description = "",
			hidden = true
	)
	default int bankedElite() { return 0; }

	@ConfigItem(
			keyName = "bankedElite",
			name = "",
			description = "",
			hidden = true
	)
	void setBankedElite(int value);

	@ConfigItem(
			keyName = "bankedMaster",
			name = "",
			description = "",
			hidden = true
	)
	default int bankedMaster() { return 0; }

	@ConfigItem(
			keyName = "bankedMaster",
			name = "",
			description = "",
			hidden = true
	)
	void setBankedMaster(int value);

	@ConfigItem(
			keyName = "hasClueOrChallengeScrollInBank_BEGINNER",
			name = "",
			description = "",
			hidden = true
	)
	default boolean hasClueOrChallengeScrollInBank_BEGINNER()
	{
		return false;
	}

	@ConfigItem(
			keyName = "hasClueOrChallengeScrollInBank_EASY",
			name = "",
			description = "",
			hidden = true
	)
	default boolean hasClueOrChallengeScrollInBank_EASY()
	{
		return false;
	}

	@ConfigItem(
			keyName = "hasClueOrChallengeScrollInBank_MEDIUM",
			name = "",
			description = "",
			hidden = true
	)
	default boolean hasClueOrChallengeScrollInBank_MEDIUM()
	{
		return false;
	}

	@ConfigItem(
			keyName = "hasClueOrChallengeScrollInBank_HARD",
			name = "",
			description = "",
			hidden = true
	)
	default boolean hasClueOrChallengeScrollInBank_HARD()
	{
		return false;
	}

	@ConfigItem(
			keyName = "hasClueOrChallengeScrollInBank_ELITE",
			name = "",
			description = "",
			hidden = true
	)
	default boolean hasClueOrChallengeScrollInBank_ELITE()
	{
		return false;
	}

	@ConfigItem(
			keyName = "hasClueOrChallengeScrollInBank_MASTER",
			name = "",
			description = "",
			hidden = true
	)
	default boolean hasClueOrChallengeScrollInBank_MASTER()
	{
		return false;
	}

}
