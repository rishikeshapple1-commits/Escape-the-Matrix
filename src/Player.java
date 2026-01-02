import java.util.*;

/**
 * Represents a player in the game, managing position, lives, and inventory.
 * Players can move around the grid, collect items, and lose lives due to penalties or revisits.
 */
public class Player {
    /** The name of the player. */
    private String name;
    /** The x-coordinate of the player's position on the grid. */
    private int x = 0;
    /** The y-coordinate of the player's position on the grid. */
    private int y = 0;
    /** The number of lives the player has remaining. */
    private int lives = 3;
    /** The list of items collected by the player. */
    private List<Item> inventory = new ArrayList<>();

    /**
     * Constructs a new Player with the specified name.
     * Initializes position at (0,0) and lives to 3.
     * @param name the name of the player
     */
    public Player(String name) {
        this.name = name;
    }

    /**
     * Gets the name of the player.
     * @return the player's name
     */
    public String getName() {
        return name;
    }

    /**
     * Gets the x-coordinate of the player's position.
     * @return the x-coordinate
     */
    public int getX() {
        return x;
    }

    /**
     * Gets the y-coordinate of the player's position.
     * @return the y-coordinate
     */
    public int getY() {
        return y;
    }

    /**
     * Gets the number of lives the player has.
     * @return the number of lives
     */
    public int getLives() {
        return lives;
    }

    /**
     * Gets the player's inventory of items.
     * @return the list of items
     */
    public List<Item> getInventory() {
        return inventory;
    }

    /**
     * Adds an item to the player's inventory.
     * @param i the item to add
     */
    public void addItem(Item i) {
        inventory.add(i);
    }

    /**
     * Increases the player's lives by the specified amount.
     * @param n the number of lives to add
     */
    public void addLife(int n) {
        lives += n;
    }

    /**
     * Decreases the player's lives by the specified amount.
     * @param n the number of lives to subtract
     */
    public void loseLife(int n) {
        lives -= n;
    }

    /**
     * Attempts to move the player in the specified direction.
     * Movement is blocked if it would go outside the grid boundaries.
     * @param d the direction to move
     * @return true if the move was successful, false if blocked by edge
     */
    public boolean move(Direction d) {
        switch(d) {
            case NORTH: if (x == 0) return false; x--; break;
            case SOUTH: if (x == 4) return false; x++; break;
            case EAST: if (y == 4) return false; y++; break;
            case WEST: if (y == 0) return false; y--; break;
        }
        return true;
    }

    /**
     * Counts the number of LIFE_BOOST items in the player's inventory.
     * @return the count of life boost items
     */
    public int getLifeBoostCount() {
        int count = 0;
        for (Item i : inventory)
            if (i.getType() == ItemType.LIFE_BOOST) count++;
        return count;
    }

    /**
     * Transfers up to the specified number of LIFE_BOOST items from this player's inventory to another player.
     * @param to the player to transfer items to
     * @param count the maximum number of items to transfer
     */
    public void transferLifeBoost(Player to, int count) {
        int transferred = 0;
        Iterator<Item> it = inventory.iterator();
        while (it.hasNext() && transferred < count) {
            Item item = it.next();
            if (item.getType() == ItemType.LIFE_BOOST) {
                to.addItem(item);
                it.remove();
                transferred++;
            }
        }
    }
}