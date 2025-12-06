import java.util.EnumMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class Room {
    private Set<Player> players = new HashSet<>();
    private boolean visited = false;
    private Item hiddenItem = null;

    private Map<Direction, Integer> doors = new EnumMap<>(Direction.class);

    public void addPlayer(Player p) {
        players.add(p);
    }

    public void removePlayer(Player p) {
        players.remove(p);
    }

    public boolean isVisited() {
        return visited;
    }

    public void setVisited(boolean v) {
        visited = v;
    }

    public Item getHiddenItem() {
        return hiddenItem;
    }

    public void setHiddenItem(Item it) {
        hiddenItem = it;
    }

    public void removeHiddenItem() {
        hiddenItem = null;
    }

    public void setDoors(Map<Direction, Integer> d) {
        doors = d;
    }

    public boolean canMove(Direction d) {
        return doors.getOrDefault(d, 0) > 0;
    }

    public void consumeDoor(Direction d) {
        doors.put(d, 0);
    }

    public Set<Player> getPlayers() {
        return players;
    }
}