package byog.Core;

import byog.TileEngine.TERenderer;
import byog.TileEngine.TETile;
import byog.TileEngine.Tileset;
import byog.phase1.Map;
import byog.phase1.Location;
import byog.phase1.GeneralUtils;
import edu.princeton.cs.introcs.StdDraw;
import java.awt.Color;
import java.awt.Font;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class Game {
    private TERenderer ter = new TERenderer();
    /* Feel free to change the width and height. */
    private static final int WIDTH = 63;
    private static final int HEIGHT = 47;
    private static long lastGame = 0;
    private static long seed = 9223372036854775807L;
    private Map lastMap = new Map(WIDTH, HEIGHT, seed);
    private String lastMouseHover = "";
    private GeneralUtils util = new GeneralUtils();
    private boolean lost = false;

    //return a string saying the name of the current tile the mouse is on
    private String mouseOver() {
        double xPos = StdDraw.mouseX();
        double yPos = StdDraw.mouseY();
        TETile[][] currentMap = lastMap.spaces();
        if (xPos < WIDTH && xPos >= 0 && yPos < HEIGHT - 2 && yPos >= 0) {
            TETile mouseOn = currentMap[(int) xPos][ (int) yPos];
            return mouseOn.description();
        }
        return "";
    }

    //create start menu
    private void startMenu() {
        StdDraw.setCanvasSize(1000, 800);
        Font titleFont = new Font("Monaco", Font.BOLD, 30);
        Font font = new Font("Monaco", Font.BOLD, 20);
        StdDraw.setXscale(0, WIDTH);
        StdDraw.setYscale(0, HEIGHT);
        StdDraw.clear(Color.BLACK);
        StdDraw.setPenColor(Color.white);
        printScreen(WIDTH / 2, HEIGHT * 3 / 4, titleFont, "CS61B: THE GAME");
        printScreen(WIDTH / 2, HEIGHT / 2, font, "New Game (N)");
        printScreen(WIDTH / 2, (HEIGHT / 2) - 2, font, "Load Game (L)");
        printScreen(WIDTH / 2, (HEIGHT / 2) - 4, font,  "Quit (Q)");
    }

    //takes in commands to start game
    private void menuChoice() {
        while (true) {
            if (StdDraw.hasNextKeyTyped()) {
                String input = Character.toString(StdDraw.nextKeyTyped());
                if (input.equals("q") || input.equals("Q")) {
                    System.exit(0);
                }
                if (input.equals("n") || input.equals("N")) {
                    lastMap = makeNewGame();
                    break;
                }
                if (input.equals("l") || input.equals("L")) {
                    Map savedMap = loadGame();
                    lastMap = savedMap;
                    lastMap.loadMovementsIntoStack();
                    ter.renderFrame(lastMap.spaces());
                    break;
                }
            }
        }
    }

    //run game loop
    private void runGame() {
        while (true) {

            if (StdDraw.hasNextKeyTyped()) {
                lost = lastMap.moveEnemy();
                String commands = Character.toString(StdDraw.nextKeyTyped());
                if (commands.equals("q") || commands.equals("Q")) {
                    saveGame(lastMap);
                    System.exit(0);
                }
                if (commands.equals("s") || commands.equals("S")) {
                    lastMap.moveUserDown(Tileset.FLOOR);
                    ter.renderFrame(lastMap.spaces());
                }
                if (commands.equals("w") || commands.equals("W")) {
                    lastMap.moveUserUp(Tileset.FLOOR);
                    ter.renderFrame(lastMap.spaces());
                }
                if (commands.equals("a") || commands.equals("A")) {
                    lastMap.moveUserLeft(Tileset.FLOOR);
                    ter.renderFrame(lastMap.spaces());
                }
                if (commands.equals("d") || commands.equals("D")) {
                    lastMap.moveUserRight(Tileset.FLOOR);
                    ter.renderFrame(lastMap.spaces());
                }
            }
            String mouseIdentifier = mouseOver();
            if (!mouseIdentifier.equals(lastMouseHover)) {
                StdDraw.clear(Color.BLACK);
                lastMouseHover = mouseIdentifier;
                ter.renderFrame(lastMap.spaces());
            }
            ter.renderFrame(lastMap.spaces());
            Font font = new Font("Monaco", Font.BOLD, 20);
            StdDraw.setFont(font);
            StdDraw.setPenColor(Color.white);
            StdDraw.textLeft(0, HEIGHT - 1, mouseIdentifier);
            StdDraw.text(WIDTH / 2, HEIGHT - 1, objective());
            if (winGame() || lost) {
                StdDraw.clear(Color.BLACK);
                ter.renderFrame(lastMap.spaces());
                break;
            }
            StdDraw.show();
        }
        while (true) {
            Font font = new Font("Monaco", Font.BOLD, 20);
            StdDraw.setFont(font);
            StdDraw.setPenColor(Color.white);
            StdDraw.text(WIDTH / 2, HEIGHT - 1, objective());
            StdDraw.textRight(WIDTH - 1, HEIGHT - 1, "Press M to play again, Q to exit");
            if (StdDraw.hasNextKeyTyped()) {
                String commands = Character.toString(StdDraw.nextKeyTyped());
                if ((commands.equals("m") || commands.equals("M"))) {
                    lost = false;
                    saveGame(lastMap);
                    break;
                }
                if (commands.equals("q") || commands.equals("Q")) {
                    saveGame(lastMap);
                    System.exit(0);
                }
            }
            StdDraw.show();
        }
    }

    //take in a seed and make a board
    private Map makeNewGame() {
        Font font = new Font("Monaco", Font.BOLD, 20);
        StdDraw.clear(Color.BLACK);
        printScreen(WIDTH / 2, HEIGHT / 2, font,
                "Please Enter a random integer seed, then press S");
        String containsSeed = "";
        while (true) {
            if (StdDraw.hasNextKeyTyped()) {
                String seedGiven = Character.toString(StdDraw.nextKeyTyped());
                if (isNumber(seedGiven)) {
                    containsSeed += seedGiven;
                    StdDraw.clear(Color.BLACK);
                    printScreen(WIDTH / 2, HEIGHT / 2, font,
                            "Please Enter a random integer seed, then press S");
                    printScreen(WIDTH / 2 - 3, HEIGHT / 2 - 3, font, containsSeed);
                }
                if (seedGiven.equals("s") || seedGiven.equals("S")) {
                    if (!containsSeed.equals("")) {
                        lastGame = Long.valueOf(containsSeed);
                    }
                    return createBoard(lastGame);
                }
            }
        }
    }

    //creates game-board using given seed
    private Map createBoard(Long num) {
        Map board = new Map(WIDTH, HEIGHT - 2, num);
        board.generateMap();
        ter.renderFrame(board.spaces());
        return board;
    }

    //checks if a string is a number
    private boolean isNumber(String s) {
        try {
            Double num = Double.parseDouble(s);
        } catch (NumberFormatException n) {
            return false;
        }
        return true;
    }

    //print to the StdDraw screen
    private void printScreen(double width, double height, Font font, String message) {
        StdDraw.setFont(font);
        StdDraw.text(width, height, message);
        StdDraw.enableDoubleBuffering();
        StdDraw.show();
    }

    //@Source SaveDemo
    //load the game
    private static Map loadGame() {
        File saveFile = new File("./savedMap.txt");
        if (saveFile.exists()) {
            try {
                FileInputStream fStream = new FileInputStream(saveFile);
                ObjectInputStream oStream = new ObjectInputStream(fStream);
                return (Map) oStream.readObject();
            } catch (FileNotFoundException e) {
                System.out.println("file not found");
                System.exit(0);
            } catch (IOException e) {
                System.out.println(e);
                System.exit(0);
            } catch (ClassNotFoundException e) {
                System.out.println("class not found");
                System.exit(0);
            }
        }
        // If no Map has been saved yet return a new one.
        return new Map(WIDTH, HEIGHT - 2, seed);
    }

    //@Source SaveDemo
    //save the game
    private static void saveGame(Map savedMap) {
        File savedFile = new File("./savedMap.txt");
        try {
            if (!savedFile.exists()) {
                savedFile.createNewFile();
            }
            FileOutputStream fStream = new FileOutputStream(savedFile);
            ObjectOutputStream oStream = new ObjectOutputStream(fStream);
            oStream.writeObject(savedMap);
        }  catch (FileNotFoundException e) {
            System.out.println("file not found");
            System.exit(0);
        } catch (IOException e) {
            System.out.println(e);
            System.exit(0);
        }
    }

    //give the current game objective
    private String objective() {
        if (lost) {
            return "YOU LOSE :///";
        } else if (winGame()) {
            return "YOU WIN!";
        } else if (lastMap.spaces()[lastMap.getDoor().getX()]
                [lastMap.getDoor().getY()].equals(Tileset.UNLOCKED_DOOR)) {
            return "door unlocked! get to safety!";
        } else {
            return "get the key!";
        }
    }

    //check if game is over
    private boolean winGame() {
        Location winningPos = lastMap.getDoor();
        return (lastMap.spaces()[winningPos.getX()][winningPos.getY()].equals(
                lastMap.getUser().getRepresentation()));
    }

    /**
     * Method used for playing a fresh game. The game should start from the main menu.
     */
    public void playWithKeyboard() {
        while (true) {
            startMenu();
            menuChoice();
            runGame();
            StdDraw.clear(Color.black);
        }
    }

    /**
     * Method used for autograding and testing the game code. The input string will be a series
     * of characters (for example, "n123sswwdasdassadwas", "n123sss:q", "lwww". The game should
     * behave exactly as if the user typed these characters into the game after playing
     * playWithKeyboard. If the string ends in ":q", the same world should be returned as if the
     * string did not end with q. For example "n123sss" and "n123sss:q" should return the same
     * world. However, the behavior is slightly different. After playing with "n123sss:q", the game
     * should save, and thus if we then called playWithInputString with the string "l", we'd expect
     * to get the exact same world back again, since this corresponds to loading the saved game.
     * @param input the input string to feed to your program
     * @return the 2D TETile[][] representing the state of the world
     */
    public TETile[][] playWithInputString(String input) {
        // Fill out this method to run the game using the input passed in,
        // and return a 2D tile representation of the world that would have been
        // drawn if the same inputs had been given to playWithKeyboard().
        char first = input.charAt(0);
        TETile[][] finalWorldFrame = null;
        String[] endOfString = input.split(":");
        int index = 1;
        if (first == 'n' || first == 'N') {
            String numToConvert = "";
            while (true) {
                String temp = Character.toString(input.charAt(index));
                if (temp.equals("s") || temp.equals("S")) {
                    index += 1;
                    break;
                } else {
                    index += 1;
                    numToConvert += temp;
                }
            }
            if (!numToConvert.equals("")) {
                seed = Long.valueOf(numToConvert);
            }
            lastGame = seed;
            Map board = new Map(WIDTH, HEIGHT - 2, lastGame);
            lastMap = board;
            lastMap.generateMap();
        } else if (first == 'l' || first == 'L') {
            lastMap = loadGame();
        }
        while (true) {
            if (index >= input.length()) {
                break;
            }
            String temp = Character.toString(input.charAt(index));
            if (temp.equals(":")) {
                break;
            }
            if (temp.equals("s") || temp.equals("S")) {
                lastMap.moveUserDown(Tileset.FLOOR);
            }
            if (temp.equals("w") || temp.equals("W")) {
                lastMap.moveUserUp(Tileset.FLOOR);
            }
            if (temp.equals("a") || temp.equals("A")) {
                lastMap.moveUserLeft(Tileset.FLOOR);
            }
            if (temp.equals("d") || temp.equals("D")) {
                lastMap.moveUserRight(Tileset.FLOOR);
            }
            index += 1;
        }
        if (endOfString.length == 2 && endOfString[1].length() == 1) {
            if (endOfString[1].charAt(0) == 'q' || endOfString[1].charAt(0) == 'Q') {
                saveGame(lastMap);
            }
        }
        finalWorldFrame = lastMap.spaces();
        return finalWorldFrame;
    }
}
