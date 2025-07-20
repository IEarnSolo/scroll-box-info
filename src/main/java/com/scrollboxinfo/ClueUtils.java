package com.scrollboxinfo;

import net.runelite.api.Client;
import net.runelite.api.ItemComposition;
import net.runelite.api.ItemID;

public class ClueUtils
{
    public static ClueTier getClueTier(Client client, int itemId)
    {
        switch (itemId)
        {
            case ItemID.SCROLL_BOX_BEGINNER:
            case ItemID.CLUE_SCROLL_BEGINNER:
                return ClueTier.BEGINNER;

            case ItemID.SCROLL_BOX_EASY:
            case ItemID.CLUE_SCROLL_EASY:
                return ClueTier.EASY;

            case ItemID.SCROLL_BOX_MEDIUM:
            case ItemID.CLUE_SCROLL_MEDIUM:
                return ClueTier.MEDIUM;

            case ItemID.SCROLL_BOX_HARD:
            case ItemID.CLUE_SCROLL_HARD:
                return ClueTier.HARD;

            case ItemID.SCROLL_BOX_ELITE:
            case ItemID.CLUE_SCROLL_ELITE:
                return ClueTier.ELITE;

            case ItemID.SCROLL_BOX_MASTER:
            case ItemID.CLUE_SCROLL_MASTER:
                return ClueTier.MASTER;

            default:
                ItemComposition item = client.getItemDefinition(itemId);
                if (item == null)
                {
                    return null;
                }

                String name = item.getName().toLowerCase();

                if (name.startsWith("clue scroll (beginner)"))
                {
                    return ClueTier.BEGINNER;
                }
                else if (name.startsWith("clue scroll (easy)"))
                {
                    return ClueTier.EASY;
                }
                else if (name.startsWith("clue scroll (medium)"))
                {
                    return ClueTier.MEDIUM;
                }
                else if (name.startsWith("clue scroll (hard)"))
                {
                    return ClueTier.HARD;
                }
                else if (name.startsWith("clue scroll (elite)"))
                {
                    return ClueTier.ELITE;
                }
                else if (name.startsWith("clue scroll (master)"))
                {
                    return ClueTier.MASTER;
                }
                else if (name.startsWith("challenge scroll (medium)"))
                {
                    return ClueTier.MEDIUM;
                }
                else if (name.startsWith("challenge scroll (hard)"))
                {
                    return ClueTier.HARD;
                }
                else if (name.startsWith("challenge scroll (elite)"))
                {
                    return ClueTier.ELITE;
                }
                else
                {
                    return null;
                }
        }
    }

}

