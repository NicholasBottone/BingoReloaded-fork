package me.steven.bingoreloaded.GUIInventories;

import me.steven.bingoreloaded.BingoReloaded;
import me.steven.bingoreloaded.InventoryItem;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

/**
 * Abstract class for Inventory based GUIs.
 * Delegates click events to a separate method that Inheritors must override.
 */
public abstract class AbstractGUIInventory implements Listener
{
    /**
     * Event delegate to handle custom click behaviour for inventory items
     * @param event The event that gets fired when an item in the inventory was clicked.
     * @param slotClicked The slot that was clicked on by the player.
     *                    Slot can be null.
     * @param player The player that clicked on the item.
     */
    public abstract void delegateClick(InventoryClickEvent event, int slotClicked, Player player);

    protected static final String TITLE_PREFIX = "" + ChatColor.GOLD + ChatColor.BOLD;

    private Inventory inventory = null;
    private final AbstractGUIInventory parent;

    public AbstractGUIInventory(int size, String title, AbstractGUIInventory parent)
    {
        this.parent = parent;

        Plugin plugin = Bukkit.getPluginManager().getPlugin(BingoReloaded.NAME);
        if (plugin == null) return;

        Bukkit.getPluginManager().registerEvents(this, plugin);

        inventory = Bukkit.createInventory(null, size, BingoReloaded.PRINT_PREFIX + ChatColor.DARK_RED + title);
    }

    @EventHandler
    public void onInventoryClick(final InventoryClickEvent event)
    {
        if (inventory == null) return;
        if (event.getInventory() == inventory)
        {
            event.setCancelled(true);

            if (event.getRawSlot() < event.getInventory().getSize() && event.getRawSlot() >= 0)
            {
                delegateClick(event, event.getSlot(), (Player)event.getWhoClicked());
            }
        }
    }

    @EventHandler
    public void onInventoryDrag(final InventoryDragEvent event)
    {
        if (inventory == null) return;
        if (event.getInventory() == inventory)
        {
            event.setCancelled(true);
        }
    }

    protected void fillOptions(int[] slots, InventoryItem[] options)
    {
        if (slots.length != options.length) throw new IllegalArgumentException("Number of options and number of slots to put them in are not equal!");
        if (inventory.getSize() < slots.length) throw new IllegalArgumentException("Cannot fill options with current inventory size (" + inventory.getSize() + ")!");

        for (int i = 0; i < slots.length; i++)
        {
            addOption(slots[i], options[i].inSlot(slots[i]));
        }
    }

    /**
     * Adds items into the specified slot. If slot is -1, it will use the next available slot or merge using Inventory#addItem().
     * @param slot inventory slot to fill
     * @param option menu item to put in the inventory
     */
    protected void addOption(int slot, InventoryItem option)
    {
        if (slot == -1)
        {
            inventory.addItem(option);
        }
        else
        {
            inventory.setItem(slot, option);
        }
    }

    public InventoryItem getOption(int slot)
    {
        ItemStack stack = inventory.getItem(slot);
        if (stack == null || stack.getType().isAir()) return null;

        return new InventoryItem(slot, inventory.getItem(slot));
    }

    public void open(HumanEntity player)
    {
        player.openInventory(inventory);
    }

    public void close(HumanEntity player)
    {
        player.closeInventory();
    }

    public void openParent(HumanEntity player)
    {
        if (parent != null)
            parent.open(player);
        else
            BingoReloaded.print("You attempted to open this Screen's parent when it was, in fact, null :O", (Player)player);
    }
}
