import java.util.*;

public class Player {
    private String name;
    private int x = 0, y = 0;
    private int lives = 3;
    private List<Item> inventory = new ArrayList<>();

    public Player(String name) {
        this.name = name;
    }

    public String getName() { return name; }
    public int getX() { return x; }
    public int getY() { return y; }
    public int getLives() { return lives; }
    public List<Item> getInventory() { return inventory; }

    public void addItem(Item i) { inventory.add(i); }
    public void addLife(int n) { lives += n; }
    public void loseLife(int n) { lives -= n; }

    public boolean move(Direction d) {
        switch(d) {
            case NORTH: if (x == 0) return false; x--; break;
            case SOUTH: if (x == 4) return false; x++; break;
            case EAST: if (y == 4) return false; y++; break;
            case WEST: if (y == 0) return false; y--; break;
        }
        return true;
    }

    // New methods for LifeBoost sharing
    public int getLifeBoostCount() {
        int count = 0;
        for (Item i : inventory)
            if (i.getType() == ItemType.LIFE_BOOST) count++;
        return count;
    }

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