package com.scrollboxinfo;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;

import java.awt.*;

@ConfigGroup("scrollboxinfo")
public interface ScrollBoxInfoConfig extends Config
{
	@ConfigItem(
			keyName = "showBanked",
			name = "Show banked",
			description = "Display the number of scroll boxes and clues banked",
			position = 1
	)
	default boolean showBanked()
	{
		return true;
	}

	@ConfigItem(
			keyName = "showCurrent",
			name = "Show current total",
			description = "Display the total number of scroll boxes and clue scrolls currently owned",
			position = 2
	)
	default boolean showCurrent()
	{
		return true;
	}

	@ConfigItem(
			keyName = "showCap",
			name = "Show stack limit",
			description = "Display the stack limit amount of how many scroll boxes you can hold of the same tier",
			position = 3
	)
	default boolean showCap()
	{
		return true;
	}

	@ConfigItem(
			keyName = "showNextUnlock",
			name = "Show next unlock",
			description = "Display how many clue completions until next stack limit unlock",
			position = 4
	)
	default boolean showNextUnlock()
	{
		return true;
	}

	@ConfigItem(
			keyName = "highlightWhenCapped",
			name = "Mark full stacks",
			description = "Mark the scroll box amount red when you’ve hit your stack limit",
			position = 5
	)
	default boolean highlightWhenCapped()
	{
		return true;
	}

	@ConfigItem(
			name = "Show tier label",
			keyName = "showTierLabel",
			description = "Show the clue tier name on clue items",
			position = 6
	)
	default boolean showTierLabel()
	{
		return true;
	}

	@ConfigItem(
			name = "Color tier label",
			keyName = "colorTierLabel",
			description = "Color the tier labels over clue items",
			position = 7
	)
	default boolean colorTierLabel()
	{
		return true;
	}

	@ConfigItem(
			keyName = "beginnerTierColor",
			name = "Beginner Tier Color",
			description = "Text color for Beginner clues",
			position = 8
	)
	default Color beginnerTierColor() {
		return new Color(0xc3bbba);
	}

	@ConfigItem(
			keyName = "easyTierColor",
			name = "Easy Tier Color",
			description = "Text color for Easy clues",
			position = 9
	)
	default Color easyTierColor() {
		return new Color(0x2b952f);
	}

	@ConfigItem(
			keyName = "mediumTierColor",
			name = "Medium Tier Color",
			description = "Text color for Medium clues",
			position = 10
	)
	default Color mediumTierColor() {
		return new Color(0x5ea4a7);
	}

	@ConfigItem(
			keyName = "hardTierColor",
			name = "Hard Tier Color",
			description = "Text color for Hard clues",
			position = 11
	)
	default Color hardTierColor() {
		return new Color(0x8f3ca5);
	}

	@ConfigItem(
			keyName = "eliteTierColor",
			name = "Elite Tier Color",
			description = "Text color for Elite clues",
			position = 12
	)
	default Color eliteTierColor() {
		return new Color(0xc2aa18);
	}

	@ConfigItem(
			keyName = "masterTierColor",
			name = "Master Tier Color",
			description = "Text color for Master clues",
			position = 13
	)
	default Color masterTierColor() {
		return new Color(0xa7342a);
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
