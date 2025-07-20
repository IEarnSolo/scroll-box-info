package com.scrollboxinfo;

import net.runelite.api.Client;
import net.runelite.api.ItemComposition;
import net.runelite.api.ItemID;

public class ClueUtils
{
    public static ClueTier getClueTier(Client client, int itemId)
    {
        return switch (itemId)
        {
            case ItemID.SCROLL_BOX_BEGINNER, ItemID.CLUE_SCROLL_BEGINNER -> ClueTier.BEGINNER;
            case ItemID.SCROLL_BOX_EASY, ItemID.CLUE_SCROLL_EASY -> ClueTier.EASY;
            case ItemID.SCROLL_BOX_MEDIUM, ItemID.CLUE_SCROLL_MEDIUM -> ClueTier.MEDIUM;
            case ItemID.SCROLL_BOX_HARD, ItemID.CLUE_SCROLL_HARD -> ClueTier.HARD;
            case ItemID.SCROLL_BOX_ELITE, ItemID.CLUE_SCROLL_ELITE -> ClueTier.ELITE;
            case ItemID.SCROLL_BOX_MASTER, ItemID.CLUE_SCROLL_MASTER -> ClueTier.MASTER;
            default -> {
                ItemComposition item = client.getItemDefinition(itemId);
                if (item == null)
                    yield null;

                String name = item.getName().toLowerCase();
                if (name.startsWith("clue scroll (beginner)")) yield ClueTier.BEGINNER;
                else if (name.startsWith("clue scroll (easy)")) yield ClueTier.EASY;
                else if (name.startsWith("clue scroll (medium)")) yield ClueTier.MEDIUM;
                else if (name.startsWith("clue scroll (hard)")) yield ClueTier.HARD;
                else if (name.startsWith("clue scroll (elite)")) yield ClueTier.ELITE;
                else if (name.startsWith("clue scroll (master)")) yield ClueTier.MASTER;

                else if (name.startsWith("challenge scroll (medium)")) yield ClueTier.MEDIUM;
                else if (name.startsWith("challenge scroll (hard)")) yield ClueTier.HARD;
                else if (name.startsWith("challenge scroll (elite)")) yield ClueTier.ELITE;

                else yield null;
            }
        };
    }
}

