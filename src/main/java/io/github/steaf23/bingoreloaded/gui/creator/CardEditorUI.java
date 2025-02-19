package io.github.steaf23.bingoreloaded.gui.creator;

import io.github.steaf23.bingoreloaded.data.BingoCardsData;
import io.github.steaf23.bingoreloaded.data.BingoTasksData;
import io.github.steaf23.bingoreloaded.gui.AbstractGUIInventory;
import io.github.steaf23.bingoreloaded.gui.FilterType;
import io.github.steaf23.bingoreloaded.gui.ListPickerUI;
import io.github.steaf23.bingoreloaded.item.InventoryItem;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class CardEditorUI extends ListPickerUI
{
    public final String cardName;
    private static final InventoryItem ADD_LIST = new InventoryItem(51, Material.EMERALD, "" + ChatColor.GREEN + ChatColor.BOLD + "Add Item List", "");
    private ListValueEditorGUI valueEditorGUI;

    public CardEditorUI(String cardName, AbstractGUIInventory parent)
    {
        super(new ArrayList<>(), "Editing '" + cardName + "'", parent, FilterType.DISPLAY_NAME);
        this.cardName = cardName;
        fillOptions(new InventoryItem[]{ADD_LIST});
    }

    @Override
    public void onOptionClickedDelegate(final InventoryClickEvent event, InventoryItem clickedOption, Player player)
    {
        //if an ItemList attached to a card was clicked on
        if (clickedOption.getItemMeta() == null) return;

        String listName = clickedOption.getItemMeta().getDisplayName();
        valueEditorGUI = new ListValueEditorGUI(this, listName, BingoCardsData.getListMax(cardName, listName), BingoCardsData.getListMin(cardName, listName));
        valueEditorGUI.open(player);
    }

    @Override
    public void delegateClick(InventoryClickEvent event, int slotClicked, Player player, ClickType clickType)
    {
        super.delegateClick(event, slotClicked, player, clickType);

        if (slotClicked == ADD_LIST.getSlot())
        {
            List<InventoryItem> items = new ArrayList<>();
            for (String listName : BingoTasksData.getListNames())
            {
                items.add(new InventoryItem(Material.PAPER, listName, "This list contains " + BingoTasksData.getTaskCount(listName) + " tasks", ChatColor.GRAY + "Click to select"));
            }

            ListPickerUI listPicker = new ListPickerUI(items, "Pick A List", this, FilterType.DISPLAY_NAME)
            {
                @Override
                public void onOptionClickedDelegate(final InventoryClickEvent event, InventoryItem clickedOption, Player player)
                {
                    ItemMeta optionMeta = clickedOption.getItemMeta();

                    if(optionMeta != null)
                    {
                        getResultFromPicker(optionMeta.getDisplayName());
                    }
                    close(player);
                }
            };
            listPicker.open(player);
        }
    }

    public void getResultFromPicker(String result)
    {
        BingoCardsData.setList(cardName, result, BingoTasksData.getTaskCount(result), 1);
    }

    @Override
    public void open(HumanEntity player)
    {
        super.open(player);
        updateCardDisplay();
    }

    public void updateCardDisplay()
    {
        clearItems();

        List<InventoryItem> newItems = new ArrayList<>();
        for (String listName : BingoCardsData.getLists(cardName))
        {
            InventoryItem item = new InventoryItem(Material.MAP, listName, "This list contains " + BingoTasksData.getTaskCount(listName) + " tasks");
            item.setAmount(Math.max(1, BingoCardsData.getListMax(cardName, listName)));
            newItems.add(item);
        }

        addItems(newItems.toArray(new InventoryItem[0]));

        applyFilter(getFilter());
    }
}
