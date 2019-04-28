package scripts;

import org.powerbot.script.Area;
import org.powerbot.script.Tile;

public class Obstacle {
    private int id;
    private String name; //Object name
    private Area gameArea; //Game area to check if player is in, to know if we should use this obstacle
    private Tile[] obstacleTiles; //Tiles that our obstacle may be on
    private int[] objectBounds;
    private String codeName; //eg. "Gap Four"

    Obstacle(int id, String name, Area gameArea, Tile[] obstacleTiles, int[] objectBounds, String codeName) {
        this.id = id;
        this.name = name;
        this.gameArea = gameArea;
        this.obstacleTiles = obstacleTiles;
        this.objectBounds = objectBounds;
        this.codeName = codeName;
    }

    public String getName() { return name; }

    public void setName(String name) { this.name = name; }

    public Tile[] getObstacleTiles() { return obstacleTiles; }

    public void setObstacleTiles(Tile[] obstacleTiles) { this.obstacleTiles = obstacleTiles; }

    Area getGameArea() {
        return gameArea;
    }

    public void setGameArea(Area gameArea) {
        this.gameArea = gameArea;
    }

    int[] getObjectBounds() {
        return objectBounds;
    }

    public void setObjectBounds(int[] objectBounds) {
        this.objectBounds = objectBounds;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCodeName() { return this.codeName; }

}
