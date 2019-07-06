package byog.phase1;
import java.io.Serializable;

public class Location implements Serializable {
    private static final long serialVersionUID = 325342525234234L;
    private int x;
    private int y;
    public Location(int newX, int newY) {
        x = newX;
        y = newY;
    }

    public int getX() {
        return this.x;
    }

    public int getY() {
        return this.y;
    }

    public void setX(int newX) {
        this.x = newX;
    }

    public void setY(int newY) {
        this.y = newY;
    }

    public void addX(int newX) {
        this.x += newX;
    }

    public void addY(int newY) {
        this.y += newY;
    }
}
