package byog.phase1;

import byog.TileEngine.TETile;
import byog.TileEngine.Tileset;
import java.util.Stack;
import java.util.Random;
import java.io.Serializable;

//Generates 2D map for generation
public class Map implements Serializable {
    private static final long serialVersionUID = 32531531513453L;
    private int width;
    private int height;
    private TETile[][] spaces;
    private long seed;
    private Random random;
    private GeneralUtils util = new GeneralUtils();
    private Location start;
    private Location door;
    private User hero;
    private Enemy villain;
    private String heroMovements = "";


    //Need to rename used for more than location

    private Stack<Location> deadEnds = new Stack<>();
    private Stack<Location> roomLocations = new Stack<>();


    public Map(int newWidth, int newHeight, long newSeed) {
        this.width = newWidth;
        this.height = newHeight;
        this.seed = newSeed;
        this.random = new Random(seed);
        spaces = new TETile[width][height];
        this.start = new Location(1, 1);
        hero = new User(start, Tileset.PLAYER);
    }

    private void fill(TETile tile) {
        for (int w = 0; w < this.width; w++) {
            for (int h = 0; h < this.height; h++) {
                spaces[w][h] = tile;
            }
        }
    }

    public void generateMap() {
        this.fill(Tileset.NOTHING);
        this.blockify(Tileset.WALL, Tileset.NOTHING);
        this.generatePerfectPath();
        while (!deadEnds.empty()) {
            Location current = deadEnds.pop();
            this.trimDeadEnd(current, Tileset.FLOOR, Tileset.WALL);
        }
        while (!roomLocations.empty()) {
            roomGenerator(roomLocations.pop(), Tileset.FLOOR);
        }
        placeObject(Tileset.LOCKED_DOOR);
        placeObject(Tileset.KEY);
        villain = new Enemy(placeEnemy(), Tileset.ENEMY, random);
        this.spaces[start.getX()][start.getY()] =
                hero.getRepresentation();
        this.spaces[villain.getPosition().getX()]
                [villain.getPosition().getY()]
                = villain.getRepresentation();
    }

    //change to public later
    private void generatePerfectPath() {
        //start with a grid that has every possible wall filled in
        this.blockify(Tileset.WALL, Tileset.NOTHING);


        Stack<Location> path = new Stack<>();

        int totalNodes = (this.width / 3) * (this.height / 3);

        // choose a cell to start from
        Location current = new Location(1, 1);
        path.push(current);
        totalNodes -= 1;

        while (totalNodes > 0) {
            int direction = this.random.nextInt(4);
            if (checkNeighbors(current, Tileset.FLOOR)) {
                if (path.empty()) {
                    break;
                } else {
                    current = path.pop();
                }
            }

            boolean moved = generateSinglePath(current.getX(),
                    current.getY(), direction, Tileset.FLOOR, 3);
            if (moved) {
                Location nextLocation = new Location(current.getX(), current.getY());
                switch (direction) {
                    case 0:
                        nextLocation.addX(3);
                        break;
                    case 1:
                        nextLocation.addX(-3);
                        break;
                    case 2:
                        nextLocation.addY(3);
                        break;
                    default:
                        nextLocation.addY(-3);
                        break;
                }

                current = nextLocation;
                path.push(current);
                if (checkNeighbors(current, Tileset.FLOOR)) {
                    deadEnds.push(current);
                    current = path.pop();
                }
                totalNodes--;
            }

        }
    }

    //generates a room of random height and width that fits within the map
    public void roomGenerator(Location current, TETile t) {
        Location xDirection = testRoomDistances(current, 2, t);
        Location yDirection = testRoomDistances(current, 1, t);
        for (int i = 0; i < xDirection.getY() % 7; i += 1) {
            for (int j = 0; j < yDirection.getY() % 7; j += 1) {
                spaces[current.getX() - i][current.getY() - j] = t;
            }
        }
        for (int i = 0; i < xDirection.getY() % 7; i += 1) {
            for (int j = 0; j < yDirection.getX() % 7; j += 1) {
                spaces[current.getX() - i][current.getY() + j] = t;
            }
        }
        for (int i = 0; i < xDirection.getX() % 7; i += 1) {
            for (int j = 0; j < yDirection.getY() % 7; j += 1) {
                spaces[current.getX() + i][current.getY() - j] = t;
            }
        }
        for (int i = 0; i < xDirection.getX() % 7; i += 1) {
            for (int j = 0; j < yDirection.getX() % 7; j += 1) {
                spaces[current.getX() + i][current.getY() + j] = t;
            }
        }
    }

    private Location testRoomDistances(Location current, int direction, TETile tile) {
        Location vals = new Location(0, 0);
        int movement1 = 0;
        int movement2 = 0;
        Location m1 = new Location(current.getX(), current.getY());
        Location m2 = new Location(current.getX(), current.getY());
        if (direction > 1) {
            movement1 = 1;
        } else {
            movement2 = 1;
        }
        //positive side
        while (checkBounds(direction, new Location(m1.getX() + 2 * movement1,
                m1.getY() + 2 * movement2))) {
            if (this.spaces[m1.getX() + 2 * movement1][m1.getY() + 2 * movement2].equals(tile)
                    &&
                        this.spaces[m1.getX() + movement1][m1.getY() + movement2].equals(tile)) {
                break;
            }
            m1.addX(movement1);
            m1.addY(movement2);

        }

        //negative side
        while (checkBounds(direction, new Location(m2.getX() - 2 * movement1,
                m2.getY() - 2 * movement2))) {
            if (this.spaces[m2.getX() - 2 * movement1][m2.getY() - 2 * movement2].equals(tile)
                    &&
                        this.spaces[m2.getX() - movement1][m2.getY() - movement2].equals(tile)) {
                break;
            }
            m2.addX(-1 * movement1);
            m2.addY(-1 * movement2);


        }

        if (direction > 1) {
            vals.setX(m1.getX() - current.getX());
            vals.setY(current.getX() - m2.getX());
        } else {
            vals.setX(m1.getY() - current.getY());
            vals.setY(current.getY() - m2.getY());
        }
        return vals;
    }

    private void trimDeadEnd(Location current, TETile tileOld, TETile tileNew) {
        if (current.getX() == 0 || current.getY() == 0
                || current.getX() >= width - 1 || current.getY() >= height - 1) {
            return;
        }

        int dir = deadEndsDirection(current);
        Location direction = util.setMovements(dir);

        if (direction.getX() != 0) {
            if (this.spaces[current.getX() + 2 * direction.getX()]
                    [current.getY() + 1].equals(tileOld)
                || this.spaces[current.getX() + 2 * direction.getX()]
                    [current.getY() - 1].equals(tileOld)) {
                roomLocations.push(current);
                return;
            }

        } else {
            if (this.spaces[current.getX() + 1]
                    [current.getY() + 2 * direction.getY()].equals(tileOld)
                || this.spaces[current.getX() - 1]
                    [current.getY() + 2 * direction.getY()].equals(tileOld)) {
                roomLocations.push(current);
                return;
            }
        }

        this.spaces[current.getX()][current.getY()] = tileNew;
        current.addX(direction.getX());
        current.addY(direction.getY());
        trimDeadEnd(current, tileOld, tileNew);
    }


   //Takes a dead end and determines where it originated from
    private int deadEndsDirection(Location current) {
        TETile tile = this.spaces[current.getX()][current.getY()];
        if (this.spaces[current.getX() + 1][current.getY()].equals(tile)) {
            return 0;
        } else if (this.spaces[current.getX() - 1][current.getY()].equals(tile)) {
            return 1;
        } else if (this.spaces[current.getX()][current.getY() + 1].equals(tile)) {
            return 2;
        } else if (this.spaces[current.getX() ][current.getY() - 1].equals(tile)) {
            return 3;
        } else {
            return -1;
        }
    }

    //Checks if a location is a dead end
    private boolean checkNeighbors(Location current, TETile tile) {
        if (current.getX() + 3 < width) {
            if (!spaces[current.getX() + 3][current.getY()].equals(tile)) {
                return false;
            }
        }
        if (current.getX() - 3 > 0) {
            if (!spaces[current.getX() - 3][current.getY()].equals(tile)) {
                return false;
            }
        }
        if (current.getY() + 3 < height) {
            if (!spaces[current.getX()][current.getY() + 3].equals(tile)) {
                return false;
            }
        }
        if (current.getY() - 3 > 0) {
            if (!spaces[current.getX()][current.getY() - 3].equals(tile)) {
                return false;
            }
        }
        return true;
    }


    //Makes a path in directions from 0-3, right, left, up, down,
    // makes path of that tile from
    // current position for that distance
    private boolean generateSinglePath(int xPosition, int yPosition,
                                       int direction, TETile tile, int distance) {
        int x = xPosition;
        int y = yPosition;
        Location movement = util.setMovements(direction);

        if (((x + (distance * movement.getX())) >= width
                || (x + (distance * movement.getX())) < 0)
                || ((y + (distance * movement.getY())) >= height
                || (y + (distance * movement.getY())) < 0)) {
            return false;
        }

        if (this.spaces[x + (distance * movement.getX())]
                [y + (distance * movement.getY())].equals(tile)) {
            return false;
        }

        for (int i = 0; i <= distance; i++) {
            this.spaces[x][y] = tile;
            x = x + movement.getX();
            y = y + movement.getY();
        }

        return true;

    }

    public boolean checkBounds(int direction, Location current) {
        if (direction > 1) {
            return (current.getX() > 0 && current.getX() < width - 1);
        }
        return (current.getY() > 0 && current.getY() < height - 1);
    }

    //Places Nodes over entire map
    private void blockify(TETile surrounding, TETile center) {
        for (int x = 0; x < this.width; x += 3) {
            for (int y = 0; y < this.height; y += 3) {
                singleBlock(x, y, surrounding, center);
            }
        }
    }

    //Single node on map that is 3x3
    private void singleBlock(int x, int y, TETile surrounding, TETile center) {

        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                this.spaces[x + i][y + j] = surrounding;
            }
        }

        this.spaces[x + 1][y + 1] = center;
    }

    public void moveUserUp(TETile check) {
        Location movement = util.setMovements(2);
        boolean movementSuccessful = this.moveUser(movement, check);
        if (movementSuccessful) {
            this.addToUserMovements("2");
        }
    }

    public void moveUserDown(TETile check) {
        Location movement = util.setMovements(3);
        boolean movementSuccessful = this.moveUser(movement, check);
        if (movementSuccessful) {
            this.addToUserMovements("3");
        }
    }

    public void moveUserRight(TETile check) {
        Location movement = util.setMovements(0);
        boolean movementSuccessful = this.moveUser(movement, check);
        if (movementSuccessful) {
            this.addToUserMovements("0");
        }
    }

    public void moveUserLeft(TETile check) {
        Location movement = util.setMovements(1);
        boolean movementSuccessful = this.moveUser(movement, check);
        if (movementSuccessful) {
            this.addToUserMovements("1");
        }
    }

    public Location moveUserBack(TETile replacement) {
        Location temp = this.hero.moveUserBack();
        this.spaces[temp.getX()][temp.getY()] = replacement;

        return this.hero.getPosition();
    }

    public void moveUserBackToLocation(Location spot, TETile replacement) {
        while (true) {
            this.heroMovements = this.heroMovements.substring(0, heroMovements.length() - 1);
            Location current = this.moveUserBack(replacement);
            if (current.getX() == spot.getX() && current.getY() == spot.getY()) {

                break;
            }
        }
    }

    public boolean moveUser(Location movement, TETile check) {
        Location current = this.hero.getPosition();
        Location newPos = new
                Location(current.getX() + movement.getX(),
                current.getY() + movement.getY());
        if (this.spaces[newPos.getX()][newPos.getY()].equals(check)
                || this.spaces[newPos.getX()][newPos.getY()]
                .equals(Tileset.UNLOCKED_DOOR)) {
            this.hero.changePosition(movement);
            this.spaces[newPos.getX()][newPos.getY()] = this.hero.getRepresentation();
            return true;
        } else if (this.spaces[newPos.getX()][newPos.getY()]
                .equals(this.hero.getRepresentation())) {
            moveUserBackToLocation(newPos, Tileset.FLOOR);
        } else if (this.spaces[newPos.getX()][newPos.getY()].equals(Tileset.KEY)) {
            this.hero.changePosition(movement);
            this.spaces[newPos.getX()][newPos.getY()] = this.hero.getRepresentation();
            this.spaces[door.getX()][door.getY()] = Tileset.UNLOCKED_DOOR;
        }
        return false;
    }

    public boolean moveEnemy() {
        Location current = villain.getPosition();
        int direction = this.scanForTarget();

        Location movement = util.setMovements(direction);
        Location nextPos = new
                Location(
                        current.getX() + movement.getX(), current.getY() + movement.getY());

        if (this.spaces[nextPos.getX()][nextPos.getY()].equals(Tileset.FLOOR)) {
            //move to spot
            this.setEnemyLocation(current, nextPos);
        } else if (this.spaces[nextPos.getX()][nextPos.getY()].equals(Tileset.PLAYER)) {
            //move onto player/path end game
            return true;
            //return True end game
        } else if (this.spaces[nextPos.getX()][nextPos.getY()].equals(Tileset.LOCKED_DOOR)
                || this.spaces[nextPos.getX()][nextPos.getY()].equals(Tileset.UNLOCKED_DOOR)) {
            //move around door object
            Location warp = new Location(nextPos.getX(), nextPos.getY());
            if (this.spaces[nextPos.getX() + 1][nextPos.getY()].equals(Tileset.FLOOR)) {
                warp.addX(1);
            } else if (this.spaces[nextPos.getX() - 1][nextPos.getY()].equals(Tileset.FLOOR)) {
                warp.addX(-1);
            } else if (this.spaces[nextPos.getX()][nextPos.getY() + 1].equals(Tileset.FLOOR)) {
                warp.addY(1);
            } else if (this.spaces[nextPos.getX()][nextPos.getY() - 1].equals(Tileset.FLOOR)) {
                warp.addY(-1);
            }
            this.setEnemyLocation(current, warp);

        } else if (this.spaces[nextPos.getX()][nextPos.getY()].equals(Tileset.WALL)) {
            villain.setDirection(random.nextInt(4));
        }

        return false;
    }


    private void setEnemyLocation(Location current, Location nextPos) {
        this.spaces[current.getX()][current.getY()] = Tileset.FLOOR;
        this.spaces[nextPos.getX()][nextPos.getY()] = villain.getRepresentation();
        villain.setPostion(nextPos);
    }

    private int scanForTarget() {
        int direction = villain.getDirection();
        Location current = villain.getPosition();
        for (int i = 0; i < 4; i++) {
            Location temp = new Location(current.getX(), current.getY());
            Location movement = util.setMovements(i);

            while (!spaces[temp.getX()][temp.getY()].equals(Tileset.WALL)) {
                if (spaces[temp.getX()][temp.getY()].equals(hero.getRepresentation())) {
                    return i;
                } else if (spaces[temp.getX()][temp.getY()].description().equals("wall")) {
                    break;
                }

                temp.addX(movement.getX());
                temp.addY(movement.getY());
            }
        }

        return direction;
    }

    private void placeObject(TETile thing) {
        while (true) {
            int rand1 = this.random.nextInt(width);
            int rand2 = this.random.nextInt(height);
            if (this.spaces[rand1][rand2].equals(Tileset.FLOOR)) {
                this.spaces[rand1][rand2] = thing;
                if (thing.equals(Tileset.LOCKED_DOOR)) {
                    door = new Location(rand1, rand2);
                }
                break;
            }
        }
    }

    private Location placeEnemy() {
        Location enemyPos = door;
        if (this.spaces[enemyPos.getX() + 1][enemyPos.getY()].equals(Tileset.FLOOR)) {
            enemyPos = new Location(enemyPos.getX() + 1, enemyPos.getY());
        } else if (this.spaces[enemyPos.getX() - 1][enemyPos.getY()].equals(Tileset.FLOOR)) {
            enemyPos = new Location(enemyPos.getX() - 1, enemyPos.getY());
        } else if (this.spaces[enemyPos.getX()][enemyPos.getY() + 1].equals(Tileset.FLOOR)) {
            enemyPos = new Location(enemyPos.getX(), enemyPos.getY() + 1);
        } else {
            enemyPos = new Location(enemyPos.getX(), enemyPos.getY() + 1);
        }
        return enemyPos;
    }

    public void loadMovementsIntoStack() {
        int count = heroMovements.length();
        Location current = new Location(1, 1);
        for (int i = 0; i < count; i++) {
            Location nextLocation = new Location(current.getX(), current.getY());
            this.hero.addToPath(nextLocation);
            Location movement =
                    util.setMovements(
                            Character.getNumericValue(heroMovements.charAt(i)));


            current.addX(movement.getX());
            current.addY(movement.getY());
        }
    }

    private void addToUserMovements(String movements) {
        this.heroMovements += movements;
    }

    public TETile[][] spaces() {
        return this.spaces;
    }

    public User getUser() {
        return this.hero;
    }

    public Location getDoor() {
        return this.door;
    }

}
