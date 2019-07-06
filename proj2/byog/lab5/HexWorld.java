package byog.lab5;

import byog.TileEngine.TERenderer;
import byog.TileEngine.TETile;
import byog.TileEngine.Tileset;


/**
 * Draws a world consisting of hexagonal regions.
 */
public class HexWorld {
    private static final int WIDTH = 60;
    private static final int HEIGHT = 30;


    //checks if out of bounds
    //generates a row of num length starting at position xPosition, yPosition
    public static boolean generateRow(int num, TETile[][] world,
                                      int xPosition, int yPosition, TETile t) {
        if (xPosition + (num - 1) >= world.length
                ||
                xPosition < 0 || yPosition < 0
                ||
                yPosition >= world[0].length) {
            return false;
        }
        for (int x = 0; x < num; x++) {
            world[xPosition + x][yPosition] = t;
        }
        return true;
    }



    //starts being from the left most box of the bottom middle row
    //will check if out of bounds
    public static boolean generateHexagon(int num, TETile[][] world,
                                          int xPosition, int yPosition, TETile t) {
        if (world[0].length <= yPosition + (num)
                ||
                world.length <= xPosition + (((num - 1) * 2) + num)
                ||
                xPosition < 0 || yPosition - (num - 1) < 0) {
            return false;
        }


        int yBottom = yPosition; //bottom middle row start
        int yTop = yPosition + 1; //top middle row start

        for (int x = num - 1; x >= 0; x--) {

            //x*2 + num will equal the row length needed start at middle level and go out
            int xPos = (xPosition + (num - (x + 1)));
            generateRow((x * 2 + num), world, xPos, yTop, t);
            generateRow((x * 2 + num), world, xPos, yBottom, t);
            yTop++;
            yBottom--;

        }

        return true;
    }



    public static void main(String[] args) {
        // initialize the tile rendering engine with a window of size WIDTH x HEIGHT
        TERenderer ter = new TERenderer();
        ter.initialize(WIDTH, HEIGHT);

        // initialize tiles
        TETile[][] world = new TETile[WIDTH][HEIGHT];
        for (int x = 0; x < WIDTH; x += 1) {
            for (int y = 0; y < HEIGHT; y += 1) {
                world[x][y] = Tileset.NOTHING;
            }
        }


        boolean check = generateHexagon(3, world, 0, 24, Tileset.FLOWER);
        System.out.println(check);

        ter.renderFrame(world);
    }

}
