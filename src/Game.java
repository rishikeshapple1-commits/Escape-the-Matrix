import java.util.*;

public class Game {
    private static final int SIZE = 5;

    private Room[][] grid = new Room[SIZE][SIZE];
    private List<Player> players = new ArrayList<>();
    private int roundsLeft = 12;
    private final Scanner sc = new Scanner(System.in);
    
    private Map<Direction, Integer> roundDoors = new EnumMap<>(Direction.class);

    public Game() {
        for (int i = 0; i < SIZE; i++) {
            for (int j = 0; j < SIZE; j++) {
                grid[i][j] = new Room();
                Map<Direction, Integer> defaultDoors = new EnumMap<>(Direction.class);
                for (Direction d : Direction.values()) defaultDoors.put(d, 0);
                grid[i][j].setDoors(defaultDoors);
            }
        }

        players.add(new Player("P1"));
        players.add(new Player("P2"));
        players.add(new Player("P3"));

        Random rnd = new Random();
        int toPlace = 3;
        int placed = 0;
        while (placed < toPlace) {
            int rx = rnd.nextInt(SIZE);
            int ry = rnd.nextInt(SIZE);
            if ((rx == 0 && ry == 0) || (rx == SIZE - 1 && ry == SIZE - 1)) continue;
            if (grid[rx][ry].getHiddenItem() != null) continue;
            ItemType t = rnd.nextBoolean() ? ItemType.LIFE_BOOST : ItemType.PENALTY;
            String name = t == ItemType.LIFE_BOOST ? "LifeBoost" : "Penalty";
            grid[rx][ry].setHiddenItem(new Item(name, t));
            placed++;
        }

        for (Player p : players) grid[0][0].addPlayer(p);
        grid[0][0].setVisited(true);
    }

    public void start() {
        System.out.println("Welcome to Possible Futures Multiplayer!");
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

    private void newRound() {
        roundsLeft--;

        Random r = new Random();
        boolean atStart = players.stream().allMatch(p -> p.getX() == 0 && p.getY() == 0);
        
        roundDoors.clear();

        if (atStart) {
            roundDoors.put(Direction.NORTH, 0);
            roundDoors.put(Direction.WEST, 0);
            roundDoors.put(Direction.EAST, r.nextBoolean() ? 1 : 0);
            roundDoors.put(Direction.SOUTH, r.nextBoolean() ? 1 : 0);
        } else {
            boolean ok = false;
            while (!ok) {
                for (Direction d : Direction.values())
                    roundDoors.put(d, r.nextBoolean() ? 1 : 0);

                if (roundDoors.values().stream().anyMatch(v -> v == 1))
                    ok = true;
            }
        }

        for (int i = 0; i < SIZE; i++)
            for (int j = 0; j < SIZE; j++)
                grid[i][j].setDoors(new EnumMap<>(roundDoors));

        System.out.println("----- NEW ROUND -----");
        System.out.println("Rounds left: " + roundsLeft);
        System.out.printf("Door availability: N=%d E=%d S=%d W=%d%n",
                roundDoors.get(Direction.NORTH),
                roundDoors.get(Direction.EAST),
                roundDoors.get(Direction.SOUTH),
                roundDoors.get(Direction.WEST));

        printGrid();
    }

    public void printGrid() {
        System.out.println("Current Grid:");
        for (int i = 0; i < SIZE; i++) {
            StringBuilder row = new StringBuilder();
            for (int j = 0; j < SIZE; j++) {
                List<String> names = new ArrayList<>();
                for (Player p : players)
                    if (p.getX() == i && p.getY() == j) names.add(p.getName());

                if (i == SIZE - 1 && j == SIZE - 1) {
                    if (!names.isEmpty()) row.append("[").append(String.join(",", names)).append(",E]");
                    else row.append("[ E ]");
                } else if (!names.isEmpty()) {
                    row.append("[").append(String.join(",", names)).append("]");
                } else {
                    row.append("[     ]");
                }
            }
            System.out.println(row.toString());
        }

        System.out.println("--- STATUS ---");
        for (Player p : players)
            System.out.printf("%s: Lives=%d Inventory=%d LifeBoosts=%d%n",
                    p.getName(), p.getLives(), p.getInventory().size(), p.getLifeBoostCount());
    }

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

    private void printHelp() {
        System.out.println("Commands:");
        System.out.println("  go P# <north|east|south|west>");
        System.out.println("  endround");
        System.out.println("  help");
    }

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

        roundDoors.put(d, 0);

        for (int i = 0; i < SIZE; i++)
            for (int j = 0; j < SIZE; j++)
                grid[i][j].consumeDoor(d);

        current.removePlayer(p);

        Room newRoom = grid[p.getX()][p.getY()];
        newRoom.addPlayer(p);

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

        if (newRoom.isVisited()) {
            if (newRoom.getPlayers().size() == 1) {
                p.loseLife(1);
                System.out.println(p.getName() + " revisited and lost 1 life. Lives=" + p.getLives());
            }
        }

        newRoom.setVisited(true);

        System.out.println(p.getName() + " moved " + d + ".");
        printGrid();
    }

    private Player getPlayer(String name) {
        for (Player p : players)
            if (p.getName().equalsIgnoreCase(name))
                return p;
        return null;
    }

    private boolean checkWin() {
        for (Player p : players) {
            if (p.getX() == SIZE - 1 && p.getY() == SIZE - 1) {
                System.out.println("Player " + p.getName() + " reached EXIT! YOU WIN!");
                return true;
            }
        }
        return false;
    }

    private boolean checkLose() {
        return players.stream().allMatch(p -> p.getLives() <= 0);
    }
}