package byog.lab5;

import byog.phase1.Map;
import byog.phase1.Location;
import byog.TileEngine.TERenderer;
import byog.TileEngine.Tileset;

/**
 *  Draws a world that is mostly empty except for a small region.
 */
public class BoringWorldDemo {
    private static final int WIDTH = 63;
    private static final int HEIGHT = 45;
    private static final long SEED = 123;


    public static void main(String[] args) {
        // initialize the tile rendering engine with a window of size WIDTH x HEIGHT
        TERenderer ter = new TERenderer();
        ter.initialize(WIDTH, HEIGHT);

        Map board = new Map(WIDTH, HEIGHT, SEED);

        // initialize tiles
        
        
        board.generateMap();




        board.moveUserUp(Tileset.FLOOR);



        board.moveUserBackToLocation(new Location(1, 1), Tileset.FLOOR);
        

        // draws the world to the screen
        ter.renderFrame(board.spaces());
    }


}



