package byog.phase1;


import byog.TileEngine.TETile;
import byog.TileEngine.Tileset;
import java.io.Serializable;

public class Pieces implements Serializable {
    private static final long serialVersionUID = 325342525234234L;
    private Location position;
    private TETile representation;

    Pieces() {
        this.position = new Location(1, 1);
        this.representation = Tileset.FLOOR;
    }

    Pieces(Location l, TETile r) {
        this.position = l;
        this.representation = r;
    }

    public void changePosition(Location l) {
        this.position = l;
    }

    public Location getPosition() {
        return this.position;
    }

    public void setPostion(Location l) {
        this.position = l;
    }

    public void changeRepresentation(TETile r) {
        this.representation = r;
    }

    public TETile getRepresentation() {
        return this.representation;
    }


}
