public class Item {
    private String name;
    private ItemType type;

    public Item(String name, ItemType type) {
        this.name = name;
        this.type = type;
    }

    public String getName() { return name; } // getter for name
    public ItemType getType() { return type; } // getter for type
}