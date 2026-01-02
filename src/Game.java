import java.util.*;

/**
 * The main game class for "Escape-the-Matrix", a grid-based adventure game.
 * Manages the game state, including the grid of rooms, players, rounds, and user commands.
 * Players must navigate from the start to the exit while managing lives and collecting items.
 */
public class Game {
    /**
     * The size of the square grid (5x5).
     */
    private static final int SIZE = 5;

    /**
     * The 2D grid of rooms representing the game board.
     */
    private Room[][] grid = new Room[SIZE][SIZE];
    /**
     * The list of players participating in the game.
     */
    private List<Player> players = new ArrayList<>();
    /**
     * The number of rounds remaining in the game.
     */
    private int roundsLeft = 12;
    /**
     * Scanner for reading user input from the console.
     */
    private final Scanner sc = new Scanner(System.in);

    /**
     * Map storing the door availability counters for each direction in the current round.
     * Values are 0 (unavailable) or 1 (available), and are decremented when a move is made.
     */
    private Map<Direction, Integer> roundDoors = new EnumMap<>(Direction.class);

    /**
     * Constructor for the Game class.
     * Initializes the grid with rooms, places hidden items randomly, and positions all players at the start.
     */
    public Game() {
        // Initialize grid with default rooms and doors
        for (int i = 0; i < SIZE; i++) {
            for (int j = 0; j < SIZE; j++) {
                grid[i][j] = new Room();
                Map<Direction, Integer> defaultDoors = new EnumMap<>(Direction.class);
                for (Direction d : Direction.values()) defaultDoors.put(d, 0);
                grid[i][j].setDoors(defaultDoors);
            }
        }

        // Add players
        players.add(new Player("P1"));
        players.add(new Player("P2"));
        players.add(new Player("P3"));

        // Place hidden items in random rooms, avoiding start and exit
        Random rnd = new Random();
        int toPlace = 3;
        int placed = 0;
        while (placed < toPlace) {
            int rx = rnd.nextInt(SIZE);
            int ry = rnd.nextInt(SIZE);
            if ((rx == 0 && ry == 0) || (rx == SIZE - 1 && ry == SIZE - 1)) continue; // Skip start and exit
            if (grid[rx][ry].getHiddenItem() != null) continue; // Ensure no duplicate items
            ItemType t = rnd.nextBoolean() ? ItemType.LIFE_BOOST : ItemType.PENALTY;
            String name = t == ItemType.LIFE_BOOST ? "LifeBoost" : "Penalty";
            grid[rx][ry].setHiddenItem(new Item(name, t));
            placed++;
        }

        // Place all players at start and mark as visited
        for (Player p : players) grid[0][0].addPlayer(p);
        grid[0][0].setVisited(true);
    }

    /**
     * Starts the game loop, handling rounds and user commands until win, loss, or exit.
     */
    public void start() {
        System.out.println("Welcome to Escape-the-Matrix!");
        System.out.println("Type 'help' for commands.");
        while (roundsLeft > 0) {
            newRound();
            while (true) {
                System.out.print("> ");
                String line = sc.nextLine();
                if (line == null) return;
                String input = line.trim();
                if (input.isEmpty()) continue;

                if (input.equalsIgnoreCase("endround")) break;
                if (input.equalsIgnoreCase("exit")) {
                    System.out.println("Exiting game. Goodbye!");
                    return;
                }

                processCommand(input);

                if (checkWin()) return;
                if (checkLose()) return;
            }
        }
        System.out.println("No rounds remaining. YOU LOSE!");
    }

    /**
     * Initializes a new round by decrementing rounds left, resetting door availability,
     * and printing the grid and status.
     */
    private void newRound() {
        roundsLeft--;

        Random r = new Random();
        boolean atStart = players.stream().allMatch(p -> p.getX() == 0 && p.getY() == 0);

        // Reset doors based on position
        roundDoors.clear();

        if (atStart) {
            // At start: North and West blocked, East/South random
            roundDoors.put(Direction.NORTH, 0);
            roundDoors.put(Direction.WEST, 0);
            roundDoors.put(Direction.EAST, r.nextBoolean() ? 1 : 0);
            roundDoors.put(Direction.SOUTH, r.nextBoolean() ? 1 : 0);
        } else {
            // Elsewhere: Random, but ensure at least one direction is available
            boolean ok = false;
            while (!ok) {
                for (Direction d : Direction.values())
                    roundDoors.put(d, r.nextBoolean() ? 1 : 0);

                if (roundDoors.values().stream().anyMatch(v -> v == 1))
                    ok = true;
            }
        }

        // Apply door settings to all rooms
        for (int i = 0; i < SIZE; i++)
            for (int j = 0; j < SIZE; j++)
                grid[i][j].setDoors(new EnumMap<>(roundDoors));

        System.out.println("----- NEW ROUND -----");
        printRoundUpdate();
        printGrid();
    }

    /**
     * Prints the current state of the grid, showing player positions and the exit.
     * Also prints player statuses.
     */
    public void printGrid() {
        final int CELL_WIDTH = 8; // content width (inside brackets)

        System.out.println("Current Grid:");
        for (int i = 0; i < SIZE; i++) {
            StringBuilder row = new StringBuilder();

            for (int j = 0; j < SIZE; j++) {
                List<String> names = new ArrayList<>();
                for (Player p : players) {
                    if (p.getX() == i && p.getY() == j) {
                        names.add(p.getName());
                    }
                }
                String cellContent = "";

                if (i == SIZE - 1 && j == SIZE - 1) {
                    // Special case for exit room
                    cellContent = names.isEmpty()
                            ? "EXIT"
                            : String.join(",", names) + ", EXIT";
                } else if (!names.isEmpty()) {
                    cellContent = String.join(",", names);
                }

                // Trim if too long
                if (cellContent.length() > CELL_WIDTH) {
                    cellContent = cellContent.substring(0, CELL_WIDTH);
                }
                // Pad to fixed width
                cellContent = String.format("%-" + CELL_WIDTH + "s", cellContent);

                row.append("[").append(cellContent).append("]");
            }

            System.out.println(row);
        }

        System.out.println("--- STATUS ---");
        for (Player p : players) {
            System.out.printf("%s: Lives=%d Inventory=%d LifeBoosts=%d%n",
                    p.getName(), p.getLives(), p.getInventory().size(), p.getLifeBoostCount());
        }
    }

    /**
     * Processes a user command input string.
     *
     * @param input the command string to process
     */
    private void processCommand(String input) {
        String[] parts = input.split("\\s+");
        if (parts.length == 0) return;

        String cmd = parts[0].toLowerCase(Locale.ROOT);

        switch (cmd) {
            case "help":
                printHelp();
                break;

            case "go":
                if (parts.length < 3) {
                    System.out.println("Usage: go P# <north|east|south|west>");
                    break;
                }
                handleGo(parts[1], parts[2]);
                break;

            default:
                System.out.println("Invalid command. Type 'help' for commands.");
        }
    }

    /**
     * Prints the list of available commands.
     */
    private void printHelp() {
        System.out.println("Commands:");
        System.out.println("  go P# <north|east|south|west>");
        System.out.println("  endround");
        System.out.println("  help");
    }

    /**
     * Handles the "go" command to move a player in a specified direction.
     * Checks validity, updates positions, deducts doors, applies penalties, and handles items.
     *
     * @param playerName the name of the player (e.g., "P1")
     * @param dirText    the direction string (north, east, south, west)
     */
    private void handleGo(String playerName, String dirText) {
        Player p = getPlayer(playerName);
        if (p == null) {
            System.out.println("Invalid player.");
            return;
        }

        if (p.getLives() <= 0) {
            System.out.println(p.getName() + " has no lives left and cannot move.");
            return;
        }

        Direction d;
        try {
            d = Direction.valueOf(dirText.toUpperCase());
        } catch (Exception e) {
            System.out.println("Invalid direction. Use north/east/south/west.");
            return;
        }

        Room current = grid[p.getX()][p.getY()];

        if (!current.canMove(d)) {
            System.out.println("Cannot move " + d + " — already used this round.");
            return;
        }

        boolean moved = p.move(d);
        if (!moved) {
            System.out.println(p.getName() + " cannot move " + d + ". At edge.");
            return;
        }

        // Deduct door globally and update all rooms
        roundDoors.put(d, 0);
        for (int i = 0; i < SIZE; i++) {
            for (int j = 0; j < SIZE; j++) {
                grid[i][j].consumeDoor(d);
            }
        }

        current.removePlayer(p);

        Room newRoom = grid[p.getX()][p.getY()];
        newRoom.addPlayer(p);

        // Check for hidden item in the new room
        Item item = newRoom.getHiddenItem();
        if (item != null) {
            if (item.getType() == ItemType.PENALTY) {
                p.loseLife(1);
                System.out.println("TRAP! " + p.getName() + " found a Penalty and lost 1 life.");
            } else if (item.getType() == ItemType.LIFE_BOOST) {
                p.addItem(item);
                System.out.println("LUCKY! " + p.getName() + " found a LifeBoost! Added to inventory.");
            }
            newRoom.removeHiddenItem();
        }

        // Penalty for revisiting a room alone
        if (newRoom.isVisited()) {
            if (newRoom.getPlayers().size() == 1) {
                p.loseLife(1);
                System.out.println(p.getName() + " revisited and lost 1 life. Lives=" + p.getLives());
            }
        }

        newRoom.setVisited(true);

        System.out.println(p.getName() + " moved " + d + ".");
        printRoundUpdate();
        printGrid();
    }

    /**
     * Retrieves a player by name (case-insensitive).
     *
     * @param name the player's name
     * @return the Player object, or null if not found
     */
    private Player getPlayer(String name) {
        for (Player p : players)
            if (p.getName().equalsIgnoreCase(name))
                return p;
        return null;
    }

    /**
     * Checks if any player has reached the exit, triggering a win condition.
     *
     * @return true if a player won, false otherwise
     */
    private boolean checkWin() {
        for (Player p : players) {
            if (p.getX() == SIZE - 1 && p.getY() == SIZE - 1) {
                System.out.println("Player " + p.getName() + " reached EXIT! YOU WIN!");
                return true;
            }
        }
        return false;
    }

    /**
     * Checks if all players have lost all lives, triggering a loss condition.
     *
     * @return true if all players lost, false otherwise
     */
    private boolean checkLose() {
        return players.stream().allMatch(p -> p.getLives() <= 0);
    }

    /**
     * Prints the current round information and door availability status.
     * Shows the number of rounds remaining and which doors (North, East, South, West)
     * are available for movement in the current round.
     */
    public void printRoundUpdate() {
        System.out.println("Rounds left: " + roundsLeft);
        System.out.printf("Door availability: N=%d E=%d S=%d W=%d%n",
                roundDoors.get(Direction.NORTH),
                roundDoors.get(Direction.EAST),
                roundDoors.get(Direction.SOUTH),
                roundDoors.get(Direction.WEST));
    }
}