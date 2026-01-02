/**
 * Represents an item in the game, such as life boosts or penalties.
 * Items can be collected by players and affect their lives or inventory.
 */
public class Item {
    /** The name of the item. */
    private String name;
    /** The type of the item, determining its effect. */
    private ItemType type;

    /**
     * Constructs a new Item with the specified name and type.
     * @param name the name of the item
     * @param type the type of the item
     */
    public Item(String name, ItemType type) {
        this.name = name;
        this.type = type;
    }

    /**
     * Gets the name of the item.
     * @return the name of the item
     */
    public String getName() {
        return name;
    }

    /**
     * Gets the type of the item.
     * @return the type of the item
     */
    public ItemType getType() {
        return type;
    }
}