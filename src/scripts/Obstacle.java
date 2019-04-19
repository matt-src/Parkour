package scripts;

import org.powerbot.script.Area;
import org.powerbot.script.rt4.GameObject;

public class Obstacle {
    private int id;
    private GameObject gameObject;
    private Area gameArea;
    private int[] objectBounds;
    private String action;

    Obstacle(int id, GameObject gameObject, Area gameArea, int[] objectBounds, String action) {
        this.id = id;
        this.gameObject = gameObject;
        this.gameArea = gameArea;
        this.objectBounds = objectBounds;
    }

    GameObject getGameObject() {
        return gameObject;
    }

    public void setGameObject(GameObject gameObject) {
        this.gameObject = gameObject;
    }

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

    String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

}
