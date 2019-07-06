package byog.phase1;

import byog.TileEngine.TETile;
import java.util.Random;
import java.io.Serializable;

public class Enemy extends GameCharacter implements Serializable {
    private static final long serialVersionUID = 325342525234234L;
    private Random random;

    private int direction;
    private GeneralUtils util = new GeneralUtils();


    public Enemy(Location l, TETile r, Random sr) {
        super(l, r);
        random = sr;
        this.direction = 0;

    }

    @Override
    public void changePosition(Location l) {
        Location current = this.getPosition();
        current.addX(l.getX());
        current.addY(l.getY());
    }

    public void setDirection(int d) {
        this.direction = d;
    }

    public int getDirection() {
        return this.direction;
    }
}
