package byog.phase1;

import java.io.Serializable;

public class GeneralUtils implements Serializable {
    //Makes a path in directions from 0-3
    //returns a location object consisting of pair (1,0), (-1,0), (0,1), (0,-1) 
    //these are then used to determine which way to go based off inputted directions
    private static final long serialVersionUID = 325342525234234L;
    public Location setMovements(int direction) {
        Location dir = new Location(0, 0);
        switch (direction) {
            case 0: 
                dir.setX(1);
                break;
            case 1: 
                dir.setX(-1);
                break;
            case 2: 
                dir.setY(1);
                break;
            default:
                dir.setY(-1);
                break;
        }
        return dir;
    }

}
