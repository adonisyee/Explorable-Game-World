package byog.TileEngine;

import java.awt.Color;

/**
 * Contains constant tile objects, to avoid having to remake the same tiles in different parts of
 * the code.
 *
 * You are free to (and encouraged to) create and add your own tiles to this file. This file will
 * be turned in with the rest of your code.
 *
 * Ex:
 *      world[x][y] = Tileset.FLOOR;
 *
 * The style checker may crash when you try to style check this file due to use of unicode
 * characters. This is OK.
 */

public class Tileset {
    // public static final TETile PLAYER = new TETile('@', Color.white, Color.black, "player");
    public static final TETile WALL =
            new TETile('#', new Color(0, 31, 63), new Color(0, 31, 63),
            "wall");
    public static final TETile FLOOR =
            new TETile('·', new Color(100, 100, 100), new Color(221, 221, 221),
            "floor");
    public static final TETile NOTHING =
            new TETile(' ', Color.black, Color.black, "nothing");
    public static final TETile GRASS =
            new TETile('"', Color.green, Color.black, "grass");
    public static final TETile WATER =
            new TETile('≈', Color.blue, Color.black, "water");
    public static final TETile FLOWER =
            new TETile('❀', Color.magenta, Color.pink, "flower");
    public static final TETile LOCKED_DOOR =
            new TETile('█', Color.orange, Color.black,
            "locked door");
    public static final TETile UNLOCKED_DOOR =
            new TETile('▢', Color.orange, Color.black,
            "unlocked door");
    public static final TETile SAND =
            new TETile('▒', Color.yellow, Color.black, "sand");
    public static final TETile MOUNTAIN =
            new TETile('▲', Color.gray, Color.black, "mountain");
    public static final TETile TREE =
            new TETile('♠', Color.green, Color.black, "tree");

    //Player Directions
    public static final TETile PLAYER =
            new TETile('◈', new Color(0, 116, 217), new Color(221, 221, 221),
             "player");
    public static final TETile PLAYERRIGHT =
            new TETile('◈', new Color(0, 116, 217), new Color(221, 221, 221),
             "playerright");
    public static final TETile PLAYERDOWN =
            new TETile('▼', new Color(0, 116, 217), new Color(221, 221, 221),
             "playerdown");
    public static final TETile PLAYERLEFT =
            new TETile('◃', new Color(0, 116, 217), new Color(221, 221, 221),
             "playerleft");
    public static final TETile KEY =
            new TETile('*', new Color(245, 230, 99), Color.black,
            "key");



    public static final TETile ENEMY =
            new TETile('▲', new Color(194, 24, 7), new Color(221, 221, 221),
             "enemy");
}


