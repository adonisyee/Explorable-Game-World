package byog.phase1;

import java.util.Stack;
import byog.TileEngine.TETile;
import java.io.Serializable;

public class User extends GameCharacter implements Serializable {
    private static final long serialVersionUID = 325342525234234L;
    private static Stack<Location> path = new Stack();
    public User(Location l, TETile r) {
        super(l, r);
    }
    @Override
    public void changePosition(Location l) {
        Location current = this.getPosition();
        Location nextOne = new Location(current.getX(), current.getY());

        nextOne.addX(l.getX());
        nextOne.addY(l.getY());
        this.setPostion(nextOne);


        path.push(current);
    }

    public Location moveUserBack() {
        Location temp = this.getPosition();
        Location last = this.lastLocation();
        this.setPostion((Location) this.path.pop());
        return temp;
    }

    public void addToPath(Location current) {
        path.push(current);
    }

    public Location lastLocation() {
        return (Location) this.path.peek();
    }

}
