package com.scrollboxinfo.overlay;

import com.scrollboxinfo.*;
import com.scrollboxinfo.data.ClueCountStorage;
import lombok.RequiredArgsConstructor;
import net.runelite.api.Client;
import net.runelite.api.ItemID;
import net.runelite.api.Point;
import net.runelite.api.widgets.ItemQuantityMode;
import net.runelite.api.widgets.Widget;
import net.runelite.api.widgets.ComponentID;
import net.runelite.api.widgets.WidgetItem;
import net.runelite.client.ui.overlay.WidgetItemOverlay;
import net.runelite.client.ui.overlay.components.TextComponent;
import net.runelite.client.ui.overlay.tooltip.Tooltip;
import net.runelite.client.ui.overlay.tooltip.TooltipManager;
import net.runelite.client.ui.FontManager;

import javax.inject.Inject;
import java.awt.*;
import java.util.Arrays;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;

@RequiredArgsConstructor(onConstructor_ = @Inject)
public class ClueWidgetItemOverlay extends WidgetItemOverlay
{
    private final Client client;
    private final TooltipManager tooltipManager;
    private final ClueCountStorage storage;

    @Inject
    private ClueCounter clueCounter;

    @Inject
    private ScrollBoxInfoConfig config;

    @Inject
    private QuestChecker questChecker;

    @Inject
    public void setup()
    {
        showOnInventory();
        showOnBank();
    }

    @Override
    public void renderItemOverlay(Graphics2D graphics, int itemId, WidgetItem widgetItem)
    {
        ClueTier tier = ClueUtils.getClueTier(client, itemId);
        if (tier == null)
            return;

        int current = clueCounter.getClueCounts(tier);
        int cap = StackLimitCalculator.getStackLimit(tier, client);

        if (isScrollBox(itemId) && config.markFullStack())
        {
            Color textColor = (config.markFullStack() && current >= cap) ? Color.RED : Color.YELLOW;
            widgetItem.getWidget().setItemQuantityMode(ItemQuantityMode.NEVER);
            renderQuantity(graphics, widgetItem.getCanvasBounds(), widgetItem.getQuantity(), textColor);
        }

        // Render tier label in bottom-left corner
        if (config.showTierLabel() && tier != null)
        {
            String tierName = ClueUtils.formatTierName(tier);
            if (tierName.equals("Beginner")) {
                tierName = tierName.substring(0, tierName.length() - 2);
            }

            Color tierColor;

            if (!config.colorTierLabel())
            {
                tierColor = Color.WHITE;
            }
            else
            {
                switch (tier)
                {
                    case BEGINNER:
                        tierColor = config.beginnerTierColor();
                        break;
                    case EASY:
                        tierColor = config.easyTierColor();
                        break;
                    case MEDIUM:
                        tierColor = config.mediumTierColor();
                        break;
                    case HARD:
                        tierColor = config.hardTierColor();
                        break;
                    case ELITE:
                        tierColor = config.eliteTierColor();
                        break;
                    case MASTER:
                        tierColor = config.masterTierColor();
                        break;
                    default:
                        tierColor = Color.WHITE;
                        break;
                }
            }

            Rectangle bounds = widgetItem.getCanvasBounds();

            final TextComponent label = new TextComponent();
            label.setText(tierName);
            label.setColor(tierColor); // use config color
            label.setPosition(new java.awt.Point(bounds.x, bounds.y + bounds.height));
            label.setFont(FontManager.getRunescapeSmallFont());
            label.render(graphics);
        }

        Point mousePos = client.getMouseCanvasPosition();
        if (!widgetItem.getCanvasBounds().contains(mousePos.getX(), mousePos.getY())) {
            return;
        }

        if (!questChecker.isXMarksTheSpotComplete()) {
            tooltipManager.add(new Tooltip("Complete X Marks the Spot quest to start tracking clues"));
            return;
        }

        int banked = storage.getBankCount(tier);

        List<String> lines = new ArrayList<>();

        if (config.showBanked() && banked != 0)
        {
            lines.add("Banked: " + banked);
        }

        if (config.showCurrent())
        {
            lines.add("Current total: " + current);
        }

        if (config.showCap())
        {
            lines.add("Stack limit: " + cap);
        }

        if (config.showNextUnlock())
        {
            Integer varpId = tierToVarpId.get(tier);
            int completed = varpId != null ? client.getVarpValue(varpId) : 0;
            int[] unlocks = tierUnlockThresholds.getOrDefault(tier, new int[0]);

            for (int threshold : unlocks)
            {
                if (completed < threshold)
                {
                    lines.add("Next unlock in: " + (threshold - completed) + " clues");
                    break;
                }
            }
        }

        if (!lines.isEmpty())
        {
            if (questChecker.isXMarksTheSpotComplete())
                tooltipManager.add(new Tooltip(String.join("<br>", lines)));
        }
    }

    private void renderQuantity(Graphics2D graphics, Rectangle bounds, int quantity, Color textColor)
    {
        final TextComponent textComponent = new TextComponent();
        String quantityText = String.valueOf(quantity);
        graphics.setFont(FontManager.getRunescapeSmallFont());
        textComponent.setPosition(new java.awt.Point(bounds.x, bounds.y + 10));
        textComponent.setText(quantityText);
        textComponent.setColor(textColor);
        textComponent.render(graphics);
    }

    private boolean isScrollBox(int itemId)
    {
        switch (itemId)
        {
            case ItemID.SCROLL_BOX_BEGINNER:
            case ItemID.SCROLL_BOX_EASY:
            case ItemID.SCROLL_BOX_MEDIUM:
            case ItemID.SCROLL_BOX_HARD:
            case ItemID.SCROLL_BOX_ELITE:
            case ItemID.SCROLL_BOX_MASTER:
                return true;
            default:
                return false;
        }
    }


    public void resetMarkedStacks()
    {
        List<Integer> containerComponentIds = Arrays.asList(
                ComponentID.INVENTORY_CONTAINER,
                ComponentID.BANK_ITEM_CONTAINER,
                ComponentID.BANK_INVENTORY_ITEM_CONTAINER,
                ComponentID.BANK_EQUIPMENT_PARENT,
                ComponentID.DEPOSIT_BOX_INVENTORY_ITEM_CONTAINER,
                ComponentID.EQUIPMENT_INVENTORY_ITEM_CONTAINER,
                ComponentID.GRAND_EXCHANGE_INVENTORY_INVENTORY_ITEM_CONTAINER,
                ComponentID.SHOP_INVENTORY_ITEM_CONTAINER,
                ComponentID.GUIDE_PRICES_INVENTORY_ITEM_CONTAINER,
                ComponentID.BANK_INVENTORY_EQUIPMENT_ITEM_CONTAINER
        );

        for (int componentId : containerComponentIds)
        {
            Widget container = client.getWidget(componentId);
            if (container == null || container.getDynamicChildren() == null)
                continue;

            for (Widget child : container.getDynamicChildren())
            {
                if (child != null && child.getItemId() > 0 && isScrollBox(child.getItemId()))
                {
                    child.setItemQuantityMode(ItemQuantityMode.ALWAYS);
                }
            }
        }
    }


    private static final Map<ClueTier, Integer> tierToVarpId = Map.of(
            ClueTier.BEGINNER, 2201,
            ClueTier.EASY, 1111,
            ClueTier.MEDIUM, 1112,
            ClueTier.HARD, 1354,
            ClueTier.ELITE, 1533,
            ClueTier.MASTER, 1534
    );

    private static final Map<ClueTier, int[]> tierUnlockThresholds = Map.of(
            ClueTier.BEGINNER, new int[] {50, 100},
            ClueTier.EASY, new int[] {100, 200},
            ClueTier.MEDIUM, new int[] {100, 250},
            ClueTier.HARD, new int[] {50, 150},
            ClueTier.ELITE, new int[] {50, 150},
            ClueTier.MASTER, new int[] {25, 75}
    );

}