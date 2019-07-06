package byog.phase1;

import byog.TileEngine.TETile;
import java.io.Serializable;

public class GameCharacter extends Pieces implements Serializable {
    private static final long serialVersionUID = 325342525234234L;
    //Add movement functions

    public GameCharacter(Location l, TETile r) {
        super(l, r);
    }

    //Movement checks should be done in game
    @Override
    public void changePosition(Location l) {
        Location current = this.getPosition();
        current.addX(l.getX());
        current.addY(l.getY());
    }
}
