package me.steven.bingoreloaded.cardcreator;

import java.util.HashMap;
import java.util.Map;

public class CardEntry
{
    private final String name;
    private final Map<String, Integer> itemLists = new HashMap<>();

    public CardEntry(String name)
    {
        this.name = name;
    }

    public void addItemList(String listName, int maxOccurrences)
    {
        itemLists.put(listName, maxOccurrences);
    }

    public void removeItemList(String listName)
    {
        itemLists.remove(listName);
    }

    public Map<String, Integer> getItemLists()
    {
        return itemLists;
    }

    public String getName()
    {
        return name;
    }
}
