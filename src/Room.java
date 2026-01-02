import java.util.EnumMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Represents a room in the game grid, containing players, items, and door states.
 * Rooms can be visited, hold hidden items, and have doors that control movement.
 */
public class Room {
    /** The set of players currently in this room. */
    private Set<Player> players = new HashSet<>();
    /** Whether this room has been visited by any player. */
    private boolean visited = false;
    /** The hidden item in this room, if any. */
    private Item hiddenItem = null;
    /** The map of door availabilities for each direction. */
    private Map<Direction, Integer> doors = new EnumMap<>(Direction.class);

    /**
     * Adds a player to this room.
     * @param p the player to add
     */
    public void addPlayer(Player p) {
        players.add(p);
    }

    /**
     * Removes a player from this room.
     * @param p the player to remove
     */
    public void removePlayer(Player p) {
        players.remove(p);
    }

    /**
     * Checks if this room has been visited.
     * @return true if visited, false otherwise
     */
    public boolean isVisited() {
        return visited;
    }

    /**
     * Sets the visited status of this room.
     * @param v the visited status
     */
    public void setVisited(boolean v) {
        visited = v;
    }

    /**
     * Gets the hidden item in this room.
     * @return the hidden item, or null if none
     */
    public Item getHiddenItem() {
        return hiddenItem;
    }

    /**
     * Sets the hidden item in this room.
     * @param it the item to set, or null to remove
     */
    public void setHiddenItem(Item it) {
        hiddenItem = it;
    }

    /**
     * Removes the hidden item from this room.
     */
    public void removeHiddenItem() {
        hiddenItem = null;
    }

    /**
     * Sets the door availabilities for this room.
     * @param d the map of direction to availability (0 or 1)
     */
    public void setDoors(Map<Direction, Integer> d) {
        doors = d;
    }

    /**
     * Checks if movement in the specified direction is allowed.
     * @param d the direction to check
     * @return true if the door is available, false otherwise
     */
    public boolean canMove(Direction d) {
        return doors.getOrDefault(d, 0) > 0;
    }

    /**
     * Consumes the door in the specified direction, making it unavailable.
     * @param d the direction of the door to consume
     */
    public void consumeDoor(Direction d) {
        doors.put(d, 0);
    }

    /**
     * Gets the set of players currently in this room.
     * @return the set of players
     */
    public Set<Player> getPlayers() {
        return players;
    }
}